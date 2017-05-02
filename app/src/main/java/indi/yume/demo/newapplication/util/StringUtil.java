package indi.yume.demo.newapplication.util;

import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Html;
import android.text.TextUtils;

import com.annimon.stream.Optional;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.Normalizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by yume on 16/1/13.
 */
public class StringUtil {
    /**
     * Gets string from assets file.
     *
     * @param resources the resources
     * @param fileName  the assets file name
     * @return the string from assets file
     * @throws IOException the io exception
     */
    public static String getStringFromAssets(Resources resources, String fileName) throws IOException {
        return StringUtil.inputStream2String(resources.getAssets().open(fileName));
    }

    /**
     * Input stream to string.
     *
     * @param is the InputStream
     * @return the string from InputStream
     */
    public static String inputStream2String(InputStream is) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            LogUtil.e(e);
            return "";
        }

        StringBuilder sb = new StringBuilder();

        try {
            int c;
            while ((c = reader.read()) != -1) {
                sb.append((char) c);
            }
        } catch (IOException e) {
            LogUtil.e(e);
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                LogUtil.e(e);
            }
        }

        return sb.toString();
    }

    private static final char HEX_DIGITS[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            'a', 'b', 'c', 'd', 'e', 'f'};

    /**
     * Get string's md5 string.
     *
     * @param str the origin string
     * @return the string's MD5
     */
    public static String getMD5String(String str) {
        MessageDigest md5;
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
        md5.update(str.getBytes());
        return convertToHexString(md5.digest());
    }

    private static String convertToHexString(byte[] b) {
        StringBuilder sb = new StringBuilder(b.length * 2);
        for (byte a : b) {
            sb.append(HEX_DIGITS[(a & 0xf0) >>> 4]);
            sb.append(HEX_DIGITS[a & 0x0f]);
        }
        return sb.toString();
    }

    /**
     * Separate string by separator.
     * <p>
     * eg: separator = "_"
     * <ul>
     * <li>someFieldName ---> some_Field_Name</li>
     * <li>_someFieldName ---> _some_Field_Name</li>
     * <li>aStringField ---> a_String_Field</li>
     * <li>aURL ---> a_U_R_L</li>
     * </ul>
     *
     * @param string    the origin string
     * @param separator the separator
     * @return the separate string.
     */
    public static String separateCamelCase(String string, String separator) {
        StringBuilder translation = new StringBuilder();
        char oldChar = 0;
        for (int i = 0; i < string.length(); i++) {
            char character = string.charAt(i);
            String s = string.substring(0, i);
            if (!s.endsWith(separator) && Character.isUpperCase(character) && translation.length() != 0) {
                translation.append(separator);
            }
            translation.append(character);
            oldChar = character;
        }
        return translation.toString();
    }

    /**
     * Upper case first letter string.
     * <p>
     * <ul>
     * <li>someFieldName ---> SomeFieldName</li>
     * <li>_someFieldName ---> _SomeFieldName</li>
     * </ul>
     *
     * @param string the string
     * @return the string
     */
    public static String upperCaseFirstLetter(String string) {
        StringBuilder fieldNameBuilder = new StringBuilder();
        int index = 0;
        char firstCharacter = string.charAt(index);

        while (index < string.length() - 1) {
            if (Character.isLetter(firstCharacter)) {
                break;
            }

            fieldNameBuilder.append(firstCharacter);
            firstCharacter = string.charAt(++index);
        }

        if (index == string.length()) {
            return fieldNameBuilder.toString();
        }

        if (!Character.isUpperCase(firstCharacter)) {
            String modifiedTarget = modifyString(Character.toUpperCase(firstCharacter), string, ++index);
            return fieldNameBuilder.append(modifiedTarget).toString();
        } else {
            return string;
        }
    }

    private static String modifyString(char firstCharacter, String srcString, int indexOfSubstring) {
        return (indexOfSubstring < srcString.length())
                ? firstCharacter + srcString.substring(indexOfSubstring)
                : String.valueOf(firstCharacter);
    }

    public static boolean convertBoolean(@Nullable String string) {
        if (string == null) {
            return false;
        }
        return string.equals("true") || string.equals("1");
    }

    /**
     * 半角 -> 全角
     */
    public static String convertToFullWidth(@NonNull String target) {
        return Normalizer.normalize(target, Normalizer.Form.NFKC);
    }

    /**
     * カタカナ以外の文字を排除する
     */
    public static String removeNotKatakana(@NonNull String target) {
        return target.replaceAll("[^ァ-ー]+", "");
    }

    /**
     * 引数に渡した文字列を全角カタカナのみの文字列に変換する
     */
    public static String convertToKatakana(@NonNull String target) {
        if (target.isEmpty()) {
            return target;
        }

        StringBuilder builder = new StringBuilder(convertToFullWidth(target));
        for (int i = 0; i < builder.length(); i++) {
            char c = builder.charAt(i);
            if (c >= 'ぁ' && c <= 'ん') {
                builder.setCharAt(i, (char) (c - 'ぁ' + 'ァ'));
            }
        }
        return removeNotKatakana(builder.toString());
    }

    public static String replaceSpace(String ori, String target) {
        Matcher matcher = Pattern.compile("([\\s\\p{Zs}]+)").matcher(ori);
        return matcher.replaceAll(target);
    }

    @NonNull
    public static CharSequence fromHtml(String text) {
        return Optional.ofNullable(text)
                .<CharSequence>map(Html::fromHtml)
                .orElse("");
    }

    public static int parseInt(String string, int defaultV) {
        try {
            return Integer.valueOf(string);
        } catch (NumberFormatException e) {
            return defaultV;
        }
    }

    public static int parseInt(String string) {
        return parseInt(string, 0);
    }

    public static CharSequence maxLength(CharSequence string, int length) {
        if (string != null && string.length() > length)
            return string.subSequence(0, length) + "...";
        return string;
    }

    /**
     * remove whitespace.
     */

    public static String trim(String s) {
        String result = "";
        if (null != s && !"".equals(s)) {
            result = s.replaceAll("^[　*| *| *|//s*]*", "").replaceAll("[　*| *| *|//s*]*$", "");
        }
        return result;
    }

    /**
     *  add thousandBit for num
     *  eg: 1000 - > 1,000
     */
    public static String addThousandBit(int num) {
        return String.format("%,d", num);
    }

    public static String addThousandBit(String num) {
        return String.format("%,d", parseInt(num, 0));
    }

    /**
     * Convert number string with separator.
     * eg: "12345" -> "12,345"
     */
    public static String getNumWithSeparator(CharSequence number) {
        return getNumWithSeparator(number, "");
    }

    /**
     * Convert number string with separator.
     * eg: "12345" -> "12,345"
     *
     * @param emptyText: if number string is empty will return this.
     */
    public static String getNumWithSeparator(CharSequence number, String emptyText) {
        if(TextUtils.isEmpty(number)) {
            return emptyText;
        } else {
            try {
                int num = Integer.parseInt(number.toString());
                return String.format("%,d", num);
            } catch (Exception e) {
                return number.toString();
            }
        }
    }

    /**
     * Delete prefix "0" for int string.
     * eg: "0010" to "10"
     */
    public static String delPreInt(String ints) {
        try {
            return String.valueOf(Integer.parseInt(ints));
        } catch (Exception e) {
            return ints;
        }
    }

    public static String jobIdWithoutJ(String id) {
        return id.replace("J", "");
    }

    public static String jobIdWithJ(String id) {
        return "J" + jobIdWithoutJ(id);
    }
}
