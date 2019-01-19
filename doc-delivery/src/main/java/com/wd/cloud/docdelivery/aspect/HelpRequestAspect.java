package com.wd.cloud.docdelivery.aspect;

import com.wd.cloud.commons.constant.SessionConstant;
import com.wd.cloud.commons.dto.OrgDTO;
import com.wd.cloud.commons.dto.UserDTO;
import com.wd.cloud.docdelivery.entity.Permission;
import com.wd.cloud.docdelivery.exception.ExceptionEnum;
import com.wd.cloud.docdelivery.exception.HelpTotalCeilingException;
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
 * @Description:
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
        HttpSession session = request.getSession();
        UserDTO userDTO = (UserDTO) session.getAttribute(SessionConstant.LOGIN_USER);
        OrgDTO orgDTO = (OrgDTO) session.getAttribute(SessionConstant.ORG);
        Integer level = (Integer) session.getAttribute(SessionConstant.LEVEL);
        log.info("当前等级：[{}]", level);
        long helpTotal = 0;
        long helpTotalToday = 0;
        if (userDTO != null) {
            //用户总求助量
            helpTotal = helpRecordRepository.countByHelperId(userDTO.getId());
            helpTotalToday = helpRecordRepository.countByHelperIdToday(userDTO.getId());
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
        if (permission.getTotal() != null && permission.getTotal() <= helpTotal) {
            throw new HelpTotalCeilingException(ExceptionEnum.HELP_TOTAL_CEILING);
        } else if (permission.getTodayTotal() <= helpTotalToday) {
            throw new HelpTotalCeilingException(ExceptionEnum.HELP_TOTAL_TODAY_CEILING);
        }
    }

}
