package org.garywzh.doubanzufang.model;

/**
 * Created by WZH on 2015/10/28.
 */
public class Item {
    public String tid;
    public String aid;
    public String ttl;
    public String tcr;
    public String anm;
    public String gnm;
    public String dgd;


    public static String buildUrlFromId(String tid) {
        return "http://www.douban.com/group/topic/" + tid;
    }
}
