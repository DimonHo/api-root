package com.wd.cloud.bse.service.transfrom.merge;


import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


public class PropertyTuple {
    public String name;

    public String rule;

    public Map<String, String> parameters;

    public String followName;

    public Set<String> followNames = new HashSet<>();

    public PropertyTuple(String name, String rule, Map<String, String> parameters) {
        this.name = name;
        this.rule = rule;
        if (parameters != null) {
            this.parameters = new HashMap<>(parameters);
        }
    }

    public PropertyTuple setFollowName(String followName) {
        this.followName = followName;
        return this;
    }

    public PropertyTuple addFollowName(String followName) {
        followNames.add(followName);
        return this;
    }
}
