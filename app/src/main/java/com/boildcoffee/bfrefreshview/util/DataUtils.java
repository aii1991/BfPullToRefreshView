package com.boildcoffee.bfrefreshview.util;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zjh
 *         2019/3/11
 */

public class DataUtils {
    public static List<String> generateData(int start, int end){
        final List<String> data = new ArrayList<>();
        for (int i=start; i<end; i++){
            data.add("i="+i);
        }
        return data;
    }
}
