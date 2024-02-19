package moe.plushie.armourers_workshop.compatibility.forge.mixin;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.compatibility.client.renderer.AbstractBlockEntityRendererImpl;
import moe.plushie.armourers_workshop.compatibility.forge.AbstractForgeBlockEntity;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;

@Available("[1.21, )")
@Mixin(AbstractBlockEntityRendererImpl.class)
public abstract class ForgeBlockEntityRendererMixin<T extends BlockEntity> implements BlockEntityRenderer<T> {

    @Override
    public AABB getRenderBoundingBox(T blockEntity) {
        if (blockEntity instanceof AbstractForgeBlockEntity) {
            AABB result = ((AbstractForgeBlockEntity) blockEntity).getRenderBoundingBox();
            if (result != null) {
                return result;
            }
        }
        return BlockEntityRenderer.super.getRenderBoundingBox(blockEntity);
    }
}
