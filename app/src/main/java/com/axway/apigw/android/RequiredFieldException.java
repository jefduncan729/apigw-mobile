package com.axway.apigw.android;

/**
 * Created by su on 10/11/2014.
 */
public class RequiredFieldException extends ValidationException {

    public RequiredFieldException(String fldName) {
        super(String.format("%s is required", fldName));
    }
}
