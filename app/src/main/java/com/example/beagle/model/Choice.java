package com.example.beagle.model;

import com.google.gson.annotations.SerializedName;

public class Choice {
    private APIMessage message;
    private int index;
    @SerializedName("finish_reason") private String finishReason;
    // @SerializedName per specificare il nome del campo JSON

    public APIMessage getMessage() {
        return message;
    }

    public void setMessage(APIMessage message) {
        this.message = message;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getFinishReason() {
        return finishReason;
    }

    public void setFinishReason(String finishReason) {
        this.finishReason = finishReason;
    }
}
