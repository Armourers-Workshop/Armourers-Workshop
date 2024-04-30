package moe.plushie.armourers_workshop.compatibility.forge;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.common.IArgumentSerializer;
import moe.plushie.armourers_workshop.api.common.IArgumentType;
import moe.plushie.armourers_workshop.compatibility.AbstractArgumentTypeInfo;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.commands.synchronization.ArgumentTypeInfos;
import net.minecraft.resources.ResourceLocation;

@Available("[1.19, )")
public class AbstractForgeArgumentType {

    public static <T extends IArgumentType<?>> AbstractArgumentTypeInfo<T> register(ResourceLocation registryName, Class<T> argumentType, IArgumentSerializer<T> argumentSerializer) {
        AbstractArgumentTypeInfo<T> info = new AbstractArgumentTypeInfo<>(argumentSerializer);
        ArgumentTypeInfo<?, ?> info1 = ArgumentTypeInfos.registerByClass(argumentType, info);
        AbstractForgeRegistries.COMMAND_ARGUMENT_TYPES.register(registryName.getPath(), () -> info1);
        return info;
    }
}
