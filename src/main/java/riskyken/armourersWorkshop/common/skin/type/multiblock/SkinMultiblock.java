package riskyken.armourersWorkshop.common.skin.type.multiblock;

import java.util.ArrayList;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import riskyken.armourersWorkshop.api.common.skin.Point3D;
import riskyken.armourersWorkshop.api.common.skin.Rectangle3D;
import riskyken.armourersWorkshop.api.common.skin.type.ISkinPartType;
import riskyken.armourersWorkshop.common.skin.type.AbstractSkinTypeBase;

public class SkinMultiblock extends AbstractSkinTypeBase {

    private ArrayList<ISkinPartType> skinParts;
    
    public SkinMultiblock() {
        this.skinParts = new ArrayList<ISkinPartType>();
        Rectangle3D size = new Rectangle3D(-2, 0, -2, 5, 5, 5);
        
        for (int ix = 0; ix < size.getWidth(); ix++) {
            for (int iy = 0; iy < size.getHeight(); iy++) {
                for (int iz = 0; iz < size.getDepth(); iz++) {
                    int x = ix + size.getX();
                    int y = iy + size.getY();
                    int z = iz + size.getZ();
                    this.skinParts.add(new SkinMultiblockPartBase(this)
                            .setPartName(x + ":" + y + ":" + z)
                            .setOffset(new Point3D(16 * x, -1 + (16 * -y), 16 * z)));
                }
            }
        }
    }
    
    @Override
    public ArrayList<ISkinPartType> getSkinParts() {
        return skinParts;
    }

    @Override
    public String getRegistryName() {
        return "armourers:multiblock";
    }

    @Override
    public String getName() {
        return "multiblock";
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerIcon(IIconRegister register) {
    }
    
    @Override
    public boolean enabled() {
        return false;
    }
}
