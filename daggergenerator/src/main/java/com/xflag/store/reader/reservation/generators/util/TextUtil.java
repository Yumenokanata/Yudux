package indi.yume.demo.newapplication.generators.util;

/**
 * Created by yume on 15/9/27.
 */
public class TextUtil {
    public static String firstToLower(String s){
        return s.substring(0, 1).toLowerCase() + s.substring(1);
    }

    public static String firstToUpper(String s){
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }
}
