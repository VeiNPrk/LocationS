package com.example.vnprk.locationsearch;

/**
 * Created by VNPrk on 31.10.2018.
 */

public class MessageEvent {

    public final String message;
    public final int typeDescribe;
    public int count;

    public MessageEvent(String message, int typeDescribe) {
        this.message = message;
        this.typeDescribe=typeDescribe;
    }

    public MessageEvent(String message, int typeDescribe, int count) {
        this.message = message;
        this.typeDescribe=typeDescribe;
        this.count=count;
    }
}
