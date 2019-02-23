package com.wd.cloud.apigateway.config;

import cn.hutool.core.util.URLUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONUtil;
import com.wd.cloud.apigateway.utils.CasUtil;
import com.wd.cloud.apigateway.utils.HttpUtil;
import com.wd.cloud.commons.enums.StatusEnum;
import com.wd.cloud.commons.model.ResponseModel;
import org.jasig.cas.client.authentication.AuthenticationRedirectStrategy;
import org.jasig.cas.client.util.CommonUtils;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author He Zhigang
 * @date 2019/2/21
 * @Description:
 */
@Component
public class ApiAuthenticationRedirectStrategy implements AuthenticationRedirectStrategy {

    private static final String FACES_PARTIAL_AJAX_PARAMETER = "ajax";

    public ApiAuthenticationRedirectStrategy() {
    }

    @Override
    public void redirect(HttpServletRequest request, HttpServletResponse response, String potentialRedirectUrl) throws IOException {

        if (CommonUtils.isNotBlank(request.getParameter(FACES_PARTIAL_AJAX_PARAMETER))) {
            response.setContentType("application/json");
            response.setStatus(200);
            PrintWriter writer = response.getWriter();
            ResponseModel responseModel = ResponseModel.fail(StatusEnum.UNAUTHORIZED);
            writer.write(JSONUtil.toJsonStr(responseModel));
        } else {
            response.sendRedirect(potentialRedirectUrl);
        }

    }
}
