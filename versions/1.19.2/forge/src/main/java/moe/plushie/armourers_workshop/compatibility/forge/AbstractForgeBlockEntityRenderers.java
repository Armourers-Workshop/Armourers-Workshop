package moe.plushie.armourers_workshop.compatibility.forge;

import moe.plushie.armourers_workshop.api.common.IBlockEntityRendererProvider;
import moe.plushie.armourers_workshop.init.platform.RendererManager;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

public interface AbstractForgeBlockEntityRenderers {

    static <T extends BlockEntity> void register(BlockEntityType<? extends T> entityType, IBlockEntityRendererProvider<T> provider) {
        BlockEntityRenderers.register(entityType, (context) -> provider.getBlockEntityRenderer(RendererManager.getBlockContext()));
    }
}
