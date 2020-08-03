package moe.plushie.armourers_workshop.utils;

import java.util.ArrayList;

public class ArrayUtils {

    public static String[] explode(String value, String spliter) {
        return value.split(spliter);
    }

    public static String implode(String[] value, String spliter) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < value.length; i++) {
            sb.append(value[i]);
            if (i != value.length - 1) {
                sb.append(spliter);
            }
        }
        return sb.toString();
    }

    public static <T> ArrayList<T> removeDuplicates(ArrayList<T> arrayList) {
        ArrayList<T> newList = new ArrayList<T>();
        for (T listItem : arrayList) {
            if (!newList.contains(listItem)) {
                newList.add(listItem);
            }
        }
        return newList;
    }
}
