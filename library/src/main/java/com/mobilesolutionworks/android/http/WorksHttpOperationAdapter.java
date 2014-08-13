package com.mobilesolutionworks.android.http;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpUriRequest;

/**
 * Created by yunarta on 13/8/14.
 */
public class WorksHttpOperationAdapter<Result> implements WorksHttpOperationListener<Result> {
    @Override
    public void onPreExecute(WorksHttpRequest request, HttpUriRequest httpRequest) {

    }

    @Override
    public boolean onValidateResponse(WorksHttpRequest request, HttpResponse httpResponse) {
        return true;
    }

    @Override
    public boolean onHandleResponse(WorksHttpRequest request, HttpUriRequest httpRequest, WorksHttpResponse<Result> response, HttpResponse httpResponse) {
        return false;
    }

    @Override
    public void onLoadFinished(WorksHttpRequest request, int statusCode, Result data) {

    }

    @Override
    public void onReadProgressUpdate(int read, int size) {

    }

    @Override
    public void onProcessError(WorksHttpRequest request, Throwable exception) {

    }

    @Override
    public void onNetError(WorksHttpRequest request, int statusCode) {

    }

    @Override
    public void onCancelled(WorksHttpRequest request) {

    }
}
