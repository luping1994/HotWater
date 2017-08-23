package net.suntrans.hotwater.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Looney on 2017/7/1.
 */

public class ParseCMD {

    public static short[] flags ={1,2,4,8,16,32,64,128,256,512};


    public static Map<String,String>  check(short channel, short command){
        Map<String,String> result = new HashMap<>();
        for (int i =0;i<flags.length;i++){
            if((channel&flags[i])==flags[i]){
                String number =String.valueOf((i+1));
                String status = (command&flags[i])==flags[i]?"1":"0";
                result.put(number,status);
            }
        }
        return result;
    }
}
