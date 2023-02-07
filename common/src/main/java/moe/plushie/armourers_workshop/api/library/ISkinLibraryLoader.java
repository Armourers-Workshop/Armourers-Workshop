package moe.plushie.armourers_workshop.api.library;

import java.io.InputStream;

public interface ISkinLibraryLoader {

    InputStream loadSkin(String skinId) throws Exception;
}
