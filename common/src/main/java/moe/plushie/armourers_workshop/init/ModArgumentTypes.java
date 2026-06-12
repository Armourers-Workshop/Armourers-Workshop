package moe.plushie.armourers_workshop.init;

import moe.plushie.armourers_workshop.api.common.IArgumentSerializer;
import moe.plushie.armourers_workshop.api.common.IArgumentType;
import moe.plushie.armourers_workshop.api.core.IRegistryHolder;
import moe.plushie.armourers_workshop.api.registry.IArgumentTypeBuilder;
import moe.plushie.armourers_workshop.init.command.ColorArgumentType;
import moe.plushie.armourers_workshop.init.command.ColorSchemeArgumentType;
import moe.plushie.armourers_workshop.init.command.FileArgumentType;
import moe.plushie.armourers_workshop.init.command.ListArgumentType;
import moe.plushie.armourers_workshop.init.platform.Platform;

@SuppressWarnings("unused")
public class ModArgumentTypes {

    public static IRegistryHolder<ListArgumentType> ITEMS = normal(ListArgumentType.TYPE).build("items");
    public static IRegistryHolder<FileArgumentType> FILES = normal(FileArgumentType.TYPE).build("files");
    public static IRegistryHolder<ColorSchemeArgumentType> DYE = normal(ColorSchemeArgumentType.TYPE).build("dye");
    public static IRegistryHolder<ColorArgumentType> COLOR = normal(ColorArgumentType.TYPE).build("color");

    private static <T extends IArgumentType<?>> IArgumentTypeBuilder<T> normal(IArgumentSerializer<T> serializer) {
        return Platform.get().common().builder().argumentType(serializer);
    }

    public static void init() {
    }
}
