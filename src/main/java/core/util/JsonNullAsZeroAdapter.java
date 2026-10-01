package core.util;

import com.google.gson.*;
import java.lang.reflect.Type;

/**
 * Custom json serializer for Integer that writes null as 0
 */
public class JsonNullAsZeroAdapter implements JsonSerializer<Integer>, JsonDeserializer<Integer> {

    @Override
    public JsonElement serialize(Integer src, Type typeOfSrc, JsonSerializationContext context) {
        // If null, write 0
        return new JsonPrimitive(src == null ? 0 : src);
    }

    @Override
    public Integer deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
        throws JsonParseException {
        // If null or not a number, return null or 0 depending on your needs
        if (json == null || json.isJsonNull()) {
            return null; // or return 0 if you want default on read
        }
        try {
            return json.getAsInt();
        } catch (NumberFormatException e) {
            throw new JsonParseException("Invalid number format", e);
        }
    }
}
