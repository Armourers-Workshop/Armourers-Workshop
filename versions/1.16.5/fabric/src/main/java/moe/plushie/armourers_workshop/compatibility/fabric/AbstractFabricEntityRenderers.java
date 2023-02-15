package moe.plushie.armourers_workshop.compatibility.fabric;

import moe.plushie.armourers_workshop.api.common.IBlockEntityRendererProvider;
import moe.plushie.armourers_workshop.api.common.IEntityRendererProvider;
import moe.plushie.armourers_workshop.api.common.IRegistryKey;
import moe.plushie.armourers_workshop.init.platform.RendererManager;
import net.fabricmc.fabric.api.client.rendereregistry.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

public interface AbstractFabricEntityRenderers {

    static <T extends Entity> void registerEntity(IRegistryKey<EntityType<T>> entityType, IEntityRendererProvider<T> provider) {
        EntityRendererRegistry.INSTANCE.register(entityType.get(), (a, b) -> provider.getEntityRenderer(RendererManager.getEntityContext()));
    }

    static <T extends BlockEntity> void registerBlockEntity(IRegistryKey<BlockEntityType<T>> entityType, IBlockEntityRendererProvider<T> provider) {
        BlockEntityRendererRegistry.INSTANCE.register(entityType.get(), context -> provider.getBlockEntityRenderer(RendererManager.getBlockEntityContext()));
    }

}
