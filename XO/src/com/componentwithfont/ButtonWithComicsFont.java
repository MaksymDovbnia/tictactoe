package com.componentwithfont;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.Button;

/**
 * Date: 19.12.13
 * Time: 23:04
 *
 * @author Maksym Dovbnia (maksym.dovbnia@gmail.com)
 */
public class ButtonWithComicsFont extends Button {
    public ButtonWithComicsFont(Context context) {
        super(context);
        initFont(context);

    }

    public ButtonWithComicsFont(Context context, AttributeSet attrs) {
        super(context, attrs);
        initFont(context);

    }

    public ButtonWithComicsFont(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initFont(context);
    }

    private void initFont(Context context) {
        Typeface mFont = Typeface.createFromAsset(context.getAssets(), "fonts/COMIC.TTF");
        setTypeface(mFont);
    }
}
