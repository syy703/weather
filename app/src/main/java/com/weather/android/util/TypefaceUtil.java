package com.weather.android.util;

import android.content.Context;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.widget.TextView;

/**
 * Created by syy on 2018/5/12.
 */

public class TypefaceUtil{
        private Context mContext;
        private Typeface mTypeface;

             public TypefaceUtil(Context context, String ttfPath) {
                mContext = context;
                mTypeface = getTypefaceFromTTF(ttfPath);
             }


            public Typeface getTypefaceFromTTF(String ttfPath) {

                 if (ttfPath == null) {
                     return Typeface.DEFAULT;
                } else {
                     return Typeface.createFromAsset(mContext.getAssets(), ttfPath);
                 }
             }


             public void setTypeface(TextView tv, boolean isBold) {
                 tv.setTypeface(mTypeface);
                 setBold(tv, isBold);
             }


             public void setBold(TextView tv, boolean isBold) {
                 TextPaint tp = tv.getPaint();
                 tp.setFakeBoldText(isBold);
             }


             public void setDefaultTypeFace(TextView tv, boolean isBold) {
                 tv.setTypeface(Typeface.DEFAULT);
                 setBold(tv, isBold);
             }


             public void setmTypeface(String ttfPath) {
                 mTypeface = getTypefaceFromTTF(ttfPath);
            }

}
