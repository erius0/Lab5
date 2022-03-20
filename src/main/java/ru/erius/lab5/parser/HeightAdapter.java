package ru.erius.lab5.parser;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class HeightAdapter extends XmlAdapter<String, Integer> {

    @Override
    public Integer unmarshal(String v) throws Exception {
        if (v == null)
            return null;
        int result = Integer.parseInt(v);
        if (result <= 0)
            return 1;
        return result;
    }

    @Override
    public String marshal(Integer v) throws Exception {
        return v.toString();
    }
}
