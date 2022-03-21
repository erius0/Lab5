package ru.erius.lab5.parser;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public final class Adapters {

    public static class LocalDateAdapter extends XmlAdapter<String, LocalDate> {
        @Override
        public LocalDate unmarshal(String v) throws Exception {
            return LocalDate.parse(v);
        }

        @Override
        public String marshal(LocalDate v) throws Exception {
            return v.format(DateTimeFormatter.ISO_LOCAL_DATE);
        }
    }

    public static class CoordinateYAdapter extends XmlAdapter<String, Float> {
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

    public static class HeightAdapter extends XmlAdapter<String, Integer> {
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

    public static class NameAdapter extends XmlAdapter<String, String> {
        @Override
        public String unmarshal(String v) throws Exception {
            return v.isEmpty() ? "none" : v;
        }

        @Override
        public String marshal(String v) throws Exception {
            return v;
        }
    }

    public static class PassportAdapter extends XmlAdapter<String, String> {
        @Override
        public String unmarshal(String v) throws Exception {
            return v.length() < 8 ? "no_passport" : v;
        }

        @Override
        public String marshal(String v) throws Exception {
            return v;
        }
    }
}
