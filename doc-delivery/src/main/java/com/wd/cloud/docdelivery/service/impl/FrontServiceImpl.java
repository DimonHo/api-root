package com.wd.cloud.docdelivery.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.http.HtmlUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.wd.cloud.commons.exception.FeignException;
import com.wd.cloud.commons.model.ResponseModel;
import com.wd.cloud.commons.util.DateUtil;
import com.wd.cloud.commons.util.FileUtil;
import com.wd.cloud.docdelivery.config.Global;
import com.wd.cloud.docdelivery.dto.HelpRecordDTO;
import com.wd.cloud.docdelivery.entity.*;
import com.wd.cloud.docdelivery.enums.AuditEnum;
import com.wd.cloud.docdelivery.enums.GiveTypeEnum;
import com.wd.cloud.docdelivery.enums.HelpStatusEnum;
import com.wd.cloud.docdelivery.exception.ExceptionCode;
import com.wd.cloud.docdelivery.exception.GiveException;
import com.wd.cloud.docdelivery.exception.HelpException;
import com.wd.cloud.docdelivery.exception.NotFoundException;
import com.wd.cloud.docdelivery.feign.FsServerApi;
import com.wd.cloud.docdelivery.repository.*;
import com.wd.cloud.docdelivery.service.FileService;
import com.wd.cloud.docdelivery.service.FrontService;
import com.wd.cloud.docdelivery.service.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * @author He Zhigang
 * @date 2018/5/7
 * @Description:
 */
@Service("frontService")
@Transactional(rollbackFor = Exception.class)
public class FrontServiceImpl implements FrontService {

    private static final Log log = LogFactory.get();

    @Autowired
    Global global;
    @Autowired
    LiteratureRepository literatureRepository;

    @Autowired
    HelpRecordRepository helpRecordRepository;

    @Autowired
    GiveRecordRepository giveRecordRepository;

    @Autowired
    DocFileRepository docFileRepository;

    @Autowired
    FsServerApi fsServerApi;

    @Autowired
    FileService fileService;

    @Autowired
    MailService mailService;

    @Autowired
    PermissionRepository permissionRepository;


    @Override
    public boolean checkExists(String email, Long literatureId) {
        HelpRecord helpRecord = helpRecordRepository.findByHelperEmailAndLiteratureId(email, literatureId);
        if (helpRecord != null) {
            return true;
        }
        return false;
    }

    @Override
    public DocFile saveDocFile(Literature literature, String fileId, String filaName) {
        DocFile docFile = docFileRepository.findByLiteratureAndFileId(literature, fileId).orElse(new DocFile());
        docFile.setFileId(fileId);
        docFile.setLiterature(literature);
        return docFileRepository.save(docFile);
    }

    @Override
    public String help(HelpRecord helpRecord, String docTitle, String docHref) {
        log.info("用户:[{}]正在求助文献:[{}]", helpRecord.getHelperEmail(), docTitle);
        // 防止调用者传过来的docTitle包含HTML标签，在这里将标签去掉
        docTitle = clearHtml(docTitle.trim());
        docHref = docHref.trim();
        String unid = SecureUtil.md5(docTitle + docHref);
        // 先查询元数据是否存在
        Literature literature = literatureRepository.findByUnid(unid);
        String msg = "waiting:文献求助已发送，应助结果将会在24h内发送至您的邮箱，请注意查收";
        if (null == literature) {
            // 如果不存在，则新增一条元数据
            literature = saveLiterature(docTitle, docHref);
        }
        if (checkExists(helpRecord.getHelperEmail(), literature.getId())) {
            throw new HelpException(2008, "error:您最近15天内已求助过这篇文献,请注意查收邮箱");
        }
        helpRecord.setLiteratureId(literature.getId());
        DocFile docFile = getReusingFile(literature);
        // 如果文件已存在，自动应助成功
        if (null != docFile) {
            helpRecord.setStatus(HelpStatusEnum.HELP_SUCCESSED.getValue());
            GiveRecord giveRecord = new GiveRecord();
            giveRecord.setFileId(docFile.getFileId());
            giveRecord.setType(GiveTypeEnum.AUTO.getCode());
            giveRecord.setGiverName("自动应助");
            //先保存求助记录，得到求助ID，再关联应助记录
            helpRecord = saveHelpRecord(helpRecord);
            giveRecord.setHelpRecordId(helpRecord.getId());
            saveGiveRecord(giveRecord);
            String downloadUrl = fileService.getDownloadUrl(helpRecord.getId());
            mailService.sendMail(helpRecord.getHelpChannel(),
                    helpRecord.getHelperScname(),
                    helpRecord.getHelperEmail(),
                    literature.getDocTitle(),
                    downloadUrl,
                    HelpStatusEnum.HELP_SUCCESSED,
                    helpRecord.getId());
            msg = "success:文献求助成功,请登陆邮箱" + helpRecord.getHelperEmail() + "查收结果";
        } else {
            try {
                // 保存求助记录
                saveHelpRecord(helpRecord);
                // 发送通知邮件
                mailService.sendNotifyMail(helpRecord.getHelpChannel(), helpRecord.getHelperScname(), helpRecord.getHelperEmail(), helpRecord.getId());
            } catch (Exception e) {
                throw new HelpException(1002, msg);
            }
        }
        return msg;
    }

