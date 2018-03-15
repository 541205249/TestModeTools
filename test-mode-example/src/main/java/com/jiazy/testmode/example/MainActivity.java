package com.jiazy.testmode.example;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.jiazy.testmode.annotation.CollectElapsedTime;

import java.util.Random;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.click1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                test();
            }
        });
    }

    private void test(){
        Thread t = new Thread(new Runnable() {
            @CollectElapsedTime(target = "start")
            @Override
            public void run() {
                int i = new Random().nextInt(5);
                try {
                    long start = System.currentTimeMillis();
                    Thread.sleep(1000*i);
                    Log.e("gaga","test run time :"+(System.currentTimeMillis()-start));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        t.start();
    }
}
