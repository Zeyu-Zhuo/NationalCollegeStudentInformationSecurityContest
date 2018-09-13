package com.jiacyer.newpaydemo.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.jiacyer.newpaydemo.R;
import com.jiacyer.newpaydemo.bean.MsgContent;
import com.jiacyer.newpaydemo.control.ScrollingActivity;
import com.jiacyer.newpaydemo.handler.ScrollingHandler;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

/**
 *  Created by Jack on 2016/9/16.
 */

public class ToolbarAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private Context context;
    private ArrayList<MsgContent> list;

    public ToolbarAdapter(Context context, ArrayList<MsgContent> list){
        this.context=context;
        this.list = list;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType==0){
            return new ItemView(LayoutInflater.from(context).inflate(R.layout.shenghuo_head2,parent, false));
        }else{
            View view = LayoutInflater.from(context).inflate(R.layout.simple_alert_card, parent,false);
            return  new AlertCard(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof AlertCard) {
            AlertCard alertCard = (AlertCard) holder;
            final MsgContent msgContent = list.get(position);
            final String tip;

            if (msgContent.getMsg().equals("暂无异常消息！")) {
                alertCard.msg.setText(R.string.no_security_tip);
                alertCard.submitTip.setVisibility(View.GONE);
                tip = "";
            } else {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
                String date = dateFormat.format(msgContent.getPushDate());
                tip = "您于 " + date + " 在 " + msgContent.getLoc()
                        + " 出现异常支付！细节如下：" + msgContent.getMsg();
                alertCard.msg.setText(tip);
            }
            ((AlertCard) holder).cardLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (msgContent == null ||msgContent.isChecked())
                        return;

                    AlertDialog.Builder submitDialog = new AlertDialog.Builder(context);
                    submitDialog.setNegativeButton("是本人所为", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Handler handler = ScrollingHandler.getInstance(null);
                            Message message = Message.obtain();
                            message.what = ScrollingActivity.NORMAL_PAY;
                            message.obj = msgContent;
                            handler.sendMessage(message);
                            msgContent.setChecked(true);
                        }
                    });
                    submitDialog.setPositiveButton("非本人所为", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Handler handler = ScrollingHandler.getInstance(null);
                            Message message = Message.obtain();
                            message.what = ScrollingActivity.ABNORMAL_PAY;
                            message.obj = msgContent;
                            handler.sendMessage(message);
                            msgContent.setChecked(true);
                        }
                    });
                    submitDialog.setTitle(R.string.submit_title);
                    submitDialog.setMessage(tip);
                    submitDialog.create();

                    submitDialog.show();
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public int getItemViewType(int position) {
        if(position==0){
            return 0;
        }else{
            return 1;
        }
    }

    class AlertCard extends RecyclerView.ViewHolder{
        TextView msg;
        TextView submitTip;
        FrameLayout cardLayout;

        public AlertCard(View view) {
            super(view);
            msg = (TextView) view.findViewById(R.id.security_tip);
            submitTip = (TextView) view.findViewById(R.id.submit_tip);
            cardLayout = (FrameLayout) view.findViewById(R.id.cardLayout);
        }
    }

    class ItemView extends RecyclerView.ViewHolder{
        public ItemView(View itemView) {
            super(itemView);
        }
    }

}
