package com.entity;

import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class Font {
	
	
	public static final void setAppFont(ViewGroup mContainer, Typeface mFont)
	{
	    if (mContainer == null || mFont == null) return;

	    final int mCount = mContainer.getChildCount();

	    // Loop through all of the children.
	    for (int i = 0; i < mCount; ++i)
	    {
	        final View mChild = mContainer.getChildAt(i);
	        if (mChild instanceof TextView )
	        {
	            // Set the font if it is a TextView.
	            ((TextView) mChild).setTypeface(mFont);
	        }
	        else if (mChild instanceof Button){
	        	 ((Button) mChild).setTypeface(mFont);
	        	 
	        }
	        else if (mChild instanceof ViewGroup)
	        {
	            // Recursively attempt another ViewGroup.
	        	Font.setAppFont((ViewGroup) mChild, mFont);
	        }
	        else if (mChild instanceof LinearLayout)
	        {
	            // Recursively attempt another ViewGroup.
	        	Font.setAppFont((ViewGroup) mChild, mFont);
	        }
	    }
	}
	
	


}
