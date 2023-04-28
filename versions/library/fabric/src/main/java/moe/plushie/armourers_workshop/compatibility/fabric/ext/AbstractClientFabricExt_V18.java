package moe.plushie.armourers_workshop.compatibility.fabric.ext;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.common.IRegistryKey;
import moe.plushie.armourers_workshop.compatibility.client.AbstractBlockEntityRendererProvider;
import moe.plushie.armourers_workshop.compatibility.client.AbstractEntityRendererProvider;
import moe.plushie.armourers_workshop.compatibility.fabric.AbstractFabricClientNativeProvider;
import moe.plushie.armourers_workshop.init.environment.EnvironmentExecutor;
import moe.plushie.armourers_workshop.init.environment.EnvironmentType;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.function.Consumer;

@Available("[1.18, )")
public interface AbstractClientFabricExt_V18 extends AbstractFabricClientNativeProvider {

    @Override
    default void entityRendererRegistry(Consumer<MyEntityRendererRegistry> consumer) {
        consumer.accept(new MyEntityRendererRegistry() {
            @Override
            public <T extends Entity> void registerEntity(IRegistryKey<EntityType<T>> entityType, AbstractEntityRendererProvider<T> provider) {
                EntityRendererRegistry.register(entityType.get(), provider::create);
            }

            @Override
            public <T extends BlockEntity> void registerBlockEntity(IRegistryKey<BlockEntityType<T>> entityType, AbstractBlockEntityRendererProvider<T> provider) {
                EnvironmentExecutor.didInit(EnvironmentType.CLIENT, () -> () -> {
                    BlockEntityRendererRegistry.register(entityType.get(), provider::create);
                });
            }
        });
    }
}
