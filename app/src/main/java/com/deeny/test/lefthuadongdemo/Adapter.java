package com.deeny.test.lefthuadongdemo;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by deeny on 2016/9/23.
 */
public class Adapter extends BaseAdapter implements View.OnClickListener {
    private List<String> list;
    private Context context;
    private ListView listView;

    public Adapter(List<String> list, Context context,ListView listView) {
        this.list = list;
        this.context = context;
        this.listView = listView;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        ViewHolder viewHolder = null;
        if(convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.item,null);
        }
        viewHolder = getHolder(convertView);
        viewHolder.customItem.setListView(listView);
        viewHolder.customItem.setTag(viewHolder.customItem.getId(),i);
        viewHolder.contentView.setText(list.get(i));
        viewHolder.tv_test.setText("这是测试的第"+i+"条数据");
        viewHolder.deleteView.setOnClickListener(this);
        viewHolder.deleteView.setTag(i);//把他所在的位置设置为tag
        return convertView;
    }

    private ViewHolder getHolder(View view){
        ViewHolder viewHolder = (ViewHolder) view.getTag();
        if(viewHolder == null){
            viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);
        }
        return viewHolder;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.tv_deleteView:
                String position = String.valueOf(view.getTag());//拿到位置
                Log.e("删除的id是：",position);
                //为了防止复用错乱问题，需要在移除之前先关掉当前打开的这个
                ((CustomItem)view.getParent()).quickClose();//当前点击的这个view的父容器就是自定义的这个item
                list.remove(Integer.parseInt(position));
                notifyDataSetChanged();
                break;
        }
    }

    class ViewHolder{
        TextView contentView;
        TextView deleteView;
        TextView tv_test;
        CustomItem customItem;

        public ViewHolder(View convertView){
            contentView = (TextView) convertView.findViewById(R.id.tv_contentView);
            deleteView = (TextView) convertView.findViewById(R.id.tv_deleteView);
            tv_test = (TextView) convertView.findViewById(R.id.tv_test);
            customItem = (CustomItem) convertView;
        }
    }
}
