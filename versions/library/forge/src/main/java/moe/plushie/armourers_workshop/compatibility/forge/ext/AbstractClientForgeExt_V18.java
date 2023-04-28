package moe.plushie.armourers_workshop.compatibility.forge.ext;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.common.IRegistryKey;
import moe.plushie.armourers_workshop.compatibility.client.AbstractBlockEntityRendererProvider;
import moe.plushie.armourers_workshop.compatibility.client.AbstractEntityRendererProvider;
import moe.plushie.armourers_workshop.compatibility.client.AbstractItemStackRendererProvider;
import moe.plushie.armourers_workshop.compatibility.forge.AbstractForgeClientNativeProvider;
import moe.plushie.armourers_workshop.init.platform.forge.NotificationCenterImpl;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.client.event.EntityRenderersEvent;

import java.util.function.Consumer;

@Available("[1.18, )")
public interface AbstractClientForgeExt_V18 extends AbstractForgeClientNativeProvider {

    void willRegisterItemRenderer(Consumer<ItemRendererRegistry> consumer);

    interface ItemRendererRegistry {
        void register(Item item, AbstractItemStackRendererProvider provider);
    }

    @Override
    default void entityRendererRegistry(Consumer<MyEntityRendererRegistry> consumer) {
        consumer.accept(new MyEntityRendererRegistry() {
            @Override
            public <T extends Entity> void registerEntity(IRegistryKey<EntityType<T>> entityType, AbstractEntityRendererProvider<T> provider) {
                NotificationCenterImpl.observer(EntityRenderersEvent.RegisterRenderers.class, event -> event.registerEntityRenderer(entityType.get(), provider::create));
            }

            @Override
            public <T extends BlockEntity> void registerBlockEntity(IRegistryKey<BlockEntityType<T>> entityType, AbstractBlockEntityRendererProvider<T> provider) {
                NotificationCenterImpl.observer(EntityRenderersEvent.RegisterRenderers.class, event -> event.registerBlockEntityRenderer(entityType.get(), provider::create));
            }
        });
    }
}
