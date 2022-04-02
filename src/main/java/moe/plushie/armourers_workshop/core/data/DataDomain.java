package moe.plushie.armourers_workshop.core.data;

public enum DataDomain {
    LOCAL("fs"), SERVER("ws"), DATABASE("db"), DATABASE_LINK("db-ln");

    private final String namespace;

    DataDomain(String namespace) {
        this.namespace = namespace;
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
        return DataDomain.SERVER.matches(path);
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
