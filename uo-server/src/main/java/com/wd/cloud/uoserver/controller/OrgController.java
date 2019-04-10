package com.wd.cloud.uoserver.controller;

import cn.hutool.core.util.StrUtil;
import com.wd.cloud.commons.exception.ParamException;
import com.wd.cloud.commons.model.ResponseModel;
import com.wd.cloud.uoserver.exception.NotFoundOrgException;
import com.wd.cloud.uoserver.pojo.dto.OrgDTO;
import com.wd.cloud.uoserver.pojo.dto.OrgDeptDTO;
import com.wd.cloud.uoserver.pojo.entity.OrgIp;
import com.wd.cloud.uoserver.service.OrgService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

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

    @Autowired
    RedisTemplate redisTemplate;


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
            @ApiImplicitParam(name = "ip", value = "ip地址", dataType = "String", paramType = "query")
    })
    @GetMapping("/org")
    public ResponseModel getOrg(@RequestParam(required = false) String name,
                                @RequestParam(required = false) String flag,
                                @RequestParam(required = false) String ip) {
        if (StrUtil.isBlank(flag) && StrUtil.isNotBlank(ip)) {
            Optional<OrgIp> optionalOrgIp = orgService.findIp(ip);
            flag = optionalOrgIp.map(OrgIp::getOrgFlag).orElse(null);
        }
        if (StrUtil.isBlank(name) && StrUtil.isBlank(flag)){
            throw new NotFoundOrgException();
        }
        return ResponseModel.ok().setBody(orgService.findOrg(name, flag));
    }

    @ApiOperation(value = "获取机构列表", tags = {"机构查询"})
    @GetMapping("/org/list")
    public ResponseModel getOrgList(@SortDefault(value = "name") Sort sort) {
        return ResponseModel.ok().setBody(orgService.getOrgList(sort));
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
            @ApiImplicitParam(name = "isFilter", value = "是否只返回符合prodStatus和isExp条件的产品",defaultValue = "false", dataType = "Boolean", paramType = "query"),
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
