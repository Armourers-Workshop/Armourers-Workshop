package moe.plushie.armourers_workshop.api;

import moe.plushie.armourers_workshop.api.common.ISkinNBTUtils;
import moe.plushie.armourers_workshop.api.common.capability.IEntitySkinCapability;
import moe.plushie.armourers_workshop.api.common.capability.IPlayerWardrobeCap;
import moe.plushie.armourers_workshop.api.common.capability.IWardrobeCap;
import moe.plushie.armourers_workshop.api.common.lib.LibApi;
import moe.plushie.armourers_workshop.api.common.painting.IPaintTypeRegistry;
import moe.plushie.armourers_workshop.api.common.skin.entity.ISkinnableEntityRegisty;
import moe.plushie.armourers_workshop.api.common.skin.type.ISkinTypeRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.fml.common.Loader;

public final class ArmourersWorkshopApi {

    @CapabilityInject(IEntitySkinCapability.class)
    public static final Capability<IEntitySkinCapability> ENTITY_SKIN_CAP = null;

    @CapabilityInject(IWardrobeCap.class)
    public static final Capability<IWardrobeCap> ENTITY_WARDROBE_CAP = null;

    @CapabilityInject(IPlayerWardrobeCap.class)
    public static final Capability<IPlayerWardrobeCap> PLAYER_WARDROBE_CAP = null;

    public static ISkinNBTUtils skinNBTUtils;
    public static ISkinTypeRegistry skinTypeRegistry;
    public static ISkinnableEntityRegisty skinnableEntityRegisty;
    public static IPaintTypeRegistry paintTypeRegistry;

    private ArmourersWorkshopApi() {
        throw new IllegalAccessError();
    }

    public static boolean isAvailable() {
        return Loader.isModLoaded(LibApi.MOD_ID);
    }

    public static ISkinNBTUtils getSkinNBTUtils() {
        return skinNBTUtils;
    }

    public static ISkinTypeRegistry getSkinTypeRegistry() {
        return skinTypeRegistry;
    }

    public static ISkinnableEntityRegisty getISkinnableEntityRegisty() {
        return skinnableEntityRegisty;
    }

    public static IPaintTypeRegistry getPaintTypeRegistry() {
        return paintTypeRegistry;
    }

    public static IEntitySkinCapability getEntitySkinCapability(Entity entity) {
        return entity.getCapability(ENTITY_SKIN_CAP, null);
    }

    public static IWardrobeCap getEntityWardrobeCapability(Entity entity) {
        return entity.getCapability(ENTITY_WARDROBE_CAP, null);
    }

    public static IPlayerWardrobeCap getPlayerWardrobeCapability(EntityPlayer player) {
        return player.getCapability(PLAYER_WARDROBE_CAP, null);
    }
}
