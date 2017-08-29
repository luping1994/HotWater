package net.suntrans.hotwater.utils;

import java.lang.reflect.Field;

/**
 * Created by Looney on 2017/8/29.
 */

public class Utils {
    /***
     21      * 设置私有成员变量的值
     22      *
     23      */
    public static void setValue(Object instance, String fileName, Object value)
            throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
        Field field = instance.getClass().getDeclaredField(fileName);
        field.setAccessible(true);
        field.set(instance, value);
    }
}
