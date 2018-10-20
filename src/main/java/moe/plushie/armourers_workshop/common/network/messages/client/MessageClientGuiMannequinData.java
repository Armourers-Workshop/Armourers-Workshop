package moe.plushie.armourers_workshop.common.network.messages.client;

import io.netty.buffer.ByteBuf;
import moe.plushie.armourers_workshop.common.capability.wardrobe.ExtraColours;
import moe.plushie.armourers_workshop.common.capability.wardrobe.ExtraColours.ExtraColourType;
import moe.plushie.armourers_workshop.common.data.TextureType;
import moe.plushie.armourers_workshop.common.inventory.ContainerMannequin;
import moe.plushie.armourers_workshop.common.tileentities.TileEntityMannequin;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageClientGuiMannequinData implements IMessage, IMessageHandler<MessageClientGuiMannequinData, IMessage> {

    private float xOffset;
    private float yOffset;
    private float zOffset;
    private final ExtraColours extraColours;
    private String username;
    private boolean renderExtras;
    private boolean flying;
    private boolean visible;
    private TextureType textureType;
    
    public MessageClientGuiMannequinData() {
        extraColours = new ExtraColours();
    }
    
    public MessageClientGuiMannequinData(float xOffset, float yOffset,
            float zOffset, ExtraColours extraColours, String username,
            boolean renderExtras, boolean flying, boolean visible, TextureType textureType) {
        this.xOffset = xOffset;
        this.yOffset = yOffset;
        this.zOffset = zOffset;
        this.extraColours = extraColours;
        this.username = username;
        this.renderExtras = renderExtras;
        this.flying = flying;
        this.visible = visible;
        this.textureType = textureType;
    }


    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeFloat(xOffset);
        buf.writeFloat(yOffset);
        buf.writeFloat(zOffset);
        for (ExtraColourType colourType : ExtraColourType.values()) {
            buf.writeInt(extraColours.getColour(colourType));
        }
        ByteBufUtils.writeUTF8String(buf, username);
        buf.writeBoolean(renderExtras);
        buf.writeBoolean(flying);
        buf.writeBoolean(visible);
        buf.writeByte(textureType.ordinal());
    }
    
    @Override
    public void fromBytes(ByteBuf buf) {
        xOffset = buf.readFloat();
        yOffset = buf.readFloat();
        zOffset = buf.readFloat();
        for (ExtraColourType colourType : ExtraColourType.values()) {
            extraColours.setColour(colourType, buf.readInt());
        }
        username = ByteBufUtils.readUTF8String(buf);
        renderExtras = buf.readBoolean();
        flying = buf.readBoolean();
        visible = buf.readBoolean();
        textureType = TextureType.values()[buf.readByte()];
    }

    @Override
    public IMessage onMessage(MessageClientGuiMannequinData message, MessageContext ctx) {
        EntityPlayerMP player = ctx.getServerHandler().player;
        if (player == null) {
            return null;
        }
        Container container = player.openContainer;
        if (container != null && container instanceof ContainerMannequin) {
            TileEntityMannequin tileEntity = ((ContainerMannequin)container).getTileEntity();
            
            /*tileEntity.disableSync();
            tileEntity.setOffset(message.xOffset, message.yOffset, message.zOffset);
            tileEntity.setExtraColours(extraColours);
            tileEntity.setImageUrl(username);
            tileEntity.setRenderExtras(message.renderExtras);
            tileEntity.setFlying(message.flying);
            tileEntity.setVisible(message.visible);
            tileEntity.setTextureType(message.textureType);
            tileEntity.enableSync();*/
            
        }
        return null;
    }
}
