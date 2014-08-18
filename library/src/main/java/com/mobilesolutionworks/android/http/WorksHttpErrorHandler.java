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

/**
 * Works http error handler listener.
 */
public interface WorksHttpErrorHandler {

    /**
     * Process error in operation.
     *
     * @param request   works http request
     * @param exception exception
     */
    void onProcessError(WorksHttpRequest request, Throwable exception);

    /**
     * Process on net validation error.
     *
     * @param request    works http request
     * @param statusCode status code
     */
    void onNetError(WorksHttpRequest request, int statusCode);

    /**
     * Process on cancelled
     *
     * @param request works http request
     */
    void onCancelled(WorksHttpRequest request);
}
