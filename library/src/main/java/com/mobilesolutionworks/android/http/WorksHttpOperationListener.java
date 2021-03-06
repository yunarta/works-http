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
import org.apache.http.protocol.HttpContext;

/**
 * Works http operation listener.
 */
public interface WorksHttpOperationListener<Result> extends WorksHttpErrorHandler {

    /**
     * Called before the request is executed.
     *
     * @param request     works http request
     * @param httpRequest commons http client request
     */
    void onPreExecute(WorksHttpRequest request, HttpUriRequest httpRequest);

    /**
     * Validate whether the response is valid, usually validate by status code.
     *
     * @param request      works http request
     * @param httpResponse commons http client response
     * @return true if response is valid
     */
    boolean onValidateResponse(WorksHttpRequest request, HttpResponse httpResponse);

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
    boolean onHandleResponse(WorksHttpRequest request, HttpUriRequest httpRequest, WorksHttpResponse<Result> response, HttpResponse httpResponse);

    /**
     * Called when the operation is finished and the data is ready.
     *
     * @param request    works http request
     * @param statusCode status code
     * @param data       result data
     */
    void onLoadFinished(WorksHttpRequest request, int statusCode, Result data);

    /**
     * Implementation of process update.
     *
     * @param read total read
     * @param size total size
     */
    void onReadProgressUpdate(int read, int size);

    /**
     * Get HttpContext which required for cookie management.
     *
     * @return HttpContext, can be used for cookie management.
     */
    HttpContext getHttpContext();
}