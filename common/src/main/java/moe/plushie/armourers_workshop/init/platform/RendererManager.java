package moe.plushie.armourers_workshop.init.platform;

import moe.plushie.armourers_workshop.compatibility.AbstractModelPartRegistries;
import moe.plushie.armourers_workshop.core.client.other.SkinAnimationManager;
import moe.plushie.armourers_workshop.core.client.skinrender.SkinRendererManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class RendererManager {

    public static void init() {
        SkinAnimationManager.init();
        AbstractModelPartRegistries.init();
        SkinRendererManager.init();
    }
}
