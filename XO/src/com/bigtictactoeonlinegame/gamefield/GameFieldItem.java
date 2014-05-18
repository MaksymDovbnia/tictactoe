package com.bigtictactoeonlinegame.gamefield;

import android.content.*;
import android.graphics.*;
import android.util.*;
import android.widget.*;

import com.bigtictactoeonlinegame.activity.*;

import java.util.*;

/**
 * Created by Maksym on 10/6/13.
 */
public class GameFieldItem extends ImageView {
    private static Bitmap bitmap_x;
    private static Bitmap bitmap_o;
    private static Bitmap bitmap_main_backgroud;
    private static Bitmap bitmapLastMove;
    private static Bitmap bitmapInSight;
    static Bitmap bitmapMainBackGround;
    static Bitmap scaledBitmapX;
    static Bitmap scaledBitmapO;
    static Bitmap bitmapSight;
    private static List<Bitmap> bitmapsForXAnimation;

    private boolean isLastMove = false;
    private boolean isInSight = false;
    private int i;
    private int j;


    public enum FieldType {X, O}

    ;

    public enum WinLineType {HORIZONTAL, VERTICAl, LEFT, RIGHT}

    ;
    private FieldType fieldType;
    private WinLineType winLineType;


    public GameFieldItem(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        if (bitmap_main_backgroud == null)
            bitmap_main_backgroud = BitmapFactory.decodeResource(context.getResources(), R.drawable.field);

        if (bitmap_o == null)
            bitmap_o = BitmapFactory.decodeResource(context.getResources(), R.drawable.o_without_back);
        if (bitmap_x == null)
            bitmap_x = BitmapFactory.decodeResource(context.getResources(), R.drawable.x_without_bac);
        if (bitmapLastMove == null)
            bitmapLastMove = BitmapFactory.decodeResource(context.getResources(), R.drawable.green_background);
        if (bitmapInSight == null)
            bitmapInSight = BitmapFactory.decodeResource(context.getResources(), R.drawable.sight_background);

    }

    public void setFieldTypeAndDraw(FieldType fieldType) {
        this.fieldType = fieldType;
        invalidate();
    }

    public FieldType getFieldType() {
        return fieldType;
    }

    public WinLineType getWinLineType() {
        return winLineType;
    }

    public void setWinLineType(WinLineType winLineType) {
        this.winLineType = winLineType;
        invalidate();
    }

    public int getI() {
        return i;
    }

    public void setI(int i) {
        this.i = i;
    }

    public int getJ() {
        return j;
    }

    public void setJ(int j) {
        this.j = j;
    }


    public static void destroyAllBitmaps() {
        bitmap_x.recycle();
        bitmap_x = null;
        bitmap_o.recycle();
        bitmap_o = null;
        bitmap_main_backgroud.recycle();
        bitmap_main_backgroud = null;
        bitmapLastMove.recycle();
        bitmapLastMove = null;
        bitmapInSight.recycle();
        bitmapInSight = null;
        bitmapMainBackGround.recycle();
        bitmapMainBackGround = null;
        if (scaledBitmapX != null) scaledBitmapX.recycle();
        scaledBitmapX = null;
        if (scaledBitmapO != null) scaledBitmapO.recycle();
        scaledBitmapO = null;
        if (bitmapSight != null) bitmapSight.recycle();
        bitmapSight = null;
    }

    @Override
    protected void onDraw(Canvas canvas) {

        //i am svetlana garkusha and i am happy to pass my free time with maksym dovbnia because he is my little and bad boy
        int width = getLayoutParams().width;
        int height = getLayoutParams().height;

        if (bitmapMainBackGround == null)
            bitmapMainBackGround = Bitmap.createScaledBitmap(bitmap_main_backgroud,
                    getLayoutParams().width, getLayoutParams().height, true);

        canvas.drawBitmap(bitmapMainBackGround, 0, 0, null);


        if (fieldType != null) {
            if (fieldType == FieldType.X && drawXWithAnimation(height, width, canvas)) {
                return;

            }
            if (scaledBitmapX == null)
                scaledBitmapX = Bitmap.createScaledBitmap(bitmap_x, getLayoutParams().width,
                        getLayoutParams().height, true);

            if (scaledBitmapO == null)
                scaledBitmapO = Bitmap.createScaledBitmap(bitmap_o, getLayoutParams().width,
                        getLayoutParams().height, true);

            canvas.drawBitmap(fieldType == FieldType.X ? scaledBitmapX : scaledBitmapO, 0, 0, null);
        }


        if (isLastMove && !isInSight) {
            if (bitmapLastMove == null) bitmapLastMove = Bitmap.createScaledBitmap(
                    this.bitmapLastMove, getLayoutParams().width, getLayoutParams().height, true);
            canvas.drawBitmap(bitmapLastMove, 0, 0, null);
        }

        if (isInSight) {
            if (bitmapSight == null) {
                bitmapSight = Bitmap.createScaledBitmap(this.bitmapInSight,
                        getLayoutParams().width, getLayoutParams().height, true);
            }
            canvas.drawBitmap(bitmapSight, 0, 0, null);
        }

        if (winLineType != null) {
            Bitmap bitmap = null;

            switch (winLineType) {
                case HORIZONTAL:
                    bitmap = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(),
                            R.drawable.horizontal_line), getLayoutParams().width,
                            getLayoutParams().height, true);
                    break;
                case VERTICAl:
                    bitmap = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(),
                            R.drawable.vertical_line), getLayoutParams().width,
                            getLayoutParams().height, true);
                    break;
                case LEFT:
                    bitmap = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(),
                            R.drawable.left_line), getLayoutParams().width,
                            getLayoutParams().height, true);
                    break;
                case RIGHT:
                    bitmap = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(),
                            R.drawable.right_line), getLayoutParams().width,
                            getLayoutParams().height, true);
                    break;
            }

            canvas.drawBitmap(bitmap, 0, 0, null);

        }


    }

    private boolean drawXWithAnimation(int heiht, int width, Canvas canvas) {
        if (bitmapsForXAnimation == null) {
            bitmapsForXAnimation = new ArrayList<Bitmap>();
            Bitmap x4 = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(),
                    R.drawable.x_4), getLayoutParams().width,
                    getLayoutParams().height, true);
            bitmapsForXAnimation.add(x4);
            Bitmap x3 = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(),
                    R.drawable.x_3), getLayoutParams().width,
                    getLayoutParams().height, true);
            bitmapsForXAnimation.add(x3);
            Bitmap x2 = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(),
                    R.drawable.x_2), getLayoutParams().width,
                    getLayoutParams().height, true);
            bitmapsForXAnimation.add(x2);
            Bitmap x1 = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(),
                    R.drawable.x_1), getLayoutParams().width,
                    getLayoutParams().height, true);
            bitmapsForXAnimation.add(x2);
        }


        return false;
    }


    public void setMarkAboutLastMove(boolean isLastMove) {
        this.isLastMove = isLastMove;
        invalidate();
    }

    public void setMarkAboutInSight(boolean inSight) {
        isInSight = inSight;
        invalidate();
    }


    public void clear() {
        isInSight = false;
        isLastMove = false;
        fieldType = null;
        winLineType = null;
        invalidate();

    }


}
