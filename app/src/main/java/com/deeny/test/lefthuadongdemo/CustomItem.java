package com.deeny.test.lefthuadongdemo;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by deeny on 2016/9/19.
 *
 */
public class CustomItem extends FrameLayout {
    /**用于标记当前item的状态*/
    private SwipeState swipeState = SwipeState.Close;//默认是关闭的
    private ListView listView;//他所依赖的listview
    private View contentView,deleteView;
    private int contentWidth,contentHeight,deleteWidth;
    private ViewDragHelper viewDragHelper = ViewDragHelper.create(this,new myCallBack());
    public CustomItem(Context context) {
        super(context);
    }

    public CustomItem(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomItem(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     *
     * @param listView
     */
    public void setListView(ListView listView) {
        this.listView = listView;
    }

    /**
     * 这个方法就是布局加载完成的回掉，只知道内部有什么子控件，但是在这个时候控件的宽高还没有计算出来
     */
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        contentView = getChildAt(0);
        deleteView = getChildAt(1);
    }

    /**
     * 执行到这个方法的时候就知道控件的宽高了
     * @param w
     * @param h
     * @param oldw
     * @param oldh
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        contentWidth = contentView.getMeasuredWidth();
        contentHeight = contentView.getMeasuredHeight();
        deleteWidth = deleteView.getMeasuredWidth();
    }

    /**
     * 这个方法是对控件的位置进行重新的摆放
     * @param changed
     * @param left
     * @param top
     * @param right
     * @param bottom
     */
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        quickClose();
    }

    @Override
    public boolean onInterceptHoverEvent(MotionEvent event) {
        return viewDragHelper.shouldInterceptTouchEvent(event);//交给helper来处理拦截事件
    }

