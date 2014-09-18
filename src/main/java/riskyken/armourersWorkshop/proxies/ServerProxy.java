package riskyken.armourersWorkshop.proxies;

import java.util.BitSet;

import net.minecraft.block.Block;
import riskyken.armourersWorkshop.client.render.PlayerSkinInfo;
import riskyken.armourersWorkshop.common.custom.equipment.armour.ArmourType;
import riskyken.armourersWorkshop.common.custom.equipment.data.CustomArmourItemData;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.SERVER)
public class ServerProxy extends CommonProxy {

    @Override
    public void init() {
    }

    @Override
    public void initRenderers() {
    }

    @Override
    public void postInit() {
    }
    
    @Override
    public void registerKeyBindings() {   
    }
    
    @Override
    public void addCustomArmour(String playerName, CustomArmourItemData armourData) {
    }

    @Override
    public void removeCustomArmour(String playerName, ArmourType type) {
    }

    @Override
    public void removeAllCustomArmourData(String playerName) {
    }

    @Override
    public int getPlayerModelCacheSize() {
        return 0;
    }

    @Override
    public void setPlayersNakedData(String playerName, boolean isNaked, int skinColour, int pantsColour, BitSet armourOverride, boolean headOverlay) {
    }

    @Override
    public PlayerSkinInfo getPlayersNakedData(String playerName) {
        return null;
    }

    @Override
    public int getRenderType(Block block) {
        // TODO Auto-generated method stub
        return 0;
    }
}
