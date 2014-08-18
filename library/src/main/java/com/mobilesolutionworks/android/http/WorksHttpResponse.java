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
 * Created by yunarta on 22/1/14.
 */
public class WorksHttpResponse<Data> {

    public WorksHttpRequest request;

    public Data data;

    public String text;

    public int statusCode;

    public ErrorCode errorCode;

    public Exception exception;

    public void markErrorInHandler(Exception e) {
        errorCode = WorksHttpResponse.ErrorCode.ERR_ERROR_IN_HANDLER;
        exception = e;
    }

    public void markErrorInExecution(Exception e) {
        errorCode = WorksHttpResponse.ErrorCode.ERR_EXCEPTION;
        exception = e;
    }

    public void markInvalidHttpStatus(int status) {
        statusCode = status;
        errorCode = WorksHttpResponse.ErrorCode.ERR_INVALID_HTTP_STATUS;
    }

    public void markSuccess() {
        errorCode = WorksHttpResponse.ErrorCode.OK;
    }

    public void markCancelled() {
        errorCode = WorksHttpResponse.ErrorCode.ERR_CANCELLED;
    }

    public enum ErrorCode {
        OK,
        ERR_CANCELLED,
        ERR_EXCEPTION,
        ERR_INVALID_HTTP_STATUS,
        ERR_ERROR_IN_HANDLER
    }
}
