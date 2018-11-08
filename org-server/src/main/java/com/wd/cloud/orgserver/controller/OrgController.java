package com.wd.cloud.orgserver.controller;

import com.wd.cloud.commons.model.ResponseModel;
import com.wd.cloud.orgserver.entity.OrgInfo;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

/**
 * @author He Zhigang
 * @date 2018/11/5
 * @Description:
 */
@RestController
@RequestMapping("/org")
public class OrgController {

    /**
     * 添加一个机构
     * @return
     */
    @PostMapping("/add")
    public ResponseModel addOrg(String orgName){
       return ResponseModel.ok();
    }

    /**
     * 删除一个机构
     * @param id
     * @return
     */
    @DeleteMapping("/remove/{id}")
    public ResponseModel removeOrg(Long id){
        return ResponseModel.ok();
    }

    /**
     * 修改一个机构
     * @param id
     * @param orgName
     * @param orgFlag
     * @return
     */
    @PatchMapping("/modify/{id}")
    public ResponseModel modifyOrg(@PathVariable Long id,
                                   @RequestParam String orgName,
                                   @RequestParam String orgFlag){
        return ResponseModel.ok();
    }

    /**
     * 获取机构信息
     * @param id
     * @return
     */
    @GetMapping("/get/{id}")
    public ResponseModel getOrg(Long id){
        return ResponseModel.ok();
    }

    /**
     * 分页获取所有机构
     * @return
     */
    @GetMapping("/all")
    public ResponseModel getAll(@PageableDefault(sort = {"gmtCreate"}, direction = Sort.Direction.DESC) Pageable pageable){
        return ResponseModel.ok();
    }

    /**
     * 获取IP所属机构
     * @param ip
     * @return
     */
    @GetMapping("/get")
    public ResponseModel<OrgInfo> getOrg(@RequestParam String ip){
        return ResponseModel.ok();
    }
}
