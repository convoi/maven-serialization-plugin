package com.blocksberg.vsc.testmodel.good;

import com.blocksberg.vsc.markers.VersionedSerialized;
import uk.co.jemos.podam.common.PodamLongValue;
import uk.co.jemos.podam.common.PodamStrategyValue;

import java.io.Serializable;
import java.util.Date;

/**
 * @author Justin Heesemann
 */
@VersionedSerialized(id = "foo", version = 0)
public class Foo implements Serializable {

    private int intValue;
    private long longValue;
    private boolean booleanValue;
    private float floatValue;
    private double doubleValue;

    private int[] intArray;


    private Integer integerObject;
    private Boolean BooleanObject;
    private String stringObject;
    private Long longObject;
    private Float floatObject;
    private Double doubleObject;

    private Bar bar;

    public Foo(int[] bla) {
        intArray = bla;
    }

    public int getIntValue() {
        return intValue;
    }

    public long getLongValue() {
        return longValue;
    }

    public boolean isBooleanValue() {
        return booleanValue;
    }

    public float getFloatValue() {
        return floatValue;
    }

    public double getDoubleValue() {
        return doubleValue;
    }

    public int[] getIntArray() {
        return intArray;
    }

    public Integer getIntegerObject() {
        return integerObject;
    }

    public Boolean getBooleanObject() {
        return BooleanObject;
    }

    public String getStringObject() {
        return stringObject;
    }

    public Long getLongObject() {
        return longObject;
    }

    public Float getFloatObject() {
        return floatObject;
    }

    public Double getDoubleObject() {
        return doubleObject;
    }

    public void setDoubleObject(Double doubleObject) {
        this.doubleObject = doubleObject;
    }

    public void setStringObject(String stringObject) {
        this.stringObject = stringObject;
    }

    public Bar getBar() {
        return bar;
    }

    public void setBar(Bar bar) {
        this.bar = bar;
    }
}
