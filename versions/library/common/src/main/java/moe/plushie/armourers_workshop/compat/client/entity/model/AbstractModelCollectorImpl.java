package moe.plushie.armourers_workshop.compat.client.entity.model;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.core.utils.Collections;

import java.util.Map;

@Available("[18, 26)")
public class AbstractModelCollectorImpl {

    public static void apply(Map<String, Map<String, String>> builder) {
        builder.put("minecraft:model/humanoid", Collections.immutableMap(it -> {
            it.put("root.head", "headParts[0]");
            it.put("root.head.hat", "bodyParts[5]");
            it.put("root.right_arm", "bodyParts[1]");
            it.put("root.left_leg", "bodyParts[4]");
            it.put("root.left_arm", "bodyParts[2]");
            it.put("root.right_leg", "bodyParts[3]");
            it.put("root.body", "bodyParts[0]");
        }));
        builder.put("minecraft:model/player", Collections.immutableMap(it -> {
            it.put("root.right_arm.right_sleeve", "bodyParts[9]");
            it.put("root.left_leg.left_pants", "bodyParts[6]");
            it.put("root.left_arm.left_sleeve", "bodyParts[8]");
            it.put("root.right_leg.right_pants", "bodyParts[7]");
            it.put("root.body.jacket", "bodyParts[10]");
        }));

        builder.put("minecraft:model/chicken", Collections.immutableMap(it -> {
            it.put("root.head", "headParts[0]");
            it.put("root.head.beak", "headParts[1]");
            it.put("root.head.red_thing", "headParts[2]");
            it.put("root.left_leg", "bodyParts[2]");
            it.put("root.right_leg", "bodyParts[1]");
            it.put("root.right_wing", "bodyParts[3]");
            it.put("root.left_wing", "bodyParts[4]");
            it.put("root.body", "bodyParts[0]");
        }));
        builder.put("minecraft:model/horse", Collections.immutableMap(it -> {
            it.put("root.right_front_leg", "bodyParts[3]");
            it.put("root.right_hind_leg", "bodyParts[1]");
            it.put("root.head_parts", "headParts[0]");
            it.put("root.left_hind_leg", "bodyParts[2]");
            it.put("root.body", "bodyParts[0]");
            it.put("root.body.tail", "bodyParts[0].tail");
            it.put("root.left_front_leg", "bodyParts[4]");
        }));

        builder.put("minecraft:model/boat", Collections.immutableMap(it -> {
            it.put("root.bottom", "parts[0]");
            it.put("root.back", "parts[1]");
            it.put("root.front", "parts[2]");
            it.put("root.right", "parts[3]");
            it.put("root.left", "parts[4]");
            it.put("root.left_paddle", "parts[5]");
            it.put("root.right_paddle", "parts[6]");
        }));
        builder.put("minecraft:model/raft", Collections.immutableMap(it -> {
            it.put("root.bottom", "parts[0]");
            it.put("root.left_paddle", "parts[1]");
            it.put("root.right_paddle", "parts[2]");
        }));
    }
}
