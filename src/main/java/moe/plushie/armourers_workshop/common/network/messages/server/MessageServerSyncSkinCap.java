package moe.plushie.armourers_workshop.common.network.messages.server;

import org.apache.logging.log4j.Level;

import io.netty.buffer.ByteBuf;
import moe.plushie.armourers_workshop.api.common.capability.IEntitySkinCapability;
import moe.plushie.armourers_workshop.common.capability.entityskin.EntitySkinCapability;
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
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Send from the server to a client when a player walks into range or they edit
 * their skins.
 * 
 * @author RiskyKen
 *
 */
public class MessageServerSyncSkinCap implements IMessage, IMessageHandler<MessageServerSyncSkinCap, IMessage>, IDelayedMessage {

    private int entityId;
    private NBTTagCompound compound;

    public MessageServerSyncSkinCap(int entityId, NBTTagCompound compound) {
        this.entityId = entityId;
        this.compound = compound;
    }

    public MessageServerSyncSkinCap() {
    }

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
    public IMessage onMessage(MessageServerSyncSkinCap message, MessageContext ctx) {
        DelayedMessageHandler.addDelayedMessage(message);
        return null;
    }

    @Override
    public boolean isReady() {
        return clientReady();
    }

    @SideOnly(Side.CLIENT)
    private boolean clientReady() {
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
                IEntitySkinCapability skinCapability = EntitySkinCapability.get(entity);
                if (skinCapability != null) {
                    EntitySkinCapability.ENTITY_SKIN_CAP.getStorage().readNBT(EntitySkinCapability.ENTITY_SKIN_CAP, skinCapability, null, compound);
                }
            } else {
                ModLogger.log(Level.WARN, String.format("Failed to get entity with %d when updating IEntitySkinCapability.", entityId));
            }
        }
    }
}
