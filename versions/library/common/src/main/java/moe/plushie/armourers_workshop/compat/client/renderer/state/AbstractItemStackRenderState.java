package moe.plushie.armourers_workshop.compat.client.renderer.state;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.compat.client.item.model.AbstractItemModel;
import moe.plushie.armourers_workshop.core.client.render.model.SkinItemModel;
import moe.plushie.armourers_workshop.core.client.render.model.SkinItemModelResolver;
import moe.plushie.armourers_workshop.core.client.render.state.ItemStackRenderState;
import moe.plushie.armourers_workshop.core.utils.OpenItemDisplayContext;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

@Available("[1.16, 1.22)")
public class AbstractItemStackRenderState {

    private static ItemStack RENDERING_GUI_ITEM = null;

    public static ItemStackRenderState wrap(ItemStack itemStack, @Nullable LivingEntity entity, @Nullable Level level, BakedModel originItemModel) {
        return new ItemStackRenderState(itemStack, AbstractItemModel.wrap(originItemModel), new ModelResolverImpl(entity, level), itemStack == RENDERING_GUI_ITEM);
    }

    public static void startRenderGuiItem(ItemStack itemStack) {
        RENDERING_GUI_ITEM = itemStack;
    }

    public static void endRenderGuiItem(ItemStack itemStack) {
        RENDERING_GUI_ITEM = null;
    }

    private static class ModelResolverImpl implements SkinItemModelResolver {

        private final Entity entity;
        private final Level level;

        public ModelResolverImpl(Entity entity, Level level) {
            this.entity = entity;
            this.level = level;
        }

        @Override
        public SkinItemModel resolve(SkinItemModel itemModel, ItemStack itemStack, int flags, OpenItemDisplayContext displayContext) {
            return itemModel.resolve(itemStack, entity, level, flags, displayContext);
        }
    }
}
