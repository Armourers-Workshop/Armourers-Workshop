package moe.plushie.armourers_workshop.common.network.messages.client;

import com.mojang.authlib.GameProfile;

import io.netty.buffer.ByteBuf;
import moe.plushie.armourers_workshop.common.GameProfileCache;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageClientRequestGameProfile implements IMessage, IMessageHandler<MessageClientRequestGameProfile, IMessage> {
    
    private GameProfile gameProfile;
    
    public MessageClientRequestGameProfile() {}
    
    public MessageClientRequestGameProfile(GameProfile gameProfile) {
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
    public IMessage onMessage(MessageClientRequestGameProfile message, MessageContext ctx) {
        GameProfileCache.onClientRequstProfile(ctx.getServerHandler().player, message.gameProfile);
        return null;
    }
}
