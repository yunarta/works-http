package com.mobilesolutionworks.android.http;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpUriRequest;

/**
 * Created by yunarta on 22/1/14.
 */
public interface WorksHttpOperationListener<Result> extends WorksHttpErrorHandler
{
    void onPreExecute(WorksHttpRequest request, HttpUriRequest httpRequest);

    boolean onValidateResponse(WorksHttpRequest request, HttpResponse httpResponse);

    boolean onHandleResponse(WorksHttpRequest request, HttpUriRequest httpRequest, WorksHttpResponse<Result> response, HttpResponse httpResponse);

    void onLoadFinished(WorksHttpRequest request, int statusCode, Result data);

    void onReadProgressUpdate(int read, int size);
}