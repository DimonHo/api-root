package com.wd.cloud.bse.service.transfrom.merge;

import java.util.List;

public class DefaultSelectRule<T> {

    public Object merge(List<T> dataList) {
        for (Object obj : dataList) {
            if (obj != null) {
                return obj;
            }
        }
        return null;
    }

}
