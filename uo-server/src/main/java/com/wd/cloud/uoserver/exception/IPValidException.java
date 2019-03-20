package com.wd.cloud.uoserver.exception;

import com.wd.cloud.commons.exception.ApiException;
import com.wd.cloud.uoserver.pojo.entity.IpRange;

import java.util.List;

/**
 * @Author: He Zhigang
 * @Date: 2019/3/19 13:57
 * @Description:
 */
public class IPValidException extends ApiException {

    public IPValidException(Integer status,String message) {
        super(status, message);
    }

    public IPValidException(Integer status, String message, List<IpRange> overlayIpRanges) {
        super(status, message,overlayIpRanges);
    }

    /**
     * Ip格式錯誤
     * @param ip
     * @return
     */
    public static IPValidException validNotIp(String ip){
        return new IPValidException(ExceptionStatus.NOT_IP,"["+ip+"]不是合法的IP地址");
    }

    /**
     * IP衝突
     * @param beginIp 开始IP
     * @param endIp 结束IP
     * @param overlayIpRanges 重叠的IP列表
     * @return
     */
    public static IPValidException existsIp(String beginIp,String endIp,List<IpRange> overlayIpRanges){
        return new IPValidException(ExceptionStatus.EXISTS_IP,"["+beginIp+"-"+endIp+"]范围已存在",overlayIpRanges);
    }

}

