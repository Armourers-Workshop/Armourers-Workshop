package moe.plushie.armourers_workshop.init.platform;

import moe.plushie.armourers_workshop.core.client.skinrender.SkinRendererManager;
import moe.plushie.armourers_workshop.utils.ObjectUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;

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
//        // FIXME: @SAGESSE
//        SkinRendererManager.getInstance().init();
//    }
}
