package com.mobilesolutionworks.android.http;

import android.text.TextUtils;

import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by yunarta on 22/1/14.
 */
public class WorksHttpRequest
{
    public String url;

    public Method method = Method.GET;

    public boolean returnTransfer = true;

    public OutputStream out;

    public WorksHttpPreExecutor preExecutor;

    public static enum Method
    {
        GET, POST
    }

    protected Map<String, String> httpParams;

    public Map<String, String> getHttpParams()
    {
        return httpParams;
    }

    public String getPostParam(String key)
    {
        if (httpParams == null)
        {
            return null;
        }

        return httpParams.get(key);
    }

    public void setPostParam(String key, String value)
    {
        if (httpParams == null)
        {
            httpParams = new HashMap<String, String>();
        }

        if (!TextUtils.isEmpty(value))
        {
            value = value.trim();
            httpParams.put(key, value);
        }
    }

    protected Map<String, Object> params;

    public <D> D getParameter(String key)
    {
        if (params == null)
        {
            return null;
        }

        return (D) params.get(key);
    }

    public void setParameter(String key, Object value)
    {
        if (params == null)
        {
            params = new HashMap<String, Object>();
        }

        params.put(key, value);
    }
}
