package com.wd.cloud.uoserver.controller;

import com.wd.cloud.commons.exception.ParamException;
import com.wd.cloud.commons.model.ResponseModel;
import com.wd.cloud.uoserver.pojo.dto.OrgDTO;
import com.wd.cloud.uoserver.pojo.dto.OrgDeptDTO;
import com.wd.cloud.uoserver.service.OrgService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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


    @ApiOperation(value = "查找机构标识是否已经存在", tags = {"机构查询"})
    @ApiImplicitParams({
            @ApiImplicitParam(name = "flag", value = "机构标识", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "name", value = "机构名称", dataType = "String", paramType = "query")
    })
    @GetMapping("/org/exists")
    public ResponseModel<Boolean> findExistsFlag(@RequestParam(required = false) String flag,
                                                 @RequestParam(required = false) String name) {
        // 参数不能全部为空
        if (flag == null && name == null) {
            throw ParamException.notAllNull("flag", "name");
        }
        Boolean existsFlag = orgService.orgExists(flag, name);
        return ResponseModel.ok().setBody(existsFlag);
    }

    /**
     * 查询机构
     *
     * @return
     * @sort 排序字段，默认为name
     */
    @ApiOperation(value = "获取机构信息", tags = {"机构查询"})
    @ApiImplicitParams({
            @ApiImplicitParam(name = "name", value = "机构全称", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "flag", value = "机构标识", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "ip", value = "ip地址", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "include", value = "返回中包含哪些数据（ip,prod,cdb,linkman,dept）", dataType = "String", paramType = "query")
    })
    @GetMapping("/org")
    public ResponseModel getOrg(@RequestParam(required = false) String name,
                                @RequestParam(required = false) String flag,
                                @RequestParam(required = false) String ip,
                                @RequestParam(required = false) List<String> include) {
        OrgDTO orgDTO = orgService.findOrg(name, flag, ip,include);
        return ResponseModel.ok().setBody(orgDTO);
    }

    @ApiOperation(value = "获取机构列表", tags = {"机构查询"})
    @GetMapping("/org/list")
    public ResponseModel getOrgList() {
        return ResponseModel.ok().setBody(orgService.getOrgList());
    }

    /**
     * 查询机构
     *
     * @return
     * @sort 排序字段，默认为name
     */
    @ApiOperation(value = "分页获取所有机构信息", tags = {"机构查询"})
    @ApiImplicitParams({
            @ApiImplicitParam(name = "name", value = "机构名称（模糊）", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "flag", value = "机构标识", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "ip", value = "ip地址", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "prodStatus", value = "产品状态（0：停用，1：试用，2：购买）", dataType = "List", paramType = "query"),
            @ApiImplicitParam(name = "isExp", value = "产品是否过期", dataType = "Boolean", paramType = "query"),
            @ApiImplicitParam(name = "include", value = "返回中包含哪些数据（ip,prod,cdb,linkman,dept）", dataType = "String", paramType = "query")
    })
    @GetMapping("/org/query")
    public ResponseModel<Page> queryOrg(@RequestParam(required = false) String name,
                                        @RequestParam(required = false) String flag,
                                        @RequestParam(required = false) String ip,
                                        @RequestParam(required = false) List<Integer> prodStatus,
                                        @RequestParam(required = false) Boolean isExp,
                                        @RequestParam(required = false, defaultValue = "false") Boolean isFilter,
                                        @RequestParam(required = false) List<String> include,
                                        @PageableDefault(sort = {"name"}, direction = Sort.Direction.ASC) Pageable pageable) {
        Page<OrgDTO> orgPages = orgService.likeOrg(name, flag, ip, prodStatus, isExp, isFilter,include, pageable);
        return ResponseModel.ok().setBody(orgPages);
    }


    @ApiOperation(value = "根据学校查询学院", tags = {"机构查询"})
    @GetMapping("/org/dept")
    public ResponseModel<OrgDeptDTO> findByOrgFlag(@RequestParam String orgFlag) {
        List<OrgDeptDTO> byOrgFlag = orgService.findOrgDept(orgFlag);
        return ResponseModel.ok().setBody(byOrgFlag);
    }


}
