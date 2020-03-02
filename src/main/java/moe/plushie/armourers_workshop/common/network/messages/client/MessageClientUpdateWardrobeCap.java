package moe.plushie.armourers_workshop.common.network.messages.client;

import org.apache.logging.log4j.Level;

import io.netty.buffer.ByteBuf;
import moe.plushie.armourers_workshop.api.common.capability.IWardrobeCap;
import moe.plushie.armourers_workshop.common.capability.wardrobe.WardrobeCap;
import moe.plushie.armourers_workshop.utils.ModLogger;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * Sent from the client to the server when a player
 * changes a value in their wardrobe.
 * @author RiskyKen
 *
 */
public class MessageClientUpdateWardrobeCap implements IMessage, IMessageHandler<MessageClientUpdateWardrobeCap, IMessage> {

    private int entityId;
    private NBTTagCompound compound;
    
    public MessageClientUpdateWardrobeCap() {}

    public MessageClientUpdateWardrobeCap(int entityId, NBTTagCompound compound) {
        this.entityId = entityId;
        this.compound = compound;
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
    public IMessage onMessage(MessageClientUpdateWardrobeCap message, MessageContext ctx) {
        Entity entity = ctx.getServerHandler().player.world.getEntityByID(message.entityId);
        if (entity != null) {
            IWardrobeCap wardrobeCapability = WardrobeCap.get(entity);
            if (wardrobeCapability != null) {
                WardrobeCap.WARDROBE_CAP.getStorage().readNBT(WardrobeCap.WARDROBE_CAP, wardrobeCapability, null, message.compound);
                wardrobeCapability.syncToAllTracking();
            } else {
                ModLogger.log(Level.WARN, "Failed to get wardrobe capability when updating players wardrobe.");
            }
        } else {
            ModLogger.log(Level.WARN, "Failed to get entity when updating wardrobe capability.");
        }

        return null;
    }
}
