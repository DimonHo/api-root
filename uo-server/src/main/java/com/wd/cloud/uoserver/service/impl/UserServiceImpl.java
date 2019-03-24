package com.wd.cloud.uoserver.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import com.wd.cloud.commons.exception.FeignException;
import com.wd.cloud.commons.exception.NotFoundException;
import com.wd.cloud.commons.model.ResponseModel;
import com.wd.cloud.uoserver.constants.GlobalProperties;
import com.wd.cloud.uoserver.enums.OutsideEnum;
import com.wd.cloud.uoserver.enums.PermissionTypeEnum;
import com.wd.cloud.uoserver.exception.NotFoundOrgException;
import com.wd.cloud.uoserver.exception.NotFoundUserException;
import com.wd.cloud.uoserver.exception.UserExistsException;
import com.wd.cloud.uoserver.feign.FsServerApi;
import com.wd.cloud.uoserver.pojo.dto.OrgDTO;
import com.wd.cloud.uoserver.pojo.dto.UserDTO;
import com.wd.cloud.uoserver.pojo.entity.*;
import com.wd.cloud.uoserver.pojo.vo.BackUserVO;
import com.wd.cloud.uoserver.pojo.vo.PerfectUserVO;
import com.wd.cloud.uoserver.pojo.vo.PermissionVO;
import com.wd.cloud.uoserver.pojo.vo.UserVO;
import com.wd.cloud.uoserver.repository.*;
import com.wd.cloud.uoserver.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * @author He Zhigang
 * @date 2019/3/4
 * @Description:
 */
