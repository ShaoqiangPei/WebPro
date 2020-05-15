package com.other.util;


import android.text.TextUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtil {

    /**
     * 判断给定字符串是否空白串。 空白串是指由空格、制表符、回车符、换行符组成的字符串 若输入字符串为null或空字符串，返回true
     *
     * @param input
     * @return boolean
     */
    public static boolean isEmpty(String input) {
        if (TextUtils.isEmpty(input)) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isNotEmpty(String input) {
        return !isEmpty(input);
    }


}