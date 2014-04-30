package com.example.pinnedheaderlistviewdemo.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.TextView;
import com.example.pinnedheaderlistviewdemo.MainActivity;
import com.example.pinnedheaderlistviewdemo.R;
import com.example.pinnedheaderlistviewdemo.db.CityDao;
import com.example.pinnedheaderlistviewdemo.db.DBHelper;

/**
         * 竖向选择器   字母索引
         */
public class BladeView extends View {
    private static final String TAG ="BladeView" ;

    private OnItemClickListener mOnItemClickListener;
    private static String[] mBlade ;
//            = {"当前", "A", "B", "C", "D", "F", "G", "H", "J", "K",
//            "L", "M", "N", "O", "P", "Q", "R", "S", "T", "W", "X",
//            "Y", "Z"};
    int choose = -1;
    Paint paint = new Paint();
    boolean showBkg = false;
    private PopupWindow mPopupWindow;
    private TextView mPopupText;
    private Handler handler = new Handler();

    {
        mBlade = new CityDao(new DBHelper()).sectionsAndBlade("当前");
    }
    public BladeView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public BladeView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BladeView(Context context) {
        super(context);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (showBkg) {
            canvas.drawColor(Color.parseColor("#AAAAAA"));
        }

        int height = getHeight();
        int width = getWidth();
        int singleHeight = height / mBlade.length;
        for (int i = 0; i < mBlade.length; i++) {
            //竖向选择器中字母默认显示的颜色
            paint.setColor(Color.parseColor("#ff2f2f2f"));
//			paint.setTypeface(Typeface.DEFAULT_BOLD);	//加粗
            paint.setTextSize(getResources().getDimensionPixelSize(R.dimen.bladeview_fontsize));//设置字体的大小
            paint.setFakeBoldText(true);
            paint.setAntiAlias(true);
            if (i == choose) {
                //竖向选择器中  被选中字母的颜色
                paint.setColor(Color.parseColor("#ff0000"));
//                paint.setColor(Color.parseColor("#3399ff"));
            }
            float xPos = width / 2 - paint.measureText(mBlade[i]) / 2;
            float yPos = singleHeight * i + singleHeight;
            canvas.drawText(mBlade[i], xPos, yPos, paint);
            paint.reset();
        }

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        final int action = event.getAction();
        final float y = event.getY();
        final int oldChoose = choose;
        //用户点击选中的字母
        final int choiceLetter = (int) (y / getHeight() * mBlade.length);

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                //如果此处设置为true，意为当竖向选择器被点击(包括滑动状态)时，显示背景颜色
//                showBkg = true;
                if (oldChoose != choiceLetter) {
                    if (choiceLetter >= 0 && choiceLetter < mBlade.length) {    //让第一个字母响应点击事件
                        performItemClicked(choiceLetter);
                        choose = choiceLetter;
                        invalidate();
                    }
                }

                break;
            case MotionEvent.ACTION_MOVE:
                if (oldChoose != choiceLetter) {
                    if (choiceLetter >= 0 && choiceLetter < mBlade.length) {    //让第一个字母响应点击事件
                        performItemClicked(choiceLetter);
                        choose = choiceLetter;
                        /*
                        invalidate:
                         Invalidate[ɪn'vælɪdeɪt] the whole view. If the view is visible,
                         onDraw(android.graphics.Canvas) will be called at some point in
                         the future. This must be called from a UI thread. To call from a non-UI thread,
                         call postInvalidate().
                         */
                        invalidate();
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                showBkg = false;
                choose = -1;
                dismissPopup();
                invalidate();
                break;
        }
        return true;
    }

    /**
     * 弹出显示   字母索引
     * @param item
     */
    private void showPopup(int item) {
        if (mPopupWindow == null) {

            handler.removeCallbacks(dismissRunnable);
            mPopupText = new TextView(getContext());
            mPopupText.setBackgroundColor(Color.GRAY);
            mPopupText.setTextColor(Color.WHITE);
            mPopupText.setTextSize(getResources().getDimensionPixelSize(R.dimen.bladeview_popup_fontsize));
            mPopupText.setGravity(Gravity.CENTER_HORIZONTAL
                    | Gravity.CENTER_VERTICAL);

            int height = getResources().getDimensionPixelSize(R.dimen.bladeview_popup_height);

            mPopupWindow = new PopupWindow(mPopupText, height, height);
        }

        String text = "";
        if (item == 0) {
            text = "当前";
        } else {
            text = MainActivity.ALL_CHARACTER.substring(item, item + 1) ;
//            text = Character.toString((char) ('A' + item - 1));
        }
        mPopupText.setText(text);
        if (mPopupWindow.isShowing()) {
            mPopupWindow.update();
        } else {
            mPopupWindow.showAtLocation(getRootView(),
                    Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
        }
    }

    private void dismissPopup() {
        handler.postDelayed(dismissRunnable, 800);
    }

    Runnable dismissRunnable = new Runnable() {

        @Override
        public void run() {
            // TODO Auto-generated method stub
            if (mPopupWindow != null) {
                mPopupWindow.dismiss();
            }
        }
    };

    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mOnItemClickListener = listener;
    }

    private void performItemClicked(int item) {
        if (mOnItemClickListener != null) {
            Log.d(TAG,"item :" + item + "对应的字母为" + mBlade[item]) ;
            mOnItemClickListener.onItemClick(mBlade[item]);
            showPopup(item);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(String s);
    }

}
