package com.threadinteractivetest;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;

/**
 * Created by Administrator on 2017/4/20.
 */

public class TestAnsycTaskActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG = "TESTASYNCTASK";

    ProgressBar mProgressBar;

    Button mBtn;

    MyAnyscTask  myAnyscTask = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            //标题栏透明
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //导航栏透明
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }

        setContentView(R.layout.test_ansyctask_layout);

        try{
            initView();
            initListener();
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private void initView() {

        mProgressBar = (ProgressBar)findViewById(R.id.test_ansyctask_download_progressBar);

        mBtn = (Button)findViewById(R.id.test_ansyctask_download_btn);

    }

    private void initListener() {

        mBtn.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {

        myAnyscTask = new MyAnyscTask();
        //执行异步任务
        myAnyscTask.execute();

    }

    /**
     * 自定义异步任务
     */
    class MyAnyscTask extends AsyncTask<Void,Integer,Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.i(TAG,Thread.currentThread().getName());
        }

        @Override
        protected Void doInBackground(Void... params) {
            Log.i(TAG,Thread.currentThread().getName());
            //该方法运行在子线程，用于耗时操作
            for(int i = 0; i < 100; i ++){
                //判断当myAnyscTask.isCancelled()标记为取消时，退出耗时操作
                if(myAnyscTask.isCancelled()){
                    break;
                }
                publishProgress(i);
                try{
                    Thread.sleep(1000);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Log.i(TAG,Thread.currentThread().getName());
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            Log.i(TAG,Thread.currentThread().getName());
            if(myAnyscTask.isCancelled()){
                return;
            }
            mProgressBar.setProgress(values[0]);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(myAnyscTask != null && myAnyscTask.getStatus() == AsyncTask.Status.RUNNING){
            //Asynctask设置cancelled仅仅只是通知Asynctask设置一个取消标志，Asynctask并不能马上退出
            myAnyscTask.cancel(true);
        }
    }
}
