package riskyken.armourersWorkshop.common.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import riskyken.armourersWorkshop.common.lib.LibBlockNames;
import riskyken.armourersWorkshop.common.lib.LibModInfo;

public class BlockBoundingBox extends Block {

    protected BlockBoundingBox() {
        super(Material.cloth);
        setBlockUnbreakable();
        setStepSound(soundTypeCloth);
        setBlockName(LibBlockNames.BOUNDING_BOX);
    }

    @Override
    public String getUnlocalizedName() {
        return getModdedUnlocalizedName(super.getUnlocalizedName());
    }

    protected String getModdedUnlocalizedName(String unlocalizedName) {
        String name = unlocalizedName
                .substring(unlocalizedName.indexOf(".") + 1);
        return "tile." + LibModInfo.ID.toLowerCase() + ":" + name;
    }
}
