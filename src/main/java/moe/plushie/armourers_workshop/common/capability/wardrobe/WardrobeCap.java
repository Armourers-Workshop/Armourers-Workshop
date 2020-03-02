package moe.plushie.armourers_workshop.common.capability.wardrobe;

import moe.plushie.armourers_workshop.api.common.capability.IWardrobeCap;
import moe.plushie.armourers_workshop.api.common.skin.data.ISkinDye;
import moe.plushie.armourers_workshop.api.common.skin.entity.ISkinnableEntity;
import moe.plushie.armourers_workshop.common.network.PacketHandler;
import moe.plushie.armourers_workshop.common.network.messages.client.MessageClientUpdateWardrobeCap;
import moe.plushie.armourers_workshop.common.network.messages.server.MessageServerSyncWardrobeCap;
import moe.plushie.armourers_workshop.common.skin.data.SkinDye;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class WardrobeCap implements IWardrobeCap {

    @CapabilityInject(IWardrobeCap.class)
    public static final Capability<IWardrobeCap> WARDROBE_CAP = null;

    protected final Entity entity;

    private final ISkinnableEntity skinnableEntity;

    private final ExtraColours extraColours;
    
    private final SkinDye dye;

    public WardrobeCap(Entity entity, ISkinnableEntity skinnableEntity) {
        this.entity = entity;
        this.skinnableEntity = skinnableEntity;
        extraColours = new ExtraColours();
        dye = new SkinDye();
    }

    @Override
    public ExtraColours getExtraColours() {
        return extraColours;
    }
    
    @Override
    public ISkinDye getDye() {
        return dye;
    }
    
    @Override
    public ISkinnableEntity getSkinnableEntity() {
        return skinnableEntity;
    }

    private byte[] intToByte(int value) {
        return new byte[] { (byte) (value >>> 16 & 0xFF), (byte) (value >>> 16 & 0xFF), (byte) (value & 0xFF) };
    }

    private int byteToInt(byte[] value) {
        return 0;
    }
    
    protected IMessage getUpdateMessage() {
        NBTTagCompound compound = (NBTTagCompound)WARDROBE_CAP.getStorage().writeNBT(WARDROBE_CAP, this, null);
        return new MessageServerSyncWardrobeCap(entity.getEntityId(), compound);
    }

    @Override
    public void syncToPlayer(EntityPlayerMP entityPlayer) {
        PacketHandler.networkWrapper.sendTo(getUpdateMessage(), entityPlayer);
    }

    @Override
    public void syncToAllTracking() {
        PacketHandler.networkWrapper.sendToAllTracking(getUpdateMessage(), entity);
    }

    @Override
    public void sendUpdateToServer() {
        NBTTagCompound compound = (NBTTagCompound)WARDROBE_CAP.getStorage().writeNBT(WARDROBE_CAP, this, null);
        PacketHandler.networkWrapper.sendToServer(new MessageClientUpdateWardrobeCap(entity.getEntityId(), compound));
    }

    public static IWardrobeCap get(Entity entity) {
        return entity.getCapability(WARDROBE_CAP, null);
    }
}
