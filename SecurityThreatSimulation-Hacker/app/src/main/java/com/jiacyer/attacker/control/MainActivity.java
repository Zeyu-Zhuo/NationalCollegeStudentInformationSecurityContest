package com.jiacyer.attacker.control;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.jiacyer.attacker.R;
import com.jiacyer.attacker.tools.ConfigUtil;
import com.jiacyer.attacker.tools.HttpUtil;
import com.jiacyer.attacker.tools.JsonUtil;
import com.unstoppable.submitbuttonview.SubmitButton;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    //    private ImageView qrcodeImageView;
    private Bitmap qrcodeImage;
    private Spinner attackIdListSpinner;
    private Spinner qrcodeListSpinner;
    private EditText editText;
    private SubmitButton refreshButton;
    private TextView refreshText;

    private ArrayList<String> attackIdList;
    private ArrayList<String> qrcodeList;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
//                case setQRCodeImage: {
//                    qrcodeImageView.setImageBitmap(qrcodeImage);
//                    break;
//                }
                case setAttackAdapter: {
                    ArrayAdapter attackApt = new ArrayAdapter(MainActivity.this, R.layout.spinner_item, R.id.text, attackIdList);
                    attackIdListSpinner.setAdapter(attackApt);
                    refreshButton.doResult(true);
                    break;
                }
                case setQRCodeAdapter: {
                    ArrayAdapter qrcodeApt = new ArrayAdapter(MainActivity.this, R.layout.spinner_item, R.id.text, qrcodeList);
                    qrcodeListSpinner.setAdapter(qrcodeApt);
                    refreshButton.doResult(true);
//                    qrcodeListSpinner.setSelection(0, false);
                    break;
                }
                case TIMEOUT: {
//                    Toast.makeText(MainActivity.this, "网络访问超时！", Toast.LENGTH_SHORT).show();
                    refreshButton.doResult(false);
                    break;
                }
                case REFRESH_UI: {
                    refreshButton.reset();
                    refreshText.setVisibility(View.VISIBLE);
                    break;
                }
            }
        }
    };

//    private static final int setQRCodeImage = 1;
    private static final int setQRCodeAdapter = 2;
    private static final int setAttackAdapter = 3;
    private static final int TIMEOUT = 4;
    private static final int REFRESH_UI = 5;

    private Thread getAttackIdListThread = new Thread(new Runnable() {
        @Override
        public void run() {
            String data = HttpUtil.getGetAttackListFromServer();
            JsonUtil.AttackIdResult attackIdResult = JsonUtil.parseToAttackList(data);

            Message message = new Message();
            if (attackIdResult == null) {
                message.what = TIMEOUT;
            } else {
                attackIdList = attackIdResult.getAttackIdList();
                message.what = setAttackAdapter;
            }
            handler.sendMessage(message);
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final ConfigUtil configUtil = ConfigUtil.getInstance(this);

        final Button genereteBtn = findViewById(R.id.generate);
        final Button clearBtn = findViewById(R.id.clear);
        final EditText qrcodeEditText = findViewById(R.id.qrcode_input);
//        MaterialTextField tf = findViewById(R.id.qrcode_generate);
        Toolbar toolbar = findViewById(R.id.toolbar);
        editText = findViewById(R.id.server);
        attackIdListSpinner = findViewById(R.id.attack_id_list);
        qrcodeListSpinner = findViewById(R.id.qrcode_list);
        refreshButton = findViewById(R.id.loginButton);
        refreshText = findViewById(R.id.loginBtnText);

        setSupportActionBar(toolbar);
        editText.setText(configUtil.getDefaultServer());
        editText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
                if (keyCode == KeyEvent.KEYCODE_ENTER && keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                    return true;
                }
                return false;
            }
        });


        attackIdListSpinner.setOnItemSelectedListener(this);
        qrcodeListSpinner.setOnItemSelectedListener(this);
        attackIdListSpinner.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                editText.clearFocus();
                qrcodeEditText.clearFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
                }
                return false;
            }
        });
        qrcodeListSpinner.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                editText.clearFocus();
                qrcodeEditText.clearFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
                }

                try {
                    Class<?> clazz = AdapterView.class;
                    //Field field = clazz.getDeclaredField("mOldSelectedPosition");
                    //field.setAccessible(true);
                    //field.setInt(spn,-1);
                    Field field = clazz.getDeclaredField("mOldSelectedRowId");
                    field.setAccessible(true);
                    field.setInt(qrcodeListSpinner, Integer.MIN_VALUE);
                } catch(Exception e){
                    e.printStackTrace();
                }
                return false;
            }
        });

        genereteBtn.setVisibility(View.GONE);
        clearBtn.setVisibility(View.GONE);
        qrcodeEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                System.out.println(b);
                if (b) {
                    genereteBtn.setVisibility(View.VISIBLE);
                    clearBtn.setVisibility(View.VISIBLE);
                } else {
                    genereteBtn.setVisibility(View.GONE);
                    clearBtn.setVisibility(View.GONE);
                }
            }
        });
        genereteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startAlipayActivity(String.valueOf(qrcodeEditText.getText()).trim());
            }
        });
        clearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                qrcodeEditText.setText("");
            }
        });

        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                refreshText.setVisibility(View.GONE);
                configUtil.setDefaultServer(String.valueOf(editText.getText()).trim());
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
                }
                editText.clearFocus();
                if (!getAttackIdListThread.isAlive()) {
                    getAttackIdListThread.start();
                }
            }
        });
        refreshButton.setOnResultEndListener(new SubmitButton.OnResultEndListener() {
            @Override
            public void onResultEnd() {
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
                timer.schedule(timerTask, 2000);
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        ConfigUtil configUtil = ConfigUtil.getInstance(this);
        configUtil.setDefaultServer(String.valueOf(editText.getText()).trim());
    }

    @Override
    public void onItemSelected(final AdapterView<?> parent, View view, final int position, long l) {
        switch (parent.getId()) {
            case R.id.attack_id_list: {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String data = HttpUtil.getQRCodeFromServer(String.valueOf(parent.getItemAtPosition(position)));
                        JsonUtil.QRCodeResult qrCodeResult = JsonUtil.parseToQRCode(data);

                        Message message = new Message();
                        if (qrCodeResult == null) {
                            message.what = TIMEOUT;
                        } else {
                            qrcodeList = qrCodeResult.getQrcodeList();
                            message.what = setQRCodeAdapter;
                        }
                        handler.sendMessage(message);
                    }
                }).start();
                break;
            }
            case R.id.qrcode_list: {
//                int windowSize = qrcodeImageView.getWidth()<qrcodeImageView.getHeight()?qrcodeImageView.getWidth():qrcodeImageView.getHeight();
//                qrcodeImage = QRCodeEncoder.syncEncodeQRCode(String.valueOf(parent.getItemAtPosition(position)), windowSize);
//                qrcodeImageView.setImageBitmap(qrcodeImage);
                Log.i("MainActivity", qrcodeList.get(position));
                startAlipayActivity(qrcodeList.get(position));
                break;
            }
        }
    }

    private void startAlipayActivity(String qrcode) {
        if (qrcode == null || qrcode.equals("") || qrcode.isEmpty())
            return;

        Intent intent = new Intent(this, AlipayActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra("qrcode", qrcode);
        startActivity(intent);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
