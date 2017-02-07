package com.tools.opengl.demo1;

import android.app.ActivityManager;
import android.content.pm.ConfigurationInfo;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.tools.opengl.R;

public class Demo1Activity extends AppCompatActivity {

    private GLSurfaceView glSurfaceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo1);

        ActivityManager activityManager=(ActivityManager)getSystemService(ACTIVITY_SERVICE);
        ConfigurationInfo configurationInfo=activityManager.getDeviceConfigurationInfo();
        Log.i("OpenGL","configurationInfo.reqGlEsVersion:"+configurationInfo.reqGlEsVersion);
        boolean supportsEs2=configurationInfo.reqGlEsVersion>=0x2000;
        Log.i("OpenGL","supportsEs2:"+supportsEs2+" and 0x2000:"+0x2000);

        if (supportsEs2) {
            glSurfaceView = new GLSurfaceView(this);
            glSurfaceView.setRenderer(new GLRenderer());
            setContentView(glSurfaceView);
        } else {
            setContentView(R.layout.activity_demo1);
            Toast.makeText(this, "当前设备不支持OpenGL ES 2.0!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //防止我们在切换程序时，OpenGL还在绘制图形导致程序崩溃，因此我们还需要根据Activity的生命周期针对GLSurfaceView做一些处理
        if (glSurfaceView != null) {
            glSurfaceView.onResume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        //防止我们在切换程序时，OpenGL还在绘制图形导致程序崩溃，因此我们还需要根据Activity的生命周期针对GLSurfaceView做一些处理
        if (glSurfaceView != null) {
            glSurfaceView.onPause();
        }
    }
}
