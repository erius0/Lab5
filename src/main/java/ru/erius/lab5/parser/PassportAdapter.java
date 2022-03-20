package ru.erius.lab5.parser;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class PassportAdapter extends XmlAdapter<String, String> {

    @Override
    public String unmarshal(String v) throws Exception {
        return v.length() < 8 ? "no_passport" : v;
    }

    @Override
    public String marshal(String v) throws Exception {
        return v;
    }
}
