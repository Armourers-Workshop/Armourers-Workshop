package moe.plushie.armourers_workshop.compatibility.fabric;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.config.IConfigBuilder;
import moe.plushie.armourers_workshop.api.config.IConfigSpec;
import moe.plushie.armourers_workshop.compatibility.AbstractConfigSpec;
import moe.plushie.armourers_workshop.init.ModConstants;
import moe.plushie.armourers_workshop.init.platform.fabric.config.FabricConfig;
import moe.plushie.armourers_workshop.init.platform.fabric.config.FabricConfigSpec;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import org.apache.commons.lang3.tuple.Pair;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

@Available("[1.16, )")
public class AbstractFabricConfigSpec extends AbstractConfigSpec {

    public AbstractFabricConfigSpec(Type type, HashMap<String, Value<Object>> values) {
        super(type, values);
    }

    public static <B extends IConfigBuilder> IConfigSpec create(Type type, Function<IConfigBuilder, B> applier) {
        // create a builder from loader.
        Pair<B, FabricConfigSpec> pair = new FabricConfigSpec.Builder().configure(builder -> applier.apply(new Builder() {

            @Override
            public IConfigSpec build() {
                return new AbstractFabricConfigSpec(type, values);
            }

            @Override
            protected Builder push(String name) {
                builder.push(name);
                return this;
            }

            @Override
            protected Builder pop() {
                builder.pop();
                return this;
            }

            @Override
            protected Builder comment(String... comment) {
                builder.comment(comment);
                return this;
            }

            @Override
            protected Value<Boolean> define(String path, boolean defaultValue) {
                return cast(path, builder.define(path, defaultValue));
            }

            @Override
            protected Value<Integer> defineInRange(String path, int defaultValue, int minValue, int maxValue) {
                return cast(path, builder.defineInRange(path, defaultValue, minValue, maxValue));
            }

            @Override
            protected Value<Double> defineInRange(String path, double defaultValue, double minValue, double maxValue) {
                return cast(path, builder.defineInRange(path, defaultValue, minValue, maxValue));
            }

            @Override
            protected <T> Value<List<? extends T>> defineList(String path, List<? extends T> defaultValue, Predicate<Object> elementValidator) {
                return cast(path, builder.defineList(path, defaultValue, elementValidator));
            }

            <T> Value<T> cast(String path, FabricConfigSpec.ConfigValue<T> value) {
                return new Value<>(path, value::get, value::set);
            }
        }));

        // bind the config to spec.
        AbstractFabricConfigSpec spec = (AbstractFabricConfigSpec) pair.getLeft().build();
        spec.bind(pair.getRight(), FabricConfigSpec::save);

        // registry the config into loader.
        FabricConfigSpec config = pair.getRight();
        Optional<ModContainer> container = FabricLoader.getInstance().getModContainer(ModConstants.MOD_ID);
        if (container.isPresent()) {
            FabricConfig ignored = new FabricConfig(FabricConfig.Type.valueOf(type.name()), config, container.get());
        }

        return spec;
    }
}
