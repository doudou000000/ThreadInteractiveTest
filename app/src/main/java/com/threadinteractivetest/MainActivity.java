package com.threadinteractivetest;

import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.MainThread;
import android.support.annotation.UiThread;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * 线程间的交互方法
 */
public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;

    private Intent intent = null;

    private TextView mTextView;


    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            mTextView.setText("你好！我是HandlerSend方法");
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //透明导航栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        mTextView = (TextView)findViewById(R.id.result_tv);
        EventBus.getDefault().register(this);
    }

    public void testHandler(View view){
        startThread(4);
    }
    public void testRunOnUiThread(View view){
        startThread(1);
    }
    public void testPost(View view){
        startThread(2);
    }
    public void testPostDelay(View view){
        startThread(3);
    }


    /////////////////////////////以上四种更新UI的方法本质上都是使用handler来更新

    /**
     * 异步任务
     * @param view
     */
    public void testAnsycTask(View view){

        startActivity(new Intent(this,TestAnsycTaskActivity.class));
        startThread(5);
    }

    /**
     * 使用第三方的开源库来更新ui
     * @param view
     */
    public void testEventBus(View view){

        startThread(5);
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {

        mTextView.setText(event.getMessage());

    };



    public void startThread(final int tag){

        new Thread(new Runnable() {
            @Override
            public void run() {
                switch (tag){
                    case 1:
                        upadateTextViewForRunOnUiThread();
                        break;
                    case 2:
                        upadateTextViewForPost();
                        break;
                    case 3:
                        upadateTextViewForPostDelay();
                        break;
                    case 4:
                        upadateTextViewForHandler();
                        break;
                    case 5:
                        upadateTextViewForEventBus();
                        break;
                    default:
                        break;
                }
            }
        }).start();

    }

    /**
     * 使用runOnUiThread来更新UI
     */
    private void upadateTextViewForRunOnUiThread() {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mTextView.setText("你好！我是View的RunOnUiThread方法");
            }
        });

    }

    /**
     * 使用控件post来更新UI
     */
    private void upadateTextViewForPost() {

        mTextView.post(new Runnable() {
            @Override
            public void run() {
                mTextView.setText("你好！我是控件的post方法");
            }
        });
    }

    /**
     * 使用控件postDelay来更新UI
     */
    private void upadateTextViewForPostDelay() {

        mTextView.postDelayed(new Runnable() {
            @Override
            public void run() {
                mTextView.setText("你好！我是控件的postDelay方法");
            }
        },2000);
    }

    /**
     * 使用Handler来更新UI
     */
    private void upadateTextViewForHandler() {
//        mHandler.post(new Runnable() {
//            @Override
//            public void run() {
//                mTextView.setText("你好！我是HandlerPost方法");
//            }
//        });
        mHandler.sendEmptyMessage(0);
    }

    /**
     * 使用EventBus来更新UI
     */
    private void upadateTextViewForEventBus() {
        EventBus.getDefault().post(new MessageEvent("你好！我是EventBus方法"));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
