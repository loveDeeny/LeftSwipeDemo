package com.deeny.test.lefthuadongdemo;

import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;


import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements AbsListView.OnScrollListener, AdapterView.OnItemClickListener {

    private ListView lv;
    private List<String> list;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lv = (ListView) findViewById(R.id.lv);
        initList();
        lv.setAdapter(new Adapter(list,this,lv));
        lv.setOnScrollListener(this);//listview的滑动监听
        lv.setOnItemClickListener(this);
    }

    private void initList(){
        list = new ArrayList<>();
        for(int i=0;i<20;i++){
            list.add("hdofjosifsfp[so[fdfdgd"+i);
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView absListView, int i) {

    }

    /**
     *
     * @param absListView
     * @param i 第一条可见条目
     * @param i1 这一页可见条目的个数
     * @param i2 所有的条目的个数
     */
    @Override
    public void onScroll(AbsListView absListView, int i, int i1, int i2) {
        //listview滑动的时候将所有的item项关掉
        for (int i3 = 0; i3 < CustomItem.unClosedCustomItemList.size(); i3++) {
            CustomItem.unClosedCustomItemList.get(i3).quickClose();
        }
    }


    /**
     * item的点击事件，点击没反应，原因是事件被customItem中的onTouchEvent给消耗掉了
     * @param adapterView
     * @param view
     * @param i
     * @param l
     */
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

        //和toast一样，只不过这个可以加点击事件，点击的时候可以隐藏掉
        final Snackbar snackbar = Snackbar.make(view,"当前点击的是第"+i+"条",Snackbar.LENGTH_SHORT);
        snackbar.setAction("哈哈哈", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                snackbar.dismiss();//点击事件中关闭snackbar
            }
        });
        snackbar.show();

    }


}
