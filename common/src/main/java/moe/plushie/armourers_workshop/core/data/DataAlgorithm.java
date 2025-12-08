package moe.plushie.armourers_workshop.core.data;

import moe.plushie.armourers_workshop.core.utils.Objects;

public class DataAlgorithm {

    public static final DataAlgorithm PASSWORD = new DataAlgorithm("password");
    public static final DataAlgorithm AUTH = new DataAlgorithm("auth");

    private static final DataAlgorithm[] VALUES = {PASSWORD, AUTH};

    private final String method;

    private DataAlgorithm(String method) {
        this.method = method;
    }

    public static DataAlgorithm[] values() {
        return VALUES;
    }

    public String key(String text) {
        return Objects.md5(method + ";" + Objects.md5(String.format("%s(%s)", method, text)) + ";" + "aw");
    }

    public String signature(String key) {
        return method + ";" + Objects.md5(String.format("signature(%s)", key));
    }

    public String method() {
        return method;
    }
}
