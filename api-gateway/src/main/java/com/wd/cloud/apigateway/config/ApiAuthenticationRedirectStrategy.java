package com.wd.cloud.apigateway.config;

import cn.hutool.core.lang.Console;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HtmlUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.wd.cloud.apigateway.feign.SsoServerApi;
import com.wd.cloud.commons.enums.StatusEnum;
import com.wd.cloud.commons.model.ResponseModel;
import org.jasig.cas.client.authentication.AuthenticationRedirectStrategy;
import org.jasig.cas.client.util.CommonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * @author He Zhigang
 * @date 2019/2/21
 * @Description:
 */
@Component
public class ApiAuthenticationRedirectStrategy implements AuthenticationRedirectStrategy {

    private static final String FACES_PARTIAL_AJAX_PARAMETER = "javax.faces.partial.ajax";

    public ApiAuthenticationRedirectStrategy() {
    }

    @Override
    public void redirect(HttpServletRequest request, HttpServletResponse response, String potentialRedirectUrl) throws IOException {
        if (CommonUtils.isNotBlank(request.getParameter(FACES_PARTIAL_AJAX_PARAMETER))) {
            String loginHtml  =HttpUtil.get(potentialRedirectUrl+"&method=POST");
            String execution = StrUtil.subBetween(loginHtml,"<input type=\"hidden\" name=\"execution\" value=\"","\"/>");
//            Map<String,Object> param = new HashMap<>();
//            param.put("execution",execution);
//            param.put("username","wuqilong");
//            param.put("password","123456");
//            param.put("service","http://localhost:8080/login");
//            String postResut = HttpUtil.post("http://sso.test.hnlat.com/oauth/login",param);
            response.setContentType("application/json");
            response.setStatus(200);
            PrintWriter writer = response.getWriter();
            JSONObject redirect = new JSONObject();
            redirect.put("redirect", potentialRedirectUrl);
            redirect.put("execution",execution);
            ResponseModel responseModel = ResponseModel.fail(StatusEnum.UNAUTHORIZED).setBody(redirect);
            writer.write(JSONUtil.toJsonStr(responseModel));
        } else {
            response.sendRedirect(potentialRedirectUrl);
        }

    }
}
