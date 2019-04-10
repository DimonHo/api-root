package com.wd.cloud.uoserver.controller;

import com.wd.cloud.commons.exception.AuthException;
import com.wd.cloud.commons.model.ResponseModel;
import com.wd.cloud.uoserver.pojo.entity.OrgDept;
import com.wd.cloud.uoserver.pojo.entity.OrgIp;
import com.wd.cloud.uoserver.pojo.vo.DeptVO;
import com.wd.cloud.uoserver.pojo.vo.OrgIpVO;
import com.wd.cloud.uoserver.pojo.vo.OrgProdVO;
import com.wd.cloud.uoserver.pojo.vo.OrgVO;
import com.wd.cloud.uoserver.service.OrgService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.net.UnknownHostException;
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
    public ResponseModel validatorIp(@RequestParam String username, @RequestParam String password) {
        if ("hezhigang".equals(username) && "hzg123".equals(password)) {
            List<OrgIp> errorOrgIps = orgService.validatorIp();
            return ResponseModel.ok().setBody(errorOrgIps);
        } else {
            throw new AuthException("用户名或密码不正确");
        }
    }

    @ApiOperation(value = "翻转Ip起始和结束顺序", tags = {"IP维护"})
    @PatchMapping("/manager/ip/reverse")
    public ResponseModel reverse(@RequestParam String username, @RequestParam String password) throws UnknownHostException {
        if ("hezhigang".equals(username) && "hzg123".equals(password)) {
            orgService.reverse();
            return ResponseModel.ok();
        } else {
            throw new AuthException("用户名或密码不正确");
        }

    }


    @ApiOperation(value = "查询重叠IP段", tags = {"IP维护"})
    @GetMapping("/manager/ip/overlay")
    public ResponseModel overlay(@RequestParam String username, @RequestParam String password) throws UnknownHostException {
        if ("hezhigang".equals(username) && "hzg123".equals(password)) {
            orgService.overlay();
            return ResponseModel.ok();
        } else {
            throw new AuthException("用户名或密码不正确");
        }
    }

    @ApiOperation(value = "新增,修改机构", tags = {"机构管理"})
    @PostMapping("/manager/org")
    public ResponseModel addOrg(@RequestBody OrgVO org) throws UnknownHostException {
        orgService.saveOrg(org);
        return ResponseModel.ok();
    }

    @ApiOperation(value = "新增/修改/删除机构IP", tags = {"机构管理"})
    @PostMapping("/manager/org/ip")
    public ResponseModel addOrgIp(@RequestParam String orgFlag,
                                  @RequestBody List<OrgIpVO> ip) throws UnknownHostException {
        List<OrgIp> orgIpList = orgService.saveOrgIp(orgFlag, ip);
        return ResponseModel.ok().setBody(orgIpList);
    }


    @ApiOperation(value = "新增，修改，删除院系", tags = {"机构管理"})
    @PostMapping("/manager/org/dept")
    public ResponseModel saveOrgDeptId(@RequestParam String orgFlag,
                                       @RequestBody List<DeptVO> deptVo) {
        List<OrgDept> orgDeptList = orgService.saveDept(orgFlag, deptVo);
        return ResponseModel.ok().setBody(orgDeptList);
    }

    @ApiOperation(value = "删除院系", tags = {"机构管理"})
    @DeleteMapping("/manager/org/dept")
    public ResponseModel deleteOrgDeptId(@RequestParam Long id) {
        orgService.deleteOrgDeptId(id);
        return ResponseModel.ok().setMessage("删除成功");
    }

    @ApiOperation(value = "更改订购产品状态", tags = {"机构管理"})
    @PostMapping("/manager/org/prod")
    public ResponseModel modifyOrgProd(@RequestParam String orgFlag,
                                       @RequestBody List<OrgProdVO> prod) {
        orgService.saveOrgProd(orgFlag, prod);
        return ResponseModel.ok().setMessage("更该成功");
    }

    @ApiOperation(value = "取消订购产品", tags = {"机构管理"})
    @DeleteMapping("/manager/org/prod")
    public ResponseModel cancelOrgProd(@RequestParam String orgFlag,
                                       @RequestBody(required = false) List<Long> prodId) {
        orgService.cancelProd(orgFlag, prodId);
        return ResponseModel.ok().setMessage("删除成功");
    }

}
