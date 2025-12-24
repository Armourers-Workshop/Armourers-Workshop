package moe.plushie.armourers_workshop.core.skin.molang.core;

import moe.plushie.armourers_workshop.core.skin.molang.runtime.LocalVariableStorage;

public interface ExecutionContext extends VariableStorage {

    /**
     * Creates a new child, expression evaluate context.
     *
     * <p>Child evaluators have all the bindings of
     * their parents and may have extra bindings.</p>
     *
     * <p>Child evaluators have their own stack.</p>
     *
     * @param target The new entity value
     * @return The child expression evaluate context.
     */
    ExecutionContext fork(Object target);

    VariableStorage entity();

    LocalVariableStorage stack();
}
