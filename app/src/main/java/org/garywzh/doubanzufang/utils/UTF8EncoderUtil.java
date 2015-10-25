package org.garywzh.doubanzufang.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

/**
 * Created by WZH on 2015/10/22.
 */
public class UTF8EncoderUtil {
    /**
     * UTF8转码器
     */
    private static final String UTF_8 = "utf-8";

    /**
     * 对文字进行UTF8转码
     */
    public static String encode(String str) {
        try {
            return URLEncoder.encode(str, UTF_8);
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }

    /**
     * 将转码后的文字还原
     */
    public static String decode(String str) {
        try {
            return URLDecoder.decode(str, UTF_8);
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }
}
