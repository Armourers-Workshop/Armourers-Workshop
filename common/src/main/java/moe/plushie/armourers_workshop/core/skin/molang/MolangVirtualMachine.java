package moe.plushie.armourers_workshop.core.skin.molang;

import moe.plushie.armourers_workshop.core.skin.molang.expressions.MolangValue;
import moe.plushie.armourers_workshop.core.skin.molang.math.LazyVariable;
import moe.plushie.armourers_workshop.core.skin.molang.math.Variable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.DoubleSupplier;

@SuppressWarnings("unused")
public class MolangVirtualMachine {

    private static final MolangVirtualMachine DEFAULT = new MolangVirtualMachine();

    private final MolangParser parser = new MolangParser();
    private final HashMap<String, LazyVariable> variables = new HashMap<>();

    public final LazyVariable animTime = register("query.anim_time", 0);

    public final LazyVariable lifeTime = register("query.life_time", 0);

    public final LazyVariable actorCount = register("query.actor_count", 0);

    public final LazyVariable timeOfDay = register("query.time_of_day", 0);

    public final LazyVariable moonPhase = register("query.moon_phase", 0);

    public final LazyVariable distanceFromCamera = register("query.distance_from_camera", 0);

    public final LazyVariable isOnGround = register("query.is_on_ground", 0);

    public final LazyVariable isInWater = register("query.is_in_water", 0);

    public final LazyVariable isInWaterOrRain = register("query.is_in_water_or_rain", 0);

    public final LazyVariable health = register("query.health", 0);

    public final LazyVariable maxHealth = register("query.max_health", 0);

    public final LazyVariable isOnFire = register("query.is_on_fire", 0);

    public final LazyVariable groundSpeed = register("query.ground_speed", 0);

    public final LazyVariable yawSpeed = register("query.yaw_speed", 0);


    public MolangVirtualMachine() {
    }

    public static MolangVirtualMachine get() {
        return DEFAULT;
    }

    /**
     * Create a molang expression
     */
    public MolangValue create(String expression) throws MolangException {
        return parser.parseExpression(expression);
    }

    /**
     * Set the value for a variable.<br>
     *
     * @param name  The name of the variable to set the value for
     * @param value The value supplier to set
     */
    public void setValue(String name, Double value) {
        var variable = parser.getVariable(name);
        variable.set(value);
    }

    /**
     * Set the value supplier for a variable.<br>
     *
     * @param name  The name of the variable to set the value for
     * @param value The value supplier to set
     */
    public void setValue(String name, DoubleSupplier value) {
        var variable = parser.getVariable(name);
        if (variable instanceof LazyVariable lazyVariable) {
            lazyVariable.set(value);
        } else {
            variable.set(value.getAsDouble());
        }
    }

    public LazyVariable register(String name) {
        return register(name, 0);
    }

    public LazyVariable register(String name, double value) {
        var variable = new LazyVariable(name, value);
        parser.register(variable);
        variables.put(name, variable);
        return variable;
    }

    public LazyVariable register(String name, DoubleSupplier value) {
        var variable = new LazyVariable(name, value);
        parser.register(variable);
        variables.put(name, variable);
        return variable;
    }

    public Map<String, ? extends Variable> getVariables() {
        return variables;
    }
}
