package moe.plushie.armourers_workshop.init;

import moe.plushie.armourers_workshop.api.common.IArgumentType;
import moe.plushie.armourers_workshop.api.common.IRegistryKey;
import moe.plushie.armourers_workshop.api.common.builder.IArgumentTypeBuilder;
import moe.plushie.armourers_workshop.init.command.ColorArgument;
import moe.plushie.armourers_workshop.init.command.ColorSchemeArgument;
import moe.plushie.armourers_workshop.init.command.FileArgument;
import moe.plushie.armourers_workshop.init.command.ListArgument;
import moe.plushie.armourers_workshop.init.platform.BuilderManager;


@SuppressWarnings("unused")
public class ModArgumentTypes {

    public static IRegistryKey<?> ITEMS = normal(ListArgument.class).serializer(ListArgument.Serializer::new).build("items");
    public static IRegistryKey<?> FILES = normal(FileArgument.class).serializer(FileArgument.Serializer::new).build("files");
    public static IRegistryKey<?> DYE = normal(ColorSchemeArgument.class).serializer(ColorSchemeArgument.Serializer::new).build("dye");
    public static IRegistryKey<?> COLOR = normal(ColorArgument.class).serializer(ColorArgument.Serializer::new).build("color");

    private static <T extends IArgumentType<?>> IArgumentTypeBuilder<T> normal(Class<T> clazz) {
        return BuilderManager.getInstance().createArgumentTypeBuilder(clazz);
    }

    public static void init() {
    }
}
