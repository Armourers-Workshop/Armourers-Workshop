package moe.plushie.armourers_workshop.core.skin;

import moe.plushie.armourers_workshop.api.skin.ISkinArmorType;
import moe.plushie.armourers_workshop.api.skin.ISkinPartType;
import moe.plushie.armourers_workshop.api.skin.ISkinToolType;
import moe.plushie.armourers_workshop.api.skin.ISkinType;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.tags.ITag;
import net.minecraft.util.ResourceLocation;

import java.util.List;
import java.util.function.Predicate;

public class SkinType implements ISkinType {

    protected final String name;
    protected final int id;
    protected ResourceLocation registryName;
    protected List<? extends ISkinPartType> parts;

    public SkinType(String name, int id, List<? extends ISkinPartType> parts) {
        this.parts = parts;
        this.name = name;
        this.id = id;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public ResourceLocation getRegistryName() {
        return registryName;
    }

    public void setRegistryName(ResourceLocation registryName) {
        this.registryName = registryName;
    }

    @Override
    public String toString() {
        return registryName.toString();
    }

    @Override
    public List<? extends ISkinPartType> getParts() {
        return parts;
    }


    public static class Armor extends SkinType implements ISkinArmorType {
        protected EquipmentSlotType slotType;
        public Armor(String name, int id, EquipmentSlotType slotType, List<? extends ISkinPartType> parts) {
            super(name, id, parts);
            this.slotType = slotType;
        }

        @Override
        public EquipmentSlotType getSlotType() {
            return slotType;
        }
    }

    public static class Tool extends SkinType implements ISkinToolType {

        protected Predicate<Item> predicate;

        public Tool(String name, int id, List<? extends ISkinPartType> parts, Predicate<Item> predicate) {
            super(name, id, parts);
            this.predicate = predicate;
        }

        @Override
        public boolean contains(Item item) {
            return predicate.test(item);
        }

        //        @Override
//        public ITag<Item> getTag() {
//            return tag;
//        }
    }
}
