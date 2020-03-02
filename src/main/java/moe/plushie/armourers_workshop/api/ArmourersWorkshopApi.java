package moe.plushie.armourers_workshop.api;

import moe.plushie.armourers_workshop.api.client.render.ISkinRenderHandler;
import moe.plushie.armourers_workshop.api.common.lib.LibApi;
import moe.plushie.armourers_workshop.api.common.painting.IPaintTypeRegistry;
import moe.plushie.armourers_workshop.api.common.skin.ISkinDataHandler;
import moe.plushie.armourers_workshop.api.common.skin.entity.IEntitySkinHandler;
import moe.plushie.armourers_workshop.api.common.skin.type.ISkinTypeRegistry;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public final class ArmourersWorkshopApi {

    public static ISkinDataHandler skinDataHandler;
    public static ISkinTypeRegistry skinTypeRegistry;
    public static IEntitySkinHandler npcSkinDataHandler;
    public static IPaintTypeRegistry paintTypeRegistry;
    @SideOnly(Side.CLIENT)
    public static ISkinRenderHandler skinRenderHandler;
    
    private ArmourersWorkshopApi() {
        throw new IllegalAccessError();
    }

    public static boolean isAvailable() {
        return Loader.isModLoaded(LibApi.MOD_ID);
    }
    
    public static ISkinDataHandler getSkinDataHandler() {
        return skinDataHandler;
    }
    
    public static ISkinTypeRegistry getSkinTypeRegistry() {
        return skinTypeRegistry;
    }
    
    public static IEntitySkinHandler getNpcSkinDataHandler() {
        return npcSkinDataHandler;
    }
    
    public static IPaintTypeRegistry getPaintTypeRegistry() {
        return paintTypeRegistry;
    }
    
    @SideOnly(Side.CLIENT)
    public static ISkinRenderHandler getSkinRenderHandler() {
        return skinRenderHandler;
    }
}
