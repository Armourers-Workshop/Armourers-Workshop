package moe.plushie.armourers_workshop.core.data;

import com.google.common.collect.Lists;

import java.util.HashMap;
import java.util.Map;

public class EntityActions {

    private static final Map<String, EntityActionTarget> NAMED_ACTIONS = new HashMap<>();

    public static void init() {

        register(100, "idle", EntityAction.IDLE);

        register(220, "walk", EntityAction.WALK);
        register(240, "run", EntityAction.WALK, EntityAction.RUNNING);
        register(280, "jump", EntityAction.JUMP);

        register(200, "sneak", EntityAction.SNEAK);
        register(220, "sneak_idle", EntityAction.SNEAK);
        register(240, "sneak_walk", EntityAction.SNEAK, EntityAction.WALK);

        register(300, "swim", EntityAction.SWIMMING);
        register(320, "swim_idle", EntityAction.SWIMMING);
        register(340, "swim_walk", EntityAction.SWIMMING, EntityAction.SWIMMING_WALK);
        register(380, "swim_sprint", EntityAction.SWIMMING, EntityAction.SWIMMING_BOOST);
        // up/down

        register(400, "fly", EntityAction.FLYING);
        register(420, "fly_idle", EntityAction.FLYING);
        register(440, "fly_walk", EntityAction.FLYING, EntityAction.FLYING_WALK);
        register(480, "fly_sprint", EntityAction.FLYING, EntityAction.FLYING_BOOST);
        // up/down

        register(401, "fall_fly", EntityAction.FLYING, EntityAction.FLYING_FALL);
        register(421, "fall_fly_idle", EntityAction.FLYING, EntityAction.FLYING_FALL);
        register(441, "fall_fly_walk", EntityAction.FLYING, EntityAction.FLYING_FALL, EntityAction.FLYING_WALK);
        register(481, "fall_fly_sprint", EntityAction.FLYING, EntityAction.FLYING_FALL, EntityAction.FLYING_BOOST);
        // up/down

        register(500, "ride", EntityAction.RIDING);
        register(520, "ride_idle", EntityAction.RIDING);
        register(540, "ride_walk", EntityAction.RIDING, EntityAction.RIDING_WALK);
        register(580, "ride_sprint", EntityAction.RIDING, EntityAction.FLYING_BOOST);
        // up/down boat/pig/horse

        register(501, "boat", EntityAction.RIDING, EntityAction.RIDING_BOAT);
        register(521, "boat_idle", EntityAction.RIDING, EntityAction.RIDING_BOAT);
        register(541, "boat_walk", EntityAction.RIDING, EntityAction.RIDING_BOAT, EntityAction.RIDING_WALK);
        register(581, "boat_sprint", EntityAction.RIDING, EntityAction.RIDING_BOAT, EntityAction.FLYING_BOOST);
        // up/down

        register(800, "crawl", EntityAction.CRAWLING);
        register(820, "crawl_idle", EntityAction.CRAWLING);
        register(840, "crawl_walk", EntityAction.CRAWLING, EntityAction.CRAWLING_WALK);

        register(800, "climb", EntityAction.CLIMBING);
        register(820, "climb_idle", EntityAction.CLIMBING);
        register(847, "climb_walk", EntityAction.CLIMBING, EntityAction.CLIMBING_WALK);
        register(848, "climb_walk_up", EntityAction.CLIMBING, EntityAction.CLIMBING_UP);
        register(849, "climb_walk_down", EntityAction.CLIMBING, EntityAction.CLIMBING_DOWN);
        register(880, "climb_hold", EntityAction.CLIMBING, EntityAction.CLIMBING_HOLD);

        //register("spawn", EntityAction.SPAWN);
        //register("death", EntityAction.DEATH);
    }


    public static EntityActionTarget by(String name) {
        var target = NAMED_ACTIONS.get(name);
        if (target != null) {
            return target;
        }
        return new EntityActionTarget(name, 0, Lists.newArrayList(), -1);
    }

    private static void register(float priority, String name, EntityAction... actions) {
        NAMED_ACTIONS.put(name, new EntityActionTarget(name, priority, Lists.newArrayList(actions), -1));
    }
}
