package com.wd.cloud.docdelivery.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.http.HttpUtil;
import com.wd.cloud.commons.exception.ParamException;
import com.wd.cloud.commons.model.ResponseModel;
import com.wd.cloud.docdelivery.pojo.entity.HelpRecord;
import com.wd.cloud.docdelivery.pojo.entity.Literature;
import com.wd.cloud.docdelivery.pojo.vo.HelpRequestVO;
import com.wd.cloud.docdelivery.service.HelpRequestService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

/**
 * @Author: He Zhigang
 * @Date: 2019/3/28 13:56
 * @Description:
 */
@Api(value = "求助controller", tags = {"文献求助"})
@RestController
@RequestMapping("/")
public class HelpController {

    @Autowired
    HttpServletRequest request;
    @Autowired
    HelpRequestService helpRequestService;

    @PostMapping("/help")
    public ResponseModel helpRequest(@Valid @RequestBody HelpRequestVO helpRequestVO, Errors errors) {
        if (errors.hasErrors()) {
            throw new ParamException("参数错误");
        }
        Literature literature = BeanUtil.toBean(helpRequestVO.getLiterature(), Literature.class);
        HelpRecord helpRecord = BeanUtil.toBean(helpRequestVO.getHelper(), HelpRecord.class);
        String ip = HttpUtil.getClientIP(request);
        helpRecord.setHelperIp(ip).setSend(true);
        helpRequestService.helpRequest(literature, helpRecord);
        return ResponseModel.ok();
    }
}
