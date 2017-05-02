package indi.yume.demo.newapplication.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import indi.yume.demo.newapplication.manager.api.base.IntegerTypeAdapter;
import lombok.experimental.UtilityClass;

/**
 * Created by yume on 16-7-21.
 */
@UtilityClass
public class ModelUtil {
    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(int.class, new IntegerTypeAdapter())
            .registerTypeAdapter(Integer.class, new IntegerTypeAdapter())
            .create();

    public static <S, T> T copyModel(S source, Class<T> classOfT) {
        return fromJson(toJson(source), classOfT);
    }

    public static String toJson(Object object) {
        return gson.toJson(object);
    }

    public static <T> T fromJson(String json, Class<T> classOfT) {
        return gson.fromJson(json, classOfT);
    }

    public static <T> T fromJson(String json, Type typeOfT) {
        return gson.fromJson(json, typeOfT);
    }

    public static <T> T fromJson(JsonReader json, Class<T> classOfT) {
        return gson.fromJson(json, classOfT);
    }

    public static <T> T fromJson(JsonReader json, Type typeOfT) {
        return gson.fromJson(json, typeOfT);
    }

    public static <T> T map2Object(Map<String, Object> map, Class<T> classOfT) {
        return fromJson(toJson(map), classOfT);
    }

    public static <T> T map2Object(Map<String, Object> map, Type classOfT) {
        return fromJson(toJson(map), classOfT);
    }

    public static Map<String, String> object2Map(Object object) {
        return fromJson(toJson(object), new TypeToken<Map<String, String>>(){}.getType());
    }

    public static boolean isEmpty(List<?> list) {
        return list == null || list.isEmpty();
    }

//    public static <T, K> boolean listEquals(List<T> list1, List<K> list2, Func2<T, K, Boolean> matcher) {
//        if(list1 == null
//                || list2 == null
//                || list1.size() != list2.size())
//            return false;
//
//        return Stream.zip(Stream.of(list1),
//                Stream.of(list2),
//                matcher::call)
//                .allMatch(t -> t);
//    }
}


