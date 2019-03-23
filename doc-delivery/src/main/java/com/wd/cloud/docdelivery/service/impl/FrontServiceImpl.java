package com.wd.cloud.docdelivery.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.http.HtmlUtil;
import cn.hutool.json.JSONObject;
import com.wd.cloud.commons.exception.FeignException;
import com.wd.cloud.commons.exception.NotFoundException;
import com.wd.cloud.commons.exception.UndefinedException;
import com.wd.cloud.commons.model.ResponseModel;
import com.wd.cloud.commons.util.FileUtil;
import com.wd.cloud.commons.util.StrUtil;
import com.wd.cloud.docdelivery.config.Global;
import com.wd.cloud.docdelivery.pojo.dto.GiveRecordDTO;
import com.wd.cloud.docdelivery.pojo.dto.HelpRecordDTO;
import com.wd.cloud.docdelivery.pojo.entity.*;
import com.wd.cloud.docdelivery.enums.GiveStatusEnum;
import com.wd.cloud.docdelivery.enums.GiveTypeEnum;
import com.wd.cloud.docdelivery.enums.HelpStatusEnum;
import com.wd.cloud.docdelivery.exception.AppException;
import com.wd.cloud.docdelivery.exception.ExceptionEnum;
import com.wd.cloud.docdelivery.feign.FsServerApi;
import com.wd.cloud.docdelivery.feign.PdfSearchServerApi;
import com.wd.cloud.docdelivery.repository.*;
import com.wd.cloud.docdelivery.service.FileService;
import com.wd.cloud.docdelivery.service.FrontService;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;


/**
 * @author He Zhigang
 * @date 2018/5/7
 * @Description:
 */
@Slf4j
@Service("frontService")
@Transactional(rollbackFor = Exception.class)
public class FrontServiceImpl implements FrontService {

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
    PdfSearchServerApi pdfSearchServerApi;

    @Autowired
    FileService fileService;

    @Autowired
    PermissionRepository permissionRepository;

    @Autowired
    VHelpRecordRepository vHelpRecordRepository;


    @Override
    public boolean checkExists(String email, Long literatureId) {
        HelpRecord helpRecord = helpRecordRepository.findByHelperEmailAndLiteratureId(email, literatureId);
        return helpRecord != null;
    }

    @Override
    public DocFile saveDocFile(Long literatureId, String fileId, String filaName) {
        DocFile docFile = docFileRepository.findByLiteratureIdAndFileId(literatureId, fileId).orElse(new DocFile());
        docFile.setFileId(fileId);
        docFile.setLiteratureId(literatureId);
        return docFileRepository.save(docFile);
    }

    @Override
    public String help(HelpRecord helpRecord, Literature literature) throws ConstraintViolationException {
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
        helpRecord.setLiteratureId(literature.getId());
        // 15天内不能重复求助相同的文献
        if (checkExists(helpRecord.getHelperEmail(), literature.getId())) {
            throw new AppException(ExceptionEnum.HELP_CHONGFU);
        }
        DocFile reusingDocFile = docFileRepository.findByLiteratureIdAndReusingIsTrue(literature.getId());
        // 如果有复用文件，自动应助成功
        if (null != reusingDocFile) {
            autoGive(reusingDocFile, helpRecord);
            return "复用自动应助成功！";
        }
        bigDbGive(literature, helpRecord);
        helpRecordRepository.save(helpRecord);
        return "求助已发送成功，请等待";

    }

    /**
     * 自动应助
     *
     * @param reusingDocFile
     * @param helpRecord
     */
    public void autoGive(DocFile reusingDocFile, HelpRecord helpRecord) {
        //先保存求助记录，得到求助ID，再关联应助记录
        helpRecord.setStatus(HelpStatusEnum.HELP_SUCCESSED.value());
        helpRecord = helpRecordRepository.save(helpRecord);
        GiveRecord giveRecord = new GiveRecord();
        giveRecord.setFileId(reusingDocFile.getFileId())
                .setType(GiveTypeEnum.AUTO.value())
                .setGiverName(GiveTypeEnum.AUTO.name())
                .setStatus(GiveStatusEnum.SUCCESS.value())
                .setHelpRecordId(helpRecord.getId());
        giveRecordRepository.save(giveRecord);
    }

