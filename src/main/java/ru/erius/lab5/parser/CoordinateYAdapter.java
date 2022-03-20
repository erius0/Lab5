package ru.erius.lab5.parser;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class CoordinateYAdapter extends XmlAdapter<String, Float> {
    @Override
    public Float unmarshal(String v) throws Exception {
        float result = Float.parseFloat(v);
        return result > -816F ? result : 0F;
    }

    @Override
    public String marshal(Float v) throws Exception {
        return v.toString();
    }
}
