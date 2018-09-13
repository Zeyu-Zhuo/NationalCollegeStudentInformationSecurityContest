package com.jiacyer.newpaydemo.control;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Message;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.NavigationView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jiacyer.newpaydemo.R;
import com.jiacyer.newpaydemo.adapter.ToolbarAdapter;
import com.jiacyer.newpaydemo.bean.MsgContent;
import com.jiacyer.newpaydemo.handler.ScrollingHandler;
import com.jiacyer.newpaydemo.tools.ConfigUtil;
import com.jiacyer.newpaydemo.tools.HttpUtil;
import com.jiacyer.newpaydemo.tools.JsonUtil;
import com.unstoppable.submitbuttonview.SubmitButton;

import java.util.ArrayList;

public class ScrollingActivity extends AppCompatActivity implements View.OnClickListener {

    private AppBarLayout mAppBarLayout;
//    private CollapsingToolbarLayout collapsingToolbarLayout;
    private View mToolbar1;
    private View mToolbar2;

    private ImageView mZhangdan;
    private TextView mZhangdan_txt;
    private ImageView mTongxunlu;
    private ImageView mJiahao;

    private LinearLayout saoyisaoLayout;
    private LinearLayout fukuanLayout;
    private LinearLayout zhangdanLayout;

    private ImageView mZhangdan2;
//    private ImageView mShaoyishao;
    private ImageView mSearch;
    private ImageView mZhaoxiang;

    private RecyclerView myRecyclerView;

    private ImageView usrAvatarView;
    private TextView usrNameView;
    private TextView usrSloganView;
    private TextView usrIdView;
    private SubmitButton logoutButton;
//    private TextView logoutBtnText;

    public static final int LOGOUT = 1235;
    public static final int USR_INFO = 345678;
    public static final int PULL_MSG = 9432;
    public static final int NORMAL_PAY = 2345;
    public static final int ABNORMAL_PAY = 62345;

    private ScrollingHandler handler;
    private ConfigUtil configUtil;

    private String usrId;
    private String token;

    private static boolean THREAD_FLAG;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initHandler();
        initSystemInfo();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.destroy();
    }

    private void initHandler() {
        handler = ScrollingHandler.getInstance(this);
    }

    private void initSystemInfo() {
        configUtil = ConfigUtil.getInstance(this);
        String data = getIntent().getStringExtra(LoginActivity.LOGIN_INFO);
        JsonUtil.LoginMsg dataObj = JsonUtil.parseLoginMsg(data);
        usrId = dataObj.getUsr_id();
        token = dataObj.getToken();
        getUsrInfoThread();
        getAlertMsgInfoThread();
    }

    private void getUsrInfoThread() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String responseData = HttpUtil.getUsrInfo(usrId, token);
                JsonUtil.UsrInfo obj = JsonUtil.parseUsrInfo(responseData);
                Message message = Message.obtain();
                message.what = USR_INFO;
                message.obj = obj;
                handler.sendMessage(message);
            }
        }).start();
    }

    private void getAlertMsgInfoThread() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String reponseData = HttpUtil.getMsgFromServer(usrId, token);
                JsonUtil.MsgInfo obj = JsonUtil.parseMsgInfo(reponseData);
                Message message = Message.obtain();
                message.obj = obj;
                message.what = PULL_MSG;
                handler.sendMessage(message);
            }
        }).start();
    }

    private void initView() {
        Window window = getWindow();
        // 改变状态栏颜色
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(R.color.alipayBlue, null));

        myRecyclerView=(RecyclerView)findViewById(R.id.myRecyclerView);
        myRecyclerView.setLayoutManager(new LinearLayoutManager(this));
//        collapsingToolbarLayout=(CollapsingToolbarLayout)findViewById(R.id.toolbar_layout);
//        collapsingToolbarLayout.setStatusBarScrimColor(ContextCompat.getColor(this, R.color.color1984D1));

        ArrayList<MsgContent> list = new ArrayList<MsgContent>();
        MsgContent content = new MsgContent();
        content.setMsg("暂无异常消息！");
        list.add(content);
        myRecyclerView.setAdapter(new ToolbarAdapter(ScrollingActivity.this, list));

        mAppBarLayout=(AppBarLayout)findViewById(R.id.app_bar);
        mToolbar1=(View)findViewById(R.id.toolbar1);
        mToolbar2=(View)findViewById(R.id.toolbar2);

        mZhangdan=(ImageView)findViewById(R.id.img_zhangdan);
        mZhangdan_txt=(TextView)findViewById(R.id.img_zhangdan_txt);
        mTongxunlu=(ImageView)findViewById(R.id.tongxunlu);
        mJiahao=(ImageView)findViewById(R.id.jiahao);

        mZhangdan2=(ImageView)findViewById(R.id.img_zhangdan2);
        mZhangdan2.setOnClickListener(this);
