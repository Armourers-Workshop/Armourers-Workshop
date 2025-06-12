package moe.plushie.armourers_workshop.library.data;

import moe.plushie.armourers_workshop.api.library.ISkinLibrary;
import moe.plushie.armourers_workshop.core.data.DataDomain;
import moe.plushie.armourers_workshop.core.skin.SkinType;
import moe.plushie.armourers_workshop.core.skin.SkinTypes;
import moe.plushie.armourers_workshop.core.skin.property.SkinProperty;
import moe.plushie.armourers_workshop.core.skin.serializer.SkinFile;
import moe.plushie.armourers_workshop.core.skin.serializer.SkinFileHeader;
import moe.plushie.armourers_workshop.core.utils.Collections;
import moe.plushie.armourers_workshop.core.utils.Constants;
import moe.plushie.armourers_workshop.core.utils.FileUtils;

import java.util.ArrayList;
import java.util.Collection;

public class SkinLibraryFile extends SkinFile implements ISkinLibrary.Entry {

    private Collection<String> searchableContentList;

    public SkinLibraryFile(DataDomain domain, String name, String path) {
        super(domain, name, path, null, true, domain.equals(DataDomain.DEDICATED_SERVER) && path.startsWith(Constants.PRIVATE));
    }

    public SkinLibraryFile(DataDomain domain, String name, String path, SkinFileHeader header) {
        super(domain, name, FileUtils.normalize(path, true), header, false, false);
    }

    public boolean matches(String keywords, SkinType skinType) {
        // when skin type not matches, ignore.
        if (skinType != SkinTypes.UNKNOWN && skinType != skinType()) {
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

    private boolean matchesInContentList(String keyword) {
        int index = 0;
        int length = keyword.length();
        while (index < length) {
            int lastIndex = index;
            for (var content : searchableContentList()) {
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

    private Collection<String> searchableContentList() {
        if (searchableContentList != null) {
            return searchableContentList;
        }
        var values = new ArrayList<String>();
        values.add(FileUtils.removeExtension(path));
        var properties = skinProperties();
        if (properties != null) {
            values.add(properties.get(SkinProperty.ALL_CUSTOM_NAME));
            values.add(properties.get(SkinProperty.ALL_AUTHOR_NAME));
            values.add(properties.get(SkinProperty.ALL_FLAVOUR_TEXT));
        }
        searchableContentList = Collections.compactMap(values, s -> {
            if (!s.isEmpty()) {
                return s.toLowerCase();
            }
            return null;
        });
        return searchableContentList;
    }
}
