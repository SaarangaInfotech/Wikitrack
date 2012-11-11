package com.saaranga.wikikannada;

import android.content.Context;
import android.util.AttributeSet;
import android.webkit.WebView;

/**
 * custom webview - this view when used inside a view pager lets the user scroll horizontally to read the text in the webview
 * to scroll to the hidden text just tap once on the screen
 * 
 * @author supreeth
 *
 * Copyright Saaranga infotech
 */
public class ExtendedWebView extends WebView {
    public ExtendedWebView(Context context) {
        super(context);
    }

    public ExtendedWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public boolean canScrollHor(int direction) {
        final int offset = computeHorizontalScrollOffset();
        final int range = computeHorizontalScrollRange() - computeHorizontalScrollExtent();
        if (range == 0) return false;
        if (direction < 0) {
            return offset > 0;
        } else {
            return offset < range - 1;
        }
    }
}
