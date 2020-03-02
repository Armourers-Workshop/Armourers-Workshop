package moe.plushie.armourers_workshop.common.network.messages.server;

import org.apache.logging.log4j.Level;

import io.netty.buffer.ByteBuf;
import moe.plushie.armourers_workshop.api.common.capability.IWardrobeCap;
import moe.plushie.armourers_workshop.common.capability.wardrobe.WardrobeCap;
import moe.plushie.armourers_workshop.common.network.messages.client.DelayedMessageHandler;
import moe.plushie.armourers_workshop.common.network.messages.client.DelayedMessageHandler.IDelayedMessage;
import moe.plushie.armourers_workshop.utils.ModLogger;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
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
public class MessageServerSyncWardrobeCap implements IMessage, IMessageHandler<MessageServerSyncWardrobeCap, IMessage>, IDelayedMessage {

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
        DelayedMessageHandler.addDelayedMessage(message);
        return null;
    }

    @Override
    public boolean isReady() {
        if (Minecraft.getMinecraft().world != null) {
            return Minecraft.getMinecraft().world.getEntityByID(entityId) != null;
        }
        return false;
    }

    @Override
    public void onDelayedMessage() {
        if (Minecraft.getMinecraft().world != null) {
            Entity entity = Minecraft.getMinecraft().world.getEntityByID(entityId);
            if (entity != null) {
                IWardrobeCap wardrobeCapability = WardrobeCap.get(entity);
                if (wardrobeCapability != null) {
                    WardrobeCap.WARDROBE_CAP.getStorage().readNBT(WardrobeCap.WARDROBE_CAP, wardrobeCapability, null, compound);
                }
            } else {
                ModLogger.log(Level.WARN, String.format("Failed to get entity with %d when updating IWardrobeCapability.", entityId));
            }

        }
    }
}
