package moe.plushie.armourers_workshop.library.data;

import com.mojang.datafixers.util.Pair;
import moe.plushie.armourers_workshop.api.skin.ISkinLibrary;
import moe.plushie.armourers_workshop.api.skin.ISkinProperties;
import moe.plushie.armourers_workshop.api.skin.ISkinType;
import moe.plushie.armourers_workshop.core.skin.SkinTypes;
import moe.plushie.armourers_workshop.core.skin.data.property.SkinProperty;
import moe.plushie.armourers_workshop.init.common.AWConstants;
import net.minecraft.network.PacketBuffer;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.util.Strings;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

public class SkinLibraryFile implements Comparable<SkinLibraryFile>, ISkinLibrary.Entry {

    protected final String name;
    protected final String namespace;
    protected final String path;
    protected final Pair<ISkinType, ISkinProperties> header;
    protected final boolean isDirectory;
    protected final boolean isPrivateDirectory;

    private Collection<String> searchableContentList;

    public SkinLibraryFile(String namespace, String name, String path) {
        this.name = name;
        this.namespace = namespace;
        this.path = FilenameUtils.normalize(path, true);
        this.header = null;
        this.isDirectory = true;
        this.isPrivateDirectory = namespace.equals(AWConstants.Namespace.SERVER) && path.startsWith("/private");
    }

    public SkinLibraryFile(String namespace, String name, String path, Pair<ISkinType, ISkinProperties> header) {
        this.name = name;
        this.namespace = namespace;
        this.path = FilenameUtils.normalize(path, true);
        this.header = header;
        this.isDirectory = false;
        this.isPrivateDirectory = false;
    }

    public boolean matches(String keywords, ISkinType skinType) {
        // when skin type not matches, ignore.
        if (!isDirectory && skinType != SkinTypes.UNKNOWN && skinType != getSkinType()) {
            return false;
        }
        if (keywords != null && !keywords.isEmpty()) {
            return matchesInContentList(keywords);
        }
        return true;
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
        return namespace + ":" + path;
    }

    public String getName() {
        return name;
    }

    public String getNamespace() {
        return namespace;
    }

    public String getPath() {
        return path;
    }

    public ISkinType getSkinType() {
        if (header != null) {
            return header.getFirst();
        }
        return null;
    }

    public ISkinProperties getSkinProperties() {
        if (header != null) {
            return header.getSecond();
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
        values.add(FilenameUtils.removeExtension(path));
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
