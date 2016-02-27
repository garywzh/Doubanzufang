package org.garywzh.doubanzufang.model;

import com.google.common.base.Objects;

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


    public Item(String tid, String aid, String ttl, String tcr, String anm, String gnm, String dgd) {
        this.tid = tid;
        this.aid = aid;
        this.ttl = ttl;
        this.tcr = tcr;
        this.anm = anm;
        this.gnm = gnm;
        this.dgd = dgd;
    }

    public static String buildUrlFromId(String tid) {
        return "http://www.douban.com/group/topic/" + tid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Item)) return false;
        Item item = (Item) o;
        return Objects.equal(ttl, item.ttl)
                && Objects.equal(anm, item.anm);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(ttl, anm);
    }
}
