//package moe.plushie.armourers_workshop.core.skin.type.block;
//
//import moe.plushie.armourers_workshop.core.api.common.skin.ISkinPartType;
//import moe.plushie.armourers_workshop.core.api.common.skin.ISkinProperty;
//import moe.plushie.armourers_workshop.core.skin.data.property.SkinProperties;
//import moe.plushie.armourers_workshop.core.skin.type.AbstractSkinType;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class SkinBlock extends AbstractSkinType {
//
//    public final ISkinPartType partBase;
//    public final ISkinPartType partMultiblock;
//    private ArrayList<ISkinPartType> skinParts;
//
//    public SkinBlock() {
//        this.skinParts = new ArrayList<>();
//        this.partBase = new SkinBlockPartBase();
//        this.partMultiblock = new SkinBlockPartMultiBlock();
//        this.skinParts.add(this.partBase);
//        this.skinParts.add(this.partMultiblock);
//    }
//
//    @Override
//    public List<? extends ISkinPartType> getParts() {
//        return skinParts;
//    }
//
//
//    @Override
//    public ArrayList<ISkinProperty<?>> getProperties() {
//        ArrayList<ISkinProperty<?>> properties = super.getProperties();
//        properties.add(SkinProperty.BLOCK_BED);
//        properties.add(SkinProperty.BLOCK_GLOWING);
//        properties.add(SkinProperty.BLOCK_LADDER);
//        properties.add(SkinProperty.BLOCK_MULTIBLOCK);
//        properties.add(SkinProperty.BLOCK_NO_COLLISION);
//        properties.add(SkinProperty.BLOCK_SEAT);
//        properties.add(SkinProperty.BLOCK_INVENTORY);
//        properties.add(SkinProperty.BLOCK_INVENTORY_WIDTH);
//        properties.add(SkinProperty.BLOCK_INVENTORY_HEIGHT);
//        properties.add(SkinProperty.BLOCK_ENDER_INVENTORY);
//        return properties;
//    }
//}
