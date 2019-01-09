package com.wd.cloud.docdelivery.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.wd.cloud.commons.exception.FeignException;
import com.wd.cloud.commons.model.ResponseModel;
import com.wd.cloud.commons.util.FileUtil;
import com.wd.cloud.docdelivery.config.Global;
import com.wd.cloud.docdelivery.dto.DocFileDTO;
import com.wd.cloud.docdelivery.dto.HelpRecordDTO;
import com.wd.cloud.docdelivery.dto.LiteratureDTO;
import com.wd.cloud.docdelivery.entity.*;
import com.wd.cloud.docdelivery.enums.AuditEnum;
import com.wd.cloud.docdelivery.enums.GiveTypeEnum;
import com.wd.cloud.docdelivery.enums.HelpStatusEnum;
import com.wd.cloud.docdelivery.exception.NotFoundException;
import com.wd.cloud.docdelivery.feign.FsServerApi;
import com.wd.cloud.docdelivery.repository.*;
import com.wd.cloud.docdelivery.service.BackendService;
import com.wd.cloud.docdelivery.service.FileService;
import com.wd.cloud.docdelivery.service.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.io.IOException;
import java.util.*;


/**
 * @author He Zhigang
 * @date 2018/5/8
 * @Description:
 */
@Service("backendService")
public class BackendServiceImpl implements BackendService {

    private static final Log log = LogFactory.get();

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
        Short helpUserScid = (Short) param.get("helperScid");
        Short status = (Short) param.get("status");
        //  https://www.tapd.cn/33969136/bugtrace/bugs/view?bug_id=1133969136001000485
        String keywords = ((String) param.get("keyword"));
        String keyword = keywords != null ? keywords.replaceAll("\\\\", "\\\\\\\\") : null;
        String beginTime = (String) param.get("beginTime");
        String endTime = param.get("endTime") + " 23:59:59";
        Page<VHelpRecord> result = vHelpRecordRepository.findAll(new Specification<VHelpRecord>() {
            @Override
            public Predicate toPredicate(Root<VHelpRecord> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> list = new ArrayList<Predicate>();
                if (helpUserScid != null && helpUserScid != 0) {
                    list.add(cb.equal(root.get("helperScid").as(Integer.class), helpUserScid));
                }
                if (status != null && status != 0) {
                    //列表查询未处理
                    if (status == 1) {
                        list.add(cb.or(cb.equal(root.get("status").as(Integer.class), 0), cb.equal(root.get("status").as(Integer.class), 1), cb.equal(root.get("status").as(Integer.class), 2)));
                    } else {
                        list.add(cb.equal(root.get("status").as(Integer.class), status));
                    }
                }
                if (!StringUtils.isEmpty(keyword)) {
                    list.add(cb.or(
                            cb.like(root.get("literature").get("docTitle").as(String.class), "%" + keyword.trim() + "%"),
                            cb.like(root.get("helperEmail").as(String.class), "%" + keyword.trim() + "%")
                            )
                    );
                }
                if (!StringUtils.isEmpty(beginTime)) {
                    list.add(cb.between(root.get("gmtCreate").as(Date.class), DateUtil.parse(beginTime), DateUtil.parse(endTime)));
                }
                Predicate[] p = new Predicate[list.size()];
                return cb.and(list.toArray(p));
            }
        }, pageable);

