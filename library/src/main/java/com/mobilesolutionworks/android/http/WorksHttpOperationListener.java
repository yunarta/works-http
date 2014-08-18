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
 * Created by yunarta on 22/1/14.
 */
public interface WorksHttpOperationListener<Result> extends WorksHttpErrorHandler {

    void onPreExecute(WorksHttpRequest request, HttpUriRequest httpRequest);

    boolean onValidateResponse(WorksHttpRequest request, HttpResponse httpResponse);

    boolean onHandleResponse(WorksHttpRequest request, HttpUriRequest httpRequest, WorksHttpResponse<Result> response, HttpResponse httpResponse);

    void onLoadFinished(WorksHttpRequest request, int statusCode, Result data);

    void onReadProgressUpdate(int read, int size);
}