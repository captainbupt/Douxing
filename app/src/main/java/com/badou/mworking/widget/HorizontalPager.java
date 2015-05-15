package com.badou.mworking.widget;
 
import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Scroller;
 
/**
 * 左右滑动页面控件
 * @Description: 左右滑动页面控件

 * @FileName: HorizontalPager.java 

 * @Package com.slide.pager 

 * @Author Hanyonglu

 * @Date 2012-4-22 下午03:16:36 

 * @Version V1.0
 */
public final class HorizontalPager extends ViewGroup {
	
    // 左右滑动页面时间
    private static final int ANIMATION_SCREEN_SET_DURATION_MILLIS = 500;

    // 左右1/2时进行切换
    private static final int FRACTION_OF_SCREEN_WIDTH_FOR_SWIPE = 2;
    private static final int INVALID_SCREEN = -1;
    
    private static final int SNAP_VELOCITY_DIP_PER_SECOND = 600;

    // 每毫秒1像素
    private static final int VELOCITY_UNIT_PIXELS_PER_SECOND = 1000;
 
    private static final int TOUCH_STATE_REST = 0;
    private static final int TOUCH_STATE_HORIZONTAL_SCROLLING = 1;
    private static final int TOUCH_STATE_VERTICAL_SCROLLING = -1;
    private int mCurrentScreen;
    private int mDensityAdjustedSnapVelocity;
    private boolean mFirstLayout = true;
    private float mLastMotionX;
    private float mLastMotionY;
    private OnScreenSwitchListener mOnScreenSwitchListener;
    private int mMaximumVelocity;
    private int mNextScreen = INVALID_SCREEN;
    private Scroller mScroller;
    private int mTouchSlop;
    private int mTouchState = TOUCH_STATE_REST;
    private VelocityTracker mVelocityTracker;
    private int mLastSeenLayoutWidth = -1;
 
    public HorizontalPager(final Context context) {
        super(context);
        init();
    }
 
    public HorizontalPager(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        init();
    }
 
    /*
     *初始化页面控件
     */
    private void init() {
        mScroller = new Scroller(getContext());
 
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay()
                .getMetrics(displayMetrics);
        mDensityAdjustedSnapVelocity = (int) (displayMetrics.density * SNAP_VELOCITY_DIP_PER_SECOND);
 
        final ViewConfiguration configuration = ViewConfiguration.get(getContext());
        mTouchSlop = configuration.getScaledTouchSlop();
        mMaximumVelocity = configuration.getScaledMaximumFlingVelocity();
    }
 
    @Override
    protected void onMeasure(final int widthMeasureSpec, final int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
 
        final int width = MeasureSpec.getSize(widthMeasureSpec);
        final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        if (widthMode != MeasureSpec.EXACTLY) {
            throw new IllegalStateException("ViewSwitcher can only be used in EXACTLY mode.");
        }
 
        final int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        if (heightMode != MeasureSpec.EXACTLY) {
            throw new IllegalStateException("ViewSwitcher can only be used in EXACTLY mode.");
        }
 
        final int count = getChildCount();
        
        for (int i = 0; i < count; i++) {
            getChildAt(i).measure(widthMeasureSpec, heightMeasureSpec);
        }
 
        if (mFirstLayout) {
            scrollTo(mCurrentScreen * width, 0);
            mFirstLayout = false;
        }
 
        else if (width != mLastSeenLayoutWidth) {
            Display display =
                    ((WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE))
                            .getDefaultDisplay();
            int displayWidth = display.getWidth();
 
            mNextScreen = Math.max(0, Math.min(getCurrentScreen(), getChildCount() - 1));
            final int newX = mNextScreen * displayWidth;
            final int delta = newX - getScrollX();
 
            mScroller.startScroll(getScrollX(), 0, delta, 0, 0);
        }
 
        mLastSeenLayoutWidth   = width;
    }
 
