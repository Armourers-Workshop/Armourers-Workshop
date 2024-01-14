package moe.plushie.armourers_workshop.init.platform;

import moe.plushie.armourers_workshop.core.client.skinrender.SkinRendererManager;
import moe.plushie.armourers_workshop.init.ModEntityProfiles;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class SkinRendererRegistries {

    public static void init() {
        SkinRendererManager manager = SkinRendererManager.getInstance();
        ModEntityProfiles.addListener(manager::unbind, manager::bind);
    }
}
