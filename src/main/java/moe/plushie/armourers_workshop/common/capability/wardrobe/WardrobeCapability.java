package moe.plushie.armourers_workshop.common.capability.wardrobe;

import java.awt.Color;
import java.util.BitSet;
import java.util.HashMap;

import moe.plushie.armourers_workshop.api.common.skin.type.ISkinType;
import moe.plushie.armourers_workshop.common.config.ConfigHandler;
import moe.plushie.armourers_workshop.common.network.PacketHandler;
import moe.plushie.armourers_workshop.common.network.messages.server.MessageServerSkinCapabilitySync;
import moe.plushie.armourers_workshop.common.skin.ExPropsPlayerSkinData;
import moe.plushie.armourers_workshop.common.skin.type.SkinTypeRegistry;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;
import net.minecraftforge.common.capabilities.CapabilityInject;

public class WardrobeCapability implements IWardrobeCapability {
    
    @CapabilityInject(IWardrobeCapability.class)
    public static final Capability<IWardrobeCapability> WARDROBE_CAP = null;
    
    private static final Color COLOUR_SKIN_DEFAULT = Color.decode("#F9DFD2");
    
    private static final Color COLOUR_HAIR_DEFAULT = Color.decode("#804020");
    
    private final EntityPlayer entityPlayer;
    
    /** Colour of the players skin */
    public int skinColour;
    
    /** Colour of the players hair */
    public int hairColour;
    
    /** Bit set of what armour is hidden on the player. */
    public BitSet armourOverride;
    
    /** Is the hair/hat overlay hidden? */
    public boolean headOverlay;
    
    /** Should limb movement be limited when the player has a skin on? */
    public boolean limitLimbs;
    
    /** Number of slots the player has unlocked in the wardrobe */
    public HashMap<String, Integer> slotsUnlocked;
    
    public WardrobeCapability(EntityPlayer entityPlayer) {
        this.entityPlayer = entityPlayer;
        skinColour = COLOUR_SKIN_DEFAULT.getRGB();
        hairColour = COLOUR_HAIR_DEFAULT.getRGB();
        armourOverride = new BitSet(4);
        headOverlay = false;
        limitLimbs = true;
        slotsUnlocked = new HashMap<String, Integer>();
        ISkinType[] validSkins = ExPropsPlayerSkinData.validSkins;
        for (int i = 0; i < validSkins.length; i++) {
            ISkinType skinType = validSkins[i];
            slotsUnlocked.put(skinType.getRegistryName(), getUnlockedSlotsForSkinType(skinType));
        }
    }
    
    public int getUnlockedSlotsForSkinType(ISkinType skinType) {
        if (skinType == SkinTypeRegistry.skinBow) {
            return 1;
        }
        if (skinType == SkinTypeRegistry.skinSword) {
            return 5;
        }
        if (slotsUnlocked.containsKey(skinType.getRegistryName())) {
            return slotsUnlocked.get(skinType.getRegistryName());
        } else {
            return ConfigHandler.startingWardrobeSlots;
        }
    }
    
    public void setUnlockedSlotsForSkinType(ISkinType skinType, int value) {
        slotsUnlocked.put(skinType.getRegistryName(), value);
    }
    
    @Override
    public void syncToPlayerDelayed(EntityPlayerMP entityPlayer, int delay) {
        IStorage<IWardrobeCapability> storage = WARDROBE_CAP.getStorage();
        NBTTagCompound compound = (NBTTagCompound) storage.writeNBT(WARDROBE_CAP, this, null);
        MessageServerSkinCapabilitySync message = new MessageServerSkinCapabilitySync(entityPlayer.getEntityId(), compound);
        PacketHandler.sendToDelayed(message, entityPlayer, delay);
    }
    
    @Override
    public void syncToPlayer(EntityPlayerMP entityPlayer) {
        syncToPlayerDelayed(entityPlayer, 0);
    }

    @Override
    public void syncToAllAround() {
        NBTTagCompound compound = (NBTTagCompound)WARDROBE_CAP.getStorage().writeNBT(WARDROBE_CAP, this, null);
        MessageServerSkinCapabilitySync message = new MessageServerSkinCapabilitySync(entityPlayer.getEntityId(), compound);
        PacketHandler.networkWrapper.sendToAllTracking(message, entityPlayer);
    }
}
