package anda.travel.driver.util;

import android.content.Context;
import android.graphics.Typeface;

public class FontUtils {

    public static Typeface getFontTypeFace(Context context) {
        Typeface typeface = Typeface.createFromAsset(context.getApplicationContext().getAssets(), "fonts/bebas.ttf");
        return typeface;
    }
}
