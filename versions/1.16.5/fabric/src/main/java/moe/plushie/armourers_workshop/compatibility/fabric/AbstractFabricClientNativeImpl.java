package moe.plushie.armourers_workshop.compatibility.fabric;

import moe.plushie.armourers_workshop.compatibility.AbstractClientNativeImpl;
import moe.plushie.armourers_workshop.init.platform.fabric.provider.ClientNativeProviderImpl;
import net.fabricmc.fabric.api.object.builder.v1.client.model.FabricModelPredicateProviderRegistry;

import java.util.function.Consumer;

public class AbstractFabricClientNativeImpl extends AbstractClientNativeImpl implements ClientNativeProviderImpl {

    @Override
    public void willRegisterItemProperty(Consumer<ItemPropertyRegistry> consumer) {
        consumer.accept(((registryName, item, property) -> FabricModelPredicateProviderRegistry.register(item, registryName, (itemStack, level, entity) -> property.getValue(itemStack, level, entity, 0))));
    }
}
