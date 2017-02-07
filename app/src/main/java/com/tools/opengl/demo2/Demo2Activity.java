package com.tools.opengl.demo2;

import android.app.ActivityManager;
import android.content.pm.ConfigurationInfo;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.tools.opengl.R;

public class Demo2Activity extends AppCompatActivity {

    private GLSurfaceView glSurfaceView;
    private GLRenderer glRenderer;
    private float rotateDegreen = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo2);

        ActivityManager activityManager=(ActivityManager)getSystemService(ACTIVITY_SERVICE);
        ConfigurationInfo configurationInfo=activityManager.getDeviceConfigurationInfo();
        Log.i("OpenGL","configurationInfo.reqGlEsVersion:"+configurationInfo.reqGlEsVersion);
        boolean supportsEs2=configurationInfo.reqGlEsVersion>=0x2000;
        Log.i("OpenGL","supportsEs2:"+supportsEs2+" and 0x2000:"+0x2000);

        if (supportsEs2) {
            glSurfaceView = new GLSurfaceView(this);
            glRenderer = new GLRenderer(this);
            glSurfaceView.setRenderer(glRenderer);
            setContentView(glSurfaceView);
        } else {
            setContentView(R.layout.activity_demo1);
            Toast.makeText(this, "当前设备不支持OpenGL ES 2.0!", Toast.LENGTH_SHORT).show();
        }
    }

    public void rotate(float degree) {
        glRenderer.rotate(degree);
        glSurfaceView.invalidate();
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            rotate(rotateDegreen);
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        if (glSurfaceView != null) {
            glSurfaceView.onResume();

            //不断改变rotateDegreen值，实现旋转
            new Thread() {
                @Override
                public void run() {
                    while (true) {
                        try {
                            sleep(100);

                            rotateDegreen += 5;
                            handler.sendEmptyMessage(0x001);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }
            }.start();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (glSurfaceView != null) {
            glSurfaceView.onPause();
        }
    }
}
