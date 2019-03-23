package com.wd.cloud.uoserver.controller;

import com.wd.cloud.commons.model.ResponseModel;
import com.wd.cloud.uoserver.pojo.entity.Department;
import com.wd.cloud.uoserver.pojo.entity.IpRange;
import com.wd.cloud.uoserver.pojo.vo.DeptVO;
import com.wd.cloud.uoserver.pojo.vo.OrgIpVO;
import com.wd.cloud.uoserver.pojo.vo.OrgProductVO;
import com.wd.cloud.uoserver.pojo.vo.OrgVO;
import com.wd.cloud.uoserver.service.OrgService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Author: He Zhigang
 * @Date: 2019/3/19 11:20
 * @Description:
 */
@Slf4j
@RestController
public class OrgManagerController {

    @Autowired
    OrgService orgService;

    @ApiOperation(value = "检查错误IP", tags = {"IP维护"})
    @GetMapping("/manager/ip/validator-ip")
    public ResponseModel validatorIp() {
        List<IpRange> errorIpRanges = orgService.validatorIp();
        return ResponseModel.ok().setBody(errorIpRanges);
    }

    @ApiOperation(value = "翻转Ip起始和结束顺序", tags = {"IP维护"})
    @PatchMapping("/manager/ip/reverse")
    public ResponseModel reverse() {
        orgService.reverse();
        return ResponseModel.ok();
    }


    @ApiOperation(value = "查询重叠IP段", tags = {"IP维护"})
    @GetMapping("/manager/ip/overlay")
    public ResponseModel overlay() {
        orgService.overlay();
        return ResponseModel.ok();
    }

    @ApiOperation(value = "新增,修改机构", tags = {"机构管理"})
    @PostMapping("/manager/org")
    public ResponseModel addOrg(@RequestBody OrgVO org) {
        orgService.saveOrg(org);
        return ResponseModel.ok();
    }

    @ApiOperation(value = "新增/修改/删除机构IP", tags = {"机构管理"})
    @PostMapping("/manager/org/ip")
    public ResponseModel addOrgIp(@RequestParam String orgFlag,
                                  @RequestBody List<OrgIpVO> ip){
        List<IpRange> ipRangeList = orgService.saveOrgIp(orgFlag,ip);
        return ResponseModel.ok().setBody(ipRangeList);
    }


    @ApiOperation(value = "新增，修改，删除院系",tags = {"机构管理"})
    @PostMapping("/manager/org/department")
    public ResponseModel saveDepartmentId(@RequestParam String orgFlag,
                                          @RequestBody List<DeptVO> deptVo) {
        List<Department> departmentList = orgService.saveDept(orgFlag,deptVo);
        return ResponseModel.ok().setBody(departmentList);
    }

    @ApiOperation(value = "删除院系",tags = {"机构管理"})
    @DeleteMapping("/manager/org/department")
    public ResponseModel deleteDepartmentId(@RequestParam Long id) {
        orgService.deleteDepartmentId(id);
        return ResponseModel.ok().setMessage("删除成功");
    }

    @ApiOperation(value = "更改订购产品状态",tags = {"机构管理"})
    @PostMapping("/manager/org/product")
    public ResponseModel modifyOrgProduct(@RequestParam String orgFlag,
                                          @RequestBody List<OrgProductVO> product) {
        orgService.saveOrgProduct(orgFlag, product);
        return ResponseModel.ok().setMessage("更该成功");
    }

    @ApiOperation(value = "取消订购产品",tags = {"机构管理"})
    @DeleteMapping("/manager/org/product")
    public ResponseModel cancelOrgProduct(@RequestParam String orgFlag,
                                          @RequestBody(required = false) List<Long> productId) {
        orgService.cancelProduct(orgFlag, productId);
        return ResponseModel.ok().setMessage("删除成功");
    }

}
