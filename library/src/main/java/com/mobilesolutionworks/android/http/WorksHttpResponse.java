package com.mobilesolutionworks.android.http;

/**
 * Created by yunarta on 22/1/14.
 */
public class WorksHttpResponse<Data>
{
    public WorksHttpRequest mRequest;

    public Data mData;

    public String mText;

    public int mStatusCode;

    public ErrorCode mErrorCode;

    public Exception mException;

    public enum ErrorCode
    {
        OK,
        ERR_CANCELLED,
        ERR_EXCEPTION,
        ERR_INVALID_HTTP_STATUS,
        ERR_ERROR_IN_HANDLER

    }
}
