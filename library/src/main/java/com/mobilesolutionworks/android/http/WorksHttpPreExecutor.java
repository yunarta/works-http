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

import org.apache.http.client.methods.HttpUriRequest;

/**
 * Created by mi on 30/3/14.
 */
public interface WorksHttpPreExecutor {

    /**
     * Called before the request is executed.
     *
     * @param request     works http request
     * @param httpRequest commons http client request
     */
    void onPreExecute(WorksHttpRequest request, HttpUriRequest httpRequest);
}
