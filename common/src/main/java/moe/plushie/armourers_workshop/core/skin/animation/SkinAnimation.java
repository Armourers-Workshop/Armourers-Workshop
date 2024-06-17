package moe.plushie.armourers_workshop.core.skin.animation;

import moe.plushie.armourers_workshop.utils.Constants;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SkinAnimation {

    private final String name;

    private final SkinAnimationLoop loop;

    private final float duration;

    private final Map<String, List<SkinAnimationValue>> values;

    public SkinAnimation(CompoundTag tag) {
        this.name = tag.getOptionalString(Keys.NAME, null);
        this.duration = tag.getOptionalFloat(Keys.DURATION, 0f);
        this.loop = tag.getOptionalEnum(Keys.LOOP_MODE, SkinAnimationLoop.LOOP);
        this.values = new HashMap<>();
        var valueTags = tag.getCompound(Keys.VALUES);
        for (var key : valueTags.getAllKeys()) {
            var valueList = new ArrayList<SkinAnimationValue>();
            for (var valueTag : valueTags.getList(key, Constants.TagFlags.COMPOUND)) {
                valueList.add(new SkinAnimationValue((CompoundTag) valueTag));
            }
            values.put(key, valueList);
        }
    }

    public SkinAnimation(String name, float duration, SkinAnimationLoop loop, Map<String, List<SkinAnimationValue>> values) {
        this.name = name;
        this.duration = duration;
        this.loop = loop;
        this.values = values;
    }

    public CompoundTag serializeNBT() {
        var valueTags = new CompoundTag();
        values.forEach((key, values) -> {
            var listTag = new ListTag();
            values.forEach(it -> listTag.add(it.serializeNBT()));
            valueTags.put(key, listTag);
        });
        var tag = new CompoundTag();
        tag.putOptionalString(Keys.NAME, name, null);
        tag.putOptionalEnum(Keys.LOOP_MODE, loop, SkinAnimationLoop.LOOP);
        tag.putOptionalFloat(Keys.DURATION, duration, 0f);
        tag.put(Keys.VALUES, valueTags);
        return tag;
    }

    public String getName() {
        return name;
    }

    public SkinAnimationLoop getLoop() {
        return loop;
    }

    public float getDuration() {
        return duration;
    }

    public Map<String, List<SkinAnimationValue>> getValues() {
        return values;
    }

    public static class Keys {
        public static final String NAME = "Name";
        public static final String LOOP_MODE = "Loop";
        public static final String DURATION = "Duration";
        public static final String VALUES = "Values";
    }
}
