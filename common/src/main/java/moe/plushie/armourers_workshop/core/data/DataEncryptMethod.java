package moe.plushie.armourers_workshop.core.data;

import moe.plushie.armourers_workshop.utils.ObjectUtils;

public enum DataEncryptMethod {

    PASSWORD("password"),
    AUTH("auth");

    private final String method;

    DataEncryptMethod(String method) {
        this.method = method;
    }

    public String key(String text) {
        return ObjectUtils.md5(method + ";" + ObjectUtils.md5(String.format("%s(%s)", method, text)) + ";" + "aw");
    }

    public String signature(String key) {
        return method + ";" + ObjectUtils.md5(String.format("signature(%s)", key));
    }

    public String method() {
        return method;
    }

}
