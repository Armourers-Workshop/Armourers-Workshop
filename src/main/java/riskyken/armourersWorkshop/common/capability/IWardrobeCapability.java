package riskyken.armourersWorkshop.common.capability;

import java.util.BitSet;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import riskyken.armourersWorkshop.api.common.skin.type.ISkinType;
import riskyken.armourersWorkshop.common.inventory.WardrobeInventoryContainer;
import riskyken.armourersWorkshop.common.skin.EquipmentWardrobeData;

public interface IWardrobeCapability {
    
    public EntityPlayer getPlayer();
    
    public WardrobeInventoryContainer getWardrobeInventoryContainer();
    
    public IInventory getColumnInventory(ISkinType skinType);
    
    public void setSkinStack(ItemStack stack, int columnIndex);
    
    public ItemStack getSkinStack(ISkinType skinType, int columnIndex);
    
    public void removeSkinStack(ISkinType skinType, int columnIndex);
    
    public void clearAllSkinStacks();
    
    public EquipmentWardrobeData getEquipmentWardrobeData();
    
    public BitSet getArmourOverride();
    
    public void sendWardrobeDataToAllAround();
    
    public void sendWardrobeDataToPlayer(EntityPlayerMP targetPlayer);
    
    public void setColumnCount(int count);
    
    public int getColumnCount();
    
    public void addColumn();
    
    public void removeColumn();
}
