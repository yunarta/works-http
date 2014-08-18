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
import android.os.Handler;
import android.os.Process;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;

import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.FutureTask;

/**
 * Implementation that is using FutureTask for background operation.
 */
public abstract class WorksHttpFutureTask<Result> implements WorksHttpOperationListener {

    /**
     * Application context
     */
    Context mContext;

    /**
     * Future task.
     */
    FutureTask<Result> mFuture;

    /**
     * Runnable for the future task.
     */
    WorkerRunnable<WorksHttpRequest, Result> mWorker;

    /**
     *
     */
    WorksHttpProgress mProgress;

    /**
     * Create works http future task for specified context.
     *
     * @param context any android context
     */

    public WorksHttpFutureTask(Context context) {
        mContext = context;
        mProgress = new WorksHttpProgress();
    }

    /**
     * Execute specified works http request in parallel.
     *
     * @param request works http request
     */
    public void execute(WorksHttpRequest request) {
        execute(request, new Handler(), WorksHttpExecutor.THREAD_POOL_EXECUTOR);
    }

    /**
     * Execute specified works http request in parallel.
     *
     * @param request works http request
     * @param handler android handler
     */
    public void execute(WorksHttpRequest request, Handler handler) {
        execute(request, handler, WorksHttpExecutor.THREAD_POOL_EXECUTOR);
    }

    /**
     * Execute specified works http request in parallel.
     *
     * @param request works http request
     * @param handler android handler
     * @param exec    executor so it can be run in serial or other controlled manner
     */
    public void execute(WorksHttpRequest request, Handler handler, Executor exec) {
        mWorker = new WorkerRunnable<WorksHttpRequest, Result>() {
            @Override
            public Result call() throws Exception {
                Thread.currentThread().setUncaughtExceptionHandler(new UncaughtExceptionHandler(mHandler, mParams[0]));

                Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
                WorksHttpResponse<Result> response = WorksHttpClient.executeOperation(mContext, mParams[0], WorksHttpFutureTask.this);

                return postResult(mHandler, response);
            }
        };

        mFuture = new FutureTask<Result>(mWorker);

        mWorker.mHandler = handler;
        mWorker.mParams = new WorksHttpRequest[]{request};
        exec.execute(mFuture);
    }

    /**
     * Cancel the operation.
     */
    public void cancel() {
        mFuture.cancel(true);
    }

    protected Result postResult(Handler handler, final WorksHttpResponse<Result> response) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (mFuture.isCancelled()) {
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
            }
        });

        if (response.request.returnTransfer) {
            return (Result) response.text;
        } else {
            return response.data;
        }

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
        return (statusLine.getStatusCode() >= 200) && (statusLine.getStatusCode() < 400);
    }

    /**
     * Implementation of process update.
     *
     * @param read total read
     * @param size total size
     */
    @Override
    public void onReadProgressUpdate(final int read, final int size) {

        mWorker.mHandler.post(new Runnable() {
            @Override
            public void run() {
                mProgress.read = read;
                mProgress.size = size;
                onReadProgressUpdate(mProgress);
            }
        });
    }

    /**
     * Read progress update.
     *
     * @param progress works http progress
     */
    protected void onReadProgressUpdate(WorksHttpProgress progress) {

    }

    protected static abstract class WorkerRunnable<Params, Result> implements Callable<Result> {

        /**
         * Parameters.
         */
        Params[] mParams;

        /**
         * Handler.
         */
        Handler mHandler;
    }

    private class UncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {

        private Handler mHandler;

        private WorksHttpRequest mRequest;

        public UncaughtExceptionHandler(Handler handler, WorksHttpRequest request) {
            mHandler = handler;
            mRequest = request;
        }

        @Override
        public void uncaughtException(Thread thread, final Throwable ex) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    onProcessError(mRequest, ex);
                }
            });
        }
    }
}
