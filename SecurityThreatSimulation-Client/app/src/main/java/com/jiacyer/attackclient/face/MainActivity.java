package com.jiacyer.attackclient.face;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.jiacyer.attackclient.R;
import com.jiacyer.attackclient.control.AttackActivity;
import com.jiacyer.attackclient.service.DetectService;
import com.jiacyer.attackclient.tools.ConfigUtil;
import com.jiacyer.attackclient.tools.MainContralTools;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends FragmentActivity {
    private ViewPager mViewPager;
    private FragmentPagerAdapter mAdapter;
    private List<Fragment> mDatas;
    private ImageView mTabLine;
    private int mCurrentPageIndex;

    private int mScreen1_3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MainContralTools.setMainActivity(this);

        initConfigView();
        initTabLine();
        initView();
    }

    private void initConfigView() {
        ImageView imageView = findViewById(R.id.config);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final EditText edit = new EditText(MainActivity.this);
                final ConfigUtil configUtil = ConfigUtil.getInstance(MainActivity.this);
                edit.setText(String.valueOf(configUtil.getDefaultServer()));
                edit.setOnKeyListener(new View.OnKeyListener() {
                    @Override
                    public boolean onKey(View v, int keyCode, KeyEvent event) {
                        if (KeyEvent.KEYCODE_ENTER == keyCode && event.getAction() == KeyEvent.ACTION_DOWN) {
                            return true;
                        }
                        return false;
                    }
                });

                AlertDialog.Builder aboutView = new AlertDialog.Builder(MainActivity.this, R.style.AlertDialogCustom);
                aboutView.setPositiveButton("保存", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        configUtil.setDefaultServer(String.valueOf(edit.getText()).trim());
                    }
                });
                aboutView.setNegativeButton("关闭", null);
                aboutView.setTitle("服务器IP");
                aboutView.setView(edit);
                aboutView.create();
                aboutView.show();
            }
        });
    }

    private void initTabLine() {
        mTabLine = (ImageView) findViewById(R.id.iv_tabLine);

        Display display = getWindow().getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);
        mScreen1_3 = outMetrics.widthPixels / 3;

        ViewGroup.LayoutParams layoutParams = mTabLine.getLayoutParams();
        layoutParams.width = mScreen1_3;
        mTabLine.setLayoutParams(layoutParams);
    }

    private void initView() {
        LinearLayout stopButton = findViewById(R.id.stopLayout);
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DetectService.setFlag(false);
                Intent intent = new Intent(MainActivity.this, DetectService.class);
                stopService(intent);
            }
        });


        mViewPager = (ViewPager) findViewById(R.id.viewPager);
        mDatas = new ArrayList<>();

        ChatMainTabFragment chatView = new ChatMainTabFragment();
        FindMainTabFragment findView = new FindMainTabFragment();
        ContactMainTabFragment contactView = new ContactMainTabFragment();

        mDatas.add(chatView);
        mDatas.add(findView);
        mDatas.add(contactView);

        mAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                mCurrentPageIndex = position;
                return mDatas.get(position);
            }

            @Override
            public int getCount() {
                return mDatas.size();
            }
        };
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                LinearLayout.LayoutParams lp = (android.widget.LinearLayout.LayoutParams) mTabLine.getLayoutParams();

                lp.leftMargin= (int) (mCurrentPageIndex*mScreen1_3+(positionOffset+position-mCurrentPageIndex)*mScreen1_3);
                mTabLine.setLayoutParams(lp);
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        mViewPager.setAdapter(mAdapter);
    }

    private void initService() {
//        handler = new MainHandler();
        Log.i("MainActivity", "initService:isOnlyOne---"+DetectService.isOnlyOne());
        if (DetectService.isOnlyOne()) {
            DetectService.setFlag(true);
            Intent intent = new Intent(this, DetectService.class);
            startService(intent);
            Log.i("MainActivity", "initService Succeed");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        initService();
        if (DetectService.isSucceed()) {
            Intent intent = new Intent(MainActivity.this, AttackActivity.class);
            startActivity(intent);
            finish();
        }
        Log.i("MainActivity", "Activity Resume！");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (DetectService.isSucceed()) {
//            Intent intent = new Intent(MainActivity.this, AttackActivity.class);
//            startActivity(intent);
            finish();
        }
        Log.i("MainActivity", "Activity Restart！");
    }

    @Override
    protected void onPause() {
        super.onPause();
//        if (cameraUtil != null)
//            cameraUtil.stopCamera();
//        cameraUtil = null;
//        DetectService.setFlag(true);

//        ConfigUtil configUtil = ConfigUtil.getInstance(this);
//        configUtil.setDefaultServer(String.valueOf(editText.getText()).trim());

        Log.i("MainActivity", "Activity Pause!");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        if (cameraUtil != null)
//            cameraUtil.stopCamera();
//        cameraUtil = null;
        MainContralTools.removeMainActivity();
        Log.i("MainActivity", "MainActivity stopped!");
    }

    public void showTip(String s) {
        Toast.makeText(MainActivity.this, s, Toast.LENGTH_SHORT).show();
    }

//    public TextureView getTextureView() {
//        return textureView;
//    }
}
