package com.wd.cloud.docdelivery.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.http.HtmlUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.wd.cloud.commons.constant.SessionConstant;
import com.wd.cloud.commons.dto.UserDTO;
import com.wd.cloud.commons.exception.FeignException;
import com.wd.cloud.commons.model.ResponseModel;
import com.wd.cloud.commons.util.FileUtil;
import com.wd.cloud.docdelivery.config.Global;
import com.wd.cloud.docdelivery.dto.GiveRecordDTO;
import com.wd.cloud.docdelivery.dto.HelpRecordDTO;
import com.wd.cloud.docdelivery.entity.*;
import com.wd.cloud.docdelivery.enums.GiveStatusEnum;
import com.wd.cloud.docdelivery.enums.GiveTypeEnum;
import com.wd.cloud.docdelivery.enums.HelpStatusEnum;
import com.wd.cloud.docdelivery.exception.*;
import com.wd.cloud.docdelivery.feign.FsServerApi;
import com.wd.cloud.docdelivery.feign.PdfSearchServerApi;
import com.wd.cloud.docdelivery.repository.*;
import com.wd.cloud.docdelivery.service.FileService;
import com.wd.cloud.docdelivery.service.FrontService;
import com.wd.cloud.docdelivery.service.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
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
    MailService mailService;

    @Autowired
    PermissionRepository permissionRepository;

    @Autowired
    VHelpRecordRepository vHelpRecordRepository;


    @Override
    public boolean checkExists(String email, Long literatureId) {
        HelpRecord helpRecord = helpRecordRepository.findByHelperEmailAndLiteratureId(email, literatureId);
        if (helpRecord != null) {
            return true;
        }
        return false;
    }

    @Override
    public DocFile saveDocFile(Long literatureId, String fileId, String filaName) {
        DocFile docFile = docFileRepository.findByLiteratureIdAndFileId(literatureId, fileId).orElse(new DocFile());
        docFile.setFileId(fileId);
        docFile.setLiteratureId(literatureId);
        return docFileRepository.save(docFile);
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
            throw new HelpException(2008, "error:您最近15天内已求助过这篇文献,请注意查收邮箱");
        }
        helpRecord.setLiteratureId(literature.getId());
        DocFile reusingDocFile = docFileRepository.findByLiteratureIdAndReusingIsTrue(literature.getId());
        // 如果有复用文件，自动应助成功
        if (null != reusingDocFile) {
            GiveRecord giveRecord = new GiveRecord();
            giveRecord.setFileId(reusingDocFile.getFileId());
            giveRecord.setType(GiveTypeEnum.AUTO.getCode());
            giveRecord.setGiverName(GiveTypeEnum.AUTO.getName());
            //先保存求助记录，得到求助ID，再关联应助记录
            helpRecord.setStatus(HelpStatusEnum.HELP_SUCCESSED.getValue());
            helpRecord = helpRecordRepository.save(helpRecord);
            giveRecord.setHelpRecordId(helpRecord.getId());
            giveRecordRepository.save(giveRecord);
            Optional<VHelpRecord> optionalVHelpRecord = vHelpRecordRepository.findById(helpRecord.getId());
            optionalVHelpRecord.ifPresent(vHelpRecord -> mailService.sendMail(vHelpRecord));
            return "复用自动应助成功！";
        } else {
            ResponseModel<String> pdfResponse = pdfSearchServerApi.search(literature);
            if (!pdfResponse.isError()) {
                String fileId = pdfResponse.getBody();
                DocFile docFile = docFileRepository.findByFileIdAndLiteratureId(fileId, literature.getId()).orElse(new DocFile());
                docFile.setFileId(fileId).setLiteratureId(literature.getId());
                docFileRepository.save(docFile);
                GiveRecord giveRecord = new GiveRecord();
                giveRecord.setFileId(fileId);
                giveRecord.setType(GiveTypeEnum.BIG_DB.getCode());
                giveRecord.setGiverName(GiveTypeEnum.BIG_DB.getName());
                //先保存求助记录，得到求助ID，再关联应助记录
                helpRecord.setStatus(HelpStatusEnum.HELP_SUCCESSED.getValue());
                helpRecord = helpRecordRepository.save(helpRecord);
                giveRecord.setHelpRecordId(helpRecord.getId());
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
                throw new HelpException(1002, "主键冲突");
            }
        }
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
        DocFile docFile = saveDocFile(literature.getId(), fileId, fileName);
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
            giveRecord.setStatus(GiveStatusEnum.WAIT_UPLOAD.getValue());
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
            giveRecordRepository.deleteByHelpRecordIdAndStatusAndGiverId(helpRecordId, GiveStatusEnum.WAIT_UPLOAD.getValue(), giverId);
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
    public Long getCountHelpRecordToDay(String email) {
        return helpRecordRepository.countByHelperEmailToday(email);
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
    public void createGiveRecord(HelpRecord helpRecord, long giverId, DocFile docFile, String giveIp) {
        //更新求助状态为待审核
        helpRecord.setStatus(HelpStatusEnum.WAIT_AUDIT.getValue());
        GiveRecord giveRecord = giveRecordRepository.findByHelpRecordIdAndStatusAndGiverId(helpRecord.getId(), GiveStatusEnum.WAIT_UPLOAD.getValue(), giverId);
        //关联应助记录
        giveRecord.setFileId(docFile.getFileId());
        giveRecord.setGiverId(giverId);
        giveRecord.setGiverIp(giveIp);
        giveRecord.setHelpRecordId(helpRecord.getId());
        //前台上传的，需要后台人员再审核
        giveRecord.setStatus(GiveStatusEnum.WAIT_AUDIT.getValue());
        giveRecordRepository.save(giveRecord);
        helpRecordRepository.save(helpRecord);

    }

    @Override
    public Page<HelpRecordDTO> myHelpRecords(List<Integer> status, Pageable pageable) {
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        HttpSession session = request.getSession();
        UserDTO userDTO = (UserDTO) session.getAttribute(SessionConstant.LOGIN_USER);
        if (userDTO == null) {
            throw new AuthException();
        }
        Page<VHelpRecord> vHelpRecords = vHelpRecordRepository.findAll(VHelpRecordRepository.SpecificationBuilder.buildVhelpRecord(null, status, null, userDTO.getId(), null), pageable);
        return coversHelpRecordDTO(vHelpRecords);
    }

    @Override
    public Page<GiveRecordDTO> myGiveRecords(List<Integer> status, Pageable pageable) {
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        HttpSession session = request.getSession();
        UserDTO userDTO = (UserDTO) session.getAttribute(SessionConstant.LOGIN_USER);
        if (userDTO == null) {
            throw new AuthException();
        }
        Page<GiveRecord> giveRecords = giveRecordRepository.findAll(GiveRecordRepository.SpecificationBuilder.buildGiveRecord(status, userDTO.getId()), pageable);
        return coversGiveRecordDTO(giveRecords);
    }


    @Override
    public Page<HelpRecordDTO> getHelpRecords(List<Integer> channel, List<Integer> status, String email, String keyword, Pageable pageable) {
        Page<VHelpRecord> vHelpRecords = vHelpRecordRepository.findAll(VHelpRecordRepository.SpecificationBuilder.buildVhelpRecord(channel, status, null, null, null), pageable);
        return coversHelpRecordDTO(vHelpRecords);
    }


    @Override
    public Page<HelpRecordDTO> getWaitHelpRecords(List<Integer> channel, Pageable pageable) {

        List<Integer> status = CollectionUtil.newArrayList(
                HelpStatusEnum.WAIT_HELP.getValue(),
                HelpStatusEnum.HELPING.getValue(),
                HelpStatusEnum.WAIT_AUDIT.getValue(),
                HelpStatusEnum.HELP_THIRD.getValue());

        Page<VHelpRecord> waitHelpRecords = vHelpRecordRepository.findAll(VHelpRecordRepository.SpecificationBuilder.buildVhelpRecord(channel, status, null, null, null), pageable);
        return waitHelpRecords.map(helpRecord -> {
            HelpRecordDTO helpRecordDTO = new HelpRecordDTO();
            BeanUtil.copyProperties(anonymous(helpRecord), helpRecordDTO);
            Optional<Literature> optionalLiterature = literatureRepository.findById(helpRecord.getLiteratureId());
            optionalLiterature.ifPresent(literature -> helpRecordDTO.setDocTitle(literature.getDocTitle()).setDocHref(literature.getDocHref()));
            //如果有用户正在应助
            if (helpRecord.getStatus() == HelpStatusEnum.HELPING.getValue()) {
                Optional<GiveRecord> optionalGiveRecord = giveRecordRepository.findByHelpRecordIdAndStatus(helpRecord.getId(), GiveStatusEnum.WAIT_UPLOAD.getValue());
                optionalGiveRecord.ifPresent(helpRecordDTO::setGiving);
            }
            return helpRecordDTO;
        });
    }

    @Override
    public Page<HelpRecordDTO> getFinishHelpRecords(List<Integer> channel, Pageable pageable) {
        List<Integer> status = CollectionUtil.newArrayList(HelpStatusEnum.HELP_SUCCESSED.getValue(), HelpStatusEnum.HELP_FAILED.getValue());
        Page<VHelpRecord> finishHelpRecords = vHelpRecordRepository.findAll(VHelpRecordRepository.SpecificationBuilder.buildVhelpRecord(channel, status, null, null, null), pageable);
        return coversHelpRecordDTO(finishHelpRecords);
    }


    @Override
    public Page<HelpRecordDTO> getSuccessHelpRecords(List<Integer> channel, Pageable pageable) {
        List<Integer> status = CollectionUtil.newArrayList(HelpStatusEnum.HELP_SUCCESSED.getValue());
        Page<VHelpRecord> finishHelpRecords = vHelpRecordRepository.findAll(VHelpRecordRepository.SpecificationBuilder.buildVhelpRecord(channel, status, null, null, null), pageable);
        return coversHelpRecordDTO(finishHelpRecords);

    }

    @Override
    public Page<HelpRecordDTO> getFailedHelpRecords(List<Integer> channel, Pageable pageable) {
        List<Integer> status = CollectionUtil.newArrayList(HelpStatusEnum.HELP_FAILED.getValue());
        Page<VHelpRecord> finishHelpRecords = vHelpRecordRepository.findAll(VHelpRecordRepository.SpecificationBuilder.buildVhelpRecord(channel, status, null, null, null), pageable);
        return coversHelpRecordDTO(finishHelpRecords);
    }


    @Override
    public DocFile getReusingFile(Long literatureId) {
        return docFileRepository.findByLiteratureIdAndReusingIsTrue(literatureId);
    }


    @Override
    public String checkExistsGiveing(long giverId) {
        GiveRecord giveRecord = giveRecordRepository.findByGiverIdGiving(giverId);
        if (giveRecord != null) {
            HelpRecord helpRecord = helpRecordRepository.findById(giveRecord.getHelpRecordId()).orElse(null);
            Literature literature = helpRecord != null ? literatureRepository.findById(helpRecord.getLiteratureId()).orElse(null) : null;
            return literature != null ? literature.getDocTitle() : null;
        } else {
            return null;
        }
    }

    @Override
    public Permission nextLevel(Long orgId, Integer level) {
        int nextLevel = nexLevel(level);
        return getPermission(orgId, nextLevel);
    }

    @Override
    public Permission getPermission(Long orgId, Integer level) {
        Permission permission = null;
        if (orgId != null) {
            permission = permissionRepository.getOrgIdAndLevel(orgId, level);
        }
        if (permission == null) {
            permission = permissionRepository.findByOrgIdIsNullAndLevel(level);
        }
        return permission;
    }

    private int nexLevel(int level) {
        switch (level) {
            case '0':
                return 2;
            case '1':
                return 3;
            case '2':
                return 6;
            case '3':
                return 7;
            default:
                return 0;
        }
    }


    private Page<HelpRecordDTO> coversHelpRecordDTO(Page<VHelpRecord> helpRecordPage) {
        return helpRecordPage.map(helpRecord -> {
            HelpRecordDTO helpRecordDTO = new HelpRecordDTO();
            BeanUtil.copyProperties(anonymous(helpRecord), helpRecordDTO);
            Literature literature = literatureRepository.findById(helpRecord.getLiteratureId()).orElse(null);
            helpRecordDTO.setDocTitle(literature.getDocTitle()).setDocHref(literature.getDocHref());
            return helpRecordDTO;
        });
    }

    private Page<GiveRecordDTO> coversGiveRecordDTO(Page<GiveRecord> giveRecordPage) {
        return giveRecordPage.map(giveRecord -> {
            GiveRecordDTO giveRecordDTO = new GiveRecordDTO();
            BeanUtil.copyProperties(giveRecord, giveRecordDTO);
            return giveRecordDTO;
        });
    }

    private VHelpRecord anonymous(VHelpRecord vHelpRecord) {
        if (vHelpRecord.isAnonymous()) {
            vHelpRecord.setHelperEmail("匿名").setHelperName("匿名");
        } else {
            String helperEmail = vHelpRecord.getHelperEmail();
            String s = helperEmail.replaceAll("(\\w?)(\\w+)(\\w)(@\\w+\\.[a-z]+(\\.[a-z]+)?)", "$1****$3$4");
            vHelpRecord.setHelperEmail(s);
        }
        return vHelpRecord;
    }


}
