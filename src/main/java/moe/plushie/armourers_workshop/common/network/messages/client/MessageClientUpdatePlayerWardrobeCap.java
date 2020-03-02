package moe.plushie.armourers_workshop.common.network.messages.client;

import org.apache.logging.log4j.Level;

import io.netty.buffer.ByteBuf;
import moe.plushie.armourers_workshop.api.common.capability.IPlayerWardrobeCap;
import moe.plushie.armourers_workshop.common.capability.wardrobe.player.PlayerWardrobeCap;
import moe.plushie.armourers_workshop.utils.ModLogger;
import net.minecraft.entity.player.EntityPlayer;
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
public class MessageClientUpdatePlayerWardrobeCap implements IMessage, IMessageHandler<MessageClientUpdatePlayerWardrobeCap, IMessage> {
    
    private NBTTagCompound compound;
    
    public MessageClientUpdatePlayerWardrobeCap() {}

    public MessageClientUpdatePlayerWardrobeCap(NBTTagCompound compound) {
        this.compound = compound;
    }
    
    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeTag(buf, compound);
    }
    
    @Override
    public void fromBytes(ByteBuf buf) {
        compound = ByteBufUtils.readTag(buf);
    }

    @Override
    public IMessage onMessage(MessageClientUpdatePlayerWardrobeCap message, MessageContext ctx) {
        IPlayerWardrobeCap wardrobeCapability = PlayerWardrobeCap.get((EntityPlayer) ctx.getServerHandler().player);
        if (wardrobeCapability != null) {
            PlayerWardrobeCap.PLAYER_WARDROBE_CAP.getStorage().readNBT(PlayerWardrobeCap.PLAYER_WARDROBE_CAP, wardrobeCapability, null, message.compound);
            wardrobeCapability.syncToAllTracking();
        } else {
            ModLogger.log(Level.WARN, "Failed to get wardrobe capability when updating players wardrobe.");
        }
        return null;
    }
}
