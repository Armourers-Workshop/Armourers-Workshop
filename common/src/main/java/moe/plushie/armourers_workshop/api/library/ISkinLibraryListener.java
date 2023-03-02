package moe.plushie.armourers_workshop.api.library;

public interface ISkinLibraryListener {

    default void libraryDidReload(ISkinLibrary library) {
    }

    default void libraryDidChanges(ISkinLibrary library, ISkinLibrary.Difference difference) {
    }
}
