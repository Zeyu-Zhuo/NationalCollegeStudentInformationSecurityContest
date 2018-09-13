package com.jiacyer.newpaydemo.control;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jiacyer.newpaydemo.R;
import com.jiacyer.newpaydemo.tools.ConfigUtil;
import com.jiacyer.newpaydemo.tools.Encrypt;
import com.jiacyer.newpaydemo.tools.HttpUtil;
import com.jiacyer.newpaydemo.tools.JsonUtil;
import com.jiacyer.newpaydemo.handler.LoginHandler;
import com.unstoppable.submitbuttonview.SubmitButton;

import java.util.Timer;
import java.util.TimerTask;

/**
 * 登陆界面
 * Created by Jiacy on 2017/11/16.
 */

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{
    /**
     * 控件
     */
    private SubmitButton mLoginButton;
//    private TextView mLoginBtnText;
    private EditText mAccountView;
    private EditText mPasswordView;

    private boolean isSucceed = false; // 是否登陆成功，用于登陆按钮的动画
    private boolean isFirstTryToLogin = true;
    private String responseData;
    private LoginHandler handler;
    private ConfigUtil configUtil;
    private Thread loginThread;

    public static final String LOGIN_INFO = "loginInfo"; // 传递给MainActivity的key
    /**
     * 静态值： LOGIN_SUCCEED - 登陆成功
     *          LOGIN_FAILED - 登陆失败
     *          REFRESH_UI - 刷新UI
     *          NETWORK_FALSE - 网络异常
     */
    public static final int LOGIN_SUCCEED = 1;
    public static final int LOGIN_FAILED = 2;
    public static final int REFRESH_UI = 3;
    public static final int NETWORK_FALSE = 4;

    private static final long time = 3000; // 3秒

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        configUtil = ConfigUtil.getInstance(this);
        initView();
        initHandler();
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkAutoLoginStatus();
    }

    @Override
    protected void onDestroy() {
        handler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }

    private void checkAutoLoginStatus() {
        if (configUtil.isAutoLogin() && isFirstTryToLogin) {
            loginThread(configUtil.getUsrId(), configUtil.getPassword());
            try {
                loginThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (isSucceed) {
                startMainActivity();
            }
        }
    }

    /**
     * 处理Message信息
     */
    private void initHandler() {
        handler = new LoginHandler(this);
    }

    public void showLoginSucceed() {
        System.out.println("LOGIN_SUCCEED");
        mLoginButton.doResult(isSucceed);
    }

    public void showLoginFailed() {
        Toast.makeText(LoginActivity.this, R.string.loginFailed, Toast.LENGTH_SHORT).show();
        mLoginButton.doResult(isSucceed);
    }

    public void refreshUI() {
        mLoginButton.reset();
//        mLoginBtnText.setVisibility(View.VISIBLE);
    }

    public void showNetworkFalse() {
        Toast.makeText(LoginActivity.this, R.string.networkFalse, Toast.LENGTH_SHORT).show();
        mLoginButton.doResult(isSucceed);
    }

    /**
     * 初始化界面
     */
    private void initView() {
        Window window = getWindow();
        // 改变状态栏颜色
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(R.color.alipayBlue, null));

        mLoginButton = (SubmitButton) findViewById(R.id.loginButton);
//        mLoginBtnText = (TextView) findViewById(R.id.loginBtnText);
        mAccountView = (EditText) findViewById(R.id.accountView);
        mPasswordView = (EditText) findViewById(R.id.passwordView);
        ImageView aboutMeButton = (ImageView) findViewById(R.id.aboutMe);

        if (configUtil.isAutoLogin()) {
            mAccountView.setText(configUtil.getUsrId());
            mPasswordView.setText(configUtil.getPassword());
        }

        aboutMeButton.setOnClickListener(this);
        mLoginButton.setOnClickListener(this);
        mPasswordView.setOnClickListener(this);
        mPasswordView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
                if (keyCode == KeyEvent.KEYCODE_ENTER && keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                    // 按下软键盘的回车，则隐藏软键盘
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
                    }
                    return true;
                }
                return false;
            }
        });
        mLoginButton.setOnResultEndListener(new SubmitButton.OnResultEndListener() {
            @Override
            public void onResultEnd() {
                System.out.println("Do result!");
                if (isSucceed) {
                    // 登陆成功，则Button显示√
                    startMainActivity();
                } else {
                    // 否则显示×，并在3秒后刷新Button
                    Timer timer = new Timer();
                    TimerTask timerTask = new TimerTask() {
                        @Override
                        public void run() {
                            Message message = new Message();
                            message.what = REFRESH_UI;
                            handler.sendMessage(message);
                        }
                    };
                    timer.schedule(timerTask, time);
                }
            }
        });
    }

    private void startMainActivity() {
        Intent intent = new Intent(LoginActivity.this, ScrollingActivity.class);
        intent.putExtra(LOGIN_INFO, responseData);
        startActivity(intent);
        finish();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.loginButton:{
                loginAction();
                break;
            }
            case R.id.aboutMe: {
                initConfigDialog();
                break;
            }
        }
    }

    private void initConfigDialog() {
        final EditText edit = new EditText(LoginActivity.this);
        final ConfigUtil configUtil = ConfigUtil.getInstance(LoginActivity.this);
        final CheckBox checkBox = new CheckBox(LoginActivity.this);
        TextView textView = new TextView(LoginActivity.this);
        textView.setText(R.string.configText);
        textView.setTextSize(20f);
        LinearLayout linearLayout = new LinearLayout(LoginActivity.this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        edit.setText(String.valueOf(configUtil.getDefaultServer()));
        checkBox.setChecked(configUtil.isAutoLogin());
        checkBox.setText(R.string.autoLogin);
        linearLayout.addView(textView);
        linearLayout.addView(edit);
        linearLayout.addView(checkBox);

        edit.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (KeyEvent.KEYCODE_ENTER == keyCode && event.getAction() == KeyEvent.ACTION_DOWN) {
                    return true;
                }
                return false;
            }
        });

        AlertDialog.Builder aboutView = new AlertDialog.Builder(LoginActivity.this);
        aboutView.setPositiveButton("保存并关闭", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                configUtil.setDefaultServer(String.valueOf(edit.getText()).trim());
                configUtil.setAutoLogin(checkBox.isChecked());
                System.out.println(checkBox.isChecked());
            }
        });
        aboutView.setTitle(R.string.aboutMeTitle);
        aboutView.setMessage(R.string.aboutMeMsg);
        aboutView.setView(linearLayout);
        aboutView.create();
        aboutView.show();
    }

    private void loginAction() {
//        mLoginBtnText.setVisibility(View.GONE);
        String mAccountString = mAccountView.getText().toString();
        String mPasswordString = Encrypt.md5(mPasswordView.getText().toString()).toLowerCase();
        mPasswordString = Encrypt.md5(mPasswordString).toLowerCase();
        System.out.println("account="+mAccountString+"\tpassword="+mPasswordString);
        if (mAccountString.equals("") || mPasswordString.equals("")) {
            Toast.makeText(LoginActivity.this, R.string.blankText, Toast.LENGTH_SHORT).show();
            Message message = new Message();
            message.what = REFRESH_UI;
            handler.sendMessage(message);
        } else {
            if (configUtil.isAutoLogin() && isFirstTryToLogin) {
                mPasswordString = configUtil.getPassword();
                isFirstTryToLogin = false;
            } else if (!mPasswordString.equals(configUtil.getPassword())) {
                configUtil.setUerInfo(mAccountString, mPasswordString);
            }
            System.out.println("account="+mAccountString+"\tpassword="+mPasswordString);
            loginThread(mAccountString, mPasswordString);
        }
    }

    /**
     * 登陆线程
     * 提交帐号和MD5加密的密码到服务器
     */
    private void loginThread(final String usrId, final String password) {
        if (loginThread != null)
            return;

        loginThread = new Thread(new Runnable() {
            @Override
            public void run() {
                Message message = new Message();
                if (HttpUtil.getNetworkState(LoginActivity.this) == HttpUtil.Work) {
                    // 有网络时
                    responseData = HttpUtil.login(usrId, password);
                    isSucceed = JsonUtil.isLoginSucceed(responseData);
//                  isSucceed = true; // 无服务器，临时属性
                    if (isSucceed)
                        message.what = LOGIN_SUCCEED;
                    else if(responseData == null)
                        message.what = NETWORK_FALSE;
                    else
                        message.what = LOGIN_FAILED;
                } else {
                    // 无网络时
                    message.what = NETWORK_FALSE;
                    isSucceed = false;
                }
                handler.sendMessage(message);
                loginThread = null;
            }
        });
        loginThread.start();
    }

}
