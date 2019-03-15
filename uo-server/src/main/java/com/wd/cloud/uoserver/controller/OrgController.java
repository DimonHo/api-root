package com.wd.cloud.uoserver.controller;

import com.wd.cloud.commons.dto.CdbDTO;
import com.wd.cloud.commons.dto.DepartmentDTO;
import com.wd.cloud.uoserver.entity.Department;
import com.wd.cloud.uoserver.entity.IpRange;
import com.wd.cloud.uoserver.entity.Org;
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

    @ApiOperation(value = "根据学校查询学院")
    @GetMapping("/findByOrgId")
    public ResponseModel<DepartmentDTO> findByOrgId(@RequestParam(required = false) Long orgId) {
        List<DepartmentDTO> byOrgId = orgService.findByOrgId(orgId);
        return ResponseModel.ok().setBody(byOrgId);
    }

    @ApiOperation(value = "根据ID获取学校信息")
    @GetMapping("/getOrgId")
    public ResponseModel<Org> getOrgId(@RequestParam(required = false) Long orgId) {
        Org org = orgService.getOrgId(orgId);
        return ResponseModel.ok().setBody(org);
    }

    @ApiOperation(value = "新增院系")
    @PostMapping("/insertDepartment")
    public ResponseModel<Department> insertDepartment(@RequestParam(required = false) Long orgId,
                                                      @RequestParam(required = false) String name) {
        orgService.insertDepartment(orgId, name);
        return ResponseModel.ok().setMessage("新增院系成功");
    }


    @ApiOperation(value = "修改院系")
    @PostMapping("/updateDepartment")
    public ResponseModel<Department> updateDepartment(@RequestParam(required = false) Long id,
                                                      @RequestParam(required = false) Long orgId,
                                                      @RequestParam(required = false) String name) {
        orgService.updateDepartment(id, orgId, name);

        return ResponseModel.ok().setMessage("修改院系成功");
    }

    @ApiOperation(value = "删除院系")
    @PostMapping("/deleteDepartmentId")
    public ResponseModel deleteDepartmentId(@RequestParam(required = false) Long id) {
        orgService.deleteDepartmentId(id);
        return ResponseModel.ok().setMessage("删除成功");
    }

    @ApiOperation(value = "学校基本信息")
    @GetMapping("/findByNameAndIp")
    public ResponseModel<Page<CdbDTO>> findByNameAndIp(@RequestParam(required = false) String orgName,
                                                       @RequestParam(required = false) String ip,
                                                       @PageableDefault(sort = {"name"}, direction = Sort.Direction.ASC) Pageable pageable) {
        Page<OrgDTO> orgProductDTO = orgService.findByNameAndIp(pageable, orgName, ip);
        return ResponseModel.ok().setBody(orgProductDTO);
    }


}
