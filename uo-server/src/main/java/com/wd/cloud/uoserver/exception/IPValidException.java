package com.wd.cloud.uoserver.exception;

import com.wd.cloud.commons.exception.ApiException;
import com.wd.cloud.uoserver.pojo.entity.OrgIp;

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

    public IPValidException(Integer status, String message, List<OrgIp> overlayOrgIps) {
        super(status, message,overlayOrgIps);
    }

    public IPValidException(Integer status, String message, OrgIp overlayOrgIp) {
        super(status, message, overlayOrgIp);
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
     * @param overlayOrgIps 重叠的IP列表
     * @return
     */
    public static IPValidException existsIp(String beginIp,String endIp,List<OrgIp> overlayOrgIps){
        return new IPValidException(ExceptionStatus.EXISTS_IP,"["+beginIp+"-"+endIp+"]范围已存在",overlayOrgIps);
    }

    public static IPValidException existsIp(String beginIp, String endIp, OrgIp overlayOrgIp) {
        return new IPValidException(ExceptionStatus.EXISTS_IP, "[" + beginIp + "-" + endIp + "]范围已存在", overlayOrgIp);
    }

}

