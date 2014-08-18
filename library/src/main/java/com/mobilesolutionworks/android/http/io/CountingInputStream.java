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

package com.mobilesolutionworks.android.http.io;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by yunarta on 23/1/14.
 */
public class CountingInputStream extends InputStream {

    public static interface OnInputListener {

        void onInputRead(int read, int size);
    }

    InputStream mIn;

    OnInputListener mInputListener;

    int mCurrent;

    int mMaxSize;

    public CountingInputStream(InputStream in, OnInputListener inputListener, int maxSize) {
        mIn = in;
        mInputListener = inputListener;
        mMaxSize = maxSize;
    }

    @Override
    public int read(byte[] buffer) throws IOException {
        int read = mIn.read(buffer);
        if (read != -1) {
            mCurrent += read;
            mInputListener.onInputRead(mCurrent, mMaxSize);
        }

        return read;
    }

    @Override
    public int read(byte[] buffer, int byteOffset, int byteCount) throws IOException {
        int read = mIn.read(buffer, byteOffset, byteCount);

        if (read != -1) {
            mCurrent += read;
            mInputListener.onInputRead(mCurrent, mMaxSize);
        }

        return read;
    }

    @Override
    public int read() throws IOException {
        int read = mIn.read();

        if (read != -1) {
            mCurrent += 1;
            mInputListener.onInputRead(mCurrent, mMaxSize);
        }

        return read;
    }
}
