package com.blocksberg.vsc.testmodel.good;

import java.io.Serializable;
import java.util.Date;

/**
 * //TODO class description
 */
public class Bar implements Serializable{
    private String string;
    private String date;

    public String getString() {
        return string;
    }

    public void setString(String string) {
        this.string = string;
    }

    public String getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date.toString();
    }
}
