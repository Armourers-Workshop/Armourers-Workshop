package moe.plushie.armourers_workshop.api.library;


import moe.plushie.armourers_workshop.api.skin.ISkinType;

public interface ISkinLibrary {

    interface Entry {

        String getName();

        String getNamespace();

        String getPath();

        ISkinType getSkinType();

        boolean isDirectory();

        boolean isPrivateDirectory();
    }
}
