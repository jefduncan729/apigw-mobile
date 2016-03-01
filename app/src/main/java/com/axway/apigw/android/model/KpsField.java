package com.axway.apigw.android.model;

/**
 * Created by su on 2/20/2016.
 */
public class KpsField {
    public String fldName;
    public String fldType;
    public  boolean isIndex;
    public  boolean isGenerated;
    public  boolean isSecure;
    public  boolean isPrimaryKey;

    public KpsField(String name, String typ) {
        super();
        fldName = name;
        fldType = typ;
        isIndex = false;
        isGenerated = false;
        isSecure = false;
        isPrimaryKey = false;
    }
}
