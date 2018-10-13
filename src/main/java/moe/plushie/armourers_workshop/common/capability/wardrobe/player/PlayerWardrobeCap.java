package moe.plushie.armourers_workshop.common.capability.wardrobe.player;

import java.util.BitSet;

import moe.plushie.armourers_workshop.api.common.skin.entity.ISkinnableEntity;
import moe.plushie.armourers_workshop.common.capability.wardrobe.WardrobeCap;
import moe.plushie.armourers_workshop.common.network.PacketHandler;
import moe.plushie.armourers_workshop.common.network.messages.client.MessageClientUpdatePlayerWardrobeCap;
import moe.plushie.armourers_workshop.common.network.messages.server.MessageServerSyncPlayerWardrobeCap;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class PlayerWardrobeCap extends WardrobeCap implements IPlayerWardrobeCap {

    @CapabilityInject(IPlayerWardrobeCap.class)
    public static final Capability<IPlayerWardrobeCap> PLAYER_WARDROBE_CAP = null;
    
    /** Bit set of what armour is hidden on the player. */
    public BitSet armourOverride;
    
    public PlayerWardrobeCap(EntityPlayer entity, ISkinnableEntity skinnableEntity) {
        super(entity, skinnableEntity);
        armourOverride = new BitSet(4);
    }

    @Override
    public BitSet getArmourOverride() {
        return armourOverride;
    }

    @Override
    public void setArmourOverride(BitSet armourOverride) {
        this.armourOverride = armourOverride;
    }
    
    public static IPlayerWardrobeCap get(EntityPlayer entity) {
        return entity.getCapability(PLAYER_WARDROBE_CAP, null);
    }
    
    @Override
    protected IMessage getUpdateMessage() {
        NBTTagCompound compound = (NBTTagCompound)PLAYER_WARDROBE_CAP.getStorage().writeNBT(PLAYER_WARDROBE_CAP, this, null);
        return new MessageServerSyncPlayerWardrobeCap(entity.getEntityId(), compound);
    }
    
    @Override
    public void sendUpdateToServer() {
        NBTTagCompound compound = (NBTTagCompound)WARDROBE_CAP.getStorage().writeNBT(WARDROBE_CAP, this, null);
        MessageClientUpdatePlayerWardrobeCap message = new MessageClientUpdatePlayerWardrobeCap(compound);
        PacketHandler.networkWrapper.sendToServer(message);
    }
}
