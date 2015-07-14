package com.blocksberg.vsc.testmodel.good;

import java.io.Serializable;
import java.util.Date;

/**
 * //TODO class description
 */
public class NoSetters implements Serializable{
    private Date date;
    public NoSetters() {}

    public void init() {
        date = new Date();
    }

    public Date getDate() {
        return date;
    }
}
