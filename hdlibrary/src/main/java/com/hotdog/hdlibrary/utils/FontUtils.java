package com.hotdog.hdlibrary.utils;

import android.graphics.Paint;

public class FontUtils {
    
    /**
     * @return 返回指定paint的文字高度
     */
    public static float getFontHeight(Paint paint)  {
        Paint.FontMetrics fm = paint.getFontMetrics();
        return fm.descent - fm.ascent;
    }
    
    /**
     * @return 返回指定paint离文字顶部的基准距离
     */
    public static float getFontLeading(Paint paint)  {
        Paint.FontMetrics fm = paint.getFontMetrics();
        return fm.leading- fm.ascent;
    }
    
}
