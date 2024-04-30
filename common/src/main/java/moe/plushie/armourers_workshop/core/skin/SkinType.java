package moe.plushie.armourers_workshop.core.skin;

import moe.plushie.armourers_workshop.api.skin.ISkinArmorType;
import moe.plushie.armourers_workshop.api.skin.ISkinPartType;
import moe.plushie.armourers_workshop.api.skin.ISkinToolType;
import moe.plushie.armourers_workshop.api.skin.ISkinType;
import moe.plushie.armourers_workshop.utils.ObjectUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;

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
    public String getName() {
        return registryName.toString();
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
        return ObjectUtils.makeDescription(this, "id", id, "name", registryName);
    }

    @Override
    public List<? extends ISkinPartType> getParts() {
        return parts;
    }


    public static class Armor extends SkinType implements ISkinArmorType {
        protected EquipmentSlot slotType;

        public Armor(String name, int id, EquipmentSlot slotType, List<? extends ISkinPartType> parts) {
            super(name, id, parts);
            this.slotType = slotType;
        }

        @Override
        public EquipmentSlot getSlotType() {
            return slotType;
        }
    }

    public static class Tool extends SkinType implements ISkinToolType {

        protected Predicate<ItemStack> predicate;

        public Tool(String name, int id, List<? extends ISkinPartType> parts, Predicate<ItemStack> predicate) {
            super(name, id, parts);
            this.predicate = predicate;
        }

        @Override
        public boolean contains(ItemStack itemStack) {
            return predicate.test(itemStack);
        }
    }
}
