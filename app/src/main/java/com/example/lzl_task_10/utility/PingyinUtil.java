package com.example.lzl_task_10.utility;

import android.text.TextUtils;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

public class PingyinUtil {
    public static String toPingyin(String chinese)
    {
        if (!TextUtils.isEmpty(chinese))
        {
            StringBuilder stringBuilder = new StringBuilder();
            char[] chars = chinese.toCharArray();
            HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
            format.setCaseType(HanyuPinyinCaseType.LOWERCASE);
            format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
            format.setVCharType(HanyuPinyinVCharType.WITH_V);
            for (int i = 0; i < chars.length; i++) {

                char word=chars[i];
                if (word>128)
                {
                    try {
                        stringBuilder.append(PinyinHelper.toHanyuPinyinStringArray(word,format)[0]+" ");

                    } catch (BadHanyuPinyinOutputFormatCombination badHanyuPinyinOutputFormatCombination) {
                        badHanyuPinyinOutputFormatCombination.printStackTrace();
                    }
                }
                else {
                    stringBuilder.append(word);
                }

            }
            return stringBuilder.toString();
        }
        return "";
    }
    public static String toPingyinFirstLetter(String chinese)
    {
        if (!TextUtils.isEmpty(chinese))
        {
            StringBuilder stringBuilder = new StringBuilder();
            String s = toPingyin(chinese);
            String[] split = s.split("\\s");
            for (int i = 0; i < split.length; i++) {
                stringBuilder.append(split[i].charAt(0));

            }
            return stringBuilder.toString();
        }
        return "";
    }
}