    @Override
    public HelpRecord give(Long helpRecordId, Long giverId, String giverName, String ip) {
        HelpRecord helpRecord = helpRecordRepository.findById(helpRecordId).get();
        // 该求助记录状态为非待应助，那么可能已经被其他人应助过或已应助完成
        if (HelpStatusEnum.HELPING.getValue() == helpRecord.getStatus()) {
            throw new GiveException(ExceptionCode.GIVING, "该求助正在被应助");
        }
        if (helpRecord.getStatus() == HelpStatusEnum.HELP_THIRD.getValue() || helpRecord.getStatus() == HelpStatusEnum.WAIT_HELP.getValue()) {
            //检查用户是否已经认领了应助
            String docTitle = checkExistsGiveing(giverId);
            if (docTitle != null) {
                throw new GiveException(ExceptionCode.DOC_FINISH_GIVING, "请先完成您正在应助的文献:" + docTitle);
            }
            helpRecord = givingHelp(helpRecordId, giverId, giverName, ip);
            return helpRecord;
        } else {
            throw new NotFoundException("该求助无须应助");
        }

    }

    @Override
    public void uploadFile(HelpRecord helpRecord, Long giverId, MultipartFile file, String ip) {
        String fileId = null;
        try {
            String fileMd5 = FileUtil.fileMd5(file.getInputStream());
            ResponseModel<JSONObject> checkResult = fsServerApi.checkFile(global.getHbaseTableName(), fileMd5);
            if (!checkResult.isError() && checkResult.getBody() != null) {
                log.info("文件已存在，秒传成功！");
                fileId = checkResult.getBody().getStr("fileId");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (StrUtil.isBlank(fileId)) {
            //保存文件
            ResponseModel<JSONObject> responseModel = fsServerApi.uploadFile(global.getHbaseTableName(), file);
            if (responseModel.isError()) {
                log.error("文件服务调用失败：{}", responseModel.getMessage());
                throw new FeignException("fsServer.uploadFile");
            }
            fileId = responseModel.getBody().getStr("fileId");
        }
        String fileName = file.getOriginalFilename();
        Literature literature = literatureRepository.findById(helpRecord.getLiteratureId()).get();
        DocFile docFile = saveDocFile(literature, fileId, fileName);
        //更新记录
        createGiveRecord(helpRecord, giverId, docFile, ip);
    }

    @Override
    public HelpRecord givingHelp(long helpRecordId, long giverId, String giverName, String giverIp) {
        HelpRecord helpRecord = helpRecordRepository.findByIdAndStatus(helpRecordId, HelpStatusEnum.WAIT_HELP.getValue());
        if (helpRecord != null) {
            helpRecord.setStatus(HelpStatusEnum.HELPING.getValue());
            GiveRecord giveRecord = new GiveRecord();
            giveRecord.setHelpRecordId(helpRecordId);
            giveRecord.setGiverId(giverId);
            giveRecord.setGiverIp(giverIp);
            giveRecord.setGiverName(giverName);
            giveRecord.setType(GiveTypeEnum.USER.getCode());
            giveRecord.setStatus(AuditEnum.WAIT_UPLOAD.getCode());
            //保存的同时，关联更新求助记录状态
            giveRecordRepository.save(giveRecord);
            helpRecordRepository.save(helpRecord);
        }
        return helpRecord;
    }

    @Override
    public boolean cancelGivingHelp(long helpRecordId, long giverId) {
        HelpRecord helpRecord = helpRecordRepository.findByIdAndStatus(helpRecordId, HelpStatusEnum.HELPING.getValue());
        boolean flag = false;
        if (helpRecord != null) {
            giveRecordRepository.deleteByHelpRecordIdAndAuditStatusAndGiverId(helpRecordId, AuditEnum.WAIT_UPLOAD.getCode(), giverId);
            //更新求助记录状态
            helpRecord.setStatus(HelpStatusEnum.WAIT_HELP.getValue());
            helpRecordRepository.save(helpRecord);
            flag = true;
        }
        return flag;
    }

    @Override
    public HelpRecord getHelpingRecord(long helpRecordId) {
        return helpRecordRepository.findByIdAndStatus(helpRecordId, HelpStatusEnum.HELPING.getValue());
    }

    @Override
    public int getCountHelpRecordToDay(String email) {
        return helpRecordRepository.findByHelperEmailAndGmtCreateAfter(email, DateUtil.beginOfDay(new Date()).toSqlDate()).size();
    }

    @Override
    public HelpRecord getWaitOrThirdHelpRecord(Long id) {
        return helpRecordRepository.findByIdAndStatusIn(id,
                new int[]{HelpStatusEnum.WAIT_HELP.getValue(), HelpStatusEnum.HELP_THIRD.getValue()});
    }

    @Override
    public HelpRecord getNotWaitRecord(long helpRecordId) {
        return helpRecordRepository.findByIdAndStatusNot(helpRecordId, HelpStatusEnum.WAIT_HELP.getValue());
    }

    @Override
    public String clearHtml(String docTitle) {
        return HtmlUtil.unescape(HtmlUtil.cleanHtmlTag(docTitle));
    }

    @Override
    public Literature saveLiterature(Literature literature) {
        return literatureRepository.save(literature);
    }

    public Literature saveLiterature(String docTitle, String docHref) {
        Literature literature = new Literature();
        literature.setDocTitle(docTitle).setDocHref(docHref);
        return literatureRepository.save(literature);
    }

    @Override
    public GiveRecord saveGiveRecord(GiveRecord giveRecord) {
        return giveRecordRepository.save(giveRecord);
    }

    @Override
    public void createGiveRecord(HelpRecord helpRecord, long giverId, DocFile docFile, String giveIp) {
        //更新求助状态为待审核
        helpRecord.setStatus(HelpStatusEnum.WAIT_AUDIT.getValue());
        GiveRecord giveRecord = giveRecordRepository.findByHelpRecordIdAndAuditStatusAndGiverId(helpRecord.getId(), AuditEnum.WAIT_UPLOAD.getCode(), giverId);
        //关联应助记录
        giveRecord.setDocFileId(docFile.getId());
        giveRecord.setGiverId(giverId);
        giveRecord.setGiverIp(giveIp);
        giveRecord.setHelpRecordId(helpRecord.getId());
        //前台上传的，需要后台人员再审核
        giveRecord.setAuditStatus(AuditEnum.WAIT.getCode());
        giveRecordRepository.save(giveRecord);
        helpRecordRepository.save(helpRecord);

    }

    @Override
    public HelpRecord saveHelpRecord(HelpRecord helpRecord) {
        return helpRecordRepository.save(helpRecord);
    }

    @Override
    public HelpRecord getHelpRecord(long helpRecordId) {
        return helpRecordRepository.getOne(helpRecordId);
    }

    @Override
    public Page<HelpRecordDTO> getHelpRecordsForUser(long helperId, Integer status, Pageable pageable) {
        Page<HelpRecord> helpRecordPage = helpRecordRepository.findByHelperIdAndStatus(helperId, status, pageable);
        Page<HelpRecordDTO> helpRecordDTOS = coversHelpRecordDTO(helpRecordPage);
        return helpRecordDTOS;
    }

    private Page<HelpRecordDTO> coversHelpRecordDTO(Page<VHelpRecord> helpRecordPage) {
        return helpRecordPage.map(helpRecord -> {

            HelpRecordDTO helpRecordDTO = anonymous(helpRecord);
            Literature literature = literatureRepository.findById(helpRecord.getLiteratureId()).orElse(null);
            helpRecordDTO.setDocTitle(literature.getDocTitle()).setDocHref(literature.getDocHref());
            return helpRecordDTO;
        });
    }

    @Override
    public Page<HelpRecordDTO> getHelpRecordsForEmail(String helperEmail, Pageable pageable) {
        Page<HelpRecordDTO> helpRecordDTOS = coversHelpRecordDTO(helpRecordRepository.findByHelperEmail(helperEmail, pageable));
        return helpRecordDTOS;
    }

    @Override
    public Page<HelpRecordDTO> getWaitHelpRecords(int helpChannel, Pageable pageable) {

        int[] status = {HelpStatusEnum.WAIT_HELP.getValue(),
                HelpStatusEnum.HELPING.getValue(),
                HelpStatusEnum.WAIT_AUDIT.getValue(),
                HelpStatusEnum.HELP_THIRD.getValue()};

        Page<HelpRecord> waitHelpRecords = filterHelpRecords(helpChannel, pageable, status);
        Page<HelpRecordDTO> waitHelps = waitHelpRecords.map(helpRecord -> {
            HelpRecordDTO helpRecordDTO = anonymous(helpRecord);
            Literature literature = literatureRepository.findById(helpRecord.getLiteratureId()).orElse(null);
            helpRecordDTO.setDocTitle(literature.getDocTitle()).setDocHref(literature.getDocHref());
            //如果有用户正在应助
            if (helpRecord.getStatus() == HelpStatusEnum.HELPING.getValue()) {
                GiveRecord givingRecord = giveRecordRepository.findByHelpRecordIdAndAuditStatusEquals(helpRecord.getId(), AuditEnum.WAIT_UPLOAD.getCode());
                helpRecordDTO.setGiving(givingRecord);
            }
            return helpRecordDTO;
        });
        return waitHelps;
    }

    private Page<HelpRecord> filterHelpRecords(int helpChannel, Pageable pageable, int[] status) {
        Page<HelpRecord> waitHelpRecords;
        if (helpChannel == 0) {
            waitHelpRecords = helpRecordRepository.findByStatusIn(status, pageable);
        } else {
            waitHelpRecords = helpRecordRepository.findByHelpChannelAndStatusIn(helpChannel, status, pageable);
        }
        return waitHelpRecords;
    }


    @Override
    public Page<HelpRecordDTO> getFinishHelpRecords(int helpChannel, Pageable pageable) {
        int[] status = {HelpStatusEnum.HELP_SUCCESSED.getValue(), HelpStatusEnum.HELP_FAILED.getValue()};
        Page<HelpRecord> finishHelpRecords = filterHelpRecords(helpChannel, pageable, status);
        return coversHelpRecordDTO(finishHelpRecords);
    }

    @Override
    public Page<HelpRecordDTO> getSuccessHelpRecords(int helpChannel, Pageable pageable) {
        int[] status = {HelpStatusEnum.HELP_SUCCESSED.getValue()};
        Page<HelpRecord> successHelpRecords = filterHelpRecords(helpChannel, pageable, status);
        return coversHelpRecordDTO(successHelpRecords);
    }

    @Override
    public Page<HelpRecordDTO> getFailedHelpRecords(int helpChannel, Pageable pageable) {
        int[] status = {HelpStatusEnum.HELP_FAILED.getValue()};
        Page<HelpRecord> failedHelpRecords = filterHelpRecords(helpChannel, pageable, status);
        return coversHelpRecordDTO(failedHelpRecords);
    }

    @Override
    public Page<HelpRecordDTO> getAllHelpRecord(Pageable pageable) {
        return coversHelpRecordDTO(helpRecordRepository.findAll(pageable));
    }

    @Override
    public DocFile getReusingFile(Literature literature) {
        DocFile docFiles = docFileRepository.findByLiteratureAndReusingIsTrue(literature);
        return docFiles;
    }

    @Override
    public Page<HelpRecordDTO> search(String keyword, Pageable pageable) {
        Page<HelpRecord> helpRecords = helpRecordRepository.findAll(new Specification<HelpRecord>() {
            @Override
            public Predicate toPredicate(Root<HelpRecord> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                List<Predicate> list = new ArrayList<Predicate>();
                if (!StringUtils.isEmpty(keyword)) {
                    list.add(criteriaBuilder.or(criteriaBuilder.like(root.get("literature").get("docTitle").as(String.class), "%" + keyword.trim() + "%"), criteriaBuilder.like(root.get("helperEmail").as(String.class), "%" + keyword.trim() + "%")));
                }
                Predicate[] p = new Predicate[list.size()];
                return criteriaBuilder.and(list.toArray(p));
            }
        }, pageable);
        return coversHelpRecordDTO(helpRecords);
    }

    @Override
    public String checkExistsGiveing(long giverId) {
        GiveRecord giveRecord = giveRecordRepository.findByGiverIdAndAuditStatus(giverId, AuditEnum.WAIT_UPLOAD.getCode());
        if (giveRecord != null) {
            HelpRecord helpRecord = helpRecordRepository.findById(giveRecord.getHelpRecordId()).orElse(null);
            Literature literature = helpRecord != null ? literatureRepository.findById(helpRecord.getLiteratureId()).orElse(null) : null;
            return literature != null ? literature.getDocTitle() : null;
        } else {
            return null;
        }
    }

    @Override
    public Permission getOrgIdAndRule(Long orgId, int rule) {
        return permissionRepository.getOrgIdAndRule(orgId, rule);
    }

    private HelpRecordDTO anonymous(VHelpRecord helpRecord) {
        HelpRecordDTO helpRecordDTO = new HelpRecordDTO();
        BeanUtil.copyProperties(helpRecord, helpRecordDTO);
        if (helpRecord.isAnonymous()) {
            helpRecordDTO.setHelperEmail("匿名").setHelperName("匿名");
        } else {
            String helperEmail = helpRecord.getHelperEmail();
            String s = helperEmail.replaceAll("(\\w?)(\\w+)(\\w)(@\\w+\\.[a-z]+(\\.[a-z]+)?)", "$1****$3$4");
            helpRecordDTO.setHelperEmail(s);
        }
        return helpRecordDTO;
    }


}
