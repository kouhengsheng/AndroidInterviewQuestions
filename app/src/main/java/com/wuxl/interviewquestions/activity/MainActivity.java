package com.wuxl.interviewquestions.activity;

import android.content.Intent;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.wuxl.interviewquestions.AppConfig;
import com.wuxl.interviewquestions.R;
import com.wuxl.interviewquestions.utils.CacheUtils;
import com.wuxl.interviewquestions.utils.SendMsg;

import java.lang.reflect.Field;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.wuxl.interviewquestions.AppConfig.ANDROID_BASE_FLAG;
import static com.wuxl.interviewquestions.AppConfig.ANDROID_SENIOR_FLAG;
import static com.wuxl.interviewquestions.AppConfig.JAVA_FLAG;
import static com.wuxl.interviewquestions.AppConfig.JAVA_WEB_FLAG;

/**
 * 主界面
 * Created by wuxianglong on 2016/10/10.
 */
public class MainActivity extends AppCompatActivity {

    private String CACHE_DIR = null;

    @BindView(R.id.btn_java)
    LinearLayout btnJava;
    @BindView(R.id.btn_android_base)
    LinearLayout btnAndroidBase;
    @BindView(R.id.btn_android_senior)
    LinearLayout btnAndroidSenior;
    @BindView(R.id.btn_java_web)
    LinearLayout btnJavaWeb;
    @BindView(R.id.img_java)
    ImageView imgJava;
    @BindView(R.id.img_android_base)
    ImageView imgAndroidBase;
    @BindView(R.id.img_android_senior)
    ImageView imgAndroidSenior;
    @BindView(R.id.img_java_web)
    ImageView imgJavaWeb;

    private Intent intent = new Intent();

    private Drawable drawable;

    //是否显示JavaWeb
    private boolean showJavaWeb = true;

    private String isExists;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        makeActionOverflowMenuShown();
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setting();
    }

    /**
     * 初始化一些设置
     */
    private void setting() {
        //缓存目录
        CACHE_DIR = this.getCacheDir().getPath();
        //在文件不存在的情况下，返回的为null
        if (CacheUtils.load(CACHE_DIR + AppConfig.SETTING_DIR) == null) {
            //默认显示为true，进行保存
            CacheUtils.save(showJavaWeb, CACHE_DIR + AppConfig.SETTING_DIR);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        try {
            startAnimator(imgJava);
            startAnimator(imgAndroidBase);
            startAnimator(imgAndroidSenior);
            startAnimator(imgJavaWeb);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //显示或隐藏Java web
        setShowJavaWeb();
    }

    /**
     * 是否显示Java web
     */
    private void setShowJavaWeb() {
        showJavaWeb = (boolean) CacheUtils.load(CACHE_DIR + AppConfig.SETTING_DIR);
        if (!showJavaWeb)
            btnJavaWeb.setVisibility(View.GONE);
        else
            btnJavaWeb.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_about:
                SendMsg.sendMsgToZhuge(this, getResources().getString(R.string.action_about));
                startActivity(new Intent(MainActivity.this, AboutActivity.class));
                break;
            case R.id.action_setting:
                SendMsg.sendMsgToZhuge(this, getResources().getString(R.string.action_setting));
                startActivity(new Intent(MainActivity.this, SettingActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private long pre_press_time = 0;
    private static final long INTERVAL_TIME = 2000;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            long cur_time = System.currentTimeMillis();
            if (cur_time - pre_press_time > INTERVAL_TIME) {
                Snackbar.make(btnJava, getString(R.string.exit_app_hint), Snackbar.LENGTH_SHORT).show();
                pre_press_time = cur_time;
                return false;
            }
            // 退出app
            MainActivity.this.finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 有的手机不显示菜单栏
     */
    private void makeActionOverflowMenuShown() {
        //devices with hardware menu button (e.g. Samsung Note) don't show action overflow menu
        try {
            ViewConfiguration config = ViewConfiguration.get(this);
            Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
            if (menuKeyField != null) {
                menuKeyField.setAccessible(true);
                menuKeyField.setBoolean(config, false);
            }
        } catch (Exception e) {

        }
    }

    @OnClick({R.id.btn_java, R.id.btn_android_base, R.id.btn_android_senior, R.id.btn_java_web})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_java:
                toQuestionsActivity(JAVA_FLAG);
                break;
            case R.id.btn_android_base:
                toQuestionsActivity(ANDROID_BASE_FLAG);
                break;
            case R.id.btn_android_senior:
                toQuestionsActivity(ANDROID_SENIOR_FLAG);
                break;
            case R.id.btn_java_web:
                toQuestionsActivity(JAVA_WEB_FLAG);
                break;
        }
    }

    /**
     * 跳转到问题列表界面
     *
     * @param flag
     */
    private void toQuestionsActivity(String flag) {
        SendMsg.sendMsgToZhuge(this, flag);
        if (!TextUtils.isEmpty(flag)) {
            intent.putExtra("flag", flag);
            intent.setClass(MainActivity.this, QuestionListActivity.class);
            startActivity(intent);
        }
    }

    /**
     * 启动动画
     *
     * @param imageView
     */
    private void startAnimator(ImageView imageView) {
        drawable = imageView.getDrawable();
        if (drawable instanceof Animatable) {
            ((Animatable) drawable).start();
        }
    }
}
