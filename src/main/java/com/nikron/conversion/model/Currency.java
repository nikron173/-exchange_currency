package com.nikron.conversion.model;

import javax.json.Json;
import javax.json.JsonObject;

public class Currency {
    private int id; //Айди валюты, автоинкремент, первичный ключ
    private String code; //Код валюты
    private String fullName; //Полное имя валюты
    private String sing; //Символ валюты

    public Currency(String code, String fullName, String sing){
        this.code = code;
        this.fullName = fullName;
        this.sing = sing;
    }

    public Currency(int id, String code, String fullName, String sing){
        this.id = id;
        this.code = code;
        this.fullName = fullName;
        this.sing = sing;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getSing() {
        return sing;
    }

    public void setSing(String sing) {
        this.sing = sing;
    }

    @Override
    public String toString() {
        return "{" +
                "id=" + id + ",\n" +
                "code='" + code + '\'' + ",\n" +
                "fullName='" + fullName + '\'' + ",\n" +
                "sing='" + sing + '\'' + "\n" +
                '}' + "\n";
    }

    public JsonObject convertJson(){
        return Json.createObjectBuilder().add("id", id)
                .add("code", code)
                .add("name", fullName)
                .add("sing", sing).build();
    }
}
