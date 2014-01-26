package com.componentwithfont;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Date: 15.12.13
 * Time: 23:14
 *
 * @author Maksym Dovbnia (maksym.dovbnia@gmail.com)
 */
public class TextViewWithFontMainActivity extends TextView {

    public TextViewWithFontMainActivity(Context context) {
        super(context);
        initFont(context);
    }

    public TextViewWithFontMainActivity(Context context, AttributeSet attrs) {
        super(context, attrs);
        initFont(context);
    }

    public TextViewWithFontMainActivity(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initFont(context);
    }

    private void initFont(Context context) {
        Typeface mFont = Typeface.createFromAsset(context.getAssets(), "fonts/COMIC.TTF");
        setTypeface(mFont);
    }


}
