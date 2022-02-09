package moe.plushie.armourers_workshop.core.config;

import moe.plushie.armourers_workshop.core.api.common.skin.ISkinPart;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Set;

public class SkinConfig {

    // Performance
    public static int renderDistanceSkin;
    public static int renderDistanceBlockSkin;
    public static int renderDistanceMannequinEquipment;
    public static int modelBakingThreadCount;
    public static double lodDistance = 32F;
    public static boolean multipassSkinRendering = true;
    public static int maxLodLevels = 4;
    public static boolean useClassicBlockModels;

    // Misc
    public static int skinLoadAnimationTime;

    // Cache
    public static int skinCacheExpireTime;
    public static int skinCacheMaxSize;
    public static int modelPartCacheExpireTime;
    public static int modelPartCacheMaxSize;
    public static int textureCacheExpireTime;
    public static int textureCacheMaxSize;
    public static int maxSkinRequests;
    public static int fastCacheSize;

    // Skin preview
    public static boolean skinPreEnabled = true;
    public static boolean skinPreDrawBackground = true;
    public static int skinPreSize = 96;
    public static double skinPreLocHorizontal = 0.0;
    public static double skinPreLocVertical = 0.5;
    public static boolean skinPreLocFollowMouse = true;

    // Tool-tip
    public static boolean tooltipHasSkin;
    public static boolean tooltipSkinName;
    public static boolean tooltipSkinAuthor;
    public static boolean tooltipSkinType;
    public static boolean tooltipDebug = false;
    public static boolean tooltipFlavour;
    public static boolean tooltipOpenWardrobe;

    // Debug
    public static int texturePaintingType;
    public static boolean showF3DebugInfo;
    public static float ploOffset = -0.01f;

    public static boolean enableModelOverridden = true;
    public static boolean enableWireframeRender;

    // Debug tool
    public static boolean showArmourerDebugRender;
    public static boolean showLodLevels;
    public static boolean showSkinBlockBounds;
    public static boolean showSkinRenderBounds;
    public static boolean showSortOrderToolTip;

    public static boolean showDebugFullBounds = false;
    public static boolean showDebugPartBounds = false;
    public static boolean showDebugTargetBounds = false;

    public static boolean showDebugTextureBounds = false;
    public static boolean showDebugSpin = false;
    public static Set<String> disabledSkinParts = new HashSet<>();

    public static int getNumberOfRenderLayers() {
        if (multipassSkinRendering) {
            return 4;
        } else {
            return 2;
        }
    }

    public static boolean isSkinnableEntity(@Nullable Entity entity) {
        if (entity instanceof PlayerEntity) {
            return true;
        }
        return false;
    }

    public static boolean isEnableSkinPart(ISkinPart skinPart) {
        return disabledSkinParts.contains(skinPart.getType().getRegistryName());
    }

    public static TexturePaintType getTexturePaintType() {
        if (SkinConfig.texturePaintingType < 0) {
            return TexturePaintType.DISABLED;
        }
        if (SkinConfig.texturePaintingType == 0) {
//            if (ModLoader.isModLoaded("tlauncher_custom_cape_skin")) {
//                return TexturePaintType.MODEL_REPLACE_AW;
//            }
            return TexturePaintType.TEXTURE_REPLACE;
        }
        return TexturePaintType.values()[SkinConfig.texturePaintingType];
    }

    public enum TexturePaintType {
        DISABLED, TEXTURE_REPLACE, MODEL_REPLACE_MC, MODEL_REPLACE_AW
    }

}