    /**
     * 数据平台应助
     *
     * @param literature
     * @param helpRecord
     */
    @Async
    public void bigDbGive(Literature literature, HelpRecord helpRecord) {
        try {
            ResponseModel<String> pdfResponse = pdfSearchServerApi.search(literature);
            if (!pdfResponse.isError()) {
                //先保存求助记录，得到求助ID，再关联应助记录
                helpRecord.setStatus(HelpStatusEnum.HELP_SUCCESSED.value());
                helpRecord = helpRecordRepository.save(helpRecord);
                String fileId = pdfResponse.getBody();
                DocFile docFile = docFileRepository.findByFileIdAndLiteratureId(fileId, literature.getId()).orElse(new DocFile());
                docFile.setFileId(fileId).setLiteratureId(literature.getId()).setBigDb(true);
                docFileRepository.save(docFile);
                GiveRecord giveRecord = new GiveRecord();
                giveRecord.setFileId(fileId)
                        .setType(GiveTypeEnum.BIG_DB.value())
                        .setGiverName(GiveTypeEnum.BIG_DB.name())
                        .setStatus(GiveStatusEnum.SUCCESS.value());
                giveRecord.setHelpRecordId(helpRecord.getId());
                giveRecordRepository.save(giveRecord);
            }
        } catch (Exception e) {
            log.warn("pdfsearch-server调用失败");
        }

    }

    @Override
    public void give(Long helpRecordId, String giverName, String ip) {
        Optional<HelpRecord> optionalHelpRecord = helpRecordRepository.findById(helpRecordId);

        optionalHelpRecord.ifPresent(helpRecord -> {
            // 该求助记录状态为应助中，表示已经被其它用户认领
            if (HelpStatusEnum.HELPING.value() == helpRecord.getStatus()) {
                throw new AppException(ExceptionEnum.GIVE_ING);
            }
            // 可认领待应助，求助第三方或疑难状态的求助
            if (helpRecord.getStatus() == HelpStatusEnum.HELP_THIRD.value()
                    || helpRecord.getStatus() == HelpStatusEnum.WAIT_HELP.value()
                    || helpRecord.getStatus() == HelpStatusEnum.HELP_FAILED.value()) {
                //检查用户是否已经认领了应助
                String docTitle = findGivingDocTitle(giverName);
                if (docTitle != null) {
                    throw new AppException(ExceptionEnum.GIVE_CLAIM.status(), "请先完成您正在应助的文献:" + docTitle);
                }
                GiveRecord giveRecord = new GiveRecord();
                giveRecord.setHelpRecordId(helpRecordId)
                        .setGiverIp(ip)
                        .setGiverName(giverName)
                        .setType(GiveTypeEnum.USER.value())
                        .setStatus(GiveStatusEnum.WAIT_UPLOAD.value());
                helpRecord.setStatus(HelpStatusEnum.HELPING.value());
                //保存的同时，关联更新求助记录状态
                giveRecordRepository.save(giveRecord);
                helpRecordRepository.save(helpRecord);
            } else {
                throw new NotFoundException();
            }
        });
    }

