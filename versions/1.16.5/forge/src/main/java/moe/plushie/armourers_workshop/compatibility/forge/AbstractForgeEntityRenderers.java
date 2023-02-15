package moe.plushie.armourers_workshop.compatibility.forge;

import moe.plushie.armourers_workshop.api.common.IBlockEntityRendererProvider;
import moe.plushie.armourers_workshop.api.common.IEntityRendererProvider;
import moe.plushie.armourers_workshop.api.common.IRegistryKey;
import moe.plushie.armourers_workshop.init.environment.EnvironmentExecutor;
import moe.plushie.armourers_workshop.init.environment.EnvironmentType;
import moe.plushie.armourers_workshop.init.platform.RendererManager;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

public interface AbstractForgeEntityRenderers {

    static <T extends Entity> void registerEntity(IRegistryKey<EntityType<T>> entityType, IEntityRendererProvider<T> provider) {
        EnvironmentExecutor.didInit(EnvironmentType.CLIENT, () -> () -> {
            RenderingRegistry.registerEntityRenderingHandler(entityType.get(), context -> provider.getEntityRenderer(RendererManager.getEntityContext()));
        });
    }

    static <T extends BlockEntity> void registerBlockEntity(IRegistryKey<BlockEntityType<T>> entityType, IBlockEntityRendererProvider<T> provider) {
        EnvironmentExecutor.didInit(EnvironmentType.CLIENT, () -> () -> {
            ClientRegistry.bindTileEntityRenderer(entityType.get(), (context) -> provider.getBlockEntityRenderer(RendererManager.getBlockEntityContext()));
        });
    }
}
