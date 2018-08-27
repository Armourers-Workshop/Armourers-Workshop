package riskyken.armourersWorkshop.common.network.messages.client;

import com.mojang.authlib.GameProfile;

import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import riskyken.armourersWorkshop.common.GameProfileCache;

public class MessageClientRequestGameProfile implements IMessage, IMessageHandler<MessageClientRequestGameProfile, IMessage> {
    
    private GameProfile gameProfile;
    
    public MessageClientRequestGameProfile() {}
    
    public MessageClientRequestGameProfile(GameProfile gameProfile) {
        this.gameProfile = gameProfile;
    }
    
    @Override
    public void toBytes(ByteBuf buf) {
        NBTTagCompound profileTag = new NBTTagCompound();
        NBTUtil.func_152460_a(profileTag, this.gameProfile);
        ByteBufUtils.writeTag(buf, profileTag);
    }
    
    @Override
    public void fromBytes(ByteBuf buf) {
        NBTTagCompound profileTag = ByteBufUtils.readTag(buf);
        gameProfile = NBTUtil.func_152459_a(profileTag);
    }

    @Override
    public IMessage onMessage(MessageClientRequestGameProfile message, MessageContext ctx) {
        GameProfileCache.onClientRequstProfile(ctx.getServerHandler().playerEntity, message.gameProfile);
        return null;
    }
}
