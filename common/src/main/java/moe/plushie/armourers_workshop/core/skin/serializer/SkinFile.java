package moe.plushie.armourers_workshop.core.skin.serializer;

import moe.plushie.armourers_workshop.core.data.DataDomain;
import moe.plushie.armourers_workshop.core.skin.SkinType;
import moe.plushie.armourers_workshop.core.skin.property.SkinProperties;

public class SkinFile implements Comparable<SkinFile> {

    protected final String name;
    protected final String path;
    protected final DataDomain domain;
    protected final SkinFileHeader header;
    protected final boolean isDirectory;
    protected final boolean isPrivateDirectory;

    public SkinFile(DataDomain domain, String name, String path, SkinFileHeader header, boolean isDirectory, boolean isPrivateDirectory) {
        this.domain = domain;
        this.name = name;
        this.path = path;
        this.header = header;
        this.isDirectory = isDirectory;
        this.isPrivateDirectory = isPrivateDirectory;
    }

    public String name() {
        return name;
    }

    public String namespace() {
        return domain.namespace();
    }

    public String path() {
        return path;
    }

    public int lastModified() {
        if (header != null) {
            return header.lastModified();
        }
        return 0;
    }

    public int skinVersion() {
        if (header != null) {
            return header.version();
        }
        return 0;
    }

    public String skinIdentifier() {
        return namespace() + ":" + path();
    }

    public SkinType skinType() {
        if (header != null) {
            return header.type();
        }
        return null;
    }

    public SkinFileHeader skinHeader() {
        return header;
    }

    public SkinProperties skinProperties() {
        if (header != null) {
            return header.properties();
        }
        return null;
    }

    public boolean isDirectory() {
        return isDirectory;
    }

    public boolean isPrivateDirectory() {
        return isPrivateDirectory;
    }

    @Override
    public int compareTo(SkinFile o) {
        if (isDirectory & !o.isDirectory) {
            return path.compareToIgnoreCase(o.path) - 1000000;
        } else if (!isDirectory & o.isDirectory) {
            return path.compareToIgnoreCase(o.path) + 1000000;
        }
        return path.compareToIgnoreCase(o.path);
    }

    @Override
    public String toString() {
        return domain.normalize(path);
    }

    public boolean isChildDirectory(String rootPath) {
        // /xxxx/
        int length = rootPath.length();
        return length < path.length() && path.startsWith(rootPath) && path.indexOf('/', length) < 0;
    }
}
