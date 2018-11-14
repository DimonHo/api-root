package com.wd.cloud.orgserver.config;

import org.hibernate.dialect.MySQL5Dialect;
import org.hibernate.dialect.function.SQLFunctionTemplate;
import org.hibernate.type.StringType;

/**
 * @author He Zhigang
 * @date 2018/11/14
 * @Description: 自定义数据库方言，支持中文排序
 */
public class WdSQLDialect extends MySQL5Dialect {

    public WdSQLDialect() {
        super();
        registerFunction("convert_gbk", new SQLFunctionTemplate(StringType.INSTANCE, "convert(?1 using gbk)"));
    }
}
