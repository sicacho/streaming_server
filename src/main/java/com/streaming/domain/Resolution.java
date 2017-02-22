package com.streaming.domain;

/**
 * Created by Administrator on 6/11/2016.
 */
public enum Resolution {
    RE_1080("37"),
    RE_720("22"),
    RE_480("59"),
    RE_360("18"),
    RE_480_WEBM("43");

    private String id;

    Resolution(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return id;
    }
}
