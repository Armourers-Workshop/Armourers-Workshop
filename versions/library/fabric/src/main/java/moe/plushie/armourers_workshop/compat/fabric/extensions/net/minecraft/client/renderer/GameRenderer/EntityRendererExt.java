package moe.plushie.armourers_workshop.compat.fabric.extensions.net.minecraft.client.renderer.GameRenderer;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.client.IBlockEntityRenderer;
import moe.plushie.armourers_workshop.api.client.IEntityRenderer;
import moe.plushie.armourers_workshop.api.common.IBlockEntityType;
import moe.plushie.armourers_workshop.api.common.IEntityType;
import moe.plushie.armourers_workshop.api.core.IRegistryHolder;
import moe.plushie.armourers_workshop.compat.client.renderer.blockentity.AbstractBlockEntityRenderer;
import moe.plushie.armourers_workshop.compat.client.renderer.entity.AbstractEntityRenderer;
import moe.plushie.armourers_workshop.init.environment.EnvironmentExecutor;
import moe.plushie.armourers_workshop.init.environment.EnvironmentType;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BlockEntity;

import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.ThisClass;

@Available("[18, )")
@Extension
public class EntityRendererExt {

    public static <T extends Entity> void registerEntityRendererFA(@ThisClass Class<?> clazz, IRegistryHolder<IEntityType<T>> entityType, IEntityRenderer.Provider<T> provider) {
        EntityRendererRegistry.register(entityType.get().get(), AbstractEntityRenderer.builder(provider));
    }

    public static <T extends BlockEntity> void registerBlockEntityRendererFA(@ThisClass Class<?> clazz, IRegistryHolder<IBlockEntityType<T>> entityType, IBlockEntityRenderer.Provider<T> provider) {
        EnvironmentExecutor.willInit(EnvironmentType.CLIENT, () -> () -> {
            BlockEntityRendererRegistry.register(entityType.get().get(), AbstractBlockEntityRenderer.builder(provider));
        });
    }
}

