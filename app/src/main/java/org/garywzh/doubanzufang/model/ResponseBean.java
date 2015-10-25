package org.garywzh.doubanzufang.model;

import java.util.List;

/**
 * Created by WZH on 2015/10/22.
 */
public class ResponseBean {
    public int errno;
    public String last_update_time;
    public List<Item> items;
    public List<String> random_keywords;

    public static class Item {
        public String tid;
        public String aid;
        public String ttl;
        public String tcr;
        public String anm;
        public String gnm;
        public String dgd;
    }
}
