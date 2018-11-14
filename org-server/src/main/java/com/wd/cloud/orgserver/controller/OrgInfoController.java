package com.wd.cloud.orgserver.controller;

import com.wd.cloud.commons.model.ResponseModel;
import com.wd.cloud.orgserver.entity.Org;
import com.wd.cloud.orgserver.service.OrgInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author He Zhigang
 * @date 2018/11/5
 * @Description:
 */
@RestController
@RequestMapping("/")
public class OrgInfoController {

    @Autowired
    OrgInfoService orgInfoService;

    /**
     * 添加一个机构
     *
     * @return
     */
    @PostMapping("/orginfo")
    public ResponseModel addOrg(@RequestParam String orgName) {
        return ResponseModel.ok();
    }

    /**
     * 删除一个机构
     *
     * @param id
     * @return
     */
    @DeleteMapping("/orginfo/{id}")
    public ResponseModel removeOrg(Long id) {
        return ResponseModel.ok();
    }

    /**
     * 修改一个机构
     *
     * @param id
     * @param orgName
     * @param orgFlag
     * @return
     */
    @PatchMapping("/orginfo/{id}")
    public ResponseModel modifyOrg(@PathVariable Long id,
                                   @RequestParam String orgName,
                                   @RequestParam String orgFlag) {
        return ResponseModel.ok();
    }

    /**
     * 获取机构信息
     *
     * @param id
     * @return
     */
    @GetMapping("/orginfo/{id}")
    public ResponseModel getOrg(@PathVariable Long id) {
        Org org = orgInfoService.get(id);
        if (org != null) {
            return ResponseModel.ok().setBody(org);
        }
        return ResponseModel.fail().setMessage("未找到对应的机构!");
    }

    /**
     * 获取所有机构
     *
     * @return
     * @sort 排序字段，默认为name
     */
    @GetMapping("/orginfo/all")
    public ResponseModel getAll(@RequestParam(required = false, defaultValue = "name") String sort) {
        List<Org> orgs = orgInfoService.getAllOrg(sort);
        return ResponseModel.ok().setBody(orgs);
    }

    /**
     * 获取IP所属机构
     *
     * @param ip
     * @return
     */
    @GetMapping("/orginfo/find")
    public ResponseModel<Org> getOrg(@RequestParam String ip) {
        return ResponseModel.ok();
    }
}
