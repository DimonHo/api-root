package com.wd.cloud.uoserver.controller;

import com.wd.cloud.commons.model.ResponseModel;
import com.wd.cloud.commons.dto.CdbDTO;
import com.wd.cloud.uoserver.service.OrgCdbService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/OrgCdb")
public class OrgCdbController {
    @Autowired
    OrgCdbService orgCdbService;

    @ApiOperation(value = "根据学校查询馆藏资源")
    @GetMapping("/findByOrgIdAndCollection")
    public ResponseModel<Page<CdbDTO>> findByOrgIdAndCollection(@RequestParam(required = false) Long orgId,
                                                                @RequestParam(required = false) Boolean collection,
                                                                @PageableDefault(sort = {"gmtCreate"}, direction = Sort.Direction.ASC) Pageable pageable){
        Page<CdbDTO> byOrgIdAndDisplay = orgCdbService.findByOrgIdAndCollection(pageable, orgId, collection);
        return ResponseModel.ok().setBody(byOrgIdAndDisplay);
    }

    @ApiOperation(value = "根据学校查询url替换地址")
    @GetMapping("/findByOrgIdAndLocalUrlIsNotNull")
    public ResponseModel<Page<CdbDTO>> findByOrgIdAndLocalUrlIsNotNull(@RequestParam(required = false) Long orgId,
                                                                       @PageableDefault(sort = {"gmtCreate"}, direction = Sort.Direction.ASC) Pageable pageable){
        Page<CdbDTO> byOrgIdAndLocalUrlIsNotNull = orgCdbService.findByOrgIdAndLocalUrlIsNotNull(pageable, orgId);
        return ResponseModel.ok().setBody(byOrgIdAndLocalUrlIsNotNull);
    }

    @ApiOperation(value = "根据资源或者网站查询详细信息")
    @GetMapping("/findByNameAndUrl")
    public ResponseModel<Page<CdbDTO>> findByNameAndUrl(@RequestParam(required = false) String keyword,
                                                        @PageableDefault Pageable pageable){
        Page<CdbDTO> byNameAndUrl = orgCdbService.findByNameAndUrl(pageable, keyword);
        return ResponseModel.ok().setBody(byNameAndUrl);
    }

    @ApiOperation(value = "根据id修改馆藏资源")
    @PostMapping("/updateOrgCdb")
    public ResponseModel<Page<CdbDTO>> updateOrgCdb(@RequestParam(required = false) Long id,
                                                    @RequestParam(required = false) String name,
                                                    @RequestParam(required = false) String url,
                                                    @RequestParam(required = false) Boolean display){
        orgCdbService.updateOrgCdb(id, name,url,display);
        return ResponseModel.ok().setMessage("修改成功");
    }

    @ApiOperation(value = "新增馆藏资源")
    @PostMapping("/insertOrgCdb")
    public ResponseModel insertOrgCdb(@RequestParam(required = false) String name,
                                      @RequestParam(required = false) String url,
                                      @RequestParam(required = false) Long orgId,
                                      @RequestParam(required = false) Boolean display){
        orgCdbService.insertOrgCdb(name, url,orgId,display);
        return ResponseModel.ok().setMessage("新增成功");
    }

    @ApiOperation(value = "删除管藏资源")
    @PostMapping("/deleteOrgCdb")
    public ResponseModel deleteOrgCdb(@RequestParam(required = false) Long id){
        orgCdbService.deleteOrgCdb(id);
        return ResponseModel.ok().setMessage("删除成功");
    }

    @ApiOperation(value = "添加一条URL规则")
    @PostMapping("/insertCdbUrl")
    public ResponseModel insertCdbUrl(@RequestParam(required = false) String name,
                                      @RequestParam(required = false) String url,
                                      @RequestParam(required = false) Long orgId,
                                      @RequestParam(required = false) String localUrl){
        orgCdbService.insertCdbUrl(name,url,orgId,localUrl);
        return ResponseModel.ok().setMessage("新增URL成功");
    }
}
