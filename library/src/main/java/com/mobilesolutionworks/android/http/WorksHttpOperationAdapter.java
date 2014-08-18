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
