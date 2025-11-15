package moe.plushie.armourers_workshop.core.client.other;

import moe.plushie.armourers_workshop.api.skin.part.features.ICanUse;
import moe.plushie.armourers_workshop.core.client.bake.BakedArmature;
import moe.plushie.armourers_workshop.core.client.bake.BakedSkin;
import moe.plushie.armourers_workshop.core.client.bake.BakedSkinPart;
import moe.plushie.armourers_workshop.core.client.render.state.ArrowRenderState;
import moe.plushie.armourers_workshop.core.client.render.state.EntityRenderState;
import moe.plushie.armourers_workshop.core.client.render.state.FishingHookRenderState;
import moe.plushie.armourers_workshop.core.client.render.state.LivingEntityRenderState;
import moe.plushie.armourers_workshop.core.client.render.state.PlayerRenderState;
import moe.plushie.armourers_workshop.core.client.render.state.ThrownTridentRenderState;
import moe.plushie.armourers_workshop.core.math.OpenMath;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartTypes;
import moe.plushie.armourers_workshop.core.utils.OpenItemDisplayContext;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class SkinRenderHelper {

    public static void apply(@Nullable EntityRenderState renderState, BakedSkin bakedSkin, BakedArmature bakedArmature, SkinItemSource itemSource) {
        for (var part : bakedSkin.parts()) {
            boolean shouldRender = false;
            if (bakedArmature != null && bakedArmature.transformByPart(part) != null) {
                shouldRender = true;
            }
            if (shouldRender) {
                shouldRender = shouldRenderPart(renderState, part, bakedSkin, itemSource);
            }
            part.setShouldRender(shouldRender);
        }
    }

    public static boolean shouldRenderPart(@Nullable EntityRenderState renderState, BakedSkinPart bakedPart, BakedSkin bakedSkin, SkinItemSource itemSource) {
        var partType = bakedPart.type();
        // hook part only render in hook entity.
        if (partType == SkinPartTypes.ITEM_FISHING_HOOK) {
            return isHookEntity(renderState);
        }
        if (partType == SkinPartTypes.ITEM_FISHING_ROD1) {
            return isFishing(renderState);
        }
        if (partType == SkinPartTypes.ITEM_FISHING_ROD) {
            if (isHookEntity(renderState)) {
                return false;
            }
            return !isFishing(renderState);
        }
        if (partType == SkinPartTypes.ITEM_ARROW) {
            // arrow part only render in arrow entity
            if (isArrowEntity(renderState)) {
                return true;
            }
            // we have some old skin that only contain arrow part,
            // so when it happens, we need to be compatible rendering it.
            // we use `UNKNOWN` to rendering the GUI/Ground/ItemFrame.
            if (itemSource.displayContext() == OpenItemDisplayContext.NONE) {
                return bakedPart.children().size() == 1;
            }
            return false;
        }
        if (isArrowEntity(renderState)) {
            return false; // arrow entity only render arrow part
        }
        if (isHookEntity(renderState)) {
            return false; // hook entity only render arrow part.
        }
        if (partType instanceof ICanUse canUse && renderState instanceof LivingEntityRenderState renderState1) {
            var useTick = getUseTick(renderState1, itemSource.itemStack());
            var useRange = canUse.useRange();
            var tickRange = bakedSkin.useTickRange();
            return useRange.contains(OpenMath.clamp(useTick, tickRange.lowerEndpoint(), tickRange.upperEndpoint()));
        }
        return true;
    }

    private static boolean isFishing(@Nullable EntityRenderState renderState) {
        return renderState instanceof PlayerRenderState renderState1 && renderState1.isFishing();
    }

    private static boolean isHookEntity(@Nullable EntityRenderState renderState) {
        return renderState instanceof FishingHookRenderState;
    }

    private static boolean isArrowEntity(@Nullable EntityRenderState renderState) {
        // in vanilla considers trident to be a special arrow,
        // but this no fits we definition of arrow skin.
        if (renderState instanceof ThrownTridentRenderState) {
            return false;
        }
        return renderState instanceof ArrowRenderState;
    }

    private static int getUseTick(LivingEntityRenderState renderState1, ItemStack itemStack) {
        // the item is using.
        if (renderState1.usingItem() == itemStack) {
            return renderState1.ticksUsingItem();
        }
        // this item is charged (only crossbow).
        if (CrossbowItem.isCharged(itemStack)) {
            return 100;
        }
        return 0;
    }
}