    private int lastx,lasty;//记录滑动时候的xy
    private int clickX,clickY;//记录点击时候的xy
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //获取触摸点的x和y的值
        int x = (int) event.getX();
        int y = (int) event.getY();
        //Log.e("")
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                lastx = clickX = (int) event.getX();
                lasty = clickY = (int) event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                if(Math.abs(x-lastx) > Math.abs(y-lasty)){//如果左右滑动的距离大于上下滑动的距离，那么事件交给自己处理
                    getParent().requestDisallowInterceptTouchEvent(true);
                }else{
                    getParent().requestDisallowInterceptTouchEvent(false);
                }
                break;
            case MotionEvent.ACTION_UP:
                //抬起时候的xy
                int upx = (int) event.getX();
                int upy = (int) event.getY();
                if(clickX == upx && clickY == upy){//按下和抬起时候的位置一样说明是点击事件
                    //在这里需要执行点击事件，执行的是listview的item的点击事件
                    AdapterView.OnItemClickListener itemClickListener = listView.getOnItemClickListener();//获取设置的onItem的点击事件
                    if(itemClickListener != null){
                        int position = (int) getTag(getId());
                        itemClickListener.onItemClick(listView,this,position,position);
                    }
                }
                break;
        }
        viewDragHelper.processTouchEvent(event);//由helper处理事件
        return true;
    }

    /**
     * 关闭的方法
     */
    public void close(){
        viewDragHelper.smoothSlideViewTo(contentView,0,0);
        ViewCompat.postInvalidateOnAnimation(CustomItem.this);//刷新界面
    }

    /**
     * 快速关闭的方法
     */
    public void quickClose(){
        //快速关闭就是不要那个动画了，直接让他回到初始位置就可以
        contentView.layout(0,0,contentWidth,contentHeight);
        deleteView.layout(contentWidth,0,deleteWidth+contentWidth,contentHeight);
        //关闭之后需要将当前这一条从集合里边移除
        unClosedCustomItemList.remove(this);//手动移除，因为手动layout，不会导致viewDrawgerHelper里边的回掉执行
    }

    /**
     * 打开的方法
     */
    public void open(){
        viewDragHelper.smoothSlideViewTo(contentView,-deleteWidth,0);
        ViewCompat.postInvalidateOnAnimation(CustomItem.this);//刷新界面
    }

    private class myCallBack extends ViewDragHelper.Callback{

        /**
         *当前触摸的子控件
         * @param child
         * @param pointerId
         * @return
         * 如果返回true，表明当前返回的子控件可以被拖动
         */
        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            //我们这里只有内容区域和删除区域，两个都是可滑动的，所以可以直接返回true。这里推荐用下边的返回值，直接返回true也可以
            return child == contentView || child == deleteView;
        }

        /**
         * 当view的位置发生了变化时的回掉
         * @param changedView
         * @param left 当前的getLeft()
         * @param top 当前的getTop()
         * @param dx 拖动的x的值
         * @param dy 拖动的y的值
         */
        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            super.onViewPositionChanged(changedView, left, top, dx, dy);
            if(changedView == contentView){
                deleteView.layout(deleteView.getLeft() + dx,deleteView.getTop(),deleteView.getRight() + dx,deleteView.getBottom());
            }else if(changedView == deleteView){
                //如果拖动的是删除区域，则应该手动改变内容区域的位置
                contentView.layout(contentView.getLeft()+dx,contentView.getTop(),contentView.getRight()+dx,contentView.getBottom());
            }

            if(contentView.getLeft() == 0 && swipeState!=SwipeState.Close){
                swipeState = SwipeState.Close;
                unClosedCustomItemList.remove(CustomItem.this);
            }else if(contentView.getLeft() == -deleteWidth && swipeState!=SwipeState.Open){
                swipeState = SwipeState.Open;
                for(int i=0;i<unClosedCustomItemList.size();i++){//遍历集合将不是自己的控件全部关闭
                    if(unClosedCustomItemList.get(i) != CustomItem.this){
                        unClosedCustomItemList.get(i).quickClose();
                    }
                }
                if(!unClosedCustomItemList.contains(CustomItem.this)){//如果集合里边不包含自己的话就把自己加进去
                    unClosedCustomItemList.add(CustomItem.this);
                }

            }else{
                swipeState = SwipeState.Swiping;
                for(int i=0;i<unClosedCustomItemList.size();i++){//遍历集合将不是自己的控件全部关闭
                    if(unClosedCustomItemList.get(i) != CustomItem.this && dx<0){//判断不是自己并且滑动方向是往左才执行，如果不加方向的判断，自动关闭回去的那个控件，又会把我们打开的控件关闭掉
                        unClosedCustomItemList.get(i).quickClose();
                    }
                }
                if(!unClosedCustomItemList.contains(CustomItem.this)){//如果集合里边不包含自己的话就把自己加进去
                    unClosedCustomItemList.add(CustomItem.this);
                }
            }
        }

        /**
         * 抬起手指或者是事件被拦截释放掉的回调
         * @param releasedChild  触摸的view
         * @param xvel  释放时候的x值
         * @param yvel  释放时候的y值
         */
        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            super.onViewReleased(releasedChild, xvel, yvel);
            //在手指抬起的时候判断是否滑动出来的超过了一半
            if(contentView.getLeft() < -deleteWidth/2){//超出了一半
                //1、把谁滚动过去  2、左边的距离   3、上边的距离
                open();
            }else{//不超过一半的时候关闭
                close();
            }
        }



        /**
         * 用于返回被触摸的view到底滑动多少的回调
         * @param child 被触摸的控件
         * @param left  当前这个控件理论上的getLeft()
         * @param dx    滑动的距离
         * @return  默认返回值0，如果返回left，就一切按照系统默认的拖动速度，你拖多少，我动多少
         */
        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            //我们需要的是只能向左滑，并且只能滑动删除区域的宽度。
            //如果不加判断的话是可以水平滑动的，并且是滑动多少都可以
            //所以这里需要加上限制
            //如果left大于0，那就是像右边滑动了，我们将left置为0
            //如果left小于-deleteWidth，那就是向左滑动超过了deleteWidth的宽度，我们将left置为-deleteWidth。
            //同时这里还需要判断当前触摸的是否是内容区域
            if(child == contentView){
                if(left > 0) left = 0;
                if(left < -deleteWidth) left = -deleteWidth;
            }else if(child == deleteView){
                //删除区域能够向左滑动的最大宽度就是删除区域的宽度,这时候的left正好是contentWidth-deleteWidth
                if(left > contentWidth) left = contentWidth;
                if(left < contentWidth-deleteWidth) left = contentWidth-deleteWidth;
            }
            return left;
        }

        /**
         * 获取view的水平拖拽范围
         * @param child
         * @return 默认返回值是0，0代表内部子控件不能滑动
         */
        @Override
        public int getViewHorizontalDragRange(View child) {
            //我们滑动的范围就是删除区域的宽度
            return deleteWidth;
        }
    }

    /**
     * scroll滑动的时候调用
     */
    @Override
    public void computeScroll() {
        super.computeScroll();
        if(viewDragHelper.continueSettling(true)){
            ViewCompat.postInvalidateOnAnimation(CustomItem.this);
        }
    }

    /**
     * 使用枚举类型，防止乱传值
     */
    enum SwipeState{
        Close,Open,Swiping
    }

    //用于存储打开状态的控件,不管是滑动的还是打开的都应该放进来
    public static List<CustomItem> unClosedCustomItemList = new ArrayList<>();
}
