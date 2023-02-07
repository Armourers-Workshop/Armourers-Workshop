package moe.plushie.armourers_workshop.core.data;

public enum DataDomain {
    LOCAL("fs"), DEDICATED_SERVER("ws"), DATABASE("db"), DATABASE_LINK("ln"), GLOBAL_SERVER("ks"), GLOBAL_SERVER_PREVIEW("kv");

    private final String namespace;

    DataDomain(String namespace) {
        this.namespace = namespace;
    }

    public static DataDomain byName(String path) {
        String namespace = getNamespace(path);
        for (DataDomain domain : values()) {
            if (domain.namespace.equals(namespace)) {
                return domain;
            }
        }
        return LOCAL;
    }

    public static String getNamespace(String path) {
        int index = path.indexOf(":");
        if (index < 0) {
            return "";
        }
        return path.substring(0, index);
    }

    public static String getPath(String path) {
        int index = path.indexOf(":");
        if (index < 0) {
            return path;
        }
        return path.substring(index + 1);
    }

    public static boolean isLocal(String path) {
        return DataDomain.LOCAL.matches(path);
    }

    public static boolean isServer(String path) {
        return DataDomain.DEDICATED_SERVER.matches(path);
    }

    public static boolean isDatabase(String path) {
        return DataDomain.DATABASE.matches(path) || DataDomain.DATABASE_LINK.matches(path);
    }

    public boolean matches(String s) {
        return s.startsWith(namespace + ":");
    }

    public String normalize(String s) {
        return namespace + ":" + s;
    }

    public String namespace() {
        return namespace;
    }
}
