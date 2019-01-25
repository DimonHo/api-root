package com.wd.cloud.docdelivery.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONObject;
import com.wd.cloud.commons.exception.FeignException;
import com.wd.cloud.commons.exception.NotFoundException;
import com.wd.cloud.commons.model.ResponseModel;
import com.wd.cloud.commons.util.FileUtil;
import com.wd.cloud.docdelivery.config.Global;
import com.wd.cloud.docdelivery.dto.DocFileDTO;
import com.wd.cloud.docdelivery.dto.HelpRecordDTO;
import com.wd.cloud.docdelivery.dto.LiteratureDTO;
import com.wd.cloud.docdelivery.entity.*;
import com.wd.cloud.docdelivery.enums.GiveStatusEnum;
import com.wd.cloud.docdelivery.enums.GiveTypeEnum;
import com.wd.cloud.docdelivery.enums.HelpStatusEnum;
import com.wd.cloud.docdelivery.feign.FsServerApi;
import com.wd.cloud.docdelivery.repository.*;
import com.wd.cloud.docdelivery.service.BackendService;
import com.wd.cloud.docdelivery.service.FileService;
import com.wd.cloud.docdelivery.service.MailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;


/**
 * @author He Zhigang
 * @date 2018/5/8
 * @Description:
 */
@Slf4j
@Service("backendService")
public class BackendServiceImpl implements BackendService {

    @Autowired
    Global global;

    @Autowired
    HelpRecordRepository helpRecordRepository;

    @Autowired
    VHelpRecordRepository vHelpRecordRepository;

    @Autowired
    GiveRecordRepository giveRecordRepository;

    @Autowired
    LiteratureRepository literatureRepository;

    @Autowired
    DocFileRepository docFileRepository;

    @Autowired
    MailService mailService;

    @Autowired
    FsServerApi fsServerApi;

    @Autowired
    FileService fileService;

    @Autowired
    ReusingLogRepository reusingLogRepository;

    @Autowired
    VLiteratureRepository vLiteratureRepository;


    @Override
    public Page<HelpRecordDTO> getHelpList(Pageable pageable, Map<String, Object> param) {
        Long helpUserScid = (Long) param.get("helperScid");
        Integer status = (Integer) param.get("status");
        //  https://www.tapd.cn/33969136/bugtrace/bugs/view?bug_id=1133969136001000485
        String keywords = ((String) param.get("keyword"));
        String keyword = keywords != null ? keywords.replaceAll("\\\\", "\\\\\\\\") : null;
        String beginTime = (String) param.get("beginTime");
        String endTime = param.get("endTime") + " 23:59:59";
        Page<VHelpRecord> result = vHelpRecordRepository.findAll(VHelpRecordRepository.SpecificationBuilder.buildBackendList(helpUserScid, status, keyword, beginTime, endTime), pageable);

        Page<HelpRecordDTO> helpRecordDTOS = result.map(vHelpRecord -> {
            HelpRecordDTO helpRecordDTO = new HelpRecordDTO();
            BeanUtil.copyProperties(vHelpRecord, helpRecordDTO);
            helpRecordDTO.setGiveRecords(giveRecordRepository.findByHelpRecordId(vHelpRecord.getId()));
            return helpRecordDTO;
        });
        return helpRecordDTOS;
    }

    @Override
    public Page<LiteratureDTO> getLiteratureList(Pageable pageable, Map<String, Object> param) {
        Boolean reusing = (Boolean) param.get("reusing");
        String keywords = ((String) param.get("keyword"));
        String keyword = keywords != null ? keywords.replaceAll("\\\\", "\\\\\\\\") : null;
        Page<VLiterature> literaturePage = vLiteratureRepository.findAll(VLiteratureRepository.SpecificationBuilder.buildBackLiteraList(reusing, keyword), pageable);

        Page<LiteratureDTO> literatureDTOPage = literaturePage.map(vLiterature -> {
            LiteratureDTO literatureDTO = new LiteratureDTO();
            BeanUtil.copyProperties(vLiterature, literatureDTO);

            List<DocFile> docFiles = docFileRepository.findByLiteratureId(vLiterature.getId());
            List<DocFileDTO> docFileDTOS = new ArrayList<>();
            docFiles.forEach(docFile -> {
                DocFileDTO docFileDTO = new DocFileDTO();
                BeanUtil.copyProperties(docFile, docFileDTO);
                docFileDTOS.add(docFileDTO);
            });
            literatureDTO.setDocFiles(docFileDTOS);
            return literatureDTO;
        });
        return literatureDTOPage;
    }

    @Override
    public List<DocFile> getDocFileList(Pageable pageable, Long literatureId) {
        return docFileRepository.getResuingDoc(literatureId);
    }

