package moe.plushie.armourers_workshop.core.client.render.element;

import moe.plushie.armourers_workshop.api.client.IEntityModel;
import moe.plushie.armourers_workshop.api.client.IGraphicsElement;
import moe.plushie.armourers_workshop.api.client.IRenderType;
import moe.plushie.armourers_workshop.compat.client.renderer.graphics.element.AbstractBlockRendererElement;
import moe.plushie.armourers_workshop.compat.client.renderer.graphics.element.AbstractEntityRendererElement;
import moe.plushie.armourers_workshop.compat.client.renderer.graphics.element.AbstractModelRendererElement;
import moe.plushie.armourers_workshop.core.client.render.state.EntityRenderState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.state.BlockState;

public abstract class SpecialRenderElement implements IGraphicsElement {

    // itemStack()

    public static SpecialRenderElement blockState(BlockState blockState, int lightmap, int overlay) {
        return AbstractBlockRendererElement.newInstance(blockState, lightmap, overlay);
    }

    public static SpecialRenderElement entity(Entity entity, float f, float partialTick, int lightmap, int overlay) {
        return AbstractEntityRendererElement.newInstance(entity, f, partialTick, lightmap, overlay);
    }

    public static SpecialRenderElement entityModel(IEntityModel<?> entityModel, EntityRenderState entityRenderState, int lightmap, int overlay, int color, IRenderType renderType) {
        return AbstractModelRendererElement.newInstance(entityModel, entityRenderState, lightmap, overlay, color, renderType);
    }
}
