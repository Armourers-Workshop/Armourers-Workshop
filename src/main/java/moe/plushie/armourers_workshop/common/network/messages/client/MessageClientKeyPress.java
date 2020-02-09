package moe.plushie.armourers_workshop.common.network.messages.client;

import io.netty.buffer.ByteBuf;
import moe.plushie.armourers_workshop.ArmourersWorkshop;
import moe.plushie.armourers_workshop.common.config.ConfigHandler;
import moe.plushie.armourers_workshop.common.lib.EnumGuiId;
import moe.plushie.armourers_workshop.common.world.undo.UndoManager;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.internal.FMLNetworkHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageClientKeyPress implements IMessage, IMessageHandler<MessageClientKeyPress, IMessage> {

    Button button;
    
    public MessageClientKeyPress() {
    }
    
    public MessageClientKeyPress(Button button) {
        this.button = button;
    }
    
    @Override
    public void fromBytes(ByteBuf buf) {
        button = Button.values()[buf.readByte()];
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeByte(button.ordinal());
    }
    
    @Override
    public IMessage onMessage(MessageClientKeyPress message, MessageContext ctx) {
        MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
        EntityPlayerMP player = ctx.getServerHandler().player;
        server.addScheduledTask(new Runnable() {
            @Override
            public void run() {
                switch (message.button) {
                case UNDO:
                    UndoManager.undoPressed(player);
                    break;
                case OPEN_WARDROBE:
                    if (ConfigHandler.canOpenWardrobe(player)) {
                        FMLNetworkHandler.openGui(player, ArmourersWorkshop.getInstance(), EnumGuiId.WARDROBE_PLAYER.ordinal(), player.getEntityWorld(), 0, 0, 0);
                    }
                    break;
                }
            }
        });
        return null;
    }
    
    public enum Button {
        UNDO,
        OPEN_WARDROBE;
    }
}
