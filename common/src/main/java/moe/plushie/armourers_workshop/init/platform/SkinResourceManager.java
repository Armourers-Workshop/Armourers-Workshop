package moe.plushie.armourers_workshop.init.platform;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value = EnvType.CLIENT)
public class SkinResourceManager {

    private static final SkinResourceManager INSTANCE = new SkinResourceManager();

    public static void init() {
//        ReloadableResourceManager resourceManager = ObjectUtils.safeCast(Minecraft.getInstance().getResourceManager(), ReloadableResourceManager.class);
//        if (resourceManager != null) {
//            resourceManager.registerReloadListener(INSTANCE);
//        }
    }

//    @Override
//    public void onResourceManagerReload(ResourceManager resourceManager) {
//        SkinRendererManager.getInstance().init();
//    }
}
