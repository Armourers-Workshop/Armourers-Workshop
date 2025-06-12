package moe.plushie.armourers_workshop.api.library;

import moe.plushie.armourers_workshop.api.skin.ISkinType;
import moe.plushie.armourers_workshop.api.skin.serializer.ISkinFileHeader;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Collection;

public interface ISkinLibrary {

    interface Entry {

        String name();

        String path();

        String skinIdentifier();

        ISkinType skinType();

        ISkinFileHeader skinHeader();

        boolean isDirectory();

        boolean isPrivateDirectory();
    }

    interface Difference {

        Collection<Entry> addedChanges();

        Collection<Entry> removedChanges();

        Collection<Pair<Entry, Entry>> updatedChanges();
    }
}
