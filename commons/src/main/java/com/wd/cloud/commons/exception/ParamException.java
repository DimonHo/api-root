package com.wd.cloud.commons.exception;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ArrayUtil;
import com.wd.cloud.commons.enums.StatusEnum;

import java.util.List;

/**
 * @Author: He Zhigang
 * @Date: 2019/3/19 17:49
 * @Description: 请求参数异常
 */
public class ParamException extends ApiException{

    public ParamException(String message){
        super(StatusEnum.BAD_REQUEST.value(),message);
    }

    /**
     * 参数不能全部为空
     * @param params 参数名称列表
     * @return
     */
    public static ParamException notAllNull(String... params){
        String message = String.format("%s不能全部为空", ArrayUtil.join(params,","));
        return new ParamException(message);
    }

    /**
     * 参数不能全部为空
     * @param param 参数名称
     * @return
     */
    public static ParamException notNull(String param){
        String message = String.format("%s不能为空", param);
        return new ParamException(message);
    }

    /**
     * 参数限定取值
     * @param param 参数名称
     * @param enums 限定值列表
     * @param <T>
     * @return
     */
    public static <T> ParamException notValid(String param, List<T> enums){
        String message = String.format("%s只能是[%s]中的值",param, CollectionUtil.join(enums,","));
        return new ParamException(message);
    }
}
