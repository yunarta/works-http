package com.mobilesolutionworks.android.http;

/**
 * Created by mi on 30/3/14.
 */
public interface WorksHttpErrorHandler
{
    void onProcessError(WorksHttpRequest request, Throwable exception);

    void onNetError(WorksHttpRequest request, int statusCode);

    void onCancelled(WorksHttpRequest request);
}
