package com.jiacyer.newpaydemo.control;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.jiacyer.newpaydemo.R;
import com.jiacyer.newpaydemo.adapter.BillAdapter;
import com.jiacyer.newpaydemo.bean.Bill;
import com.jiacyer.newpaydemo.handler.BillHandler;
import com.jiacyer.newpaydemo.tools.HttpUtil;
import com.jiacyer.newpaydemo.tools.JsonUtil;
import com.jiacyer.newpaydemo.view.PullFreshListView;
import com.jiacyer.newpaydemo.view.PullStickyListView;

import java.util.ArrayList;

/**
 * 方支付宝账单页分组+上拉加载下拉刷新效果
 */
public class BillActivity extends Activity implements PullFreshListView.ListViewPlusListener {

    private PullStickyListView listView;
    private BillAdapter adapter;
    private ArrayList<Bill> bills;
    private int page = 1;

    private Thread billThread;

    private String usrId;
    private String token;

    private BillHandler handler;

    public static final int GET_BILLS = 1;
    public static final int LOGOUT = 2;
    public static final int UPDATE_BILLS_UI = 3;
    public static final int STOP_LOAD_MORE = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill);
        Intent intent = this.getIntent();
        usrId = intent.getStringExtra("usrId");
        token = intent.getStringExtra("token");
        initView();
        initHandler();
        loadBillData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        listView = null;
        adapter = null;
        bills.clear();
        bills = null;
        handler.destroy();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
//        System.out.println("结束！");
        finish();
    }

    private void initHandler() {
        handler = BillHandler.getInstance(this);
    }

    private void initView() {
        Window window = getWindow();
        // 改变状态栏颜色
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(R.color.alipayBlue, null));

        listView = (PullStickyListView) findViewById(R.id.list);
        bills = new ArrayList<Bill>();
        listView.setLoadEnable(true);
        listView.setListViewPlusListener(this);
        adapter = new BillAdapter(this, bills);
        listView.setAdapter(adapter);
        ImageView backImageView = (ImageView) findViewById(R.id.back_button);
        backImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    public void loadData(final ArrayList<Bill> list) {
//        System.out.println("加载数据！");
        sendStopLoadMoreBills();
        if (list.size() != 0) {
//            System.out.println("设置适配器！");
            page++;
            bills.addAll(list);
            adapter.setBillList(bills);
            Message msg1 = Message.obtain();
            msg1.what = UPDATE_BILLS_UI;
            handler.sendMessage(msg1);
        }
    }

    private void sendStopLoadMoreBills() {
        Message msg = Message.obtain();
        msg.what = STOP_LOAD_MORE;
        handler.sendMessage(msg);
    }

    @Override
    public void onLoadMore() {
        boolean f = loadBillData();
        if (!f) {
            sendStopLoadMoreBills();
        }
    }

    private boolean loadBillData() {
        if (billThread != null) {
            return false;
        }

        billThread = new Thread(new Runnable() {
            @Override
            public void run() {
                String data = HttpUtil.getBillList(usrId, token, page);
                JsonUtil.BillInfo billInfo = JsonUtil.parseBillInfo(data);
                if (billInfo.getCode() == 200) {
                    Message msg = Message.obtain();
                    msg.what = GET_BILLS;
                    msg.obj = billInfo.getContent();
                    handler.handleMessage(msg);
                } else if (billInfo.getCode() == 404) {
                    Message msg = Message.obtain();
                    msg.what = LOGOUT;
                    handler.sendMessage(msg);
                }
                billThread = null;
            }
        });
        billThread.start();
        return true;
    }

    /**
     * 结束所有线程，并退出
     */
    public void logout() {
        if (HttpUtil.getNetworkState(BillActivity.this) == HttpUtil.NotWork) {
            showNetworkError();
        } else {
            logoutThread();
        }
        Intent intent = new Intent(BillActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * 网络异常提示
     */
    public void showNetworkError() {
        Toast.makeText(BillActivity.this, R.string.networkError, Toast.LENGTH_SHORT).show();
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

    public void updateBillsUi() {
        adapter.notifyDataSetChanged();
//        System.out.println("更新UI！");
    }

    public void stopLoadMoreBills() {
        listView.stopLoadMore();
    }
}
