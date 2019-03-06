package com.wd.cloud.docdelivery.aspect;

import cn.hutool.core.util.StrUtil;
import com.wd.cloud.commons.constant.SessionConstant;
import com.wd.cloud.commons.dto.OrgDTO;
import com.wd.cloud.commons.dto.UserDTO;
import com.wd.cloud.commons.exception.AuthException;
import com.wd.cloud.docdelivery.entity.Permission;
import com.wd.cloud.docdelivery.exception.AppException;
import com.wd.cloud.docdelivery.exception.ExceptionEnum;
import com.wd.cloud.docdelivery.repository.HelpRecordRepository;
import com.wd.cloud.docdelivery.repository.PermissionRepository;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Objects;

/**
 * @author He Zhigang
 * @date 2019/1/18
 * @Description: 求助拦截，求助前检查用户是否还有求助次数
 */
@Slf4j
@Aspect
@Component
public class HelpRequestAspect {

    @Autowired
    HelpRecordRepository helpRecordRepository;

    @Autowired
    PermissionRepository permissionRepository;

    @Pointcut("execution(public * com.wd.cloud.docdelivery.controller.FrontendController.helpFrom(..))")
    public void helpRequest() {
    }

    @Before("helpRequest()")
    public void doBefore(JoinPoint joinPoint) throws Throwable {
        // 接收到请求，记录请求内容
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = Objects.requireNonNull(attributes).getRequest();
        if (validateParam(request)) {
            throw new AppException(ExceptionEnum.HELP_PARAM);
        }
        HttpSession session = request.getSession();
        UserDTO userDTO = (UserDTO) session.getAttribute(SessionConstant.LOGIN_USER);
        OrgDTO orgDTO = (OrgDTO) session.getAttribute(SessionConstant.ORG);
        Integer level = (Integer) session.getAttribute(SessionConstant.LEVEL);
        Boolean isOut = (Boolean) session.getAttribute(SessionConstant.IS_OUT);
        log.info("当前等级：[{}],isOut=[{}]", level, isOut);
        level = level == null ? 0 : level;
        isOut = isOut == null ? false : isOut;
        if (isOut && userDTO == null) {
            throw new AuthException("校外必须先登录才能求助");
        }
        long helpTotal = 0;
        long helpTotalToday = 0;
        if (userDTO != null) {
            //用户总求助量
            helpTotal = helpRecordRepository.countByHelperName(userDTO.getUsername());
            helpTotalToday = helpRecordRepository.countByHelperNameToday(userDTO.getUsername());
            log.info("登陆用户【{}】正在求助", userDTO.getUsername());
        } else {
            String email = request.getParameter("helperEmail");
            helpTotal = helpRecordRepository.countByHelperEmail(email);
            helpTotalToday = helpRecordRepository.countByHelperEmailToday(email);
            log.info("邮箱【{}】正在求助", email);
        }
        Permission permission = null;
        if (orgDTO != null) {
            permission = permissionRepository.findByOrgIdAndLevel(orgDTO.getId(), level);
        }
        if (permission == null) {
            permission = permissionRepository.findByOrgIdIsNullAndLevel(level);
        }
        if (permission != null) {
            if (permission.getTotal() != null && permission.getTotal() <= helpTotal) {
                throw new AppException(ExceptionEnum.HELP_TOTAL_CEILING);
            } else if (permission.getTodayTotal() <= helpTotalToday) {
                throw new AppException(ExceptionEnum.HELP_TOTAL_TODAY_CEILING);
            }
        }
    }

    /**
     * 参数校验
     *
     * @param request
     * @return
     */
    private boolean validateParam(HttpServletRequest request) {
        String email = request.getParameter("helperEmail");
        String docTitle = request.getParameter("docTitle");
        return StrUtil.isBlank(email) || StrUtil.isBlank(docTitle);
    }

}
