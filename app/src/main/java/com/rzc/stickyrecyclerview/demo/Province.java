package com.rzc.stickyrecyclerview.demo;

/**
 * Created by rzc on 17/9/29.
 */

public class Province {
    public String name;
    public String picUrl;
    public String content1;
    public String content2;

    public Province(String name, String picUrl, String content1, String content2) {
        this.name = name;
        this.picUrl = picUrl;
        this.content1 = content1;
        this.content2 = content2;
    }

    @Override
    public String toString() {
        return "Province{" +
                "name='" + name + '\'' +
                ", picUrl='" + picUrl + '\'' +
                ", content1='" + content1 + '\'' +
                ", content2='" + content2 + '\'' +
                '}';
    }
}
