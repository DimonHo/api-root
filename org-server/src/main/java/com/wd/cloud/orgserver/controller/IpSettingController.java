package com.wd.cloud.orgserver.controller;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.wd.cloud.commons.model.ResponseModel;
import com.wd.cloud.commons.util.IpUtil;
import com.wd.cloud.orgserver.entity.IpRange;
import com.wd.cloud.orgserver.entity.Org;
import com.wd.cloud.orgserver.service.IpSettingService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author He Zhigang
 * @date 2018/11/5
 * @Description:
 */
@RestController
@RequestMapping("/ip")
public class IpSettingController {

    private static final Log log = LogFactory.get();
    @Autowired
    IpSettingService ipSettingService;

    @ApiOperation(value = "根据IP获取机构名称", tags = {"获取IP"})
    @GetMapping("/getIpRang")
    public ResponseModel getIpRang(HttpServletRequest req) {
        String ip = IpUtil.getIpAddr(req);
        log.info("ip={}",ip);
        List<IpRange> all = ipSettingService.getAll();
        Org org = null;
        for (IpRange ipRange : all) {
            String beginIp = ipRange.getBegin();
            String endIp = ipRange.getEnd();
            boolean inner = IpUtil.isInner(ip, beginIp, endIp);
            if (inner == true) {
                log.info("begin={},end={}",beginIp,endIp);
                org = ipRange.getOrg();
                break;
            }
        }
        return ResponseModel.ok().setBody(org);
    }
}
