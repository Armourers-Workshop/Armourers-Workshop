package moe.plushie.armourers_workshop.core.skin.molang;

import moe.plushie.armourers_workshop.core.skin.molang.core.Expression;
import moe.plushie.armourers_workshop.core.skin.molang.core.Variable;
import moe.plushie.armourers_workshop.core.skin.molang.impl.SyntaxException;
import moe.plushie.armourers_workshop.core.skin.molang.impl.Compiler;
import moe.plushie.armourers_workshop.core.skin.molang.impl.Optimizer;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@SuppressWarnings("unused")
public class MolangVirtualMachine {

    private static final MolangVirtualMachine DEFAULT = new MolangVirtualMachine();

    private final Compiler compiler = new Compiler();
    private final Optimizer optimizer = new Optimizer();

    private final Map<String, Variable> variables = new ConcurrentHashMap<>();

    public final Variable animTime = register("query.anim_time", 0);

    public final Variable lifeTime = register("query.life_time", 0);

    public final Variable actorCount = register("query.actor_count", 0);

    public final Variable timeOfDay = register("query.time_of_day", 0);

    public final Variable moonPhase = register("query.moon_phase", 0);

    public final Variable distanceFromCamera = register("query.distance_from_camera", 0);

    public final Variable isOnGround = register("query.is_on_ground", 0);

    public final Variable isInWater = register("query.is_in_water", 0);

    public final Variable isInWaterOrRain = register("query.is_in_water_or_rain", 0);

    public final Variable health = register("query.health", 0);

    public final Variable maxHealth = register("query.max_health", 0);

    public final Variable isOnFire = register("query.is_on_fire", 0);

    public final Variable groundSpeed = register("query.ground_speed", 0);

    public final Variable yawSpeed = register("query.yaw_speed", 0);

    public static MolangVirtualMachine get() {
        return DEFAULT;
    }

    /**
     * Create a molang expression
     */
    public Expression eval(String expression) throws SyntaxException {
        var value = compiler.compile(expression);
        return optimizer.optimize(value);
    }

    public Variable register(String name, double value) {
        var variable = new Variable(name, value);
        compiler.registerVariable(name, variable);
        variables.put(name, variable);
        return variable;
    }

    public Map<String, ? extends Variable> getVariables() {
        return variables;
    }
}
