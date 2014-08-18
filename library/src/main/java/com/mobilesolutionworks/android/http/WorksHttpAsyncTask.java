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
 * Created by yunarta on 22/1/14.
 */
public abstract class WorksHttpAsyncTask<Result> extends AsyncTask<WorksHttpRequest, WorksHttpProgress, WorksHttpResponse<Result>> implements WorksHttpOperationListener<Result> {

    Context mContext;

    WorksHttpProgress mProgress;

    WorksHttpErrorHandler mErrorHandler;

    public WorksHttpAsyncTask(Context context) {
        mContext = context;
        mProgress = new WorksHttpProgress();
    }

    public void setErrorHandler(WorksHttpErrorHandler errorHandler) {
        mErrorHandler = errorHandler;
    }

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

    @Override
    public boolean onHandleResponse(WorksHttpRequest request, HttpUriRequest httpRequest, WorksHttpResponse<Result> response, HttpResponse httpResponse) {
        return false;
    }

    @Override
    protected final void onPostExecute(WorksHttpResponse<Result> response) {
        super.onPostExecute(response);
        if (isCancelled()) {
            response.errorCode = WorksHttpResponse.ErrorCode.ERR_CANCELLED;
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

        onFinalized();
    }

    protected void onFinalized() {

    }

    @Override
    public boolean onValidateResponse(WorksHttpRequest request, HttpResponse httpResponse) {
        StatusLine statusLine = httpResponse.getStatusLine();
        return (statusLine.getStatusCode() >= 200) && (statusLine.getStatusCode() <= 404);
    }

    @Override
    public void onReadProgressUpdate(int read, int size) {
        mProgress.read = read;
        mProgress.size = size;

        publishProgress(mProgress);
    }

    @Override
    public void onProcessError(WorksHttpRequest request, Throwable exception) {
        if (mErrorHandler != null) {
            mErrorHandler.onProcessError(request, exception);
        }
    }

    @Override
    public void onNetError(WorksHttpRequest request, int statusCode) {
        if (mErrorHandler != null) {
            mErrorHandler.onNetError(request, statusCode);
        }
    }

    @Override
    public void onCancelled(WorksHttpRequest request) {
        if (mErrorHandler != null) {
            mErrorHandler.onCancelled(request);
        }
    }
}
