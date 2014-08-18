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
import android.os.AsyncTask;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.HttpUriRequest;

/**
 * AsyncTask implementation of works http operator.
 */
public abstract class WorksHttpAsyncTask<Result> extends AsyncTask<WorksHttpRequest, WorksHttpProgress, WorksHttpResponse<Result>> implements WorksHttpOperationListener<Result> {

    /**
     * Application context
     */
    Context mContext;

    /**
     * Progress monitor
     */
    WorksHttpProgress mProgress;

    /**
     * Error handler
     */
    WorksHttpErrorHandler mErrorHandler;

    /**
     * Create works http {@link android.os.AsyncTask} for specified context.
     *
     * @param context any android context
     */
    public WorksHttpAsyncTask(Context context) {
        mContext = context;
        mProgress = new WorksHttpProgress();
    }

    /**
     * Set the error handler if desired instead overriding error handling methods.
     *
     * @param errorHandler works http error handler
     */
    public void setErrorHandler(WorksHttpErrorHandler errorHandler) {
        mErrorHandler = errorHandler;
    }

    /**
     * Android context where works http executed.
     *
     * @return android context
     */
    protected Context getContext() {
        return mContext;
    }

    @Override
    protected WorksHttpResponse<Result> doInBackground(WorksHttpRequest... params) {
        return WorksHttpClient.executeOperation(mContext, params[0], this);
    }

    @Override
    public void onPreExecute(WorksHttpRequest request, HttpUriRequest httpRequest) {

    }

    /**
     * Response handler for manually process the http response data.
     * <p/>
     * Can be useful on big data or certain customized response format. You then need to set the finished response object into works http response.
     *
     * @param request      works http request
     * @param httpRequest  commons http client request
     * @param response     works http response
     * @param httpResponse commons http client response
     * @return
     */
    @Override
    public boolean onHandleResponse(WorksHttpRequest request, HttpUriRequest httpRequest, WorksHttpResponse<Result> response, HttpResponse httpResponse) {
        return false;
    }

    @Override
    protected final void onPostExecute(WorksHttpResponse<Result> response) {
        super.onPostExecute(response);

        try {
            if (isCancelled()) {
                response.markCancelled();
            }

            switch (response.errorCode) {
                case OK: {
                    try {
                        if (response.request.returnTransfer) {
                            onLoadFinished(response.request, response.statusCode, (Result) response.text);
                        } else {
                            onLoadFinished(response.request, response.statusCode, (Result) response.data);
                        }
                    } catch (Exception e) {
                        onProcessError(response.request, response.exception);
                    }
                    break;
                }

                case ERR_CANCELLED: {
                    onCancelled(response.request);
                    break;
                }

                case ERR_EXCEPTION: {
                    onProcessError(response.request, response.exception);
                    break;
                }

                case ERR_INVALID_HTTP_STATUS: {
                    onNetError(response.request, response.statusCode);
                    break;
                }
            }
        } finally {
            onFinalized();
        }
    }

    /**
     * Called after all processes is finished.
     */
    protected void onFinalized() {

    }

    /**
     * Validate whether the response is valid, usually validate by status code.
     *
     * @param request      works http request
     * @param httpResponse commons http client response
     * @return true if response is valid
     */
    @Override
    public boolean onValidateResponse(WorksHttpRequest request, HttpResponse httpResponse) {
        StatusLine statusLine = httpResponse.getStatusLine();
        return (statusLine.getStatusCode() >= 200) && (statusLine.getStatusCode() <= 404);
    }

    /**
     * Implementation of process update.
     *
     * @param read total read
     * @param size total size
     */
    @Override
    public void onReadProgressUpdate(int read, int size) {
        mProgress.read = read;
        mProgress.size = size;

        publishProgress(mProgress);
    }

    /**
     * Process error in operation.
     *
     * @param request   works http request
     * @param exception exception
     */
    @Override
    public void onProcessError(WorksHttpRequest request, Throwable exception) {
        if (mErrorHandler != null) {
            mErrorHandler.onProcessError(request, exception);
        }
    }

    /**
     * Process on net validation error.
     *
     * @param request    works http request
     * @param statusCode status code
     */
    @Override
    public void onNetError(WorksHttpRequest request, int statusCode) {
        if (mErrorHandler != null) {
            mErrorHandler.onNetError(request, statusCode);
        }
    }

    /**
     * Process on cancelled
     *
     * @param request works http request
     */
    @Override
    public void onCancelled(WorksHttpRequest request) {
        if (mErrorHandler != null) {
            mErrorHandler.onCancelled(request);
        }
    }
}
