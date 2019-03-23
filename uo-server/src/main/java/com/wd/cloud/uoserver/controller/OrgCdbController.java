package com.wd.cloud.uoserver.controller;

import com.wd.cloud.commons.model.ResponseModel;
import com.wd.cloud.uoserver.pojo.dto.OrgCdbDTO;
import com.wd.cloud.uoserver.pojo.vo.OrgCdbVO;
import com.wd.cloud.uoserver.service.OrgCdbService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/OrgCdb")
public class OrgCdbController {

    @Autowired
    OrgCdbService orgCdbService;

    @ApiOperation(value = "机构馆藏资源")
    @GetMapping("/org/cdb")
    public ResponseModel<Page<OrgCdbDTO>> findByOrgFlagAndCollection(@RequestParam String orgFlag,
                                                                     @RequestParam(required = false) Boolean collection,
                                                                     @RequestParam(required = false) Boolean local,
                                                                     @RequestParam(required = false) String keyword,
                                                                     @PageableDefault(sort = {"gmtCreate"}, direction = Sort.Direction.ASC) Pageable pageable) {
        Page<OrgCdbDTO> byOrgFlagAndDisplay = orgCdbService.findOrgCdbs(orgFlag, collection, local, keyword, pageable);
        return ResponseModel.ok().setBody(byOrgFlagAndDisplay);
    }

    @ApiOperation(value = "添加，修改，新增馆藏资源")
    @PostMapping("/org/cdb")
    public ResponseModel<Page<OrgCdbDTO>> saveOrgCdb(@RequestParam String orgFlag,
                                                     @RequestBody OrgCdbVO orgCdbVO) {
        orgCdbService.saveOrgCdb(orgFlag, orgCdbVO);
        return ResponseModel.ok().setMessage("保存成功");
    }

    @ApiOperation(value = "批量添加，修改，新增馆藏资源")
    @PostMapping("/org/cdb")
    public ResponseModel<Page<OrgCdbDTO>> saveOrgCdb(@RequestParam String orgFlag,
                                                     @RequestBody List<OrgCdbVO> orgCdbVO) {
        orgCdbService.saveOrgCdb(orgFlag, orgCdbVO);
        return ResponseModel.ok().setMessage("保存成功");
    }

}
