package moe.plushie.armourers_workshop.api.skin;

import java.io.InputStream;

public interface ISkinFileManager {

    InputStream loadSkinFile(String identifier, Object context) throws Exception;

    void saveSkinFile(String identifier, InputStream skin, Object context) throws Exception;

    void removeSkinFile(String identifier, Object context) throws Exception;
}
