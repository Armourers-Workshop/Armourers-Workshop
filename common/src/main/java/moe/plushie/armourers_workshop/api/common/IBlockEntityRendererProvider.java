package moe.plushie.armourers_workshop.api.common;

import moe.plushie.armourers_workshop.compatibility.AbstractBlockEntityRendererContext;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.world.level.block.entity.BlockEntity;

public interface IBlockEntityRendererProvider<T extends BlockEntity> {

    @Environment(value = EnvType.CLIENT)
    BlockEntityRenderer<T> getBlockEntityRenderer(AbstractBlockEntityRendererContext context);
}
