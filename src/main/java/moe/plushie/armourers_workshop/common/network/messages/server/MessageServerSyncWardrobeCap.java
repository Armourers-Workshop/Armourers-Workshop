package moe.plushie.armourers_workshop.common.network.messages.server;

import org.apache.logging.log4j.Level;

import io.netty.buffer.ByteBuf;
import moe.plushie.armourers_workshop.common.capability.wardrobe.IWardrobeCapability;
import moe.plushie.armourers_workshop.common.capability.wardrobe.WardrobeCapability;
import moe.plushie.armourers_workshop.common.data.PlayerPointer;
import moe.plushie.armourers_workshop.common.skin.PlayerWardrobe;
import moe.plushie.armourers_workshop.proxies.ClientProxy;
import moe.plushie.armourers_workshop.utils.ModLogger;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * Sent from the server to a client when a player walks
 * into tracking range or updates their wardrobe.
 * @author RiskyKen
 *
 */
public class MessageServerSyncWardrobeCap implements IMessage, IMessageHandler<MessageServerSyncWardrobeCap, IMessage> {

    private int entityId;
    private NBTTagCompound compound;
    
    public MessageServerSyncWardrobeCap(int entityId, NBTTagCompound compound) {
        this.entityId = entityId;
        this.compound = compound;
    }
    
    public MessageServerSyncWardrobeCap() {}
    
    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(entityId);
        ByteBufUtils.writeTag(buf, compound);
    }
    
    @Override
    public void fromBytes(ByteBuf buf) {
        entityId = buf.readInt();
        compound = ByteBufUtils.readTag(buf);
    }

    @Override
    public IMessage onMessage(MessageServerSyncWardrobeCap message, MessageContext ctx) {
        if (Minecraft.getMinecraft().world != null) {
            Entity entity = Minecraft.getMinecraft().world.getEntityByID(message.entityId);
            if (entity != null) {
                IWardrobeCapability wardrobeCapability = WardrobeCapability.get((EntityPlayer) entity);
                if (wardrobeCapability != null) {
                    WardrobeCapability.WARDROBE_CAP.getStorage().readNBT(WardrobeCapability.WARDROBE_CAP, wardrobeCapability, null, message.compound);
                }
            } else {
                ModLogger.log(Level.WARN, String.format("Failed to get entity with %d when updating IWardrobeCapability.", message.entityId));
            }

        }
        return null;
    }
    
    private void setEquipmentWardrobeData(PlayerPointer playerPointer, PlayerWardrobe ewd) {
        ClientProxy.equipmentWardrobeHandler.setEquipmentWardrobeData(playerPointer, ewd);
    }
}
