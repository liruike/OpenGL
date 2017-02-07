package com.tools.opengl.demo2;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;

import java.io.IOException;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by liruike on 2017/2/7.
 */

public class GLRenderer implements GLSurfaceView.Renderer {

    private Model model;
    private Point mCenterPoint;
    private Point eye = new Point(0, 0, -3);
    private Point up = new Point(0, 1, 0);
    private Point center = new Point(0, 0, 0);
    private float mScalef = 1;
    private float mDegree = 0;

    public GLRenderer(Context context) {
        try {
            model = new STLReader().parserBinStlInAssets(context, "huba.stl");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void rotate(float degree) {
        mDegree = degree;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        gl.glEnable(GL10.GL_DEPTH_TEST); // 启用深度缓存
        gl.glClearDepthf(1.0f); // 设置深度缓存值
        gl.glDepthFunc(GL10.GL_LEQUAL); // 设置深度缓存比较函数
        gl.glShadeModel(GL10.GL_SMOOTH);// 设置阴影模式GL_SMOOTH
        float r = model.getR();
        //r是半径，不是直径，因此用0.5/r可以算出放缩比例
        mScalef = 0.5f / r;
        mCenterPoint = model.getCentrePoint();

        //打开光源
        openLight(gl);
        //设置材料属性
        enableMaterial(gl);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {

        // 设置OpenGL场景的大小,(0,0)表示窗口内部视口的左下角，(width, height)指定了视口的大小
        gl.glViewport(0, 0, width, height);

        gl.glMatrixMode(GL10.GL_PROJECTION); // 设置投影矩阵
        gl.glLoadIdentity(); // 设置矩阵为单位矩阵，相当于重置矩阵
        GLU.gluPerspective(gl, 45.0f, ((float) width) / height, 1f, 100f);// 设置透视范围

        //以下两句声明，以后所有的变换都是针对模型(即我们绘制的图形)
        gl.glMatrixMode(GL10.GL_MODELVIEW);
        gl.glLoadIdentity();
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        // 清除屏幕和深度缓存
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

        gl.glLoadIdentity();// 重置当前的模型观察矩阵

        //眼睛对着原点看
        GLU.gluLookAt(gl, eye.x, eye.y, eye.z, center.x,
                center.y, center.z, up.x, up.y, up.z);

        //为了能有立体感觉，通过改变mDegree值，让模型不断旋转
        gl.glRotatef(mDegree, 0, 1, 0);

        //将模型放缩到View刚好装下
        gl.glScalef(mScalef, mScalef, mScalef);
        //把模型移动到原点
        gl.glTranslatef(-mCenterPoint.x, -mCenterPoint.y,
                -mCenterPoint.z);


        //===================begin==============================//

        //允许给每个顶点设置法向量
        gl.glEnableClientState(GL10.GL_NORMAL_ARRAY);
        // 允许设置顶点
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        // 允许设置颜色

        //设置法向量数据源
        gl.glNormalPointer(GL10.GL_FLOAT, 0, model.getVnormBuffer());
        // 设置三角形顶点数据源
        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, model.getVertBuffer());

        // 绘制三角形
        gl.glDrawArrays(GL10.GL_TRIANGLES, 0, model.getFacetCount() * 3);

        // 取消顶点设置
        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
        //取消法向量设置
        gl.glDisableClientState(GL10.GL_NORMAL_ARRAY);

        //=====================end============================//

    }


    //添加光源
    float[] ambient = {0.9f, 0.9f, 0.9f, 1.0f,};
    float[] diffuse = {0.5f, 0.5f, 0.5f, 1.0f,};
    float[] specular = {1.0f, 1.0f, 1.0f, 1.0f,};
    float[] lightPosition = {0.5f, 0.5f, 0.5f, 0.0f,};

    public void openLight(GL10 gl) {
        gl.glEnable(GL10.GL_LIGHTING);
        gl.glEnable(GL10.GL_LIGHT0);
        gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_AMBIENT, Util.floatToBuffer(ambient));
        gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_DIFFUSE, Util.floatToBuffer(diffuse));
        gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_SPECULAR, Util.floatToBuffer(specular));
        gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_POSITION, Util.floatToBuffer(lightPosition));
    }

    //设置材料属性，同样是一束光，照在不同颜色材料的物体上面，我们所看到的是不同的，反射出来的不仅仅颜色不同，光泽也是不同的。换句话说，不同的材质对最终的渲染效果影响很大！
    float[] materialAmb = {0.4f, 0.4f, 1.0f, 1.0f};
    float[] materialDiff = {0.0f, 0.0f, 1.0f, 1.0f};//漫反射设置蓝色
    float[] materialSpec = {1.0f, 0.5f, 0.0f, 1.0f};

    public void enableMaterial(GL10 gl) {
        //材料对环境光的反射情况
        gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_AMBIENT, Util.floatToBuffer(materialAmb));
        //散射光的反射情况
        gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_DIFFUSE, Util.floatToBuffer(materialDiff));
        //镜面光的反射情况
        gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_SPECULAR, Util.floatToBuffer(materialSpec));

    }
}
