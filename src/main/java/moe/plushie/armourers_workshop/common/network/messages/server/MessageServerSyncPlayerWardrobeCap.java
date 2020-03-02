package moe.plushie.armourers_workshop.common.network.messages.server;

import org.apache.logging.log4j.Level;

import io.netty.buffer.ByteBuf;
import moe.plushie.armourers_workshop.api.common.capability.IPlayerWardrobeCap;
import moe.plushie.armourers_workshop.common.capability.wardrobe.player.PlayerWardrobeCap;
import moe.plushie.armourers_workshop.common.network.messages.client.DelayedMessageHandler;
import moe.plushie.armourers_workshop.common.network.messages.client.DelayedMessageHandler.IDelayedMessage;
import moe.plushie.armourers_workshop.utils.ModLogger;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageServerSyncPlayerWardrobeCap implements IMessage, IMessageHandler<MessageServerSyncPlayerWardrobeCap, IMessage>, IDelayedMessage {

    private int entityId;
    private NBTTagCompound compound;

    public MessageServerSyncPlayerWardrobeCap(int entityId, NBTTagCompound compound) {
        this.entityId = entityId;
        this.compound = compound;
    }

    public MessageServerSyncPlayerWardrobeCap() {
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
    public IMessage onMessage(MessageServerSyncPlayerWardrobeCap message, MessageContext ctx) {
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
            if (entity != null && entity instanceof EntityPlayer) {
                IPlayerWardrobeCap wardrobeCapability = PlayerWardrobeCap.get((EntityPlayer) entity);
                if (wardrobeCapability != null) {
                    PlayerWardrobeCap.PLAYER_WARDROBE_CAP.getStorage().readNBT(PlayerWardrobeCap.PLAYER_WARDROBE_CAP, wardrobeCapability, null, compound);
                }
            } else {
                ModLogger.log(Level.WARN, String.format("Failed to get entity with %d when updating IWardrobeCapability.", entityId));
            }
        }
    }
}
