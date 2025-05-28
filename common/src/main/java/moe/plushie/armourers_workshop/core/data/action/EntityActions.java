package moe.plushie.armourers_workshop.core.data.action;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unused")
public class EntityActions {

    private static final Map<String, EntityActionTarget> NAMED_ACTIONS = new HashMap<>();

    public static final EntityActionTarget IDLE = normal().action(EntityAction.IDLE).priority(100).build("idle");

    public static final EntityActionTarget WALK = normal().action(EntityAction.WALK).priority(220).build("walk");
    public static final EntityActionTarget RUN = normal().action(EntityAction.WALK).action(EntityAction.RUNNING).priority(240).build("run");
    public static final EntityActionTarget JUMP = normal().action(EntityAction.JUMP).transition(0).priority(280).build("jump");

    public static final EntityActionTarget SNEAK = normal().action(EntityAction.SNEAK).priority(200).build("sneak");
    public static final EntityActionTarget SNEAK_IDLE = normal().action(EntityAction.SNEAK).priority(220).build("sneak_idle");
    public static final EntityActionTarget SNEAK_WALK = normal().action(EntityAction.SNEAK).action(EntityAction.WALK).priority(240).build("sneak_walk");

    public static final EntityActionTarget SWIM = normal().action(EntityAction.SWIMMING).priority(300).build("swim");
    public static final EntityActionTarget SWIM_IDLE = normal().action(EntityAction.SWIMMING).priority(320).build("swim_idle");
    public static final EntityActionTarget SWIM_WALK = normal().action(EntityAction.SWIMMING).action(EntityAction.SWIMMING_WALK).priority(340).build("swim_walk");
    public static final EntityActionTarget SWIM_SPRINT = normal().action(EntityAction.SWIMMING).action(EntityAction.SWIMMING_BOOST).priority(380).build("swim_sprint");
    // up/down

    public static final EntityActionTarget FLY = normal().action(EntityAction.FLYING).priority(400).build("fly");
    public static final EntityActionTarget FLY_IDLE = normal().action(EntityAction.FLYING).priority(420).build("fly_idle");
    public static final EntityActionTarget FLY_WALK = normal().action(EntityAction.FLYING).action(EntityAction.FLYING_WALK).priority(440).build("fly_walk");
    public static final EntityActionTarget FLY_SPRINT = normal().action(EntityAction.FLYING).action(EntityAction.FLYING_BOOST).priority(480).build("fly_sprint");
    // up/down

    public static final EntityActionTarget FALL_FLY = normal().action(EntityAction.FLYING).action(EntityAction.FLYING_FALL).priority(401).build("fall_fly");
    public static final EntityActionTarget FALL_FLY_IDLE = normal().action(EntityAction.FLYING).action(EntityAction.FLYING_FALL).priority(421).build("fall_fly_idle");
    public static final EntityActionTarget FALL_FLY_WALK = normal().action(EntityAction.FLYING).action(EntityAction.FLYING_FALL).action(EntityAction.FLYING_WALK).priority(441).build("fall_fly_walk");
    public static final EntityActionTarget FALL_FLY_SPRINT = normal().action(EntityAction.FLYING).action(EntityAction.FLYING_FALL).action(EntityAction.FLYING_BOOST).priority(481).build("fall_fly_sprint");
    // up/down

    public static final EntityActionTarget RIDE = normal().action(EntityAction.RIDING).priority(500).build("ride");
    public static final EntityActionTarget RIDE_IDLE = normal().action(EntityAction.RIDING).priority(520).build("ride_idle");
    public static final EntityActionTarget RIDE_WALK = normal().action(EntityAction.RIDING).action(EntityAction.RIDING_WALK).priority(540).build("ride_walk");
    public static final EntityActionTarget RIDE_SPRINT = normal().action(EntityAction.RIDING).action(EntityAction.FLYING_BOOST).priority(580).build("ride_sprint");
    // up/down boat/pig/horse

    public static final EntityActionTarget BOAT = normal().action(EntityAction.RIDING).action(EntityAction.RIDING_BOAT).priority(501).build("boat");
    public static final EntityActionTarget BOAT_IDLE = normal().action(EntityAction.RIDING).action(EntityAction.RIDING_BOAT).priority(521).build("boat_idle");
    public static final EntityActionTarget BOAT_WALK = normal().action(EntityAction.RIDING).action(EntityAction.RIDING_BOAT).action(EntityAction.RIDING_WALK).priority(541).build("boat_walk");
    public static final EntityActionTarget BOAT_SPRINT = normal().action(EntityAction.RIDING).action(EntityAction.RIDING_BOAT).action(EntityAction.FLYING_BOOST).priority(581).build("boat_sprint");
    // up/down

    public static final EntityActionTarget CRAWL = normal().action(EntityAction.CRAWLING).priority(800).build("crawl");
    public static final EntityActionTarget CRAWL_IDLE = normal().action(EntityAction.CRAWLING).priority(820).build("crawl_idle");
    public static final EntityActionTarget CRAWL_WALK = normal().action(EntityAction.CRAWLING).action(EntityAction.CRAWLING_WALK).priority(840).build("crawl_walk");

    public static final EntityActionTarget CLIMB = normal().action(EntityAction.CLIMBING).priority(800).build("climb");
    public static final EntityActionTarget CLIMB_IDLE = normal().action(EntityAction.CLIMBING).priority(820).build("climb_idle");
    public static final EntityActionTarget CLIMB_WALK = normal().action(EntityAction.CLIMBING).action(EntityAction.CLIMBING_WALK).priority(847).build("climb_walk");
    public static final EntityActionTarget CLIMB_WALK_UP = normal().action(EntityAction.CLIMBING).action(EntityAction.CLIMBING_UP).priority(848).build("climb_walk_up");
    public static final EntityActionTarget CLIMB_WALK_DOWN = normal().action(EntityAction.CLIMBING).action(EntityAction.CLIMBING_DOWN).priority(849).build("climb_walk_down");
    public static final EntityActionTarget CLIMB_HOLD = normal().action(EntityAction.CLIMBING).action(EntityAction.CLIMBING_HOLD).priority(880).build("climb_hold");

    //register("spawn", EntityAction.SPAWN);
    //register("death", EntityAction.DEATH);


    public static void init() {
    }

    public static EntityActionTarget by(String name) {
        var target = NAMED_ACTIONS.get(name);
        if (target != null) {
            return target;
        }
        return new EntityActionTarget.Builder().build(name);
    }

    private static Builder normal() {
        return new Builder();
    }

    protected static class Builder extends EntityActionTarget.Builder {

        @Override
        public EntityActionTarget build(String name) {
            var target = super.build(name);
            NAMED_ACTIONS.put(name, target);
            return target;
        }
    }
}
