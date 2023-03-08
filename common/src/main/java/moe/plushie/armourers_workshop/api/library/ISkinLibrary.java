package moe.plushie.armourers_workshop.api.library;

import moe.plushie.armourers_workshop.api.skin.ISkinType;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Collection;

public interface ISkinLibrary {

    interface Entry {

        String getName();

        String getNamespace();

        String getPath();

        String getSkinIdentifier();

        ISkinType getSkinType();

        boolean isDirectory();

        boolean isPrivateDirectory();
    }

    interface Difference {

        Collection<Entry> getAddedChanges();

        Collection<Entry> getRemovedChanges();

        Collection<Pair<Entry, Entry>> getUpdatedChanges();
    }
}
