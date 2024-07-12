package moe.plushie.armourers_workshop.core.data;

import moe.plushie.armourers_workshop.api.skin.ISkinType;
import moe.plushie.armourers_workshop.core.skin.Skin;
import moe.plushie.armourers_workshop.core.skin.SkinTypes;
import moe.plushie.armourers_workshop.core.skin.animation.SkinAnimation;
import moe.plushie.armourers_workshop.core.skin.property.SkinProperties;
import moe.plushie.armourers_workshop.utils.Constants;
import moe.plushie.armourers_workshop.utils.math.Rectangle3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntArrayTag;
import net.minecraft.nbt.ListTag;

import java.util.ArrayList;
import java.util.List;

public class LocalDataReference {

    protected final String id;
    protected final ISkinType type;

    protected final CollisionBoxInfo collision;
    protected final List<AnimationInfo> animations;

    protected final SkinProperties properties;
    protected final int propertiesHash;

    public LocalDataReference(String id, Skin skin) {
        this.id = id;
        this.type = skin.getType();
        // info
        this.collision = CollisionBoxInfo.from(skin);
        this.animations = AnimationInfo.from(skin);
        // properties
        this.properties = skin.getProperties();
        this.propertiesHash = properties.hashCode();
    }

    public LocalDataReference(CompoundTag tag) {
        this.id = tag.getString("UUID");
        this.type = SkinTypes.byName(tag.getString("Type"));
        // info
        this.collision = CollisionBoxInfo.from(tag);
        this.animations = AnimationInfo.from(tag);
        // properties
        this.properties = new SkinProperties();
        this.properties.readFromNBT(tag.getCompound("Properties"));
        this.propertiesHash = tag.getInt("PropertiesHash");
    }

    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putString("UUID", id);
        tag.putString("Type", type.getRegistryName().toString());
        // info
        CollisionBoxInfo.write(collision, tag);
        AnimationInfo.write(animations, tag);
        // properties
        CompoundTag props = new CompoundTag();
        properties.writeToNBT(props);
        tag.put("Properties", props);
        tag.putInt("PropertiesHash", propertiesHash);
        return tag;
    }


    public static class AnimationInfo {

        private final String name;
        private final float duration;
        private final int loop;

        public AnimationInfo(SkinAnimation animation) {
            this.name = animation.getName();
            this.duration = animation.getDuration();
            this.loop = switch (animation.getLoop()) {
                case LOOP -> -1;
                case LAST_FRAME -> 0;
                case NONE -> 1;
            };
        }

        public AnimationInfo(CompoundTag tag) {
            this.name = tag.getString("Name");
            this.duration = tag.getFloat("Duration");
            this.loop = tag.getInt("Loop");
        }

        public CompoundTag serializeNBT() {
            CompoundTag tag = new CompoundTag();
            tag.putString("Name", name);
            tag.putFloat("Duration", duration);
            tag.putInt("Loop", loop);
            return tag;
        }

        public static List<AnimationInfo> from(Skin skin) {
            var infos = new ArrayList<AnimationInfo>();
            for (var animation : skin.getAnimations()) {
                infos.add(new AnimationInfo(animation));
            }
            return infos;
        }

        public static List<AnimationInfo> from(CompoundTag tag) {
            if (tag.contains("Animations")) {
                var lists = tag.getList("Animations", Constants.TagFlags.COMPOUND);
                var animations = new ArrayList<AnimationInfo>();
                for (var animationTag : lists) {
                    if (animationTag instanceof CompoundTag tag1) {
                        animations.add(new AnimationInfo(tag1));
                    }
                }
                return animations;
            }
            return null;
        }

        public static void write(List<AnimationInfo> animations, CompoundTag tag) {
            if (animations != null && !animations.isEmpty()) {
                var listTag = new ListTag();
                for (var animation : animations) {
                    listTag.add(animation.serializeNBT());
                }
                tag.put("Animations", listTag);
            }
        }
    }

    public static class CollisionBoxInfo {

        private final List<Rectangle3i> boxes;

        public CollisionBoxInfo(List<Rectangle3i> boxes) {
            this.boxes = boxes;
        }

        public CollisionBoxInfo(CompoundTag tag) {
            this.boxes = new ArrayList<>();
            for (var boxTag : tag.getList("Rect", Constants.TagFlags.INT_ARRAY)) {
                if (boxTag instanceof IntArrayTag arrayTag && arrayTag.size() >= 6) {
                    var array = arrayTag.getAsIntArray();
                    boxes.add(new Rectangle3i(array[0], array[1], array[2], array[3], array[4], array[5]));
                }
            }
        }

        public CompoundTag serializeNBT() {
            var tag = new CompoundTag();
            var listTag = new ListTag();
            for (var box : boxes) {
                var array = new int[6];
                array[0] = box.getX();
                array[1] = box.getY();
                array[2] = box.getZ();
                array[3] = box.getWidth();
                array[4] = box.getHeight();
                array[5] = box.getDepth();
                listTag.add(new IntArrayTag(array));
            }
            tag.put("Rect", listTag);
            return tag;
        }

        public static CollisionBoxInfo from(Skin skin) {
            var settings = skin.getSettings();
            if (settings != null && settings.getCollisionBox() != null && !settings.getCollisionBox().isEmpty()) {
                return new CollisionBoxInfo(settings.getCollisionBox());
            }
            return null;
        }

        public static CollisionBoxInfo from(CompoundTag tag) {
            if (tag.contains("Collisions")) {
                return new CollisionBoxInfo(tag.getCompound("Collisions"));
            }
            return null;
        }

        public static void write(CollisionBoxInfo size, CompoundTag tag) {
            if (size != null) {
                tag.put("Collisions", size.serializeNBT());
            }
        }
    }
}
