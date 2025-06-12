package moe.plushie.armourers_workshop.core.skin.molang.thirdparty.bind;

import moe.plushie.armourers_workshop.core.skin.molang.core.ExecutionContext;
import moe.plushie.armourers_workshop.core.skin.molang.core.Name;
import moe.plushie.armourers_workshop.core.skin.molang.core.Result;
import moe.plushie.armourers_workshop.core.skin.molang.core.VariableStorage;
import moe.plushie.armourers_workshop.core.skin.molang.runtime.LocalVariableStorage;
import moe.plushie.armourers_workshop.core.skin.molang.runtime.StaticVariableStorage;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.block.entity.BlockEntity;

public class ExecutionContextImpl extends ContextSelectorImpl implements ExecutionContext {

    private static final VariableStorage FOREIGN = new StaticVariableStorage();

    protected Object target;
    protected VariableStorage entity;

    protected final LocalVariableStorage stack;

    public ExecutionContextImpl(Object entity) {
        this(entity, new LocalVariableStorage());
    }

    protected ExecutionContextImpl(Object target, LocalVariableStorage stack) {
        this.target = target;
        this.entity = createEntity(target);
        this.stack = stack;
    }


    @Override
    public ExecutionContext fork(Object target) {
        return new ExecutionContextImpl(target, stack);
    }

    @Override
    public LocalVariableStorage stack() {
        return stack;
    }

    @Override
    public VariableStorage entity() {
        return entity;
    }

    public Object target() {
        return target;
    }

    @Override
    public void setVariable(Name name, Result value) {
        FOREIGN.setVariable(name, value);
    }

    @Override
    public Result getVariable(Name name) {
        return FOREIGN.getVariable(name);
    }

    protected VariableStorage createEntity(Object target) {
        if (target instanceof Player entity1) {
            var impl = new PlayerSelectorImpl<>();
            return impl.apply(entity1, this);
        }
        if (target instanceof Projectile entity1) {
            var impl = new ProjectileEntitySelectorImpl<>();
            return impl.apply(entity1, this);
        }
        if (target instanceof LivingEntity entity1) {
            var impl = new LivingEntitySelectorImpl<>();
            return impl.apply(entity1, this);
        }
        if (target instanceof Entity entity1) {
            var impl = new EntitySelectorImpl<>();
            return impl.apply(entity1, this);
        }
        if (target instanceof BlockEntity entity1) {
            var impl = new BlockEntitySelectorImpl<>();
            return impl.apply(entity1, this);
        }
        if (target instanceof VariableStorage entity1) {
            return entity1;
        }
        return null;
    }
}
