package ru.erius.lab5.parser;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class NameAdapter extends XmlAdapter<String, String> {
    @Override
    public String unmarshal(String v) throws Exception {
        return v.isEmpty() ? "none" : v;
    }

    @Override
    public String marshal(String v) throws Exception {
        return v;
    }
}