    @Override
    public void uploadFile(HelpRecord helpRecord, String giverName, MultipartFile file, String ip) {
        String fileId = null;
        try {
            String fileMd5 = FileUtil.fileMd5(file.getInputStream());
            ResponseModel<JSONObject> checkResult = fsServerApi.checkFile(global.getHbaseTableName(), fileMd5);
            if (!checkResult.isError() && checkResult.getBody() != null) {
                log.info("文件已存在，秒传成功！");
                fileId = checkResult.getBody().getStr("fileId");
            }
        } catch (IOException e) {
            throw new UndefinedException(e);
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
        Optional<Literature> optionalLiterature = literatureRepository.findById(helpRecord.getLiteratureId());
        if (optionalLiterature.isPresent()) {
            DocFile docFile = saveDocFile(optionalLiterature.get().getId(), fileId, fileName);
            //更新记录
            createGiveRecord(helpRecord, giverName, docFile, ip);
        }

    }


    @Override
    public boolean cancelGivingHelp(long helpRecordId, String giverName) {
        //检查用户是否已经认领了应助
        String docTitle = findGivingDocTitle(giverName);
        boolean flag = false;
        if (docTitle != null) {
            HelpRecord helpRecord = helpRecordRepository.findByIdAndStatus(helpRecordId, HelpStatusEnum.HELPING.value());
            if (helpRecord != null) {
                GiveRecord giveRecord = giveRecordRepository.findByHelpRecordIdAndStatusAndGiverName(helpRecordId, GiveStatusEnum.WAIT_UPLOAD.value(), giverName);
                giveRecord.setStatus(GiveStatusEnum.CANCEL.value());
                //更新求助记录状态
                helpRecord.setStatus(helpRecord.isDifficult() ? HelpStatusEnum.HELP_FAILED.value() : HelpStatusEnum.WAIT_HELP.value());
                giveRecordRepository.save(giveRecord);
                helpRecordRepository.save(helpRecord);
                flag = true;
            }
        }
        return flag;
    }

    @Override
    public HelpRecord getHelpingRecord(long helpRecordId) {
        return helpRecordRepository.findByIdAndStatus(helpRecordId, HelpStatusEnum.HELPING.value());
    }

    @Override
    public Long getCountHelpRecordToDay(String email) {
        return helpRecordRepository.countByHelperEmailToday(email);
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
    public void createGiveRecord(HelpRecord helpRecord, String giverName, DocFile docFile, String giveIp) {
        //更新求助状态为待审核
        helpRecord.setStatus(HelpStatusEnum.WAIT_AUDIT.value());
        GiveRecord giveRecord = giveRecordRepository.findByHelpRecordIdAndStatusAndGiverName(helpRecord.getId(), GiveStatusEnum.WAIT_UPLOAD.value(), giverName);
        //关联应助记录
        giveRecord.setFileId(docFile.getFileId())
                .setGiverName(giverName)
                .setGiverIp(giveIp)
                .setHelpRecordId(helpRecord.getId());
        //前台上传的，需要后台人员再审核
        giveRecord.setStatus(GiveStatusEnum.WAIT_AUDIT.value());
        giveRecordRepository.save(giveRecord);
        helpRecordRepository.save(helpRecord);

    }

    @Override
    public Page<HelpRecordDTO> myHelpRecords(String username, List<Integer> status, Boolean isDifficult, Pageable pageable) {

        Page<VHelpRecord> vHelpRecords = vHelpRecordRepository.findAll(VHelpRecordRepository.SpecBuilder.buildVhelpRecord(null, status, null, username, null, isDifficult, null), pageable);
        return coversHelpRecordDTO(vHelpRecords);
    }

    @Override
    public Page<GiveRecordDTO> myGiveRecords(String giverName, List<Integer> status, Pageable pageable) {

        Page<GiveRecord> giveRecords = giveRecordRepository.findAll(GiveRecordRepository.SpecBuilder.buildGiveRecord(status, giverName), pageable);

        return coversGiveRecordDTO(giveRecords);
    }


    @Override
    public Page<HelpRecordDTO> getHelpRecords(List<Integer> channel, List<Integer> status, String email, String keyword, Boolean isDifficult, String orgFlag, Pageable pageable) {
        Page<VHelpRecord> vHelpRecords = vHelpRecordRepository.findAll(VHelpRecordRepository.SpecBuilder.buildVhelpRecord(channel, status, email, null, keyword, isDifficult, orgFlag), pageable);
        return coversHelpRecordDTO(vHelpRecords);
    }


    @Override
    public Page<HelpRecordDTO> getWaitHelpRecords(List<Integer> channel, Boolean isDifficult, String orgFlag, Pageable pageable) {

        List<Integer> status = CollectionUtil.newArrayList(
                HelpStatusEnum.WAIT_HELP.value(),
                HelpStatusEnum.HELPING.value(),
                HelpStatusEnum.HELP_THIRD.value());
        Page<VHelpRecord> waitHelpRecords = vHelpRecordRepository.findAll(VHelpRecordRepository.SpecBuilder.buildVhelpRecord(channel, status, null, null, null, isDifficult, orgFlag), pageable);
        return coversHelpRecordDTO(waitHelpRecords);
    }

    @Override
    public Page<HelpRecordDTO> getFinishHelpRecords(List<Integer> channel, String orgFlag, Pageable pageable) {
        List<Integer> status = CollectionUtil.newArrayList(HelpStatusEnum.HELP_SUCCESSED.value(), HelpStatusEnum.HELP_FAILED.value());
        Page<VHelpRecord> finishHelpRecords = vHelpRecordRepository.findAll(VHelpRecordRepository.SpecBuilder.buildVhelpRecord(channel, status, null, null, null, null, orgFlag), pageable);
        return coversHelpRecordDTO(finishHelpRecords);
    }


    @Override
    public Page<HelpRecordDTO> getSuccessHelpRecords(List<Integer> channel, String orgFlag, Pageable pageable) {
        List<Integer> status = CollectionUtil.newArrayList(HelpStatusEnum.HELP_SUCCESSED.value());
        Page<VHelpRecord> finishHelpRecords = vHelpRecordRepository.findAll(VHelpRecordRepository.SpecBuilder.buildVhelpRecord(channel, status, null, null, null, null, orgFlag), pageable);
        return coversHelpRecordDTO(finishHelpRecords);

    }

    @Override
    public Page<HelpRecordDTO> getFailedHelpRecords(List<Integer> channel, List<Integer> status, String orgFlag, Pageable pageable) {
        Page<VHelpRecord> finishHelpRecords = vHelpRecordRepository.findAll(VHelpRecordRepository.SpecBuilder.buildVhelpRecord(channel, status, null, null, null, true, orgFlag), pageable);
        return coversHelpRecordDTO(finishHelpRecords);
    }


    @Override
    public DocFile getReusingFile(Long literatureId) {
        return docFileRepository.findByLiteratureIdAndReusingIsTrue(literatureId);
    }


    /**
     * 查询用户正在应助的文献标题
     *
     * @param giverName
     * @return
     */
    private String findGivingDocTitle(String giverName) {
        GiveRecord giveRecord = giveRecordRepository.findByGiverNameGiving(giverName);
        if (giveRecord != null) {
            Optional<VHelpRecord> optionalVHelpRecord = vHelpRecordRepository.findById(giveRecord.getHelpRecordId());
            if (optionalVHelpRecord.isPresent()) {
                return optionalVHelpRecord.get().getDocTitle();
            }
        }
        return null;
    }

    @Override
    public Permission nextPermission(String orgFlag, Integer level) {
        int nextLevel = nexLevel(level);
        return getPermission(orgFlag, nextLevel);
    }

    @Override
    public Permission getPermission(String orgFlag, Integer level) {
        Permission permission = null;
        if (orgFlag != null) {
            permission = permissionRepository.getOrgFlagAndLevel(orgFlag, level);
        }
        if (permission == null) {
            permission = permissionRepository.findByOrgFlagIsNullAndLevel(level);
        }
        return permission;
    }

    private int nexLevel(int level) {
        switch (level) {
            case 0:
                return 2;
            case 1:
                return 3;
            case 2:
                return 6;
            case 3:
                return 7;
            default:
                return 0;
        }
    }


    private Page<HelpRecordDTO> coversHelpRecordDTO(Page<VHelpRecord> helpRecordPage) {
        return helpRecordPage.map(vHelpRecord -> {
            HelpRecordDTO helpRecordDTO = new HelpRecordDTO();
            BeanUtil.copyProperties(anonymous(vHelpRecord), helpRecordDTO);
            Optional<Literature> optionalLiterature = literatureRepository.findById(vHelpRecord.getLiteratureId());
            optionalLiterature.ifPresent(literature -> helpRecordDTO.setDocTitle(literature.getDocTitle()).setDocHref(literature.getDocHref()));
            //如果有用户正在应助
            if (vHelpRecord.getStatus() == HelpStatusEnum.HELPING.value()) {
                Optional<GiveRecord> optionalGiveRecord = giveRecordRepository.findByHelpRecordIdAndStatus(vHelpRecord.getId(), GiveStatusEnum.WAIT_UPLOAD.value());
                optionalGiveRecord.ifPresent(helpRecordDTO::setGiving);
            }
            return helpRecordDTO;
        });
    }

    private Page<GiveRecordDTO> coversGiveRecordDTO(Page<GiveRecord> giveRecordPage) {
        return giveRecordPage.map(giveRecord -> {
            GiveRecordDTO giveRecordDTO = new GiveRecordDTO();
            BeanUtil.copyProperties(giveRecord, giveRecordDTO);
            Optional<HelpRecord> optionalHelpRecord = helpRecordRepository.findById(giveRecord.getHelpRecordId());
            optionalHelpRecord.ifPresent(helpRecord -> {
                giveRecordDTO.setHelperEmail(helpRecord.isAnonymous() ? "匿名" : StrUtil.hideMailAddr(helpRecord.getHelperEmail()))
                        .setRemark(helpRecord.getRemark()).setOrgName(helpRecord.getOrgName());
                Optional<Literature> optionalLiterature = literatureRepository.findById(helpRecord.getLiteratureId());
                optionalLiterature.ifPresent(literature -> {
                    giveRecordDTO.setDocTitle(literature.getDocTitle()).setDocHref(literature.getDocHref());
                });
            });
            return giveRecordDTO;
        });
    }

    private VHelpRecord anonymous(VHelpRecord vHelpRecord) {
        if (vHelpRecord.isAnonymous()) {
            vHelpRecord.setHelperEmail("匿名").setHelperName("匿名");
        } else {
            String helperEmail = vHelpRecord.getHelperEmail();
            String s = StrUtil.hideMailAddr(helperEmail);
            vHelpRecord.setHelperEmail(s);
        }
        return vHelpRecord;
    }


}