    @Override
    public DocFile saveDocFile(Long literatureId, String fileId) {
        Optional<DocFile> optionalDocFile = docFileRepository.findByLiteratureIdAndFileId(literatureId, fileId);
        if (!optionalDocFile.isPresent()) {
            DocFile docFile = new DocFile();
            docFile.setFileId(fileId);
            docFile.setLiteratureId(literatureId);
            docFile = docFileRepository.save(docFile);
            return docFile;
        }
        return optionalDocFile.get();
    }

    @Override
    public void give(Long helpRecordId, Long giverId, String giverName, MultipartFile file) {
        HelpRecord helpRecord = getWaitOrThirdHelpRecord(helpRecordId);
        DocFile docFile = null;
        log.info("正在上传文件[file = {},size = {}]", file.getOriginalFilename(), file.getSize());
        String fileMd5 = null;
        String fileId = null;
        try {
            fileMd5 = FileUtil.fileMd5(file.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        ResponseModel<JSONObject> checkResult = fsServerApi.checkFile(global.getHbaseTableName(), fileMd5);
        if (!checkResult.isError() && checkResult.getBody() != null) {
            log.info("文件已存在，秒传成功！");
            fileId = checkResult.getBody().getStr("fileId");
        }
        if (fileId == null) {
            ResponseModel<JSONObject> uploadResult = fsServerApi.uploadFile(global.getHbaseTableName(), file);
            if (uploadResult.isError()) {
                log.error("文件服务调用失败：{}", uploadResult.getMessage());
                log.info("文件[file = {},size = {}] 上传失败 。。。", file.getOriginalFilename(), file.getSize());
                throw new FeignException("fsServer.uploadFile");
            }
            log.info("文件{}上传成功!", file.getOriginalFilename());
            fileId = uploadResult.getBody().getStr("fileId");
        }
        docFile = saveDocFile(helpRecord.getLiteratureId(), fileId);
        //如果有求助第三方的状态的应助记录，则直接处理更新这个记录
        Optional<GiveRecord> giveRecordOptional = giveRecordRepository.findByHelpRecordIdAndStatus(helpRecord.getId(), GiveTypeEnum.MANAGER.value());

        //如果没有第三方状态的记录，则新建一条应助记录
        GiveRecord giveRecord = giveRecordOptional.orElseGet(GiveRecord::new);

        giveRecord.setHelpRecordId(helpRecord.getId());
        giveRecord.setFileId(docFile.getFileId());
        //设置应助类型为管理员应助
        giveRecord.setType(GiveTypeEnum.MANAGER.value());
        giveRecord.setGiverId(giverId);
        giveRecord.setGiverName(giverName);
        //修改求助状态为应助成功
        helpRecord.setStatus(HelpStatusEnum.HELP_SUCCESSED.value());
        giveRecordRepository.save(giveRecord);
        helpRecordRepository.save(helpRecord);
        Optional<VHelpRecord> optionalVHelpRecord = vHelpRecordRepository.findById(helpRecordId);
        optionalVHelpRecord.ifPresent(vHelpRecord -> mailService.sendMail(vHelpRecord));
    }

    @Override
    public void third(Long helpRecordId, Long giverId, String giverName) {
        HelpRecord helpRecord = helpRecordRepository.findByIdAndStatus(helpRecordId, HelpStatusEnum.WAIT_HELP.value());
        if (helpRecord == null) {
            throw new NotFoundException("没有找到待求助记录");
        }
        helpRecord.setStatus(HelpStatusEnum.HELP_THIRD.value());
        GiveRecord giveRecord = new GiveRecord();
        giveRecord.setGiverId(giverId);
        giveRecord.setType(GiveTypeEnum.MANAGER.value());
        giveRecord.setGiverName(giverName);

        giveRecord.setHelpRecordId(helpRecord.getId());
        giveRecordRepository.save(giveRecord);
        helpRecordRepository.save(helpRecord);
        Optional<VHelpRecord> optionalVHelpRecord = vHelpRecordRepository.findById(helpRecordId);
        optionalVHelpRecord.ifPresent(vHelpRecord -> mailService.sendMail(vHelpRecord));
    }

    @Override
    public void failed(Long helpRecordId, Long giverId, String giverName) {
        HelpRecord helpRecord = getWaitOrThirdHelpRecord(helpRecordId);
        helpRecord.setStatus(HelpStatusEnum.HELP_FAILED.value());
        //如果有求助第三方的状态的应助记录，则直接处理更新这个记录
        Optional<GiveRecord> optionalGiveRecord = giveRecordRepository.findByHelpRecordIdAndStatus(helpRecordId, GiveTypeEnum.MANAGER.value());

        //如果没有第三方状态的记录，则新建一条应助记录
        GiveRecord giveRecord = optionalGiveRecord.orElseGet(GiveRecord::new);
        giveRecord.setGiverId(giverId);
        giveRecord.setType(GiveTypeEnum.MANAGER.value());
        giveRecord.setGiverName(giverName);
        giveRecord.setHelpRecordId(helpRecordId);
        giveRecordRepository.save(giveRecord);
        Optional<VHelpRecord> optionalVHelpRecord = vHelpRecordRepository.findById(helpRecordId);
        optionalVHelpRecord.ifPresent(vHelpRecord -> mailService.sendMail(vHelpRecord));
    }

    @Override
    public void auditPass(Long helpRecordId, Long auditorId, String auditorName) {
        HelpRecord helpRecord = getWaitAuditHelpRecord(helpRecordId);
        GiveRecord giveRecord = giveRecordRepository.findByHelpRecordIdAndStatusAndType(helpRecordId, 0, 2);
        if (giveRecord == null) {
            throw new NotFoundException("未找到待审核的应助记录");
        }

        giveRecord.setStatus(GiveStatusEnum.SUCCESS.value());
        giveRecord.setHandlerId(auditorId);
        giveRecord.setHandlerName(auditorName);
        Literature literature = literatureRepository.findById(helpRecord.getLiteratureId()).get();

        DocFile docFile = docFileRepository.findByFileIdAndLiteratureId(giveRecord.getFileId(), literature.getId()).orElse(new DocFile());
        docFile.setLiteratureId(literature.getId()).setFileId(giveRecord.getFileId());
        docFileRepository.save(docFile);

        helpRecord.setStatus(HelpStatusEnum.HELP_SUCCESSED.value());
        helpRecordRepository.save(helpRecord);
        Optional<VHelpRecord> optionalVHelpRecord = vHelpRecordRepository.findById(helpRecordId);
        optionalVHelpRecord.ifPresent(vHelpRecord -> mailService.sendMail(vHelpRecord));
    }

    @Override
    public void auditNoPass(Long helpRecordId, Long auditorId, String auditorName) {
        HelpRecord helpRecord = getWaitAuditHelpRecord(helpRecordId);
        GiveRecord giveRecord = giveRecordRepository.findByHelpRecordIdAndStatusAndType(helpRecordId, 0, 2);
        if (giveRecord == null) {
            throw new NotFoundException("未找到待审核的应助记录");
        }
        giveRecord.setStatus(GiveStatusEnum.AUDIT_NO_PASS.value());
        giveRecord.setHandlerId(auditorId);
        giveRecord.setHandlerName(auditorName);
        helpRecord.setStatus(HelpStatusEnum.WAIT_HELP.value());
        helpRecordRepository.save(helpRecord);
    }

    @Override
    public HelpRecord getWaitOrThirdHelpRecord(Long id) {
        return helpRecordRepository.findByIdAndStatusIn(id,
                new int[]{HelpStatusEnum.WAIT_HELP.value(), HelpStatusEnum.HELP_THIRD.value()});
    }

    @Override
    public HelpRecord getWaitAuditHelpRecord(Long id) {
        return helpRecordRepository.findByIdAndStatus(id, HelpStatusEnum.WAIT_AUDIT.value());
    }

    @Override
    public GiveRecord getWaitAudit(Long id) {
        return giveRecordRepository.findByIdAndStatus(id, GiveStatusEnum.WAIT_AUDIT.value());
    }


    @Override
    public GiveRecord getGiverRecord(Long helpRecordId, int auditStatus, int giverType) {
        return giveRecordRepository.findByHelpRecordIdAndStatusAndType(helpRecordId, auditStatus, giverType);
    }


    @Override
    public boolean reusing(Map<String, Object> param) {
        long literatureId = (long) param.get("literatureId");
        Literature literature = literatureRepository.findById(literatureId).orElse(null);
        long docFileId = (long) param.get("docFileId");
        boolean reusing = (boolean) param.get("reusing");
        List<DocFile> list = docFileRepository.getResuingDoc(literatureId);
        DocFile doc = null;
        for (DocFile docFile : list) {
            //如果是复用操作，并且已经有文档被复用，则返回false，如果是取消复用，则不会进入
            if (docFile.isReusing() && reusing) {
                return false;
            }
            if (docFile.getId() == docFileId) {
                doc = docFile;
                if (!reusing) {
                    break;
                }
            }
        }
        if (doc == null || literature == null) {
            return false;
        }
        doc.setReusing(reusing);
        ReusingLog reusingLog = new ReusingLog();
        reusingLog.setReusing(reusing);
        reusingLog.setHandlerId((String) param.get("handlerId"));
        reusingLog.setHandlerName((String) param.get("handlerName"));
        reusingLogRepository.save(reusingLog);
        docFileRepository.save(doc);
        literatureRepository.save(literature);
        return true;
    }


}
