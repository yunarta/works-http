package com.mobilesolutionworks.android.http.io;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by yunarta on 23/1/14.
 */
public class CountingInputStream extends InputStream
{
    public static interface OnInputListener
    {
        void onInputRead(int read, int size);
    }

    InputStream mIn;

    OnInputListener mInputListener;

    int mCurrent;

    int mMaxSize;

    public CountingInputStream(InputStream in, OnInputListener inputListener, int maxSize)
    {
        mIn = in;
        mInputListener = inputListener;
        mMaxSize = maxSize;
    }

    @Override
    public int read(byte[] buffer) throws IOException
    {
        int read = mIn.read(buffer);
        if (read != -1)
        {
            mCurrent += read;
            mInputListener.onInputRead(mCurrent, mMaxSize);
        }

        return read;
    }

    @Override
    public int read(byte[] buffer, int byteOffset, int byteCount) throws IOException
    {
        int read = mIn.read(buffer, byteOffset, byteCount);

        if (read != -1)
        {
            mCurrent += read;
            mInputListener.onInputRead(mCurrent, mMaxSize);
        }

        return read;
    }

    @Override
    public int read() throws IOException
    {
        int read = mIn.read();

        if (read != -1)
        {
            mCurrent += 1;
            mInputListener.onInputRead(mCurrent, mMaxSize);
        }
        
        return read;
    }
}
