package moe.plushie.armourers_workshop.compatibility.fabric;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.common.IArgumentSerializer;
import moe.plushie.armourers_workshop.api.common.IArgumentType;
import moe.plushie.armourers_workshop.api.core.IResourceLocation;
import moe.plushie.armourers_workshop.compatibility.AbstractArgumentTypeInfo;
import net.fabricmc.fabric.api.command.v2.ArgumentTypeRegistry;

@Available("[1.19, )")
public class AbstractFabricArgumentType {

    public static <T extends IArgumentType<?>> AbstractArgumentTypeInfo<T> register(IResourceLocation registryName, Class<T> argumentType, IArgumentSerializer<T> argumentSerializer) {
        AbstractArgumentTypeInfo<T> info = new AbstractArgumentTypeInfo<>(argumentSerializer);
        ArgumentTypeRegistry.registerArgumentType(registryName.toLocation(), argumentType, info);
        return info;
    }

//    public static <T extends IArgumentType<?>> void register(ResourceLocation registryName, Class<T> argumentType, IArgumentSerializer<T> argumentSerializer) {
//        ArgumentTypes.register(registryName.toString(), argumentType, new AbstractArgumentTypeInfo<>(argumentSerializer));
//    }
}
