package ru.erius.lab5.parser;

import ru.erius.lab5.data.Color;
import ru.erius.lab5.data.Coordinates;
import ru.erius.lab5.data.Country;
import ru.erius.lab5.data.Location;
import ru.erius.lab5.util.UtilFunctions;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.Locale;

public final class Adapters {

    public final static LocalDate DEFAULT_DATE = LocalDate.of(1970, 1, 1);
    public final static int DEFAULT_HEIGHT = 150;
    public final static Color DEFAULT_COLOR = Color.BLACK;
    public final static Country DEFAULT_COUNTRY = Country.UNITED_KINGDOM;
    public final static String DEFAULT_NAME = "name",
                                DEFAULT_PASSPORT = "passport";
    public final static int DEFAULT_COORDINATE = 0;
    public final static Coordinates DEFAULT_COORDINATES = new Coordinates(DEFAULT_COORDINATE, DEFAULT_COORDINATE);
    public final static Location DEFAULT_LOCATION = new Location(DEFAULT_COORDINATE, DEFAULT_COORDINATE, DEFAULT_COORDINATE, DEFAULT_NAME);

    public static class LocalDateAdapter extends XmlAdapter<String, LocalDate> {
        @Override
        public LocalDate unmarshal(String v) throws Exception {
            try {
                return LocalDate.parse(v);
            } catch (DateTimeParseException e) {
                System.err.printf("Не удалось преобразовать %s в тип LocalDate, используем значение по умолчанию %s\n", v, DEFAULT_DATE);
                return DEFAULT_DATE;
            }
        }

        @Override
        public String marshal(LocalDate v) throws Exception {
            return v.format(DateTimeFormatter.ISO_LOCAL_DATE);
        }
    }

    public static class CoordinateYAdapter extends XmlAdapter<String, Float> {
        @Override
        public Float unmarshal(String v) throws Exception {
            float result;
            try {
                result  = Float.parseFloat(v);
            } catch (NumberFormatException e) {
                return (float) DEFAULT_COORDINATE;
            }
            if (result <= -816F) {
                System.err.printf("Координата Y %s не может быть меньше или равна -816, используем значение по умолчанию %s\n", v, DEFAULT_COORDINATE);
                return (float) DEFAULT_COORDINATE;
            }
            return result;
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
            int result;
            try {
                result = Integer.parseInt(v);
            } catch (NumberFormatException e) {
                return DEFAULT_HEIGHT;
            }
            if (result <= 0) {
                System.err.printf("Рост %s не может быть меньше или равен 0, используем значение по умолчанию %s\n", v, DEFAULT_HEIGHT);
                return DEFAULT_HEIGHT;
            }
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
            if (v.isEmpty()) {
                System.err.printf("Имя не может быть пустым, используем значение по умолчанию %s\n", DEFAULT_NAME);
                return DEFAULT_NAME;
            }
            return v;
        }

        @Override
        public String marshal(String v) throws Exception {
            return v;
        }
    }

    public static class PassportAdapter extends XmlAdapter<String, String> {
        @Override
        public String unmarshal(String v) throws Exception {
            if (v.length() < 8) {
                System.err.printf("Номер паспорта %s должен быть как минимум 8 символов в длину, используем значение по умолчанию %s\n", v, DEFAULT_PASSPORT);
                return DEFAULT_PASSPORT;
            }
            return v;
        }

        @Override
        public String marshal(String v) throws Exception {
            return v;
        }
    }

    public static class ColorAdapter extends XmlAdapter<String, Color> {
        @Override
        public Color unmarshal(String v) throws Exception {
            Color color = UtilFunctions.enumOrNull(v.toUpperCase(Locale.ROOT), Color.class);
            if (color == null) {
                System.err.printf("Цвет глаз %s не соответствует одному из этих вариантов - %s, используем значение по умолчанию %s\n", v, Arrays.toString(Color.values()), DEFAULT_COLOR);
                return DEFAULT_COLOR;
            }
            return color;
        }

        @Override
        public String marshal(Color v) throws Exception {
            return v.toString();
        }
    }

    public static class CountryAdapter extends XmlAdapter<String, Country> {
        @Override
        public Country unmarshal(String v) throws Exception {
            Country country = UtilFunctions.enumOrNull(v.toUpperCase(Locale.ROOT), Country.class);
            if (country == null) {
                System.err.printf("Национальность %s не соответствует одному из этих вариантов - %s, используем значение по умолчанию %s\n", v, Arrays.toString(Country.values()), DEFAULT_COUNTRY);
                return DEFAULT_COUNTRY;
            }
            return country;
        }

        @Override
        public String marshal(Country v) throws Exception {
            return v.toString();
        }
    }
}