        Page<HelpRecordDTO> helpRecordDTOS = result.map(vHelpRecord -> {
            HelpRecordDTO helpRecordDTO = new HelpRecordDTO();
            BeanUtil.copyProperties(vHelpRecord,helpRecordDTO);
            helpRecordDTO.setGiveRecords(giveRecordRepository.findByHelpRecordId(vHelpRecord.getHelpRecordId()));
            return helpRecordDTO;
        });
        return helpRecordDTOS;
    }

    @Override
    public Page<LiteratureDTO> getLiteratureList(Pageable pageable, Map<String, Object> param) {
        Boolean reusing = (Boolean) param.get("reusing");
        String keywords = ((String) param.get("keyword"));
        String keyword = keywords != null ? keywords.replaceAll("\\\\", "\\\\\\\\") : null;
        Page<VLiterature> literaturePage = vLiteratureRepository.findAll(new Specification<VLiterature>() {
            @Override
            public Predicate toPredicate(Root<VLiterature> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> list = new ArrayList<Predicate>();
                if (reusing != null) {
                    list.add(cb.equal(root.get("reusing").as(boolean.class), reusing));
                }
                if (!StringUtils.isEmpty(keyword)) {
                    list.add(cb.like(root.get("docTitle").as(String.class), "%" + keyword + "%"));
                }
                //list.add(cb.isNotEmpty(root.get("docFiles")));
                Predicate[] p = new Predicate[list.size()];
                return cb.and(list.toArray(p));
            }
        }, pageable);

        Page<LiteratureDTO> literatureDTOPage = literaturePage.map(literature -> {
            LiteratureDTO literatureDTO = new LiteratureDTO();
            //BeanUtil.copyProperties(literature, literatureDTO, "docFiles");

            List<DocFile> docFiles = docFileRepository.findByLiteratureId(literature.getLiteratureId());
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
        if (!optionalDocFile.isPresent()){
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
        Optional<GiveRecord> giveRecordOptional = giveRecordRepository.findByHelpRecordIdAndStatus(helpRecord.getId(), GiveTypeEnum.MANAGER.getCode());

        //如果没有第三方状态的记录，则新建一条应助记录
        GiveRecord giveRecord = giveRecordOptional.orElseGet(GiveRecord::new);

        giveRecord.setHelpRecordId(helpRecord.getId());
        giveRecord.setFileId(docFile.getFileId());
        //设置应助类型为管理员应助
        giveRecord.setType(GiveTypeEnum.MANAGER.getCode());
        giveRecord.setGiverId(giverId);
        giveRecord.setGiverName(giverName);
        //修改求助状态为应助成功
        helpRecord.setStatus(HelpStatusEnum.HELP_SUCCESSED.getValue());
        giveRecordRepository.save(giveRecord);
        helpRecordRepository.save(helpRecord);

    }

    @Override
    public void third(Long helpRecordId, Long giverId, String giverName) {
        HelpRecord helpRecord = helpRecordRepository.findByIdAndStatus(helpRecordId, HelpStatusEnum.WAIT_HELP.getValue());
        if (helpRecord == null) {
            throw new NotFoundException("没有找到待求助记录");
        }
        helpRecord.setStatus(HelpStatusEnum.HELP_THIRD.getValue());
        GiveRecord giveRecord = new GiveRecord();
        giveRecord.setGiverId(giverId);
        giveRecord.setType(GiveTypeEnum.MANAGER.getCode());
        giveRecord.setGiverName(giverName);

        giveRecord.setHelpRecordId(helpRecord.getId());
        giveRecordRepository.save(giveRecord);
        helpRecordRepository.save(helpRecord);
    }

    @Override
    public void failed(Long helpRecordId, Long giverId, String giverName) {
        HelpRecord helpRecord = getWaitOrThirdHelpRecord(helpRecordId);
        helpRecord.setStatus(HelpStatusEnum.HELP_FAILED.getValue());
        //如果有求助第三方的状态的应助记录，则直接处理更新这个记录
        Optional<GiveRecord> optionalGiveRecord = giveRecordRepository.findByHelpRecordIdAndStatus(helpRecordId, GiveTypeEnum.MANAGER.getCode());

        //如果没有第三方状态的记录，则新建一条应助记录
        GiveRecord giveRecord = optionalGiveRecord.orElseGet(GiveRecord::new);
        giveRecord.setGiverId(giverId);
        giveRecord.setType(GiveTypeEnum.MANAGER.getCode());
        giveRecord.setGiverName(giverName);
        giveRecord.setHelpRecordId(helpRecordId);

        giveRecordRepository.save(giveRecord);
    }

    @Override
    public void auditPass(Long helpRecordId, Long auditorId, String auditorName) {
        HelpRecord helpRecord = getWaitAuditHelpRecord(helpRecordId);
        GiveRecord giveRecord = giveRecordRepository.findByHelpRecordIdAndStatusAndType(helpRecordId, 0, 2);
        if (giveRecord == null) {
            throw new NotFoundException("未找到待审核的应助记录");
        }

        giveRecord.setStatus(AuditEnum.PASS.getCode());
        giveRecord.setHandlerId(auditorId);
        giveRecord.setHandlerName(auditorName);
        Literature literature = literatureRepository.findById(helpRecord.getLiteratureId()).get();

        DocFile docFile = docFileRepository.findByFileIdAndLiteratureId(giveRecord.getFileId(),literature.getId());
        docFileRepository.save(docFile);

        helpRecord.setStatus(HelpStatusEnum.HELP_SUCCESSED.getValue());
        helpRecordRepository.save(helpRecord);

    }

    @Override
    public void auditNoPass(Long helpRecordId, Long auditorId, String auditorName) {
        HelpRecord helpRecord = getWaitAuditHelpRecord(helpRecordId);
        GiveRecord giveRecord = giveRecordRepository.findByHelpRecordIdAndStatusAndType(helpRecordId, 0, 2);
        if (giveRecord == null) {
            throw new NotFoundException("未找到待审核的应助记录");
        }
        giveRecord.setStatus(AuditEnum.NO_PASS.getCode());
        giveRecord.setHandlerId(auditorId);
        giveRecord.setHandlerName(auditorName);
        helpRecord.setStatus(HelpStatusEnum.WAIT_HELP.getValue());
        helpRecordRepository.save(helpRecord);
    }

    @Override
    public HelpRecord getWaitOrThirdHelpRecord(Long id) {
        return helpRecordRepository.findByIdAndStatusIn(id,
                new int[]{HelpStatusEnum.WAIT_HELP.getValue(), HelpStatusEnum.HELP_THIRD.getValue()});
    }

    @Override
    public HelpRecord getWaitAuditHelpRecord(Long id) {
        return helpRecordRepository.findByIdAndStatus(id, HelpStatusEnum.WAIT_AUDIT.getValue());
    }

    @Override
    public GiveRecord getWaitAudit(Long id) {
        GiveRecord giveRecord = giveRecordRepository.findByIdAndStatus(id, AuditEnum.WAIT.getCode());
        return giveRecord;
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
        ReusingLog reusingLog = null;
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
        reusingLog.setReusing(reusing);
        reusingLog.setHandlerId((String) param.get("handlerId"));
        reusingLog.setHandlerName((String) param.get("handlerName"));
        reusingLogRepository.save(reusingLog);
        docFileRepository.save(doc);
        literatureRepository.save(literature);
        return true;
    }


}
