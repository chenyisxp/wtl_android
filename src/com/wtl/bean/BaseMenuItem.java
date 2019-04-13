package com.wtl.bean;

import android.content.Context;
import android.graphics.drawable.Drawable;

public class BaseMenuItem {
	 private int id;
	    private Context mContext;
	    private String title;
	    private Drawable icon;
	    private Drawable background;
	    private int titleColor;
	    private int titleSize;
	    private int width;

	    public BaseMenuItem(Context context) {
	        mContext = context;
	    }
}
