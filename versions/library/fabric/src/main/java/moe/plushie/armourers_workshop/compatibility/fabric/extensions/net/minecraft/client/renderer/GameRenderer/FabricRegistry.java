package moe.plushie.armourers_workshop.compatibility.fabric.extensions.net.minecraft.client.renderer.GameRenderer;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.registry.IRegistryKey;
import moe.plushie.armourers_workshop.compatibility.client.AbstractBlockEntityRendererProvider;
import moe.plushie.armourers_workshop.compatibility.client.AbstractEntityRendererProvider;
import moe.plushie.armourers_workshop.init.environment.EnvironmentExecutor;
import moe.plushie.armourers_workshop.init.environment.EnvironmentType;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.ThisClass;

@Available("[1.18, )")
@Extension
public class FabricRegistry {

    public static <T extends Entity> void registerEntityRendererFA(@ThisClass Class<?> clazz, IRegistryKey<EntityType<T>> entityType, AbstractEntityRendererProvider<T> provider) {
        EntityRendererRegistry.register(entityType.get(), provider::create);
    }

    public static <T extends BlockEntity> void registerBlockEntityRendererFA(@ThisClass Class<?> clazz, IRegistryKey<BlockEntityType<T>> entityType, AbstractBlockEntityRendererProvider<T> provider) {
        EnvironmentExecutor.didInit(EnvironmentType.CLIENT, () -> () -> {
            BlockEntityRendererRegistry.register(entityType.get(), provider::create);
        });
    }
}

