package com.wd.cloud.uoserver.controller;

import com.wd.cloud.uoserver.entity.IpRange;
import com.wd.cloud.uoserver.service.OrgService;
import com.wd.cloud.commons.dto.OrgDTO;
import com.wd.cloud.commons.model.ResponseModel;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author He Zhigang
 * @date 2019/3/4
 * @Description:
 */
@RestController
@RequestMapping("/")
public class OrgController {

    @Autowired
    OrgService orgService;

    @ApiOperation(value = "添加机构信息", tags = {"机构管理"})
    @PostMapping("/org")
    public ResponseModel addOrg() {
        return ResponseModel.ok();
    }

    /**
     * 查询机构
     *
     * @return
     * @sort 排序字段，默认为name
     */
    @ApiOperation(value = "获取机构信息", tags = {"机构查询"})
    @GetMapping("/org/{orgId}")
    public ResponseModel getOrg(@PathVariable Long orgId) {
        OrgDTO orgDTO = orgService.getOrg(orgId);
        return ResponseModel.ok().setBody(orgDTO);
    }

    /**
     * 查询机构
     *
     * @return
     * @sort 排序字段，默认为name
     */
    @ApiOperation(value = "获取机构信息", tags = {"机构查询"})
    @GetMapping("/org")
    public ResponseModel getOrg(@RequestParam(required = false) String orgName,
                                @RequestParam(required = false) String flag,
                                @RequestParam(required = false) String spisFlag,
                                @RequestParam(required = false) String eduFlag,
                                @RequestParam(required = false) String ip) {
        OrgDTO orgDTO = orgService.findOrg(orgName, flag, spisFlag, eduFlag, ip);
        return ResponseModel.ok().setBody(orgDTO);
    }

    /**
     * 查询机构
     *
     * @return
     * @sort 排序字段，默认为name
     */
    @ApiOperation(value = "分页获取所有机构信息", tags = {"机构查询"})
    @GetMapping("/org/page")
    public ResponseModel<Page> getOrg(@RequestParam(required = false) String orgName,
                                      @RequestParam(required = false) String flag,
                                      @RequestParam(required = false) String spisFlag,
                                      @RequestParam(required = false) String eduFlag,
                                      @RequestParam(required = false) String ip,
                                      @PageableDefault(sort = {"name"}, direction = Sort.Direction.ASC) Pageable pageable) {
        Page<OrgDTO> orgPages = orgService.likeOrg(orgName, flag, spisFlag, eduFlag, ip, pageable);
        return ResponseModel.ok().setBody(orgPages);
    }


    @ApiOperation(value = "检查错误IP", tags = {"机构管理"})
    @GetMapping("/org/validator-ip")
    public ResponseModel validatorIp() {
        List<IpRange> errorIpRanges = orgService.validatorIp();
        return ResponseModel.ok().setBody(errorIpRanges);
    }

    @ApiOperation(value = "翻转Ip起始和结束顺序", tags = {"机构管理"})
    @PatchMapping("/org/reverse")
    public ResponseModel reverse() {
        orgService.reverse();
        return ResponseModel.ok();
    }


    @ApiOperation(value = "查询重叠IP段", tags = {"机构管理"})
    @GetMapping("/org/overlay")
    public ResponseModel overlay() {
        orgService.overlay();
        return ResponseModel.ok();
    }
}
