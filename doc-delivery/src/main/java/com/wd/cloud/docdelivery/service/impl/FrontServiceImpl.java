package com.wd.cloud.docdelivery.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.http.HtmlUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.wd.cloud.commons.exception.UndefinedException;
import com.wd.cloud.commons.model.ResponseModel;
import com.wd.cloud.commons.util.DateUtil;
import com.wd.cloud.docdelivery.config.Global;
import com.wd.cloud.docdelivery.entity.*;
import com.wd.cloud.docdelivery.enums.AuditEnum;
import com.wd.cloud.docdelivery.enums.GiveTypeEnum;
import com.wd.cloud.docdelivery.enums.HelpStatusEnum;
import com.wd.cloud.docdelivery.exception.AppException;
import com.wd.cloud.docdelivery.exception.ExceptionEnum;
import com.wd.cloud.docdelivery.feign.PdfSearchServerApi;
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

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;


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
    VHelpRecordRepository vHelpRecordRepository;

    @Autowired
    GiveRecordRepository giveRecordRepository;

    @Autowired
    FileService fileService;

    @Autowired
    MailService mailService;

    @Autowired
    PdfSearchServerApi pdfSearchServerApi;

    @Autowired
    DocFileRepository docFileRepository;

    @Override
    public Literature queryLiterature(Literature literature) {
        Literature literatureData;
        if (StrUtil.isNotEmpty(literature.getDocHref())) {
            literatureData = literatureRepository.findByDocTitleAndDocHref(literature.getDocTitle(), literature.getDocHref());
        } else {
            literatureData = literatureRepository.findByDocTitle(literature.getDocTitle());
        }
        return literatureData;
    }


    @Override
    public String help(HelpRecord helpRecord, Literature literature) {
        // 防止调用者传过来的docTitle包含HTML标签，在这里将标签去掉
        String docTitle = literature.getDocTitle();
        String docHref = literature.getDocHref();
        String unid = SecureUtil.md5(docTitle + docHref);
        log.info("用户:[{}]正在求助文献:[{}]", helpRecord.getHelperEmail(), docTitle);

        // 如果元数据已存在，则更新
        if (literatureRepository.existsByUnid(unid)) {
            Literature oldLiterature = literatureRepository.findByUnid(unid);
            literature.setId(oldLiterature.getId());
        }
        literature = literatureRepository.save(literature);
        // 15天内不能重复求助相同的文献
        if (checkExists(helpRecord.getHelperEmail(), literature.getId())) {
            throw new AppException(ExceptionEnum.HELP_CHONGFU);
        }
        helpRecord.setLiterature(literature);
        DocFile reusingDocFile = docFileRepository.findByLiteratureAndReusingIsTrue(literature);
        // 如果有复用文件，自动应助成功
        if (null != reusingDocFile) {
            helpRecord.setStatus(HelpStatusEnum.HELP_SUCCESSED.value());
            GiveRecord giveRecord = new GiveRecord();
            giveRecord.setDocFile(reusingDocFile);
            giveRecord.setGiverType(GiveTypeEnum.AUTO.value());
            giveRecord.setGiverName("自动应助");
            //先保存求助记录，得到求助ID，再关联应助记录
            helpRecord = saveHelpRecord(helpRecord);
            giveRecord.setHelpRecord(helpRecord);
            saveGiveRecord(giveRecord);
            Optional<VHelpRecord> optionalVHelpRecord = vHelpRecordRepository.findById(helpRecord.getId());
            optionalVHelpRecord.ifPresent(vHelpRecord -> mailService.sendMail(vHelpRecord));
            return "复用自动应助成功！";
        } else {
            ResponseModel<String> pdfResponse = pdfSearchServerApi.search(literature);
            if (!pdfResponse.isError()) {
                String fileId = pdfResponse.getBody();
                DocFile docFile = docFileRepository.findByFileIdAndLiterature(fileId, literature).orElse(new DocFile());
                docFile.setFileId(fileId).setLiterature(literature);
                docFile = docFileRepository.save(docFile);
                GiveRecord giveRecord = new GiveRecord();
                giveRecord.setDocFile(docFile);
                giveRecord.setGiverType(GiveTypeEnum.BIG_DB.value());
                giveRecord.setGiverName(GiveTypeEnum.BIG_DB.name());
                //先保存求助记录，得到求助ID，再关联应助记录
                helpRecord.setStatus(HelpStatusEnum.HELP_SUCCESSED.value());
                helpRecord = helpRecordRepository.save(helpRecord);
                giveRecord.setHelpRecord(helpRecord);
                giveRecordRepository.save(giveRecord);
                Optional<VHelpRecord> optionalVHelpRecord = vHelpRecordRepository.findById(helpRecord.getId());
                optionalVHelpRecord.ifPresent(vHelpRecord -> mailService.sendMail(vHelpRecord));
                return "平台自动应助成功！";
            }
            try {
                // 保存求助记录
                helpRecordRepository.save(helpRecord);
                return "求助已发送成功，请等待";
            } catch (Exception e) {
                throw new UndefinedException(e);
            }
        }
    }

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
        DocFile docFile = docFileRepository.findByLiteratureAndFileId(literature, fileId);
        if (docFile == null) {
            docFile = new DocFile();
        }
        docFile.setFileId(fileId);
        docFile.setLiterature(literature);
        docFile.setAuditStatus(0);
        docFile = docFileRepository.save(docFile);
        return docFile;
    }

    @Override
    public HelpRecord givingHelp(long helpRecordId, long giverId, String giverName, String giverIp) {
        HelpRecord helpRecord = helpRecordRepository.findByIdAndStatus(helpRecordId, HelpStatusEnum.WAIT_HELP.value());
        if (helpRecord != null) {
            helpRecord.setStatus(HelpStatusEnum.HELPING.value());
            GiveRecord giveRecord = new GiveRecord();
            giveRecord.setHelpRecord(helpRecord);
            giveRecord.setGiverId(giverId);
            giveRecord.setGiverIp(giverIp);
            giveRecord.setGiverName(giverName);
            giveRecord.setGiverType(GiveTypeEnum.USER.value());
            giveRecord.setAuditStatus(AuditEnum.WAIT_UPLOAD.value());
            //保存的同时，关联更新求助记录状态
            giveRecordRepository.save(giveRecord);
        }
        return helpRecord;
    }

    @Override
    public boolean cancelGivingHelp(long helpRecordId, long giverId) {
        HelpRecord helpRecord = helpRecordRepository.findByIdAndStatus(helpRecordId, HelpStatusEnum.HELPING.value());
        boolean flag = false;
        if (helpRecord != null) {
            giveRecordRepository.deleteByHelpRecordAndAuditStatusAndGiverId(helpRecord, AuditEnum.WAIT_UPLOAD.value(), giverId);
            //更新求助记录状态
            helpRecord.setStatus(HelpStatusEnum.WAIT_HELP.value());
            helpRecordRepository.save(helpRecord);
            flag = true;
        }
        return flag;
    }

    @Override
    public HelpRecord getHelpingRecord(long helpRecordId) {
        return helpRecordRepository.findByIdAndStatus(helpRecordId, HelpStatusEnum.HELPING.value());
    }

    @Override
    public int getCountHelpRecordToDay(String email) {
        return helpRecordRepository.findByHelperEmailAndGmtCreateAfter(email, DateUtil.beginOfDay(new Date()).toSqlDate()).size();
    }

    @Override
    public HelpRecord getWaitOrThirdHelpRecord(Long id) {
        return helpRecordRepository.findByIdAndStatusIn(id,
                new int[]{HelpStatusEnum.WAIT_HELP.value(), HelpStatusEnum.HELP_THIRD.value()});
    }

    @Override
    public HelpRecord getNotWaitRecord(long helpRecordId) {
        return helpRecordRepository.findByIdAndStatusNot(helpRecordId, HelpStatusEnum.WAIT_HELP.value());
    }

    @Override
    public String clearHtml(String docTitle) {
        return HtmlUtil.unescape(HtmlUtil.cleanHtmlTag(docTitle));
    }

    @Override
    public Literature saveLiterature(Literature literature) {
        return literatureRepository.save(literature);
    }

    @Override
    public GiveRecord saveGiveRecord(GiveRecord giveRecord) {
        return giveRecordRepository.save(giveRecord);
    }

    @Override
    public void createGiveRecord(HelpRecord helpRecord, long giverId, DocFile docFile, String giveIp) {
        //更新求助状态为待审核
        helpRecord.setStatus(HelpStatusEnum.WAIT_AUDIT.value());
        GiveRecord giveRecord = giveRecordRepository.findByHelpRecordAndAuditStatusAndGiverId(helpRecord, AuditEnum.WAIT_UPLOAD.value(), giverId);
        //关联应助记录
        giveRecord.setDocFile(docFile);
        giveRecord.setGiverId(giverId);
        giveRecord.setGiverIp(giveIp);
        giveRecord.setHelpRecord(helpRecord);
        //前台上传的，需要后台人员再审核
        giveRecord.setAuditStatus(AuditEnum.WAIT.value());
        giveRecordRepository.save(giveRecord);

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
    public Page<HelpRecord> getHelpRecordsForUser(long helperId, Pageable pageable) {
        return helpRecordRepository.findByHelperId(helperId, pageable);
    }

    @Override
    public Page<HelpRecord> getHelpRecordsForEmail(String helperEmail, Pageable pageable) {
        return helpRecordRepository.findByHelperEmail(helperEmail, pageable);
    }

    @Override
    public Page<HelpRecord> getWaitHelpRecords(int helpChannel, Pageable pageable) {

        int[] status = {HelpStatusEnum.WAIT_HELP.value(),
                HelpStatusEnum.HELPING.value(),
                HelpStatusEnum.WAIT_AUDIT.value(),
                HelpStatusEnum.HELP_THIRD.value()};

        Page<HelpRecord> waitHelpRecords = filterHelpRecords(helpChannel, pageable, status);
//        Page<HelpRecord> waitHelpRecords = helpRecordRepository.findAll(new Specification<HelpRecord>() {
//            @Override
//            public Predicate toPredicate(Root<HelpRecord> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
//                List<Predicate> list = new ArrayList<Predicate>();
//                list.add(cb.equal(root.get("helpChannel").as(Integer.class), helpChannel));
//                //list.add(root.<Integer>get("status").in(status));
//                list.add(cb.equal(root.joinSet("giveRecords").get("auditStatus").as(Integer.class),2));
//                Predicate[] p = new Predicate[list.size()];
//                return cb.and(list.toArray(p));
//            }
//        }, pageable);

        for (HelpRecord helpRecord : waitHelpRecords) {
            helpRecord.filterByNotEq("auditStatus", 2);
        }
        return waitHelpRecords;
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
    public Page<HelpRecord> getFinishHelpRecords(int helpChannel, Pageable pageable) {
        int[] status = {HelpStatusEnum.HELP_SUCCESSED.value(), HelpStatusEnum.HELP_FAILED.value()};
        Page<HelpRecord> finishHelpRecords = filterHelpRecords(helpChannel, pageable, status);
        return finishHelpRecords;
    }

    @Override
    public Page<HelpRecord> getSuccessHelpRecords(int helpChannel, Pageable pageable) {
        int[] status = {HelpStatusEnum.HELP_SUCCESSED.value()};
        Page<HelpRecord> successHelpRecords = filterHelpRecords(helpChannel, pageable, status);
        return successHelpRecords;
    }

    @Override
    public Page<HelpRecord> getFailedHelpRecords(int helpChannel, Pageable pageable) {
        int[] status = {HelpStatusEnum.HELP_FAILED.value()};
        Page<HelpRecord> failedHelpRecords = filterHelpRecords(helpChannel, pageable, status);
        return failedHelpRecords;
    }

    @Override
    public Page<HelpRecord> getAllHelpRecord(Pageable pageable) {
        return helpRecordRepository.findAll(pageable);
    }

    @Override
    public DocFile getReusingFile(Literature literature) {
        DocFile docFiles = docFileRepository.findByLiteratureAndReusingIsTrue(literature);
        return docFiles;
    }

    @Override
    public Page<HelpRecord> search(String keyword, Pageable pageable) {
        Page<HelpRecord> helpRecords = helpRecordRepository.findAll(new Specification<HelpRecord>() {
            @Override
            public Predicate toPredicate(Root<HelpRecord> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                List<Predicate> list = new ArrayList<Predicate>();
                if (!StrUtil.isEmpty(keyword)) {
                    list.add(criteriaBuilder.or(criteriaBuilder.like(root.get("literature").get("docTitle").as(String.class), "%" + keyword.trim() + "%"), criteriaBuilder.like(root.get("helperEmail").as(String.class), "%" + keyword.trim() + "%")));
                }
                Predicate[] p = new Predicate[list.size()];
                return criteriaBuilder.and(list.toArray(p));
            }
        }, pageable);
        return helpRecords;
    }

    @Override
    public String checkExistsGiveing(long giverId) {
        GiveRecord giveRecord = giveRecordRepository.findByGiverIdAndAuditStatus(giverId, AuditEnum.WAIT_UPLOAD.value());
        if (giveRecord != null) {
            return giveRecord.getHelpRecord().getLiterature().getDocTitle();
        } else {
            return null;
        }
    }


}
