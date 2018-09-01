package moe.plushie.armourers_workshop.common.network.messages.server;

import com.mojang.authlib.GameProfile;

import io.netty.buffer.ByteBuf;
import moe.plushie.armourers_workshop.common.GameProfileCache;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageServerGameProfile  implements IMessage, IMessageHandler<MessageServerGameProfile, IMessage> {

    private GameProfile gameProfile;
    
    public MessageServerGameProfile() {}
    
    public MessageServerGameProfile(GameProfile gameProfile) {
        this.gameProfile = gameProfile;
    }
    
    @Override
    public void toBytes(ByteBuf buf) {
        NBTTagCompound profileTag = new NBTTagCompound();
        NBTUtil.writeGameProfile(profileTag, this.gameProfile);
        ByteBufUtils.writeTag(buf, profileTag);
    }
    
    @Override
    public void fromBytes(ByteBuf buf) {
        NBTTagCompound profileTag = ByteBufUtils.readTag(buf);
        gameProfile = NBTUtil.readGameProfileFromNBT(profileTag);
    }

    @Override
    public IMessage onMessage(MessageServerGameProfile message, MessageContext ctx) {
        GameProfileCache.onServerSentProfile(message.gameProfile);
        return null;
    }
}
