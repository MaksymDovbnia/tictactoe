/*
 * Copyright (c) 2000~2013  Samsung Electronics, Inc.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of Samsung Electronics, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Samsung Electronics.
 */


package com.bigtictactoeonlinegame.gamefield;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.*;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import com.bigtictactoeonlinegame.activity.R;
import com.utils.Logger;

import java.util.Arrays;

/**
 * My first custom view
 *
 * @author Maksim Dovbnya(m.dovbnya@samsung.com).
 */
public class GameFieldView extends View {
    private static String LOG_TAG = GameFieldView.class.getName();
    private static final int PADDING_IN_FIELD_ITEM = 3;
    private static final float MAX_SCALE = 2.5f;
    private static final float MIN_SCALE = 1f;
    private float mScaleFactor = MIN_SCALE;
    /**
     * The scale listener, used for handling multi-finger scale gestures.
     */
    private final ScaleGestureDetector.OnScaleGestureListener mScaleGestureListener
            = new ScaleGestureDetector.SimpleOnScaleGestureListener() {
        private static final float MIN_SCALE_FACTORY = 0.95f;
        private static final float MAX_SCALE_FACTORY = 1.05f;


        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            if (!isScaleEnable) {
                return true;
            }
            float scaleFactor = Math.min(Math.max(MIN_SCALE_FACTORY, detector.getScaleFactor()), MAX_SCALE_FACTORY);
            float lastScale = mScaleFactor;
            mScaleFactor *= scaleFactor;
            if (mScaleFactor > MAX_SCALE) {
                mScaleFactor = MAX_SCALE;
                scaleFactor = MAX_SCALE / lastScale;
            } else if (mScaleFactor < MIN_SCALE) {
                mScaleFactor = MIN_SCALE;
                scaleFactor = MIN_SCALE / lastScale;
            }
            mMatrix.postScale(scaleFactor, scaleFactor, detector.getFocusX(), detector.getFocusY());
            postInvalidate();
            return true;
        }


    };
    private static final int GAME_FIELD_SIZE = 15;
    private final FieldItem mFieldItems[][] = new FieldItem[GAME_FIELD_SIZE][GAME_FIELD_SIZE];
    private static final int BORDER_PADDING = 0;
    RectF rectF;
    private boolean isDrawingLineWithAnimation;
    private Paint mLinePaint = new Paint();
    private Paint mLastMovePaint = new Paint();

    private int mStartWidth;
    private int mStartHeight;
    private int mStartScaledWidth;
    private int mStartScaeldHeight;
    private Matrix mMatrix;
    private float[] mMatrixValue = new float[9];


    private GestureDetector.SimpleOnGestureListener mGestureListener = new GestureDetector.SimpleOnGestureListener() {
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            correctMatrixMeasure(-distanceX, -distanceY);
            postInvalidate();
            return true;
        }
    };
    private FieldItemClickListener fieldItemClickListener;
    private Bitmap mXScaledBitmap;
    private Bitmap mOScaledBitmap;
    private Bitmap mXBitmap;
    private Bitmap mOBitmap;
    private int mFieldSizeInDimen;
    private ScaleGestureDetector mScaleGestureDetector;
    private GestureDetector mGestureDetector;
    private boolean mIsNeedShowWinLine = false;
    private FieldItem mFromItem;
    private FieldItem mToItem;
    private WinLineDrawer mDrawerWinLine;
    private DrawWinLineCompletedListener drawWinLineCompletedListener;
    private boolean isScaleEnable = true;

    public GameFieldView(Context context) {
        super(context);
        init();
    }

    public GameFieldView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public GameFieldView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public void setFieldItemClickListener(FieldItemClickListener fieldItemClickListener) {
        this.fieldItemClickListener = fieldItemClickListener;
    }

    private void notifyClicklListener(int i, int j) {
        if (fieldItemClickListener != null) {
            fieldItemClickListener.onFieldItemClick(i, j);
        }
    }

    private FieldItem lastItemMove;

    public void showItem(int i, int j, FieldType fieldType) {
        mFieldItems[i][j].fieldType = fieldType;
        lastItemMove = mFieldItems[i][j];
        invalidate();

    }

    public void scrollToField(int i, int j) {
        FieldItem currentPosition = getItemCurrentPosition(mFieldItems[i][j]);
        mMatrix.getValues(mMatrixValue);
        float mx = mMatrixValue[Matrix.MTRANS_X];
        float my = mMatrixValue[Matrix.MTRANS_Y];
        correctMatrixMeasure(-(mx + currentPosition.rect.top), -(my + currentPosition.rect.left));
        invalidate();
    }

    public void setItemEnable(int i, int j, boolean isEnable) {
        mFieldItems[i][j].isEnable = isEnable;
        mFieldItems[i][j].isUsed = true;

    }

    private void isFieldItemVisible(int i, int j) {
        FieldItem fieldItem = mFieldItems[i][j];
        FieldItem currentPosition = getItemCurrentPosition(mFieldItems[i][j]);
        mMatrix.getValues(mMatrixValue);
        float mx = mMatrixValue[Matrix.MTRANS_X];
        float my = mMatrixValue[Matrix.MTRANS_Y];


    }

    public void setEnableAllGameField(boolean isEnabled) {
        for (int i = 0; i < GAME_FIELD_SIZE; i++) {
            for (int j = 0; j < GAME_FIELD_SIZE; j++) {
                mFieldItems[i][j].isEnable = isEnabled;
            }

        }

    }

    public boolean isTablet(Context context) {
        boolean xlarge = ((context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_XLARGE);
        boolean large = ((context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_LARGE);
        return (xlarge || large);
    }

    public void showWinLine(int fromI, int fromJ, int toI, int toJ, DrawWinLineCompletedListener listener) {
        drawWinLineCompletedListener = listener;
        mIsNeedShowWinLine = true;
        isDrawingLineWithAnimation = true;
        mFromItem = mFieldItems[fromI][fromJ];
        mToItem = mFieldItems[toI][toJ];

        if (mToItem.rect.centerX() <= mFromItem.rect.centerX() || mToItem.rect.centerY() <= mFromItem.rect.centerY()) {
            mFromItem = mToItem;
            mFromItem = mFieldItems[fromI][fromJ];
        }
        mDrawerWinLine.init();

        invalidate();
    }

    private void init() {
        mDrawerWinLine = new WinLineDrawer();
        mLinePaint.setStrokeWidth(3);
        mLinePaint.setStyle(Paint.Style.STROKE);
        mLinePaint.setColor(getResources().getColor(R.color.game_field_line));

        mLastMovePaint.setStrokeWidth(4);
        mLastMovePaint.setStyle(Paint.Style.STROKE);
        mLastMovePaint.setColor(getResources().getColor(R.color.green_dialog));
        mMatrix = new Matrix();
        mGestureDetector = new GestureDetector(getContext(), mGestureListener);
        mScaleGestureDetector = new ScaleGestureDetector(getContext(), mScaleGestureListener);
        mXBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.x);
        mOBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.o);

        for (int i = 0; i < GAME_FIELD_SIZE; i++) {
            for (int j = 0; j < GAME_FIELD_SIZE; j++) {
                FieldItem fieldItem = new FieldItem();
                mFieldItems[i][j] = fieldItem;
            }
        }

        setOnTouchListener(new OnTouchListener() {
            private float downX;
            private float downY;
            private FieldItem dowNfieldItem;
            private static final float DELTA = 5;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();

                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        downX = event.getX();
                        downY = event.getY();
                        dowNfieldItem = getFieldItemByCoordinate(event);
                        Log.d(LOG_TAG, "ACTION_DOWN, X:" + event.getX() + " Y:" + event.getY() + " RawX " + event.getRawX() + " RawY" + event.getRawY());

                        break;
                    case MotionEvent.ACTION_UP:
                        Log.d(LOG_TAG, "ACTION_UP, X:" + event.getX() + " Y:" + event.getY() + " RawX " + event.getRawX() + " RawY" + event.getRawY());
                        FieldItem fieldItem = getFieldItemByCoordinate(event);
                        if (dowNfieldItem != null && fieldItem != null && fieldItem.i == dowNfieldItem.i && fieldItem.j == dowNfieldItem.j) {
                            notifyClicklListener(fieldItem.i, fieldItem.j);
                            return true;
                        }
                        break;
                    case MotionEvent.ACTION_MOVE:
                        Log.d(LOG_TAG, "ACTION_MOVE, X:" + event.getX() + " Y:" + event.getY() + " RawX " + event.getRawX() + " RawY" + event.getRawY());

                        if (Math.abs(downX - event.getX()) > DELTA || Math.abs(downY - event.getY()) > DELTA) {
                            dowNfieldItem = null;
                        } else {
                            return true;
                        }
                        break;

                }

                mGestureDetector.onTouchEvent(event);
                mScaleGestureDetector.onTouchEvent(event);
                return true;
            }

            private FieldItem getFieldItemByCoordinate(MotionEvent event) {
                mMatrix.getValues(mMatrixValue);
                float mx = mMatrixValue[Matrix.MTRANS_X];
                float my = mMatrixValue[Matrix.MTRANS_Y];
                float x = (event.getX() + (-mx + (BORDER_PADDING * mScaleFactor))) / mScaleFactor;
                float y = (event.getY() + (-my + (BORDER_PADDING * mScaleFactor))) / mScaleFactor;
                for (int i = 0; i < GAME_FIELD_SIZE; i++) {
                    for (int j = 0; j < GAME_FIELD_SIZE; j++) {
                        FieldItem item = mFieldItems[i][j];
                        if (!item.isUsed && item.isEnable && item.rect.contains(x, y)) {

                            return item;
                        }

                    }
                }
                return null;
            }
        });


        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    int oneItemSize;

    @SuppressLint("DrawAllocation")
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mStartHeight = getMeasuredHeight();
        mStartWidth = getMeasuredWidth();
        mFieldSizeInDimen = Math.min(mStartHeight, mStartWidth);
        isScaleEnable = !isTablet(getContext());
        oneItemSize = (mFieldSizeInDimen) / GAME_FIELD_SIZE;
        int bitmapScaleDelta = PADDING_IN_FIELD_ITEM * 2;
        initItems(oneItemSize, false);
        mXScaledBitmap = Bitmap.createScaledBitmap(mXBitmap, oneItemSize - bitmapScaleDelta, oneItemSize - bitmapScaleDelta, false);
        mOScaledBitmap = Bitmap.createScaledBitmap(mOBitmap, oneItemSize - bitmapScaleDelta, oneItemSize - bitmapScaleDelta, false);
    }

    private void initItems(int fieldItemSize, boolean isResetData) {

        Point startPoint = new Point();
        startPoint.x = BORDER_PADDING;
        startPoint.y = BORDER_PADDING + ((mStartHeight - mStartWidth) / 2);

        for (int i = 0; i < GAME_FIELD_SIZE; i++) {
            for (int j = 0; j < GAME_FIELD_SIZE; j++) {
                int leftUpX = (j * fieldItemSize) + startPoint.x;
                int leftUpY = i * fieldItemSize + startPoint.y;
                int rightDownX = (j + 1) * fieldItemSize + startPoint.x;
                int rightDownY = (i + 1) * fieldItemSize + startPoint.y;
                FieldItem fieldItem = mFieldItems[i][j];
                fieldItem.i = i;
                fieldItem.j = j;
                fieldItem.size = fieldItemSize;
                fieldItem.rect = new RectF(leftUpX, leftUpY, rightDownX, rightDownY);
                mFieldItems[i][j] = fieldItem;
                if (isResetData) {
                    fieldItem.isEnable = true;
                    fieldItem.isUsed = false;
                    fieldItem.fieldType = FieldType.EMPTY;
                }


            }

        }

    }

    private void correctMatrixMeasure(float deltaX, float deltaY) {
        mMatrix.getValues(mMatrixValue);
        float x = mMatrixValue[Matrix.MTRANS_X];
        float y = mMatrixValue[Matrix.MTRANS_Y];
        float scaledWidth = Math.round(mStartWidth * mScaleFactor);
        float scaledHeight = Math.round(mStartHeight * mScaleFactor);
        float deltaXPostTranslate = getAxisPostTranslate(deltaX, x, scaledWidth, mStartWidth, BORDER_PADDING);
        float deltaYPostTranslate = getAxisPostTranslate(deltaY, y, scaledHeight, mStartHeight, BORDER_PADDING);
        mMatrix.postTranslate(deltaXPostTranslate, deltaYPostTranslate);
    }

    private float getAxisPostTranslate(float deltaOfMoving, float currentBorderPosition, float scaledBitmapSize, float viewSize, float borderSize) {
        float resultDeltaOfMoving = deltaOfMoving;
        if (scaledBitmapSize >= viewSize) {
            if (currentBorderPosition + deltaOfMoving > 0) {
                resultDeltaOfMoving = -currentBorderPosition;
            } else if (currentBorderPosition + deltaOfMoving < -(scaledBitmapSize - viewSize)) {
                resultDeltaOfMoving = -(scaledBitmapSize - viewSize + currentBorderPosition);
            }
        } else {
            resultDeltaOfMoving = borderSize - currentBorderPosition;
        }
        return resultDeltaOfMoving;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        long s = System.currentTimeMillis();
        canvas.drawColor(getResources().getColor(R.color.game_field));
        if (rectF != null) {
            canvas.drawRect(rectF, mDrawerWinLine.mWinLinePaint);
            canvas.drawOval(rectF, mDrawerWinLine.mWinLinePaint);
        }
        canvas.concat(mMatrix);
        drawGameField(canvas);
        drawWinLine(canvas);
        Logger.logD("Time", " draw time : " + (System.currentTimeMillis() - s));
    }

    private void drawWinLine(Canvas canvas) {
        if (mIsNeedShowWinLine) {
            if (isDrawingLineWithAnimation) {
                mDrawerWinLine.drawingWinLineWithAnim(canvas);
            } else {
                mDrawerWinLine.drawWinLine(canvas);
            }

        }
    }

    private void drawGameField(Canvas canvas) {
        for (int i = 0; i < GAME_FIELD_SIZE; i++) {
            for (int j = 0; j < GAME_FIELD_SIZE; j++) {
                FieldItem item = mFieldItems[i][j];
                canvas.drawRect(item.rect, mLinePaint);
                if (item.fieldType != FieldType.EMPTY) {
                    if (item.fieldType == FieldType.X) {
                        canvas.drawBitmap(mXScaledBitmap, item.rect.left + PADDING_IN_FIELD_ITEM, item.rect.top + PADDING_IN_FIELD_ITEM, null);

                    } else {
                        canvas.drawBitmap(mOScaledBitmap, item.rect.left + PADDING_IN_FIELD_ITEM, item.rect.top + PADDING_IN_FIELD_ITEM, null);
                    }
                }
            }

        }
        if (lastItemMove != null) {
            canvas.drawRect(lastItemMove.rect, mLastMovePaint);
        }
    }

    private FieldItem getItemCurrentPosition(FieldItem fieldItem) {
        FieldItem currentPosition = new FieldItem();
        currentPosition.isEnable = fieldItem.isEnable;
        currentPosition.size = fieldItem.size * mScaleFactor;
        mMatrix.getValues(mMatrixValue);
        float mx = mMatrixValue[Matrix.MTRANS_X];
        float my = mMatrixValue[Matrix.MTRANS_Y];
        currentPosition.rect = new RectF((fieldItem.rect.left * mScaleFactor) - -mx, (fieldItem.rect.top * mScaleFactor) - -my,
                ((fieldItem.rect.right * mScaleFactor) - -mx), ((fieldItem.rect.bottom * mScaleFactor) - -my));
        return currentPosition;
    }

    public void startNewGame() {
        initItems(oneItemSize, true);
        lastItemMove = null;
        mIsNeedShowWinLine = false;
        isDrawingLineWithAnimation = false;
        mFromItem = null;
        mToItem = null;
        postInvalidate();
    }

    public static enum FieldType {
        X, O, EMPTY;
    }


    /**
     * Callback click listener
     */
    public static interface FieldItemClickListener {
        public void onFieldItemClick(int i, int j);
    }

    private static class FieldItem {
        int i;
        int j;
        float size;
        boolean isEnable = true;
        boolean isUsed;

        FieldType fieldType = FieldType.EMPTY;
        RectF rect;
    }

    private class WinLineDrawer {
        private static final int DRAW_WIN_LINE_ANIMATION_TIME = 1000;
        private static final int CADR_NUMBERS = 20;
        private float startWinLineX;
        private float startWinLineY;
        private float endWinLineX;
        private float endWinLineY;
        private float deltaX;
        private float deltaY;
        private Paint mWinLinePaint = new Paint();

        private WinLineDrawer() {
            mWinLinePaint.setStyle(Paint.Style.STROKE);
            mWinLinePaint.setStrokeWidth(5);
            mWinLinePaint.setColor(getResources().getColor(android.R.color.background_dark));
        }

        void drawingWinLineWithAnim(Canvas canvas) {
            if (isDrawingLineWithAnimation/*endWinLineX < mToItem.rect.centerX() || endWinLineY > mToItem.rect.centerY(*/) {

                endWinLineX += deltaX;
                endWinLineY += deltaY;
            /*} else*//* {*/
                if (drawWinLineCompletedListener != null) {
                    drawWinLineCompletedListener.onLineDraw();
                }
                isDrawingLineWithAnimation = false;
            }
            //  canvas.drawLine(startWinLineX, startWinLineY, endWinLineX, endWinLineY, mWinLinePaint);
            // postInvalidateDelayed(DRAW_WIN_LINE_ANIMATION_TIME / CADR_NUMBERS);
            drawWinLine(canvas);
        }

        void init() {
            startWinLineX = mFromItem.rect.centerX();
            startWinLineY = mFromItem.rect.centerY();
            endWinLineX = mFromItem.rect.centerX();
            endWinLineY = mFromItem.rect.centerY();
            deltaX = (mToItem.rect.centerX() - mFromItem.rect.centerX()) / CADR_NUMBERS;
            deltaY = (mToItem.rect.centerY() - mFromItem.rect.centerY()) / CADR_NUMBERS;
        }

        void drawWinLine(Canvas canvas) {
            FieldItem from = mFromItem;
            FieldItem to = mToItem;
            canvas.drawLine(from.rect.centerX(), from.rect.centerY(), to.rect.centerX(), to.rect.centerY(), mWinLinePaint);
        }
    }


    interface DrawWinLineCompletedListener {
        void onLineDraw();
    }
}
