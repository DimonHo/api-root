package com.wd.cloud.commons.constant;

/**
 * @author He Zhigang
 * @date 2018/6/6
 * @Description:
 */
public class SessionConstant {

    /**
     * 登陆用户
     */
    public static final String LOGIN_USER = "loginUser";

    /**
     * 登陆ip所属机构
     */
    public static final String IP_ORG = "ipOrg";

    /**
     * session有效ORG（当登陆用户信息中有所属机构，优先取用户信息中的机构，否则取IP所属机构）
     */
    public static final String ORG = "org";

    /**
     * 是否是校外访问
     */
    public static final String IS_OUT = "isOut";

    /**
     * 访问客户端类型，1：pc,2:mobile
     */
    public static final String CLIENT_TYPE = "clientType";

    /**
     * session级别
     */
    public static final String LEVEL = "level";

}
