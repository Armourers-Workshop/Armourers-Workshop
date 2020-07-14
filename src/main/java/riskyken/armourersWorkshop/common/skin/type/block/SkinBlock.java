package riskyken.armourersWorkshop.common.skin.type.block;

import java.util.ArrayList;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import riskyken.armourersWorkshop.api.common.skin.data.ISkinProperty;
import riskyken.armourersWorkshop.api.common.skin.type.ISkinPartType;
import riskyken.armourersWorkshop.client.lib.LibItemResources;
import riskyken.armourersWorkshop.common.skin.data.SkinProperties;
import riskyken.armourersWorkshop.common.skin.type.AbstractSkinTypeBase;

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

    @SideOnly(Side.CLIENT)
    @Override
    public void registerIcon(IIconRegister register) {
        this.icon = register.registerIcon(LibItemResources.TEMPLATE_BLOCK);
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
