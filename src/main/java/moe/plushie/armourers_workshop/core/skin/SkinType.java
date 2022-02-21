package moe.plushie.armourers_workshop.core.skin;

import moe.plushie.armourers_workshop.core.api.ISkinArmorType;
import moe.plushie.armourers_workshop.core.api.ISkinPartType;
import moe.plushie.armourers_workshop.core.api.ISkinToolType;
import moe.plushie.armourers_workshop.core.api.ISkinType;
import net.minecraft.item.Item;
import net.minecraft.tags.ITag;
import net.minecraft.util.ResourceLocation;

import java.util.List;

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
        public Armor(String name, int id, List<? extends ISkinPartType> parts) {
            super(name, id, parts);
        }
    }

    public static class Tool extends SkinType implements ISkinToolType {

        protected ITag<Item> tag;

        public Tool(String name, int id, List<? extends ISkinPartType> parts, ITag<Item> tag) {
            super(name, id, parts);
            this.tag = tag;
        }

        @Override
        public ITag<Item> getTag() {
            return tag;
        }
    }
}
