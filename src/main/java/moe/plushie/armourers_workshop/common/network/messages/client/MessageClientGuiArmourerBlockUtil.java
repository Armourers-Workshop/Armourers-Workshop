package moe.plushie.armourers_workshop.common.network.messages.client;

import io.netty.buffer.ByteBuf;
import moe.plushie.armourers_workshop.api.common.skin.type.ISkinPartType;
import moe.plushie.armourers_workshop.common.inventory.ContainerArmourer;
import moe.plushie.armourers_workshop.common.skin.type.SkinTypeRegistry;
import moe.plushie.armourers_workshop.common.tileentities.TileEntityArmourer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageClientGuiArmourerBlockUtil implements IMessage, IMessageHandler<MessageClientGuiArmourerBlockUtil, IMessage> {
    
    private String utilType;
    private ISkinPartType partType1;
    private ISkinPartType partType2;
    private boolean option1;
    private boolean option2;
    private boolean option3;
    
    public MessageClientGuiArmourerBlockUtil() {
    }
    
    public MessageClientGuiArmourerBlockUtil(String utilType, ISkinPartType part1, ISkinPartType part2, boolean option1, boolean option2, boolean option3) {
        this.utilType = utilType;
        this.partType1 = part1;
        this.partType2 = part2;
        this.option1 = option1;
        this.option2 = option2;
        this.option3 = option3;
    }


    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, utilType);
        buf.writeBoolean(partType1 != null);
        if (partType1 != null) {
            ByteBufUtils.writeUTF8String(buf, partType1.getRegistryName());
        }
        buf.writeBoolean(partType2 != null);
        if (partType2 != null) {
            ByteBufUtils.writeUTF8String(buf, partType2.getRegistryName());
        }
        buf.writeBoolean(option1);
        buf.writeBoolean(option2);
        buf.writeBoolean(option3);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        utilType = ByteBufUtils.readUTF8String(buf);
        if (buf.readBoolean()) {
            String registryName = ByteBufUtils.readUTF8String(buf);
            partType1 = SkinTypeRegistry.INSTANCE.getSkinPartFromRegistryName(registryName);
        }
        if (buf.readBoolean()) {
            String registryName = ByteBufUtils.readUTF8String(buf);
            partType2 = SkinTypeRegistry.INSTANCE.getSkinPartFromRegistryName(registryName);
        }
        this.option1 = buf.readBoolean();
        this.option2 = buf.readBoolean();
        this.option3 = buf.readBoolean();
    }


    @Override
    public IMessage onMessage(MessageClientGuiArmourerBlockUtil message, MessageContext ctx) {
        EntityPlayerMP player = ctx.getServerHandler().player;
        if (player == null){
            return null;
        }
        Container container = player.openContainer;
        if (container != null && container instanceof ContainerArmourer) {
            TileEntityArmourer armourerBrain = ((ContainerArmourer) container).getTileEntity();
            boolean clearBlocks = message.option1;
            boolean clearPaint = message.option2;
            boolean clearMarkers = message.option3;
            if (message.utilType.equals("clear")) {
                if (clearBlocks) {
                    armourerBrain.clearArmourCubes(message.partType1);
                }
                if (clearPaint) {
                    armourerBrain.clearPaintData(true);
                }
                if (clearMarkers) {
                    if (!clearBlocks) {
                        armourerBrain.clearMarkers(message.partType1);
                    }
                }
            }
            
            if (message.utilType.equals("copy")) {
                armourerBrain.copySkinCubes(player, message.partType1, message.partType2, message.option1);
            }
        }
        return null;
    }
}
