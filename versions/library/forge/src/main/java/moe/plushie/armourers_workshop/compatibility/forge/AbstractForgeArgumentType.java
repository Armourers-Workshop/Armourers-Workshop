package moe.plushie.armourers_workshop.compatibility.forge;

import com.mojang.brigadier.arguments.ArgumentType;
import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.common.IArgumentSerializer;
import moe.plushie.armourers_workshop.api.core.IResourceLocation;
import moe.plushie.armourers_workshop.compatibility.core.AbstractArgumentTypeInfo;
import net.minecraft.commands.synchronization.ArgumentTypeInfos;

@Available("[1.19, )")
public class AbstractForgeArgumentType {

    public static <T extends ArgumentType<?>> AbstractArgumentTypeInfo<T> register(IResourceLocation registryName, Class<T> argumentType, IArgumentSerializer<T> argumentSerializer) {
        var info = new AbstractArgumentTypeInfo<T>(argumentSerializer);
        var info1 = ArgumentTypeInfos.registerByClass(argumentType, info);
        AbstractForgeRegistries.COMMAND_ARGUMENT_TYPES.register(registryName.path(), () -> info1);
        return info;
    }
}
