package com.nikron.conversion.exceptions;

import javax.json.Json;
import javax.json.JsonObject;

public class MyException extends Exception {
    public MyException(String message) {
        super(message);
    }

//    public JsonObject convertJsonExceptionDB() {
//        return Json.createObjectBuilder().add("error", this.getMessage()).build();
//    }
//
//    public JsonObject convertJsonException() {
//        return Json.createObjectBuilder().add("error", this.getMessage()).build();
//    }

    public static JsonObject jsonExceptionBuild(String key, String value){
        return Json.createObjectBuilder().add(key, value).build();
    }
}
