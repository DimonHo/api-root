package com.wd.cloud.uoserver.controller;

import com.sun.org.apache.xpath.internal.operations.Bool;
import com.wd.cloud.commons.dto.CdbDTO;
import com.wd.cloud.commons.dto.DepartmentDTO;
import com.wd.cloud.uoserver.entity.Department;
import com.wd.cloud.uoserver.entity.IpRange;
import com.wd.cloud.uoserver.entity.Org;
import com.wd.cloud.uoserver.service.OrgService;
import com.wd.cloud.commons.dto.OrgDTO;
import com.wd.cloud.commons.model.ResponseModel;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;
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

    @ApiOperation(value = "根据学校跟ip查询学校信息")
    @GetMapping("/findByNameAndIp")
    public ResponseModel<Page<CdbDTO>> findByNameAndIp(@RequestParam(required = false) String orgName,
                                                       @RequestParam(required = false) String ip,
                                                       @PageableDefault(sort = {"name"}, direction = Sort.Direction.ASC) Pageable pageable) {
        Page<OrgDTO> orgProductDTO = orgService.findByNameAndIp(pageable, orgName, ip);
        return ResponseModel.ok().setBody(orgProductDTO);
    }
    @ApiOperation(value = "根据状态查询学校信息")
    @GetMapping("/findByStatus")
    public ResponseModel<Page<CdbDTO>> findByStatus(@RequestParam(required = false) Integer status,
                                                       @PageableDefault() Pageable pageable) {

        Page<OrgDTO> orgProductDTO = orgService.findByStatus(status, pageable);
        return ResponseModel.ok().setBody(orgProductDTO);
    }

    @ApiOperation(value = "查询所有过期产品")
    @GetMapping("/notFindByStatus")
    public ResponseModel<Page<CdbDTO>> notFindByStatus(@PageableDefault() Pageable pageable) {

        Page<OrgDTO> orgProductDTO = orgService.notFindByStatus(pageable);
        return ResponseModel.ok().setBody(orgProductDTO);
    }

    @ApiOperation(value = "学校详细信息")
    @GetMapping("/findByOrgNameDetail")
    public ResponseModel<OrgDTO> findByOrgNameDetail(@RequestParam(required = false) Long id){
        OrgDTO byOrgNameDetail = orgService.findByOrgNameDetail(id);
        return ResponseModel.ok().setBody(byOrgNameDetail);
    }



    /**
     * 修改机构基本信息
     */
    @ApiOperation(value = "修改机构基本信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "学校ID", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "orgName",value = "学校名称" , dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "flag",value = "学校标识" , dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "province",value = "省份" , dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "city", value = "地区", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "name", value = "联系人", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "email", value = "邮箱", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "phone", value = "联系电话", dataType = "String", paramType = "query")
    })
    @PostMapping("/updateOrgAndLinkman")
    public ResponseModel updateOrgAndLinkman(@RequestParam(required = false) Long id,
                                             @RequestParam(required = false) String orgName,
                                             @RequestParam(required = false) String flag,
                                             @RequestParam(required = false) String province,
                                             @RequestParam(required = false) String city,
                                             @RequestParam(required = false) String name,
                                             @RequestParam(required = false) String email,
                                             @RequestParam(required = false) String phone) {
        orgService.updateOrgAndLinkman(id, orgName, flag, province, city, name, email, phone);
        return ResponseModel.ok().setMessage("修改成功");
    }



    @ApiOperation(value = "根据学校新增产品")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "orgId", value = "学校ID", dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "productId",value = "产品ID" , dataType = "Long", paramType = "query"),
            @ApiImplicitParam(name = "beginDate",value = "开始时间" , dataType = "Date", paramType = "query"),
            @ApiImplicitParam(name = "endDate",value = "结束时间" , dataType = "Date", paramType = "query"),
            @ApiImplicitParam(name = "status", value = "状态", dataType = "Integer", paramType = "query"),
            @ApiImplicitParam(name = "single", value = "是否独立购买", dataType = "Boolean", paramType = "query")
    })
    @PostMapping("/insertOrgAndProduct")
    public ResponseModel insertOrgAndProduct(@RequestParam(required = false) Long orgId,
                                             @RequestParam(required = false) Long productId,
                                             @RequestParam(required = false) Date beginDate,
                                             @RequestParam(required = false) Date endDate,
                                             @RequestParam(required = false) Integer status,
                                             @RequestParam(required = false) Boolean single) {
        orgService.insertOrgAndProduct(orgId,productId,beginDate,endDate,status,single);
        return ResponseModel.ok().setMessage("新增成功");
    }




    @ApiOperation(value = "根据学校批量新增IP")
    @PostMapping("/insertOrgAndIpRangeS")
    public ResponseModel insertOrgAndIpRangeS(@RequestParam(required = false) Long orgId,
                                             @RequestParam(required = false) String beginAndEnd){
        orgService.insertOrgAndIpRangeS(orgId,beginAndEnd);
        return ResponseModel.ok().setMessage("新增成功");
    }

    @ApiOperation(value = "根据学校删除产品")
    @PostMapping("/deleteProductOrgId")
    public ResponseModel deleteProductOrgId(@RequestParam(required = false) Long orgId) {
        orgService.deleteProductOrgId(orgId);
        return ResponseModel.ok().setMessage("删除成功");
    }

    @ApiOperation(value = "根据学校id删除IP")
    @PostMapping("/deleteIpRangeOrgId")
    public ResponseModel deleteIpRangeOrgId(@RequestParam(required = false) Long orgId){
        orgService.deleteIpRangeOrgId(orgId);
        return ResponseModel.ok().setMessage("删除成功");
    }

    @ApiOperation(value = "新增学校基本信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "orgName",value = "学校名称" , dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "flag",value = "学校标识" , dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "province",value = "省份" , dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "city", value = "地区", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "name", value = "联系人", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "email", value = "邮箱", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "phone", value = "联系电话", dataType = "String", paramType = "query")
    })
    @PostMapping("/insertOrgAndLinkman")
    public ResponseModel<Org> insertOrgAndLinkman(@RequestParam(required = false) String orgName,
                                             @RequestParam(required = false) String flag,
                                             @RequestParam(required = false) String province,
                                             @RequestParam(required = false) String city,
                                             @RequestParam(required = false) String name,
                                             @RequestParam(required = false) String email,
                                             @RequestParam(required = false) String phone) {
        Org org = orgService.insertOrgAndLinkman(orgName, flag, province, city, name, email, phone);
        return ResponseModel.ok().setBody(org);
    }

    @ApiOperation(value = "检测IP是否存在")
    @GetMapping("/findIpRangesExist")
    public ResponseModel<Boolean> findIpRangesExist(@RequestParam(required = false) String begin,
                                             @RequestParam(required = false) String end) {
        Boolean ipRangesExist = orgService.findIpRangesExist(begin, end);
        return ResponseModel.ok().setBody(ipRangesExist);
    }

    @ApiOperation(value = "查找机构标识是否已经存在")
    @GetMapping("/findExistsFlag")
    public ResponseModel<Boolean> findExistsFlag(@RequestParam(required = false) String flag) {
        Boolean existsFlag = orgService.findExistsFlag(flag);
        return ResponseModel.ok().setBody(existsFlag);
    }

    @ApiOperation(value = "检测机构名是否存在")
    @GetMapping("/findOrgNameExist")
    public ResponseModel<Boolean> findOrgNameExist(@RequestParam(required = false) String name) {
        Boolean orgNameExist = orgService.findOrgNameExist(name);
        return ResponseModel.ok().setBody(orgNameExist);
    }

    @ApiOperation(value = "根据学校查询学校信息")
    @GetMapping("/findByName")
    public ResponseModel<Org> findByName(@RequestParam(required = false) String name) {
        Org byName = orgService.findByName(name);
        return ResponseModel.ok().setBody(byName);
    }

}
