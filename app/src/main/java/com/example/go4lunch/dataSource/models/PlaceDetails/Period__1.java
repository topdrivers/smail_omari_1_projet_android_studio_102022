
package com.example.go4lunch.dataSource.models.PlaceDetails;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Period__1 {

    @SerializedName("close")
    @Expose
    private Close__1 close;
    @SerializedName("open")
    @Expose
    private Open__1 open;

    public Close__1 getClose() {
        return close;
    }

    public void setClose(Close__1 close) {
        this.close = close;
    }

    public Open__1 getOpen() {
        return open;
    }

    public void setOpen(Open__1 open) {
        this.open = open;
    }

}
