package com.componentwithfont;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Date: 19.12.13
 * Time: 22:49
 *
 * @author Maksym Dovbnia (maksym.dovbnia@gmail.com)
 */
public class TextViewWirhComicsFont extends TextView {
    public TextViewWirhComicsFont(Context context) {
        super(context);
        initFont(context);
    }

    public TextViewWirhComicsFont(Context context, AttributeSet attrs) {
        super(context, attrs);
        initFont(context);
    }

    public TextViewWirhComicsFont(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initFont(context);
    }

    private void initFont(Context context) {
        Typeface mFont = Typeface.createFromAsset(context.getAssets(), "fonts/COMIC.TTF");
        setTypeface(mFont);
    }
}
