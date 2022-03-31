package moe.plushie.armourers_workshop.api.skin;


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
