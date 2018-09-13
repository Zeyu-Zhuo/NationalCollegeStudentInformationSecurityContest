package com.jiacyer.newpaydemo.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.jiacyer.newpaydemo.R;
import com.jiacyer.newpaydemo.bean.Bill;
import com.jiacyer.newpaydemo.bean.Item;
import com.jiacyer.newpaydemo.view.PullStickyListView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.TreeMap;


/**
 * 账单历史记录的适配器
 */
public class BillAdapter extends BaseAdapter implements PullStickyListView.PinnedSectionListAdapter {

    private Activity context;
   //服务器返回的历史账单列表
    private ArrayList<Bill> bills;
   //本地分组后的账单
    private TreeMap<String, ArrayList<Bill>> groupBills;
    //Adapter的数据源
    private ArrayList<Item> items;
    private SimpleDateFormat sdf;


    public BillAdapter(Activity activity, ArrayList<Bill> bills) {
        this.context = activity;
        this.bills = bills;
        items = new ArrayList<Item>();
        groupBills = new TreeMap<String, ArrayList<Bill>>();
        sdf = new SimpleDateFormat("yyyy-MM", Locale.getDefault());
    }

    public void setBillList(ArrayList<Bill> list) {
        bills = list;
    }

    /**
     * 初始化列表项
     */
    private void inflaterItems() {
        items.clear();
        groupBills.clear();
        for (Bill bill : bills) {//遍历bills将集合中的所有数据以月份进行分类
            String groupName = sdf.format(bill.getPayTime());
            if (groupBills.containsKey(groupName)) {//如果Map已经存在以该记录的日期为分组名的分组，则将该条记录插入到该分组中
                groupBills.get(groupName).add(bill);
            } else {//如果不存在，以该记录的日期作为分组名称创建分组，并将该记录插入到创建的分组中
                ArrayList<Bill> tempBills = new ArrayList<Bill>();
                tempBills.add(bill);
                groupBills.put(groupName, tempBills);
            }
        }

        Iterator<Entry<String, ArrayList<Bill>>> iterator = groupBills.entrySet().iterator();
        while (iterator.hasNext()) {//将分组后的数据添加到数据源的集合中
            Entry<String, ArrayList<Bill>> entry = iterator.next();
            items.add(new Item(Item.SECTION, entry.getKey()));//将分组添加到集合中
            for (Bill bill : entry.getValue()) {//将组中的数据添加到集合中
                items.add(new Item(Item.ITEM, bill));
            }
        }
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Item item = (Item) getItem(position);
        if (item.type == Item.SECTION) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_earn_detail_title, parent, false);
            TextView itemMonth = (TextView) convertView.findViewById(R.id.item_month);
            TextView itemFlow = (TextView) convertView.findViewById(R.id.item_price);
            itemMonth.setText(item.groupName);
        } else {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_earn_detail, parent, false);
            TextView tvTitle = (TextView) convertView.findViewById(R.id.item_earn_name);
            TextView tvDate = (TextView) convertView.findViewById(R.id.item_earn_time);
            TextView tvStatus = (TextView) convertView.findViewById(R.id.item_earn_price);
            tvTitle.setText(item.getPayContent()+"-"+item.getPayClass());
            tvStatus.setText("消费："+item.getPayAmount()+"元");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
            tvDate.setText(sdf.format(item.getPayTime()));
        }
        return convertView;
    }

    @Override
    public int getItemViewType(int position) {
        return items.get(position).type;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public boolean isItemViewTypePinned(int viewType) {
        return viewType == Item.SECTION;
    }

    @Override
    public void notifyDataSetChanged() {
        //重新初始化数据
        inflaterItems();
        super.notifyDataSetChanged();
    }
}
