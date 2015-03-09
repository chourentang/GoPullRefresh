package com.jack.gopullrefreshlibrary;

import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.Scroller;

/**
 * A layout for pull to refresh
 * Created by jack on 15-3-6.
 */
public abstract class PullRefreshLayout extends ViewGroup {
//    private static final String TAG = PullRefreshLayout.class.getSimpleName();

    private View headerLayout;
    private View contentLayout;
    private View footerLayout;
    private Scroller scroller;

    private int measWidth, measHeight;
    private int headerLayoutHeight;
    private int footerLayoutHeight;
    private float downY = 0, moveY = 0;
    private boolean isPullDown, isPullUp;
    private boolean isRefreshing;

    protected PullRefreshListener listener;

    public PullRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        initChildrenView();
    }

    private void initChildrenView() {
        scroller = new Scroller(getContext(), new DecelerateInterpolator());

        int childCount = getChildCount();
        if(childCount == 3) {
            headerLayout = getChildAt(0);
            contentLayout = getChildAt(1);
            footerLayout = getChildAt(2);
        }else {
            throw new IllegalArgumentException("there must be 3 child views in pull refresh");
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        // 测量屏幕的宽度和高度
        measWidth = MeasureSpec.getSize(widthMeasureSpec);
        measHeight = MeasureSpec.getSize(heightMeasureSpec);

        // 设置header/content/footer的宽度和高度
        headerLayoutHeight = measHeight / 8;
        footerLayoutHeight = measHeight / 8;
        headerLayout.measure(measWidth, headerLayoutHeight);
        contentLayout.measure(measWidth, measHeight);
        footerLayout.measure(measWidth, footerLayoutHeight);

    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        // 设置header，content， footer的位置
        headerLayout.layout(l, t - headerLayoutHeight, r, b);
        contentLayout.layout(l, t, r, b);
        footerLayout.layout(l, measHeight, r, b + footerLayoutHeight);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        boolean handled = false;
        final int action = MotionEventCompat.getActionMasked(event);
        // Always handle the case of the touch gesture being complete.
        if (action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP) {
            // Do not intercept touch event, let the child handle it
            return false;
        }

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downY = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                moveY = event.getY();
                float offsetY = moveY - downY;
                if(isTop() && offsetY > 3) {
                    handled = true;
                } else if(isBottom() && offsetY < 3) {
                    handled = true;
                }
                break;
        }
        return handled;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(isRefreshing) return true;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downY = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                moveY = event.getY();
                float offsetY = moveY - downY;
                // content view位于顶部，offsetY大于0,说明是下拉操作
                if(isTop() && offsetY > 3) {
                    scrollTo(0, -offsetY, 100);
                    isPullDown = true;
                } else if(isBottom() && offsetY < 3) {
                    isPullUp = true;
                }
                break;
            case MotionEvent.ACTION_UP:
                // 如果时下拉操作，在滑动结束后，回滚到只有header布局显示的地方
                isRefreshing = true;
                if(isPullDown) {
                    handlePullDown();
                }
                if(isPullUp) {
                    handlePullUp();
                }
                super.onInterceptTouchEvent(event);
                break;
        }

        return true;
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        super.computeScroll();
        if(scroller.computeScrollOffset()) {
            scrollTo(scroller.getCurrX(), scroller.getCurrY());
            postInvalidate();
        }
    }

    private void scrollTo(float x, float y, int duration) {
        if(duration > 0)
            scroller.startScroll(getScrollX(), getScrollY(), (int)x, (int)y, duration);
        else
            scroller.startScroll(getScrollX(), getScrollY(), (int) x, (int) y);
        invalidate();
    }

    public abstract boolean isTop();

    public abstract boolean isBottom();

    private void handlePullDown() {
        // 回滚到只有header布局显示的地方
        float finalY = -(scroller.getCurrY() + headerLayoutHeight);
        scrollTo(0, finalY, 1000);

        if(listener != null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    // 做下拉刷新的操作
                    listener.onPullDown();
                    // 视图复位
                    post(new Runnable() {
                        @Override
                        public void run() {
                            scrollTo(0, headerLayoutHeight, 600);
                        }
                    });
                    isPullDown = false;
                    // 通知刷新完成
                    isRefreshing = false;
                    listener.onRefreshFinished();
                }
            }).start();
        } else {
            isPullDown = false;
        }
    }

    private void handlePullUp() {
        scrollTo(0, footerLayoutHeight, 200);
        // 做上拉刷新的操作
        if(listener != null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    // 做上拉刷新操作
                    listener.onPullUp();
                    // 视图复位
                    post(new Runnable() {
                        @Override
                        public void run() {
                            scrollTo(0, -footerLayoutHeight, 200);
                        }
                    });
                    isPullUp = false;
                    // 通知刷新完成
                    isRefreshing = false;
                    listener.onRefreshFinished();
                }
            }).start();
        } else {
            isPullUp = false;
        }
    }

    public View getContentView() {
        return contentLayout;
    }

    public void setPullRefreshListener(PullRefreshListener listener) {
        this.listener = listener;
    }

    public interface PullRefreshListener {
        public void onPullDown();
        public void onPullUp();
        public void onRefreshFinished();
    }

}
