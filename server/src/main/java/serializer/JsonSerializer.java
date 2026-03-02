package serializer;

import com.google.gson.Gson;

public class JsonSerializer {
    private static final Gson gson = new Gson();

    public static String toJson(Object obj) {
        return gson.toJson(obj);
    }

    public static <T> T fromJson(String json, Class<T> programClass) {
        return gson.fromJson(json, programClass);
    }
}
