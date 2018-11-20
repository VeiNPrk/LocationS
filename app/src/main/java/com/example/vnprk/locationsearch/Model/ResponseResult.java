package com.example.vnprk.locationsearch.Model;

/**
 * Created by VNPrk on 16.10.2018.
 */

public class ResponseResult {
    int result;
    String error;

    public ResponseResult(){

    }

    public int getResult() {
        return result;
    }

    public String getError() {
        return error;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public void setError(String error) {
        this.error = error;
    }
}
