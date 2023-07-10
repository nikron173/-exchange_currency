package com.nikron.conversion.model;

import javax.json.Json;
import javax.json.JsonObject;

public class ExchangeVolute {
    private int id;
    private Currency base;
    private Currency target;
    private double rates;

    public ExchangeVolute(Currency base, Currency target, double rates){
        this.base = base;
        this.target = target;
        this.rates = rates;
    }

    @Override
    public String toString() {
        return "{" +
                "id=" + id + ",\n" +
                "base=" + base + ",\n" +
                "target=" + target + ",\n" +
                "rates=" + rates +
                '}';
    }

    public JsonObject convertJson(){
        return Json.createObjectBuilder().add("id", id)
                .add("base", base.convertJson())
                .add("target", target.convertJson())
                .add("rates", rates).build();
    }

    public ExchangeVolute(int id, Currency base, Currency target, double rates){
        this.id = id;
        this.base = base;
        this.target = target;
        this.rates = rates;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Currency getBase() {
        return base;
    }

    public void setBase(Currency base) {
        this.base = base;
    }

    public Currency getTarget() {
        return target;
    }

    public void setTarget(Currency target) {
        this.target = target;
    }

    public double getRates() {
        return rates;
    }

    public void setRates(double rates) {
        this.rates = rates;
    }
}
