/*
 * Copyright 2014-present Yunarta
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mobilesolutionworks.android.http;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.net.http.AndroidHttpClient;

import com.mobilesolutionworks.android.util.IOUtils;
import com.mobilesolutionworks.android.util.TypeUtils;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HttpContext;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Works http client, the main class for this library execution.
 */
public class WorksHttpClient {

    //-- class member --//

    /**
     * Application context
     */
    private Context mContext;

    /**
     *
     */
    private final String mName;

    /**
     * Constructor of http client
     *
     * @param context application context
     */
    protected WorksHttpClient(Context context) {
        mContext = context.getApplicationContext();
        String name = mContext.getPackageName();

        try {
            PackageManager pm = mContext.getPackageManager();
            if (pm != null) {
                ApplicationInfo ai = pm.getApplicationInfo(mContext.getPackageName(), 128);
                if ((ai != null) && (ai.metaData != null)) {
                    name = ai.metaData.getString("user_agent");
                }

                PackageInfo pi = pm.getPackageInfo(mContext.getPackageName(), 0);
                if (pi != null) {
                    name = name + " ver-" + pi.versionName + " build-" + pi.versionCode;
                }
            }
        } catch (PackageManager.NameNotFoundException e) {
        }

        mName = name;
    }

    /**
     * Get the android http client configured for this context
     *
     * @return the Android http client configured for this context
     */
    public AndroidHttpClient getHttpClient() {
        return AndroidHttpClient.newInstance(System.getProperty("http.agent"), mContext);
    }

    /**
     * Get the package name and version
     *
     * @return the package name and version;
     */
    public String getName() {
        return mName;
    }


    //-- static member --//

    /**
     * Works http client instance
     */
    private static WorksHttpClient sInstance;

    /**
     * Get the works http client instance for this application context
     *
     * @param context application context
     * @return works http client instance
     */
    public static WorksHttpClient getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new WorksHttpClient(context);
        }

        return sInstance;
    }

    /**
     * Execute the request on this works http client.
     *
     * @param context  application context
     * @param request  works http request
     * @param listener works operation listener
     * @return works http response
     */
    public static <Result> WorksHttpResponse<Result> executeOperation(Context context, WorksHttpRequest request, final WorksHttpOperationListener listener) {
        final HttpUriRequest httpRequest;
        HttpResponse httpResponse = null;

        WorksHttpResponse<Result> response = new WorksHttpResponse<Result>();
        response.request = request;

        WorksHttpClient instance = WorksHttpClient.getInstance(context);
        AndroidHttpClient client = instance.getHttpClient();

        switch (request.method) {
            default:
            case GET: {
                Uri.Builder builder = Uri.parse(request.url).buildUpon();

                if (request.httpParams != null) {
                    for (Map.Entry<String, String> entry : request.httpParams.entrySet()) {
                        builder.appendQueryParameter(entry.getKey(), entry.getValue());
                    }
                }

                httpRequest = new HttpGet(builder.build().toString());
                break;
            }

            case POST: {
                HttpPost httpPost = new HttpPost(request.url);
                if (request.httpParams != null) {
                    List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
                    for (Map.Entry<String, String> entry : request.httpParams.entrySet()) {
                        params.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
                    }

                    UrlEncodedFormEntity result;

                    try {
                        result = new UrlEncodedFormEntity(params, "UTF-8");
                        httpPost.setEntity(result);
                    } catch (UnsupportedEncodingException e) {
                        throw new RuntimeException(e);
                    }
                }

                httpRequest = httpPost;
            }
        }

        request.abortable = new WorksHttpRequest.Abortable() {
            @Override
            public void abort() {
                httpRequest.abort();
            }
        };

        if (request.preExecutor != null) {
            request.preExecutor.onPreExecute(request, httpRequest);
        }

        listener.onPreExecute(request, httpRequest);

        httpRequest.addHeader("Works-Http-Client", instance.getName());
        try {
            HttpContext httpContext = listener.getHttpContext();
            httpResponse = client.execute(httpRequest, httpContext);

            if (listener.onValidateResponse(request, httpResponse)) {
                response.statusCode = httpResponse.getStatusLine().getStatusCode();

                boolean handled = false;
                try {
                    handled = listener.onHandleResponse(request, httpRequest, response, httpResponse);
                } catch (Exception e) {
                    response.markErrorInHandler(e);
                    handled = true;
                }

                if (!handled) {
                    int contentLength = -1;
                    Header[] headers = httpResponse.getHeaders("Content-Length");
                    if (headers != null) {
                        for (Header header : headers) {
                            String value = header.getValue();
                            contentLength = TypeUtils.parseInt(value);
                        }
                    }

                    if (request.returnTransfer) {
                        InputStream content = httpResponse.getEntity().getContent();
                        response.text = IOUtils.consumeAsString(new CountingInputStream(content, listener, contentLength));
                    } else if (request.out != null) {
                        InputStream content = httpResponse.getEntity().getContent();
                        IOUtils.copy(new CountingInputStream(content, listener, contentLength), request.out);
                    }

                    response.markSuccess();
                }
            } else {
                response.markInvalidHttpStatus(httpResponse.getStatusLine().getStatusCode());
            }
        } catch (Exception e) {
            response.markErrorInExecution(e);
        } finally {
            try {
                client.close();
            } catch (Exception e) {
                // e.printStackTrace();
            }
            if (httpResponse != null) {
                try {
                    httpResponse.getEntity().consumeContent();
                } catch (IOException e) {
                    // e.printStackTrace();
                }
            }
        }

        return response;
    }

    /**
     * Input stream for counting purpose.
     */
    static class CountingInputStream extends InputStream {

        WorksHttpOperationListener mListener;

        InputStream mIn;

        int mCurrent;

        int mMaxSize;

        public CountingInputStream(InputStream in, WorksHttpOperationListener listener, int maxSize) {
            mIn = in;
            mListener = listener;
            mMaxSize = maxSize;
        }

        @Override
        public int read(byte[] buffer) throws IOException {
            int read = mIn.read(buffer);
            if (read != -1) {
                mCurrent += read;
                mListener.onReadProgressUpdate(mCurrent, mMaxSize);
            }

            return read;
        }

        @Override
        public int read(byte[] buffer, int byteOffset, int byteCount) throws IOException {
            int read = mIn.read(buffer, byteOffset, byteCount);

            if (read != -1) {
                mCurrent += read;
                mListener.onReadProgressUpdate(mCurrent, mMaxSize);
            }

            return read;
        }

        @Override
        public int read() throws IOException {
            int read = mIn.read();

            if (read != -1) {
                mCurrent += 1;
                mListener.onReadProgressUpdate(mCurrent, mMaxSize);
            }

            return read;
        }
    }
}
