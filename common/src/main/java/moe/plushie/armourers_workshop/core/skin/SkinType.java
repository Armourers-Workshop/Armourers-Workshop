package moe.plushie.armourers_workshop.core.skin;

import moe.plushie.armourers_workshop.api.skin.ISkinType;
import moe.plushie.armourers_workshop.core.skin.part.SkinPartType;
import moe.plushie.armourers_workshop.core.utils.Objects;
import moe.plushie.armourers_workshop.core.utils.OpenEquipmentSlot;
import moe.plushie.armourers_workshop.core.utils.OpenResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.function.Predicate;

public class SkinType implements ISkinType {

    protected final String name;
    protected final int id;
    protected OpenResourceLocation registryName;
    protected List<? extends SkinPartType> parts;

    public SkinType(String name, int id, List<? extends SkinPartType> parts) {
        this.parts = parts;
        this.name = name;
        this.id = id;
    }

    @Override
    public int id() {
        return id;
    }

    @Override
    public String name() {
        return registryName.toString();
    }

    @Override
    public OpenResourceLocation registryName() {
        return registryName;
    }

    public void setRegistryName(OpenResourceLocation registryName) {
        this.registryName = registryName;
    }

    @Override
    public String toString() {
        return Objects.toString(this, "id", id, "name", registryName);
    }

    @Override
    public List<? extends SkinPartType> parts() {
        return parts;
    }

    @Override
    public boolean isArmour() {
        return this instanceof Armor;
    }

    @Override
    public boolean isTool() {
        return this instanceof Tool;
    }

    public static class Armor extends SkinType {
        protected OpenEquipmentSlot slotType;

        public Armor(String name, int id, OpenEquipmentSlot slotType, List<? extends SkinPartType> parts) {
            super(name, id, parts);
            this.slotType = slotType;
        }

        public OpenEquipmentSlot slotType() {
            return slotType;
        }
    }

    public static class Tool extends SkinType {

        protected Predicate<ItemStack> predicate;

        public Tool(String name, int id, List<? extends SkinPartType> parts, Predicate<ItemStack> predicate) {
            super(name, id, parts);
            this.predicate = predicate;
        }

        public boolean contains(ItemStack itemStack) {
            return predicate.test(itemStack);
        }
    }
}
