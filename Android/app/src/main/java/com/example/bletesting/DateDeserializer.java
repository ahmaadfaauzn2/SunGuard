package com.example.bletesting;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonDeserializationContext;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateDeserializer implements JsonDeserializer<Date> {
    private static final String DATE_FORMAT = "EEE, dd MMM yyyy HH:mm:ss Z"; // Adjusted to match the expected format

    @Override
    public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        try {
            String dateString = json.getAsString();
            return new SimpleDateFormat(DATE_FORMAT, Locale.US).parse(dateString);
        } catch (Exception e) {
            throw new JsonParseException("Could not parse date: " + json.getAsString(), e);
        }
    }
}
