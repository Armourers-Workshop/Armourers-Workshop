package moe.plushie.armourers_workshop.api.skin;

import java.io.InputStream;

public interface ISkinFileProvider {

    InputStream loadSkin(String skinId) throws Exception;
}
