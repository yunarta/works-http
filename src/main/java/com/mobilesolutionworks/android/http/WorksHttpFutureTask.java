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
 * Created by yunarta on 22/1/14.
 */
public abstract class WorksHttpFutureTask<Result> implements WorksHttpOperationListener
{
    Context mContext;

    FutureTask<Result> mFuture;

    WorkerRunnable<WorksHttpRequest, Result> mWorker;

    WorksHttpProgress mProgress;

    public WorksHttpFutureTask(Context context)
    {
        mContext = context;
        mProgress = new WorksHttpProgress();
    }

    public void execute(WorksHttpRequest request)
    {
        execute(WorksHttpExecutor.THREAD_POOL_EXECUTOR, new Handler(), request);
    }

    public void execute(Executor exec, Handler handler, WorksHttpRequest request)
    {
        mWorker = new WorkerRunnable<WorksHttpRequest, Result>()
        {
            @Override
            public Result call() throws Exception
            {
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

    public void cancel()
    {
        mFuture.cancel(true);
    }

    protected Result postResult(Handler handler, final WorksHttpResponse<Result> response)
    {
        handler.post(new Runnable()
        {
            @Override
            public void run()
            {
                if (mFuture.isCancelled())
                {
                    response.mErrorCode = WorksHttpResponse.ErrorCode.ERR_CANCELLED;
                }

                switch (response.mErrorCode)
                {
                    case OK:
                    {
                        try
                        {
                            if (response.mRequest.returnTransfer)
                            {
                                onLoadFinished(response.mRequest, response.mStatusCode, (Result) response.mText);
                            }
                            else
                            {
                                onLoadFinished(response.mRequest, response.mStatusCode, (Result) response.mData);
                            }
                        }
                        catch (Exception e)
                        {
                            onProcessError(response.mRequest, response.mException);
                        }
                        break;
                    }

                    case ERR_CANCELLED:
                    {
                        onCancelled(response.mRequest);
                        break;
                    }

                    case ERR_EXCEPTION:
                    {
                        onProcessError(response.mRequest, response.mException);
                        break;
                    }

                    case ERR_INVALID_HTTP_STATUS:
                    {
                        onNetError(response.mRequest, response.mStatusCode);
                        break;
                    }
                }
            }
        });

        if (response.mRequest.returnTransfer)
        {
            return (Result) response.mText;
        }
        else
        {
            return response.mData;
        }

    }

    @Override
    public boolean onValidateResponse(WorksHttpRequest request, HttpResponse httpResponse)
    {
        StatusLine statusLine = httpResponse.getStatusLine();
        return (statusLine.getStatusCode() >= 200) && (statusLine.getStatusCode() < 400);
    }

    @Override
    public void onReadProgressUpdate(final int read, final int size)
    {

        mWorker.mHandler.post(new Runnable()
        {
            @Override
            public void run()
            {
                mProgress.read = read;
                mProgress.size = size;
                onReadProgressUpdate(mProgress);
            }
        });
    }

    protected abstract void onReadProgressUpdate(WorksHttpProgress progress);

    protected static abstract class WorkerRunnable<Params, Result> implements Callable<Result>
    {
        Params[] mParams;
        Handler  mHandler;
    }

    private class UncaughtExceptionHandler implements Thread.UncaughtExceptionHandler
    {
        private Handler mHandler;

        private WorksHttpRequest mRequest;

        public UncaughtExceptionHandler(Handler handler, WorksHttpRequest request)
        {
            mHandler = handler;
            mRequest = request;
        }

        @Override
        public void uncaughtException(Thread thread, final Throwable ex)
        {
            mHandler.post(new Runnable()
            {
                @Override
                public void run()
                {
                    onProcessError(mRequest, ex);
                }
            });
        }
    }
}
