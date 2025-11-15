package moe.plushie.armourers_workshop.api.common;

import com.mojang.brigadier.arguments.ArgumentType;

import java.util.Collection;
import java.util.Collections;

public interface IArgumentType<T> extends ArgumentType<T> {

    default Collection<String> examples() {
        return Collections.emptyList();
    }

    @Override
    default Collection<String> getExamples() {
        return examples();
    }
}
