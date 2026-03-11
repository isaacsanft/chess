package serializer;

import com.google.gson.Gson;

public class JsonSerializer {
    private static final Gson GSON = new Gson();

    public static String toJson(Object obj) {
        return GSON.toJson(obj);
    }

    public static <T> T fromJson(String json, Class<T> programClass) {
        return GSON.fromJson(json, programClass);
    }
}
