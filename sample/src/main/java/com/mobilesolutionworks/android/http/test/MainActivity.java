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

package com.mobilesolutionworks.android.http.test;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import com.mobilesolutionworks.android.http.WorksHttpAsyncTask;
import com.mobilesolutionworks.android.http.WorksHttpFutureTask;
import com.mobilesolutionworks.android.http.WorksHttpRequest;

/**
 * Created by yunarta on 19/8/14.
 */
public class MainActivity extends Activity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btn1).setOnClickListener(this);
        findViewById(R.id.btn2).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        WorksHttpRequest request = new WorksHttpRequest();
        request.url = "http://www.google.com/robots.txt";

        TextView textView = (TextView) findViewById(R.id.text);
        textView.setText("");

        switch (v.getId()) {
            case R.id.btn1: {
                new WorksHttpAsyncTask<String>(this) {

                    @Override
                    public void onLoadFinished(WorksHttpRequest request, int statusCode, String data) {
                        TextView textView;

                        textView = (TextView) findViewById(R.id.from);
                        textView.setText("AsyncTask");

                        textView = (TextView) findViewById(R.id.text);
                        textView.setText(data);
                    }
                }.execute(request);
                break;
            }
            case R.id.btn2: {
                new WorksHttpFutureTask<String>(this) {

                    /**
                     * Called when the operation is finished and the data is ready.
                     *
                     * @param request    works http request
                     * @param statusCode status code
                     * @param data       result data
                     */
                    @Override
                    public void onLoadFinished(WorksHttpRequest request, int statusCode, String data) {
                        TextView textView;

                        textView = (TextView) findViewById(R.id.from);
                        textView.setText("FutureTask");

                        textView = (TextView) findViewById(R.id.text);
                        textView.setText(data);
                    }
                }.execute(request);
                break;
            }
        }
    }
}
