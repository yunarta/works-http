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
 * Works http request.
 */
public class WorksHttpRequest {

    /**
     * Request url
     */
    public String url;

    /**
     * Request method.
     */
    public Method method = Method.GET;

    /**
     * Return the result as a string.
     */
    public boolean returnTransfer = true;

    /**
     * Write the result into this output stream.
     */
    public OutputStream out;

    /**
     * Pre executor
     */
    public WorksHttpPreExecutor preExecutor;

    /**
     * Request method enumeration.
     */
    public static enum Method {
        GET, POST
    }

    /**
     * Http post parameters.
     */
    protected Map<String, String> httpParams;

    /**
     * Get all http post parameters.
     *
     * @return all http post parameters
     */
    public Map<String, String> getHttpParams() {
        return httpParams;
    }

    /**
     * Get post parameter for specified key.
     *
     * @param key parameter key
     * @return parameter value
     */
    public String getPostParam(String key) {
        if (httpParams == null) {
            return null;
        }

        return httpParams.get(key);
    }

    /**
     * Set post parameter for specified key.
     *
     * @param key   parameter key
     * @param value parameter value
     */

    public void setPostParam(String key, String value) {
        if (httpParams == null) {
            httpParams = new HashMap<String, String>();
        }

        if (!TextUtils.isEmpty(value)) {
            value = value.trim();
            httpParams.put(key, value);
        }
    }

    /**
     * Request parameter.
     */
    protected Map<String, Object> params;

    /**
     * Get request parameter for specified key.
     * <p/>
     * Parameter is used for storing operation data.
     *
     * @param key parameter key
     * @param <D> parameter type
     * @return parameter value
     */
    public <D> D getParameter(String key) {
        if (params == null) {
            return null;
        }

        return (D) params.get(key);
    }

    /**
     * Set request parameter for specified key.
     * <p/>
     * Parameter is used for storing operation data.
     *
     * @param key   parameter key
     * @param value parameter value
     */

    public void setParameter(String key, Object value) {
        if (params == null) {
            params = new HashMap<String, Object>();
        }

        params.put(key, value);
    }

    interface Abortable {
        void abort();
    }

    public Abortable abortable;

    public void abort() {
        if (abortable != null) {
            abortable.abort();
        }
    }
}
