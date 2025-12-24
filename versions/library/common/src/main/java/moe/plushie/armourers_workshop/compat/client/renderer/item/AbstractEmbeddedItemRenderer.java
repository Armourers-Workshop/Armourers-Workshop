package moe.plushie.armourers_workshop.compat.client.renderer.item;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.PoseStack;
import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.annotation.Dist;
import moe.plushie.armourers_workshop.api.annotation.OnlyIn;
import moe.plushie.armourers_workshop.compat.client.entity.state.AbstractItemStackRenderState;
import moe.plushie.armourers_workshop.compat.client.entity.state.AbstractRenderState;
import moe.plushie.armourers_workshop.compat.client.renderer.graphics.AbstractGraphicsRenderer;
import moe.plushie.armourers_workshop.core.client.other.SkinRenderMode;
import moe.plushie.armourers_workshop.core.client.render.EmbeddedItemStackRenderer;
import moe.plushie.armourers_workshop.core.client.render.model.EmbeddedItemModel;
import moe.plushie.armourers_workshop.core.utils.OpenItemDisplayContext;
import moe.plushie.armourers_workshop.init.ModDebugger;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Available("[1.16, 1.26)")
@OnlyIn(Dist.CLIENT)
public class AbstractEmbeddedItemRenderer {

    @Nullable
    public static EmbeddedItemModel extract(ItemStack itemStack, @Nullable LivingEntity entity, @Nullable Level level, BakedModel originItemModel) {
        var itemRenderState = AbstractItemStackRenderState.wrap(itemStack, entity, level, originItemModel);
        var itemModel = EmbeddedItemStackRenderer.extract(itemStack, itemRenderState, entity);
        if (itemModel != null) {
            itemModel.setEntityRenderState(AbstractRenderState.wrap(entity));
        }
        itemStack.setEmbeddedItemModel(originItemModel, itemModel);
        return itemModel;
    }

    public static void render(ItemStack itemStack, OpenItemDisplayContext displayContext, PoseStack poseStack, MultiBufferSource bufferSource, int i, int j, boolean bl, BakedModel originItemModel, CallbackInfo cir) {
        var itemModel = itemStack.getEmbeddedItemModel(originItemModel);
        if (itemModel == null) {
            return;
        }
        // when the original not uses block light in the gui rendering case,
        // the vanilla will be set to lighting to flat items,
        // but we require lighting is 3d items.
        if (SkinRenderMode.inGUI() && !originItemModel.usesBlockLight()) {
            Lighting.setupFor3DItems();
        }
        var count = EmbeddedItemStackRenderer.render(itemModel, i, j, displayContext, AbstractGraphicsRenderer.wrap(poseStack, bufferSource));
        if (count != 0 && !ModDebugger.itemOverride) {
            cir.cancel();
        }
        // restore the lighting if we changed.
        if (SkinRenderMode.inGUI() && !originItemModel.usesBlockLight()) {
            Lighting.setupForFlatItems();
        }
    }
}