@Slf4j
@Service("userService")
@Transactional(rollbackFor = Exception.class)
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserMsgRepository userMsgRepository;

    @Autowired
    PermissionRepository permissionRepository;

    @Autowired
    OrgRepository orgRepository;

    @Autowired
    OrgDeptRepository orgDeptRepository;

    @Autowired
    FsServerApi fsServerApi;

    @Autowired
    HandlerLogRepository handlerLogRepository;

    @Autowired
    GlobalProperties globalProperties;

    /**
     * 注册新用户
     *
     * @param userVO
     * @return
     */
    @Override
    public User registerUser(UserVO userVO) {
        checkUserExists(userVO.getUsername(), userVO.getEmail());
        User user = BeanUtil.toBean(userVO, User.class);
        log.info("添加用户到数据库:{}",user.toString());
        user = userRepository.save(user);
        return user;
    }

    /**
     * 添加新用户
     *
     * @param backUserVO
     * @return
     */
    @Override
    public User addUser(BackUserVO backUserVO) {
        checkUserExists(backUserVO.getUsername(), backUserVO.getEmail());
        return saveUser(backUserVO);
    }

    @Override
    public User saveUser(BackUserVO backUserVO) {
        User user = userRepository.findByUsername(backUserVO.getUsername()).orElse(new User());
        BeanUtil.copyProperties(backUserVO, user);
        if (backUserVO.getOutside() != null){
            setOutsidePermission(backUserVO.getUsername(),OutsideEnum.FOREVER);
        }
        return userRepository.save(user);
    }

    /**
     * 完善用户信息，获得6个月校外访问权限
     *
     * @param perfectUserVO
     * @return
     */
    @Override
    public User perfectUser(PerfectUserVO perfectUserVO) {
        User user = userRepository.findByUsername(perfectUserVO.getUsername()).orElseThrow(NotFoundUserException::new);
        BeanUtil.copyProperties(perfectUserVO, user);
        // 完善信息自动获得6个月校外权限
        setOutsidePermission(user.getUsername(), OutsideEnum.HALF_YEAR);
        user = userRepository.save(user);
        return user;
    }

    /**
     * 根据用户名或邮箱获取用户
     *
     * @param id username or email
     * @return
     */
    @Override
    public UserDTO getUserDTO(String id) {
        User user = userRepository.findUser(id).orElseThrow(NotFoundException::new);
        UserDTO userDTO = new UserDTO();
        BeanUtil.copyProperties(user, userDTO);
        if (StrUtil.isNotBlank(user.getOrgFlag())) {
            Org org = orgRepository.findByFlag(user.getOrgFlag()).orElseThrow(NotFoundException::new);
            OrgDTO orgDTO = new OrgDTO();
            BeanUtil.copyProperties(org, orgDTO);
            userDTO.setOrg(orgDTO).setOrgName(org.getName());
            //如果有部門ID，則返回部門名稱
            if (user.getOrgDeptId() != null){
                Optional<OrgDept> optionalOrgDept = orgDeptRepository.findByOrgFlagAndId(user.getOrgFlag(),user.getOrgDeptId());
                optionalOrgDept.ifPresent(orgDept -> userDTO.setOrgDeptName(orgDept.getName()));
            }
        }
        // 加载用户权限
        List<Permission> permissionList = permissionRepository.findByUsername(userDTO.getUsername());
        if (CollectionUtil.isNotEmpty(permissionList)){
            userDTO.setPermissions(permissionList);
        }
        // 加载用户消息
        Pageable pageable = PageRequest.of(0,10, Sort.by("read").ascending());
        Page<UserMsg> userMsgPage = userMsgRepository.findByUsername(userDTO.getUsername(),pageable);
        if (userMsgPage.getTotalElements()>0){
            userDTO.setMsgs(userMsgPage);
        }
        return userDTO;
    }

    /**
     * 查询用户列表
     *
     * @param orgFlag
     * @param orgName 机构名称
     * @param orgDeptId
     * @param orgDept 部门名称
     * @param userType
     * @param keyword
     * @param pageable
     * @return
     */
    @Override
    public Page<UserDTO> queryUsers(String orgFlag, String orgName, Long orgDeptId, String orgDept, List<Integer> userType,Boolean valid,List<Integer> validStatus, String keyword, Pageable pageable) {
        // 如果根据机构名称查询
        if (StrUtil.isBlank(orgFlag) && StrUtil.isNotBlank(orgName)) {
            Org org = orgRepository.findByName(orgName).orElseThrow(NotFoundOrgException::new);
            orgFlag = org.getFlag();
        }

        if (orgDeptId == null && StrUtil.isNotBlank(orgDept) && StrUtil.isNotBlank(orgFlag)) {
            OrgDept orgDeptObj = orgDeptRepository.findByOrgFlagAndName(orgFlag, orgDept).orElseThrow(NotFoundOrgException::new);
            orgDeptId = orgDeptObj.getId();
        }
        Page<User> userPage = userRepository.findAll(UserRepository.SpecBuilder.query(orgFlag, orgDeptId, userType,valid,validStatus, keyword), pageable);
        return userPage.map(user -> convertUserToDTO(user, "org"));
    }


    /**
     * 上传头像
     *
     * @param username
     * @param file
     */
    @Override
    public String uploadHeadImg(String username, MultipartFile file) {
        String headImg = uploadImage(file);
        User user = userRepository.findByUsername(username).orElseThrow(NotFoundUserException::new);
        String resourceUrl = globalProperties.getGatewayUrl() + "/fs-server/load/" + headImg;
        user.setHeadImg(resourceUrl);
        userRepository.save(user);
        return headImg;
    }

    /**
     * 上传证件照
     *
     * @param username
     * @param file
     */
    @Override
    public String uploadIdPhoto(String username, MultipartFile file) {
        User user = userRepository.findByUsername(username).orElseThrow(NotFoundUserException::new);
        String idPhoto = uploadImage(file);
        String resourceUrl = globalProperties.getGatewayUrl() + "/fs-server/load/" + idPhoto;
        user.setIdPhoto(resourceUrl).setValidStatus(1);
        userRepository.save(user);
        return idPhoto;
    }

    /**
     * 审核验证证件照
     *
     * @param username 被审核用户名
     * @param validated 审核通过or不通过
     * @param handlerName 审核人
     * @param remark 审核失败原因
     */
    @Override
    public void auditIdPhoto(String username, Boolean validated, String handlerName, String remark) {
        User user = userRepository.findByUsername(username).orElseThrow(NotFoundUserException::new);
        if (validated) {
            // 验证通过后获得永久访问权限
            setOutsidePermission(username, OutsideEnum.FOREVER);
        }
        // 记录审核日志
        HandlerLog handlerLog = new HandlerLog();
        remark = StrUtil.format("用户：{} 证件照审核{}", username, validated ? "通过" : "不通过, 原因：" + remark);
        handlerLog.setHandlerName(handlerName).setType(1).setRemark(remark);
        handlerLogRepository.save(handlerLog);
        UserMsg userMsg = new UserMsg();
        String msg = StrUtil.format("{}您好，您的证件照审核{}", username, validated ? "已通过" : "未通过, 原因：" + remark);
        userMsg.setUsername(username).setMsg(msg);
        // 已认证or未认证
        user.setValidStatus(validated ? 2 : 0).setHandlerName(handlerName);
        userRepository.save(user);

    }


    @Override
    public void savePermission(PermissionVO permissionVO, String handlerName) {
        Permission permission = permissionRepository.findByUsernameAndType(permissionVO.getUsername(),permissionVO.getType()).orElse(new Permission());
        String remark;
        // 删除
        if (BooleanUtil.isTrue(permissionVO.getDel())){
            remark = StrUtil.format("{} 取消了用户：{} 的 {} 权限", handlerName, permissionVO.getUsername(),PermissionTypeEnum.name(permissionVO.getType()));
            permissionRepository.delete(permission);
        }else{
            // 修改
            if (permission.getId() != null){
                remark = StrUtil.format("{} 修改了用户：{} 的 {} 权限，值：{} -> {}",
                        handlerName, permissionVO.getUsername(),
                        PermissionTypeEnum.name(permissionVO.getType()),
                        permission.getValue(),permissionVO.getValue());
            }else{
                //新增
                remark = StrUtil.format("{} 新增了用户：{} 的 {} 权限，值：{}",
                        handlerName, permissionVO.getUsername(),
                        PermissionTypeEnum.name(permissionVO.getType()),
                        permissionVO.getValue());
            }
            BeanUtil.copyProperties(permissionVO,permission);
            permissionRepository.save(permission);
        }
        // 记录日志
        HandlerLog handlerLog = new HandlerLog();
        handlerLog.setHandlerName(handlerName).setType(1).setUsername(permissionVO.getUsername()).setRemark(remark);
        handlerLogRepository.save(handlerLog);
    }


    @Override
    public void deleteUser(String username) {
        userRepository.deleteByUsername(username);
    }

    /**
     * 检查用户名是否存在
     * @param username
     * @return
     */
    @Override
    public boolean checkUsernameExists(String username){
        return userRepository.existsByUsername(username);
    }

    /**
     * 检查邮箱是否存在
     * @param email
     * @return
     */
    @Override
    public boolean checkEmailExists(String email){
        return userRepository.existsByEmail(email);
    }

    /**
     * 调用fs-server上传照片
     *
     * @param file
     * @return
     */
    private String uploadImage(MultipartFile file) {
        ResponseModel<JSONObject> responseModel = fsServerApi.uploadFile(globalProperties.getImgUploadPath(), file);
        if (responseModel.isError()) {
            log.error("文件服务调用失败：{}", responseModel.getMessage());
            throw new FeignException("fsServer.uploadFile");
        }
        return responseModel.getBody().getStr("fileId");
    }

    /**
     * 设置校外访问权限
     *
     * @param username
     * @param outsideEnum
     * @return
     */
    private void setOutsidePermission(String username, OutsideEnum outsideEnum) {
        Permission permission = permissionRepository
                .findByUsernameAndType(username, PermissionTypeEnum.OUTSIDE.value())
                .orElse(new Permission());
        permission.setUsername(username)
                .setType(PermissionTypeEnum.OUTSIDE.value())
                .setValue(outsideEnum.value())
                .setEffDate(new Date())
                .setExpDate(OutsideEnum.HALF_YEAR.equals(outsideEnum)?DateUtil.offsetMonth(new Date(), 6):DateUtil.offsetMonth(new Date(), 9999));
        permissionRepository.save(permission);
    }

    /**
     * user 转换 UserDTO
     *
     * @param user
     * @param includes
     * @return
     */
    private UserDTO convertUserToDTO(User user, String... includes) {
        UserDTO userDTO = BeanUtil.toBean(user, UserDTO.class);
        if (user.getOrgDeptId() != null && StrUtil.isNotBlank(user.getOrgFlag())) {
            Optional<OrgDept> optionalOrgDept = orgDeptRepository.findByOrgFlagAndId(user.getOrgFlag(), user.getOrgDeptId());
            userDTO.setOrgDeptName(optionalOrgDept.map(OrgDept::getName).orElse(null));
        }
        for (String include : includes) {
            if ("org".equals(include) && StrUtil.isNotBlank(user.getOrgFlag())) {
                Optional<Org> optionalOrg = orgRepository.findByFlag(user.getOrgFlag());
                optionalOrg.ifPresent(org -> {
                    OrgDTO orgDTO = BeanUtil.toBean(org, OrgDTO.class);
                    userDTO.setOrg(orgDTO).setOrgName(orgDTO.getName());
                });
            }
        }
        return userDTO;
    }

    /**
     * 检查用户名或邮箱是否已存在
     *
     * @param username
     * @param email
     */
    public void checkUserExists(String username, String email) {
        if (StrUtil.isNotBlank(username)){
            // 检查username是否已存在
            userRepository.findByUsername(username).ifPresent(user -> UserExistsException.userExists(user.getUsername()));
        }
        if (StrUtil.isNotBlank(email)){
            // 检查email是否已存在
            userRepository.findByEmail(email).ifPresent(user -> UserExistsException.emailExists(user.getEmail()));
        }
    }


}
