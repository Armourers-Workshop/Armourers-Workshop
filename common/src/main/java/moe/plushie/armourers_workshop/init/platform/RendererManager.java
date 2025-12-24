package moe.plushie.armourers_workshop.init.platform;

import moe.plushie.armourers_workshop.core.client.render.model.SkinItemModelManager;
import moe.plushie.armourers_workshop.core.client.skinrender.SkinRendererManager;

public class RendererManager {

    public static void init() {
        SkinItemModelManager.init();
        SkinRendererManager.reload();
    }
}
