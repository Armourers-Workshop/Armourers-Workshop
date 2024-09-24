package moe.plushie.armourers_workshop.init.platform;

import moe.plushie.armourers_workshop.core.client.skinrender.SkinRendererManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class RendererManager {

    public static void init() {
        SkinRendererManager.init();
        //moe.plushie.armourers_workshop.core.skin.molang.impl.test.CompilerTest.main();
    }
}
