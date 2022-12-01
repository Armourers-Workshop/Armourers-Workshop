package moe.plushie.armourers_workshop.compatibility.fabric;

import moe.plushie.armourers_workshop.api.common.IBlockEntityRendererProvider;
import moe.plushie.armourers_workshop.init.platform.RendererManager;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

public interface AbstractFabricBlockEntityRenderers {

    static <T extends BlockEntity> void register(BlockEntityType<T> entityType, IBlockEntityRendererProvider<T> provider) {
        BlockEntityRendererRegistry.register(entityType, context -> provider.getBlockEntityRenderer(RendererManager.getBlockContext()));
    }
}
