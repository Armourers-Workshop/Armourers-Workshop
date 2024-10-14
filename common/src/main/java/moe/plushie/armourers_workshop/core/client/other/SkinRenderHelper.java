package moe.plushie.armourers_workshop.core.client.other;

import moe.plushie.armourers_workshop.api.action.ICanUse;
import moe.plushie.armourers_workshop.compatibility.api.AbstractItemTransformType;
import moe.plushie.armourers_workshop.core.client.bake.BakedArmature;
import moe.plushie.armourers_workshop.core.client.bake.BakedSkin;
import moe.plushie.armourers_workshop.core.client.bake.BakedSkinPart;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartTypes;
import moe.plushie.armourers_workshop.utils.MathUtils;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.entity.projectile.ThrownTrident;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class SkinRenderHelper {

    public static int getRenderCount(BakedSkin bakedSkin) {
        int count = 0;
        for (var part : bakedSkin.getParts()) {
            if (part.isVisible()) {
                count += 1;
            }
        }
        return count;
    }

    public static void apply(@Nullable Entity entity, BakedSkin bakedSkin, BakedArmature bakedArmature, SkinItemSource itemSource) {
        for (var part : bakedSkin.getParts()) {
            boolean shouldRender = false;
            if (bakedArmature != null && bakedArmature.getTransform(part) != null) {
                shouldRender = true;
            }
            if (shouldRender) {
                shouldRender = shouldRenderPart(entity, part, bakedSkin, itemSource);
            }
            part.setShouldRender(shouldRender);
        }
    }

    public static boolean shouldRenderPart(@Nullable Entity entity, BakedSkinPart bakedPart, BakedSkin bakedSkin, SkinItemSource itemSource) {
        var partType = bakedPart.getType();
        // hook part only render in hook entity.
        if (partType == SkinPartTypes.ITEM_FISHING_HOOK) {
            return isHookEntity(entity);
        }
        if (partType == SkinPartTypes.ITEM_FISHING_ROD1) {
            return entity instanceof Player player && player.fishing != null;
        }
        if (partType == SkinPartTypes.ITEM_FISHING_ROD) {
            if (isHookEntity(entity)) {
                return false;
            }
            return !(entity instanceof Player player && player.fishing != null);
        }
        if (partType == SkinPartTypes.ITEM_ARROW) {
            // arrow part only render in arrow entity
            if (isArrowEntity(entity)) {
                return true;
            }
            // we have some old skin that only contain arrow part,
            // so when it happens, we need to be compatible rendering it.
            // we use `NONE` to rendering the GUI/Ground/ItemFrame.
            if (itemSource.getTransformType() == AbstractItemTransformType.NONE) {
                return bakedPart.getChildren().size() == 1;
            }
            return false;
        }
        if (isArrowEntity(entity)) {
            return false; // arrow entity only render arrow part
        }
        if (isHookEntity(entity)) {
            return false; // hook entity only render arrow part.
        }
        if (partType instanceof ICanUse canUse && entity instanceof LivingEntity livingEntity) {
            var useTick = getUseTick(livingEntity, itemSource.getItem());
            var useRange = canUse.getUseRange();
            var tickRange = bakedSkin.getUseTickRange();
            return useRange.contains(MathUtils.clamp(useTick, tickRange.lowerEndpoint(), tickRange.upperEndpoint()));
        }
        return true;
    }

    private static boolean isHookEntity(@Nullable Entity entity) {
        return entity instanceof FishingHook;
    }

    private static boolean isArrowEntity(@Nullable Entity entity) {
        // in vanilla considers trident to be a special arrow,
        // but this no fits we definition of arrow skin.
        if (entity instanceof ThrownTrident) {
            return false;
        }
        return entity instanceof AbstractArrow;
    }


    public static int getUseTick(LivingEntity entity, ItemStack itemStack) {
        // the item is using.
        if (entity.getUseItem() == itemStack) {
            return entity.getTicksUsingItem();
        }
        // this item is charged (only crossbow).
        if (CrossbowItem.isCharged(itemStack)) {
            return 100;
        }
        return 0;
    }
}
