package com.game.gamefield;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.game.activity.R;

/**
 * Created by Maksym on 10/6/13.
 */
public class GameFieldItem extends ImageView {
    private static Bitmap bitmap_x;
    private static Bitmap bitmap_o;
    private static Bitmap bitmap_main_backgroud;
    private static Bitmap bitmapLastMove;
    private static Bitmap bitmapInSight;

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

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //i am svetlana garkusha and i am happy to pass my free time with maksym dovbnia because he is my little and bad boy

        Bitmap bitmapMainBackGround = Bitmap.createScaledBitmap(bitmap_main_backgroud, canvas.getWidth(), canvas.getHeight(), true);
        canvas.drawBitmap(bitmapMainBackGround, 0, 0, null);


        if (fieldType != null) {
            Bitmap bitmapX = Bitmap.createScaledBitmap(bitmap_x, canvas.getWidth(), canvas.getHeight(), true);
            Bitmap bitmapO = Bitmap.createScaledBitmap(bitmap_o, canvas.getWidth(), canvas.getHeight(), true);
            canvas.drawBitmap(fieldType == FieldType.X ? bitmapX : bitmapO, 0, 0, null);
        }
        if (isLastMove && !isInSight) {
            Bitmap bitmapLastMove = Bitmap.createScaledBitmap(this.bitmapLastMove, canvas.getWidth(), canvas.getHeight(), true);
            canvas.drawBitmap(bitmapLastMove, 0, 0, null);
        }

        if (isInSight) {
            Bitmap bitmapSight = Bitmap.createScaledBitmap(this.bitmapInSight, canvas.getWidth(), canvas.getHeight(), true);
            canvas.drawBitmap(bitmapSight, 0, 0, null);
        }

        if (winLineType != null) {
            Bitmap bitmap = null;
            switch (winLineType) {
                case HORIZONTAL:
                    bitmap = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.horizontal_line), canvas.getWidth(), canvas.getHeight(), true);
                    break;
                case VERTICAl:
                    bitmap = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.vertical_line), canvas.getWidth(), canvas.getHeight(), true);
                    break;
                case LEFT:
                    bitmap = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.left_line), canvas.getWidth(), canvas.getHeight(), true);
                    break;
                case RIGHT:
                    bitmap = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.right_line), canvas.getWidth(), canvas.getHeight(), true);
                    break;
            }

            canvas.drawBitmap(bitmap, 0, 0, null);

        }


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
