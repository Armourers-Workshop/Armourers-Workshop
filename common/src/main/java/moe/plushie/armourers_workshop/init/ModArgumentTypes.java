package moe.plushie.armourers_workshop.init;

import moe.plushie.armourers_workshop.api.common.IArgumentType;
import moe.plushie.armourers_workshop.api.common.IRegistryKey;
import moe.plushie.armourers_workshop.api.common.builder.IArgumentTypeBuilder;
import moe.plushie.armourers_workshop.init.command.ColorArgumentType;
import moe.plushie.armourers_workshop.init.command.ColorSchemeArgumentType;
import moe.plushie.armourers_workshop.init.command.ComponentArgumentType;
import moe.plushie.armourers_workshop.init.command.FileArgumentType;
import moe.plushie.armourers_workshop.init.command.ListArgumentType;
import moe.plushie.armourers_workshop.init.platform.BuilderManager;


@SuppressWarnings("unused")
public class ModArgumentTypes {

    public static IRegistryKey<?> ITEMS = normal(ListArgumentType.class).serializer(ListArgumentType.Serializer::new).build("items");
    public static IRegistryKey<?> FILES = normal(FileArgumentType.class).serializer(FileArgumentType.Serializer::new).build("files");
    public static IRegistryKey<?> DYE = normal(ColorSchemeArgumentType.class).serializer(ColorSchemeArgumentType.Serializer::new).build("dye");
    public static IRegistryKey<?> COLOR = normal(ColorArgumentType.class).serializer(ColorArgumentType.Serializer::new).build("color");
    public static IRegistryKey<?> COMPONENT = normal(ComponentArgumentType.class).serializer(ComponentArgumentType.Serializer::new).build("component");

    private static <T extends IArgumentType<?>> IArgumentTypeBuilder<T> normal(Class<T> clazz) {
        return BuilderManager.getInstance().createArgumentTypeBuilder(clazz);
    }

    public static void init() {
    }
}
