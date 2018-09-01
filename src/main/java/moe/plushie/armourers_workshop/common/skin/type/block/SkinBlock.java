package moe.plushie.armourers_workshop.common.skin.type.block;

import java.util.ArrayList;

import moe.plushie.armourers_workshop.api.common.skin.data.ISkinProperty;
import moe.plushie.armourers_workshop.api.common.skin.type.ISkinPartType;
import moe.plushie.armourers_workshop.common.skin.data.SkinProperties;
import moe.plushie.armourers_workshop.common.skin.type.AbstractSkinTypeBase;

public class SkinBlock extends AbstractSkinTypeBase {

    public final ISkinPartType partBase;
    public final ISkinPartType partMultiblock;
    private ArrayList<ISkinPartType> skinParts;
    
    public SkinBlock() {
        this.skinParts = new ArrayList<ISkinPartType>();
        this.partBase = new SkinBlockPartBase(this);
        this.partMultiblock = new SkinBlockPartMultiBlock(this);
        this.skinParts.add(this.partBase);
        this.skinParts.add(this.partMultiblock);
    }
    
    @Override
    public ArrayList<ISkinPartType> getSkinParts() {
        return skinParts;
    }

    @Override
    public String getRegistryName() {
        return "armourers:block";
    }

    @Override
    public String getName() {
        return "block";
    }
    
    @Override
    public ArrayList<ISkinProperty<?>> getProperties() {
        ArrayList<ISkinProperty<?>> properties = super.getProperties();
        properties.add(SkinProperties.PROP_BLOCK_BED);
        properties.add(SkinProperties.PROP_BLOCK_GLOWING);
        properties.add(SkinProperties.PROP_BLOCK_LADDER);
        properties.add(SkinProperties.PROP_BLOCK_MULTIBLOCK);
        properties.add(SkinProperties.PROP_BLOCK_NO_COLLISION);
        properties.add(SkinProperties.PROP_BLOCK_SEAT);
        properties.add(SkinProperties.PROP_BLOCK_INVENTORY);
        properties.add(SkinProperties.PROP_BLOCK_INVENTORY_WIDTH);
        properties.add(SkinProperties.PROP_BLOCK_INVENTORY_HEIGHT);
        properties.add(SkinProperties.PROP_BLOCK_ENDER_INVENTORY);
        return properties;
    }
}
