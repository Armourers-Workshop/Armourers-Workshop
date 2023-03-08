package moe.plushie.armourers_workshop.library.data;

import moe.plushie.armourers_workshop.api.library.ISkinLibrary;
import moe.plushie.armourers_workshop.api.skin.ISkinFileHeader;
import moe.plushie.armourers_workshop.api.skin.ISkinType;
import moe.plushie.armourers_workshop.api.skin.property.ISkinProperties;
import moe.plushie.armourers_workshop.core.data.DataDomain;
import moe.plushie.armourers_workshop.core.skin.SkinTypes;
import moe.plushie.armourers_workshop.core.skin.property.SkinProperty;
import moe.plushie.armourers_workshop.utils.Constants;
import moe.plushie.armourers_workshop.utils.SkinFileUtils;
import org.apache.logging.log4j.util.Strings;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

public class SkinLibraryFile implements Comparable<SkinLibraryFile>, ISkinLibrary.Entry {

    protected final String name;
    protected final String path;
    protected final DataDomain domain;
    protected final ISkinFileHeader header;
    protected final boolean isDirectory;
    protected final boolean isPrivateDirectory;

    private Collection<String> searchableContentList;

    public SkinLibraryFile(DataDomain domain, String name, String path) {
        this.name = name;
        this.path = path;
        this.domain = domain;
        this.header = null;
        this.isDirectory = true;
        this.isPrivateDirectory = domain.equals(DataDomain.DEDICATED_SERVER) && path.startsWith(Constants.PRIVATE);
    }

    public SkinLibraryFile(DataDomain domain, String name, String path, ISkinFileHeader header) {
        this.name = name;
        this.path = SkinFileUtils.normalize(path, true);
        this.domain = domain;
        this.header = header;
        this.isDirectory = false;
        this.isPrivateDirectory = false;
    }

    public boolean matches(String keywords, ISkinType skinType) {
        // when skin type not matches, ignore.
        if (skinType != SkinTypes.UNKNOWN && skinType != getSkinType()) {
            return false;
        }
        if (keywords != null && !keywords.isEmpty()) {
            return matchesInContentList(keywords);
        }
        return true;
    }

    public boolean isSameFile(SkinLibraryFile other) {
        return name.equals(other.name) && path.equals(other.path) && domain.equals(other.domain);
    }

    @Override
    public int compareTo(SkinLibraryFile o) {
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

    public String getName() {
        return name;
    }

    public String getNamespace() {
        return domain.namespace();
    }

    public String getPath() {
        return path;
    }

    public int getLastModified() {
        if (header != null) {
            return header.getLastModified();
        }
        return 0;
    }

    public int getSkinVersion() {
        if (header != null) {
            return header.getVersion();
        }
        return 0;
    }

    @Override
    public String getSkinIdentifier() {
        return getNamespace() + ":" + getPath();
    }

    @Override
    public ISkinType getSkinType() {
        if (header != null) {
            return header.getType();
        }
        return null;
    }

    public ISkinProperties getSkinProperties() {
        if (header != null) {
            return header.getProperties();
        }
        return null;
    }

    public boolean isDirectory() {
        return isDirectory;
    }

    @Override
    public boolean isPrivateDirectory() {
        return isPrivateDirectory;
    }

    public boolean isChildDirectory(String rootPath) {
        // /xxxx/
        int length = rootPath.length();
        return length < path.length() && path.startsWith(rootPath) && path.indexOf('/', length) < 0;
    }

    private boolean matchesInContentList(String keyword) {
        int index = 0;
        int length = keyword.length();
        while (index < length) {
            int lastIndex = index;
            for (String content : getSearchableContentList()) {
                int searchIndex = -1;
                for (int offset = index; offset < length; ++offset) {
                    int ch = keyword.codePointAt(offset);
                    if (Character.isSpaceChar(ch)) {
                        index = offset + 1;
                        break;
                    }
                    searchIndex = content.indexOf(ch, searchIndex + 1);
                    if (searchIndex < 0) {
                        break; // not match
                    }
                }
                if (searchIndex > 0) {
                    return true; // all keyword is matches.
                }
                if (lastIndex != index) {
                    break; // found a content
                }
            }
            if (lastIndex == index) {
                return false; // not found any content
            }
        }
        return true;
    }

    private Collection<String> getSearchableContentList() {
        if (searchableContentList != null) {
            return searchableContentList;
        }
        ArrayList<String> values = new ArrayList<>();
        values.add(SkinFileUtils.removeExtension(path));
        ISkinProperties properties = getSkinProperties();
        if (properties != null) {
            values.add(properties.get(SkinProperty.ALL_CUSTOM_NAME));
            values.add(properties.get(SkinProperty.ALL_AUTHOR_NAME));
            values.add(properties.get(SkinProperty.ALL_FLAVOUR_TEXT));
        }
        searchableContentList = values.stream().filter(Strings::isNotBlank).map(String::toLowerCase).collect(Collectors.toList());
        return searchableContentList;
    }
}
