package com.wd.cloud.bse.data;

public class Url {

    private String param;

    private Integer source;

    private String extParam;

    @Override
    public boolean equals(Object obj) {
        if (obj != null && obj instanceof Url) {
            return ((Url) obj).source.equals(source) || ((Url) obj).param.equals(param);
        }
        return false;
    }

    public String getParam() {
        return param;
    }

    public void setParam(String param) {
        this.param = param;
    }

    public Integer getSource() {
        return source;
    }

    public void setSource(Integer source) {
        this.source = source;
    }

    public String getExtParam() {
        return extParam;
    }

    public void setExtParam(String extParam) {
        this.extParam = extParam;
    }

}
