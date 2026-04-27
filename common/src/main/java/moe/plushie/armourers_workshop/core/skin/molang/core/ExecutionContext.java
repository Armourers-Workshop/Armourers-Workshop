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

    /**
     * Gets the entity-scoped variable storage for this evaluation context.
     *
     * <p>This storage is shared with child contexts created by {@link #fork(Object)}.</p>
     *
     * @return The entity-scoped variable storage.
     */
    VariableStorage entity();

    /**
     * Gets the local stack storage for this evaluation context.
     *
     * <p>Each context has its own stack storage and it is not shared across forks.</p>
     *
     * @return The local variable stack storage.
     */
    LocalVariableStorage stack();
}
