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

import android.text.TextUtils;

import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by yunarta on 22/1/14.
 */
public class WorksHttpRequest {

    public String url;

    public Method method = Method.GET;

    public boolean returnTransfer = true;

    public OutputStream out;

    public WorksHttpPreExecutor preExecutor;

    public static enum Method {
        GET, POST
    }

    protected Map<String, String> httpParams;

    public Map<String, String> getHttpParams() {
        return httpParams;
    }

    public String getPostParam(String key) {
        if (httpParams == null) {
            return null;
        }

        return httpParams.get(key);
    }

    public void setPostParam(String key, String value) {
        if (httpParams == null) {
            httpParams = new HashMap<String, String>();
        }

        if (!TextUtils.isEmpty(value)) {
            value = value.trim();
            httpParams.put(key, value);
        }
    }

    protected Map<String, Object> params;

    public <D> D getParameter(String key) {
        if (params == null) {
            return null;
        }

        return (D) params.get(key);
    }

    public void setParameter(String key, Object value) {
        if (params == null) {
            params = new HashMap<String, Object>();
        }

        params.put(key, value);
    }
}
