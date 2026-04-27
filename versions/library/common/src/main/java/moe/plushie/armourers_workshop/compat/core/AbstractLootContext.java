package moe.plushie.armourers_workshop.compat.core;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.common.IContextKey;
import moe.plushie.armourers_workshop.api.common.ILootContext;
import moe.plushie.armourers_workshop.api.common.IRandomSource;
import net.minecraft.world.level.storage.loot.LootContext;
import org.jetbrains.annotations.Nullable;

@Available("[16, 26)")
public class AbstractLootContext implements ILootContext {

    private final LootContext impl;
    private final IRandomSource source;

    public AbstractLootContext(LootContext context) {
        this.impl = context;
        this.source = AbstractRandomSource.wrap(context.getRandom());
    }

    public static AbstractLootContext wrap(LootContext context) {
        return new AbstractLootContext(context);
    }

    public static LootContext unwrap(ILootContext context) {
        return ((AbstractLootContext) context).impl;
    }

    @Override
    public boolean hasParameter(IContextKey<?> key) {
        return impl.hasParam(AbstractLootContextParams.unwrap(key));
    }

    @Override
    public <T> T getParameter(IContextKey<T> key) {
        return impl.getParamOrNull(AbstractLootContextParams.unwrap(key));
    }

    @Nullable
    public <T> T getOptionalParameter(IContextKey<T> key) {
        return impl.getParamOrNull(AbstractLootContextParams.unwrap(key));
    }

    public IRandomSource randomSource() {
        return source;
    }
}
