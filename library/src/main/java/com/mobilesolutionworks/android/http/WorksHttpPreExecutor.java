package com.mobilesolutionworks.android.http;

import org.apache.http.client.methods.HttpUriRequest;

/**
 * Created by mi on 30/3/14.
 */
public interface WorksHttpPreExecutor
{
    void onPreExecute(WorksHttpRequest request, HttpUriRequest httpRequest);
}
