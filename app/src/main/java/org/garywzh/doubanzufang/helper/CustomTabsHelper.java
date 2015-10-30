package org.garywzh.doubanzufang.helper;

import android.app.Activity;
import android.content.res.Resources;
import android.support.annotation.AttrRes;
import android.support.customtabs.CustomTabsIntent;
import android.util.TypedValue;

public class CustomTabsHelper {

    public static CustomTabsIntent.Builder getBuilder(Activity activity) {
        final int color = getAttrColor(activity.getTheme(), android.support.v7.appcompat.R.attr.colorPrimary);

        final CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder(null);

        return builder.setShowTitle(true).setToolbarColor(color);
    }

    public static int getAttrColor(Resources.Theme theme, @AttrRes int attrId) {
        final TypedValue typedValue = new TypedValue();
        if (!theme.resolveAttribute(attrId, typedValue, true)) {
            throw new IllegalArgumentException("can't found attr for: " + Integer.toHexString(attrId));
        }

        return typedValue.data;
    }
}
