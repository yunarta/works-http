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
import com.mobilesolutionworks.android.http.io.CountingInputStream;
import com.mobilesolutionworks.android.util.IOUtils;
import com.mobilesolutionworks.android.util.TypeUtils;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by yunarta on 22/1/14.
 */
public class WorksHttpClient {

    private static WorksHttpClient sInstance;

    private final String mName;

    public static WorksHttpClient getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new WorksHttpClient(context);
        }

        return sInstance;
    }

    private Context mContext;

    public WorksHttpClient(Context context) {
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
                    name = name + " " + pi.versionName;
                }
            }
        } catch (PackageManager.NameNotFoundException e) {
        }

        mName = name;
    }

    public AndroidHttpClient getHttpClient() {
        return AndroidHttpClient.newInstance(mName, mContext);
    }

    public static <Result> WorksHttpResponse<Result> executeOperation(Context context, WorksHttpRequest request, final WorksHttpOperationListener listener) {
        HttpUriRequest httpRequest;
        HttpResponse httpResponse = null;

        WorksHttpResponse<Result> response = new WorksHttpResponse<Result>();
        response.request = request;

        AndroidHttpClient client = WorksHttpClient.getInstance(context).getHttpClient();

        switch (request.method) {
            default:
            case GET: {
                Uri.Builder builder = new Uri.Builder();
                builder.appendEncodedPath(request.url);

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

                    UrlEncodedFormEntity postData = createForm(params);
                    httpPost.setEntity(postData);
                }

                httpRequest = httpPost;
            }
        }


        if (request.preExecutor != null) {
            request.preExecutor.onPreExecute(request, httpRequest);
        }

        listener.onPreExecute(request, httpRequest);

        try {
            httpResponse = client.execute(httpRequest);
            if (listener.onValidateResponse(request, httpResponse)) {
                boolean handled = false;
                try {
                    handled = listener.onHandleResponse(request, httpRequest, response, httpResponse);
                } catch (Exception e) {
                    response.errorCode = WorksHttpResponse.ErrorCode.ERR_ERROR_IN_HANDLER;
                    response.exception = e;
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
                        response.text = IOUtils.consumeAsString(new CountingInputStream(content, new CountingInputStream.OnInputListener() {
                            @Override
                            public void onInputRead(int read, int maxSize) {
                                listener.onReadProgressUpdate(read, maxSize);
                            }
                        }, contentLength));
                    } else if (request.out != null) {
                        InputStream content = httpResponse.getEntity().getContent();
                        IOUtils.copy(new CountingInputStream(content, new CountingInputStream.OnInputListener() {
                            @Override
                            public void onInputRead(int read, int maxSize) {
                                listener.onReadProgressUpdate(read, maxSize);
                            }
                        }, contentLength), request.out);
                    }

                    response.errorCode = WorksHttpResponse.ErrorCode.OK;
                }
            } else {
                response.statusCode = httpResponse.getStatusLine().getStatusCode();
                response.errorCode = WorksHttpResponse.ErrorCode.ERR_INVALID_HTTP_STATUS;
            }
        } catch (Exception e) {
            response.errorCode = WorksHttpResponse.ErrorCode.ERR_EXCEPTION;
            response.exception = e;
        } finally {
            client.close();
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

    private static UrlEncodedFormEntity createForm(List<BasicNameValuePair> params) {
        try {
            return new UrlEncodedFormEntity(params, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
}