//        mShaoyishao=(ImageView)findViewById(R.id.img_fukuang);
        mSearch=(ImageView)findViewById(R.id.img_search);
        mZhaoxiang=(ImageView)findViewById(R.id.img_zhaoxiang);

        saoyisaoLayout = (LinearLayout) findViewById(R.id.saoyisao_layout);
        saoyisaoLayout.setOnClickListener(this);
        fukuanLayout = (LinearLayout) findViewById(R.id.fukuan_layout);
        fukuanLayout.setOnClickListener(this);
        zhangdanLayout = (LinearLayout) findViewById(R.id.zhangdan_layout);
        zhangdanLayout.setOnClickListener(this);

        LinearLayout kajuanLayout = (LinearLayout) findViewById(R.id.kajuan_layout);
        kajuanLayout.setOnClickListener(this);
        LinearLayout xiuyixiuLayout = (LinearLayout) findViewById(R.id.xiuyixiu_layout);
        xiuyixiuLayout.setOnClickListener(this);

        mAppBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (verticalOffset == 0){
                    //张开
                    mToolbar1.setVisibility(View.VISIBLE);
                    mToolbar2.setVisibility(View.GONE);
                    setToolbar1Alpha(255);
                } else if (Math.abs(verticalOffset) >= appBarLayout.getTotalScrollRange()) {
                    //收缩
                    mToolbar1.setVisibility(View.GONE);
                    mToolbar2.setVisibility(View.VISIBLE);
                    setToolbar2Alpha(255);
                } else {
                    int alpha=255- Math.abs(verticalOffset);
                    if(alpha<0){
//                        Log.e("alpha",alpha+"");
                        //收缩toolbar
                        mToolbar1.setVisibility(View.GONE);
                        mToolbar2.setVisibility(View.VISIBLE);
                        setToolbar2Alpha(Math.abs(verticalOffset));
                    }else{
                        //张开toolbar
                        mToolbar1.setVisibility(View.VISIBLE);
                        mToolbar2.setVisibility(View.GONE);
                        setToolbar1Alpha(alpha);
                    }
                }
            }
        });
      // ******************************
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View headerLayout = navigationView.getHeaderView(0);

        usrAvatarView = (ImageView) headerLayout.findViewById(R.id.usr_avatar);
        usrNameView = (TextView) headerLayout.findViewById(R.id.usr_name);
        usrSloganView = (TextView) headerLayout.findViewById(R.id.usr_slogan);
        usrIdView = (TextView) headerLayout.findViewById(R.id.usr_id);
        ImageView aboutView = (ImageView) headerLayout.findViewById(R.id.aboutMe);
        logoutButton = (SubmitButton) headerLayout.findViewById(R.id.logoutButton);
//        logoutBtnText = (TextView) headerLayout.findViewById(R.id.logoutBtnText);

        logoutButton.setOnClickListener(this);
        aboutView.setOnClickListener(this);
        logoutButton.setOnResultEndListener(new SubmitButton.OnResultEndListener() {
            @Override
            public void onResultEnd() {
                Message message = new Message();
                message.what = LOGOUT;
                handler.sendMessage(message);
            }
        });

    }

    //设置展开时各控件的透明度
    public void setToolbar1Alpha(int alpha){
        mZhangdan.getDrawable().setAlpha(alpha);
        mZhangdan_txt.setTextColor(Color.argb(alpha,255,255,255));
        mTongxunlu.getDrawable().setAlpha(alpha);
        mJiahao.getDrawable().setAlpha(alpha);
    }

    //设置闭合时各控件的透明度
    public void setToolbar2Alpha(int alpha){
        mZhangdan2.getDrawable().setAlpha(alpha);
//        mShaoyishao.getDrawable().setAlpha(alpha);
        mSearch.getDrawable().setAlpha(alpha);
        mZhaoxiang.getDrawable().setAlpha(alpha);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fukuan_layout:
            case R.id.saoyisao_layout: {
                Intent intent = new Intent(ScrollingActivity.this, ScanActivity.class);
                intent.putExtra("usrId", usrId);
                startActivity(intent);
                break;
            }
            case R.id.aboutMe:
            {
                AlertDialog.Builder aboutView = new AlertDialog.Builder(ScrollingActivity.this);
                aboutView.setPositiveButton("关闭", null);
                aboutView.setTitle(R.string.aboutMeTitle);
                aboutView.setMessage(R.string.aboutMeMsg);
                aboutView.create();
                aboutView.show();
                break;
            }
            case R.id.logoutButton:
            {
//                logoutBtnText.setVisibility(View.GONE);
                logoutButton.doResult(true);
                configUtil.setAutoLogin(false);
                break;
            }
            case R.id.zhangdan_layout:
            case R.id.img_zhangdan2: {
//                System.out.println("账单被点击！");
                Intent intent = new Intent(ScrollingActivity.this, BillActivity.class);
                intent.putExtra("usrId", usrId);
                intent.putExtra("token", token);
                startActivity(intent);
                break;
            }

        }
    }

    /**
     * 结束所有线程，并退出
     */
    public void logout() {
        THREAD_FLAG = false;
        if (HttpUtil.getNetworkState(ScrollingActivity.this) == HttpUtil.NotWork) {
            showNetworkError();
        } else {
            logoutThread();
        }
        Intent intent = new Intent(ScrollingActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * 网络异常提示
     */
    public void showNetworkError() {
        Toast.makeText(ScrollingActivity.this, R.string.networkError, Toast.LENGTH_SHORT).show();
    }

    /**
     * 登出线程
     */
    private void logoutThread() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    HttpUtil.logout(usrId, token);
                } catch (Exception e) {e.printStackTrace();}
            }
        }).start();
    }

    public void updateUsrInfoUi(JsonUtil.UsrInfo obj) {
        usrNameView.setText(obj.getUsr_name());
        usrIdView.setText("账号：" + obj.getUsr_id());
    }

    public void updateAlertMsgUi(ArrayList<MsgContent> list) {
        myRecyclerView.setAdapter(new ToolbarAdapter(ScrollingActivity.this, list));
    }
}
