package moe.plushie.armourers_workshop.api.skin;


import java.io.File;
import java.util.Collection;

public interface ISkinLibrary {

    interface Entry {

        String getName();

        String getPath();

        boolean isDirectory();

        boolean isPrivateDirectory();
    }
}
