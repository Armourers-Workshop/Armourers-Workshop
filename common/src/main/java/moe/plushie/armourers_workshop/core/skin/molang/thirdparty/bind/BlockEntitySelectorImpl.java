package moe.plushie.armourers_workshop.core.skin.molang.thirdparty.bind;

import moe.plushie.armourers_workshop.core.data.EntityDataStorage;
import moe.plushie.armourers_workshop.core.skin.molang.core.Name;
import moe.plushie.armourers_workshop.core.skin.molang.core.Result;
import moe.plushie.armourers_workshop.core.skin.molang.core.VariableStorage;
import moe.plushie.armourers_workshop.core.skin.molang.runtime.bind.selector.BlockEntitySelector;
import net.minecraft.world.level.block.entity.BlockEntity;

public class BlockEntitySelectorImpl<T extends BlockEntity> implements BlockEntitySelector, VariableStorage {

    protected T entity;
    protected VariableStorage variableStorage;
    protected ContextSelectorImpl contextSelector;

    public BlockEntitySelectorImpl<T> apply(T entity, ContextSelectorImpl contextSelector) {
        this.entity = entity;
        this.contextSelector = contextSelector;
        this.variableStorage = EntityDataStorage.of(entity).variableStorage().map(it -> it.get(contextSelector)).orElse(null);
        return this;
    }

    public T getEntity() {
        return entity;
    }

    @Override
    public float partialTick() {
        return contextSelector.partialTick();
    }

    @Override
    public void setVariable(Name name, Result value) {
        variableStorage.setVariable(name, value);
    }

    @Override
    public Result getVariable(Name name) {
        return variableStorage.getVariable(name);
    }
}