    @Override
    protected void onLayout(final boolean changed, final int l, final int t, final int r,
            final int b) {
        int childLeft = 0;
        final int count = getChildCount();
 
        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            
            if (child.getVisibility() != View.GONE) {
                final int childWidth = child.getMeasuredWidth();
                child.layout(childLeft, 0, childLeft + childWidth, child.getMeasuredHeight());
                childLeft += childWidth;
            }
        }
    }
 
    @Override
    public boolean onInterceptTouchEvent(final MotionEvent ev) {
        final int action = ev.getAction();
        boolean intercept = false;
 
        switch (action) {
            case MotionEvent.ACTION_MOVE:
                if (mTouchState == TOUCH_STATE_HORIZONTAL_SCROLLING) {
                    
                    intercept = true;
                } else if (mTouchState == TOUCH_STATE_VERTICAL_SCROLLING) {
                    intercept = false;
                } else {
                    final float x = ev.getX();
                    final int xDiff = (int) Math.abs(x - mLastMotionX);
                    boolean xMoved = xDiff > mTouchSlop;
 
                    if (xMoved) {
                        mTouchState = TOUCH_STATE_HORIZONTAL_SCROLLING;
                        mLastMotionX = x;
                    }
 
                    final float y = ev.getY();
                    final int yDiff = (int) Math.abs(y - mLastMotionY);
                    boolean yMoved = yDiff > mTouchSlop;
 
                    if (yMoved) {
                        mTouchState = TOUCH_STATE_VERTICAL_SCROLLING;
                    }
                }
 
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                mTouchState = TOUCH_STATE_REST;
                break;
            case MotionEvent.ACTION_DOWN:
                mLastMotionY = ev.getY();
                mLastMotionX = ev.getX();
                break;
            default:
                break;
            }
 
        return intercept;
    }
 
    @Override
    public boolean onTouchEvent(final MotionEvent ev) {
 
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        
        mVelocityTracker.addMovement(ev);
 
        final int action = ev.getAction();
        final float x = ev.getX();
 
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                if (!mScroller.isFinished()) {
                    mScroller.abortAnimation();
                }

                mLastMotionX = x;
 
                if (mScroller.isFinished()) {
                    mTouchState = TOUCH_STATE_REST;
                } else {
                    mTouchState = TOUCH_STATE_HORIZONTAL_SCROLLING;
                }
 
                break;
            case MotionEvent.ACTION_MOVE:
                final int xDiff = (int) Math.abs(x - mLastMotionX);
                boolean xMoved = xDiff > mTouchSlop;
 
                if (xMoved) {
                    mTouchState = TOUCH_STATE_HORIZONTAL_SCROLLING;
                }
 
                if (mTouchState == TOUCH_STATE_HORIZONTAL_SCROLLING) {
                    final int deltaX = (int) (mLastMotionX - x);
                    mLastMotionX = x;
                    final int scrollX = getScrollX();
 
                    if (deltaX < 0) {
                        if (scrollX > 0) {
                            scrollBy(Math.max(-scrollX, deltaX), 0);
                        }
                    } else if (deltaX > 0) {
                        final int availableToScroll =
                                getChildAt(getChildCount() - 1).getRight() - scrollX - getWidth();
 
                        if (availableToScroll > 0) {
                            scrollBy(Math.min(availableToScroll, deltaX), 0);
                        }
                    }
                }
 
                break;
 
            case MotionEvent.ACTION_UP:
                if (mTouchState == TOUCH_STATE_HORIZONTAL_SCROLLING) {
                    final VelocityTracker velocityTracker = mVelocityTracker;
                    velocityTracker.computeCurrentVelocity(VELOCITY_UNIT_PIXELS_PER_SECOND,
                            mMaximumVelocity);
                    int velocityX = (int) velocityTracker.getXVelocity();
 
                    if (velocityX > mDensityAdjustedSnapVelocity && mCurrentScreen > 0) {
                        snapToScreen(mCurrentScreen - 1);
                    } else if (velocityX < -mDensityAdjustedSnapVelocity
                            && mCurrentScreen < getChildCount() - 1) {
                        snapToScreen(mCurrentScreen + 1);
                    } else {
                        snapToDestination();
                    }
 
                    if (mVelocityTracker != null) {
                        mVelocityTracker.recycle();
                        mVelocityTracker = null;
                    }
                }
 
                mTouchState = TOUCH_STATE_REST;
 
                break;
            case MotionEvent.ACTION_CANCEL:
                mTouchState = TOUCH_STATE_REST;
                break;
            default:
                break;
        }
 
        return true;
    }
 
    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            postInvalidate();
        } else if (mNextScreen != INVALID_SCREEN) {
            mCurrentScreen = Math.max(0, Math.min(mNextScreen, getChildCount() - 1));
 
            if (mOnScreenSwitchListener != null) {
                mOnScreenSwitchListener.onScreenSwitched(mCurrentScreen);
            }
 
            mNextScreen = INVALID_SCREEN;
        }
    }
 
    /**
     * 获取当前页面的索引值
     * @return
     */
    public int getCurrentScreen() {
        return mCurrentScreen;
    }
 
    /**
     * 设置当前页面
     * @param currentScreen
     * @param animate
     */
    public void setCurrentScreen(final int currentScreen, final boolean animate) {
        mCurrentScreen = Math.max(0, Math.min(currentScreen, getChildCount() - 1));
        
        if (animate) {
            snapToScreen(currentScreen, ANIMATION_SCREEN_SET_DURATION_MILLIS);
        } else {
            scrollTo(mCurrentScreen * getWidth(), 0);
        }
        
        invalidate();
    }
 
    /**
     * 页面滑动事件监听器
     * @param onScreenSwitchListener
     */
    public void setOnScreenSwitchListener(final OnScreenSwitchListener onScreenSwitchListener) {
        mOnScreenSwitchListener = onScreenSwitchListener;
    }
 
    /**
     * 直接切换到相应页面
     */
    private void snapToDestination() {
        final int screenWidth = getWidth();
        int scrollX = getScrollX();
        int whichScreen = mCurrentScreen;
        int deltaX = scrollX - (screenWidth * mCurrentScreen);
 
        if ((deltaX < 0) && mCurrentScreen != 0
                && ((screenWidth / FRACTION_OF_SCREEN_WIDTH_FOR_SWIPE) < -deltaX)) {
            whichScreen--;
        } else if ((deltaX > 0) && (mCurrentScreen + 1 != getChildCount())
                && ((screenWidth / FRACTION_OF_SCREEN_WIDTH_FOR_SWIPE) < deltaX)) {
            whichScreen++;
        }
 
        snapToScreen(whichScreen);
    }
 
    private void snapToScreen(final int whichScreen) {
        snapToScreen(whichScreen, -1);
    }
 
    private void snapToScreen(final int whichScreen, final int duration) {
        mNextScreen = Math.max(0, Math.min(whichScreen, getChildCount() - 1));
        final int newX = mNextScreen * getWidth();
        final int delta = newX - getScrollX();
 
        if (duration < 0) {
            mScroller.startScroll(getScrollX(), 0, delta, 0, (int) (Math.abs(delta)
                    / (float) getWidth() * ANIMATION_SCREEN_SET_DURATION_MILLIS));
        } else {
            mScroller.startScroll(getScrollX(), 0, delta, 0, duration);
        }
 
        invalidate();
    }
}