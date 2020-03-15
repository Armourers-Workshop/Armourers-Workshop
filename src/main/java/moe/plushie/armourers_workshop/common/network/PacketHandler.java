package moe.plushie.armourers_workshop.common.network;

import moe.plushie.armourers_workshop.common.init.entities.EntityMannequin;
import moe.plushie.armourers_workshop.common.lib.LibModInfo;
import moe.plushie.armourers_workshop.common.network.messages.client.MessageClientGuiAdminPanel;
import moe.plushie.armourers_workshop.common.network.messages.client.MessageClientGuiArmourerBlockUtil;
import moe.plushie.armourers_workshop.common.network.messages.client.MessageClientGuiBipedRotations;
import moe.plushie.armourers_workshop.common.network.messages.client.MessageClientGuiButton;
import moe.plushie.armourers_workshop.common.network.messages.client.MessageClientGuiColourUpdate;
import moe.plushie.armourers_workshop.common.network.messages.client.MessageClientGuiLoadSaveArmour;
import moe.plushie.armourers_workshop.common.network.messages.client.MessageClientGuiSetArmourerSkinProps;
import moe.plushie.armourers_workshop.common.network.messages.client.MessageClientGuiSetArmourerSkinType;
import moe.plushie.armourers_workshop.common.network.messages.client.MessageClientGuiSetSkin;
import moe.plushie.armourers_workshop.common.network.messages.client.MessageClientGuiSkinLibraryCommand;
import moe.plushie.armourers_workshop.common.network.messages.client.MessageClientGuiToolOptionUpdate;
import moe.plushie.armourers_workshop.common.network.messages.client.MessageClientGuiUpdateMannequin;
import moe.plushie.armourers_workshop.common.network.messages.client.MessageClientGuiUpdateTileProperties;
import moe.plushie.armourers_workshop.common.network.messages.client.MessageClientKeyPress;
import moe.plushie.armourers_workshop.common.network.messages.client.MessageClientLoadArmour;
import moe.plushie.armourers_workshop.common.network.messages.client.MessageClientRequestGameProfile;
import moe.plushie.armourers_workshop.common.network.messages.client.MessageClientRequestSkinData;
import moe.plushie.armourers_workshop.common.network.messages.client.MessageClientSkinPart;
import moe.plushie.armourers_workshop.common.network.messages.client.MessageClientToolPaintBlock;
import moe.plushie.armourers_workshop.common.network.messages.client.MessageClientUpdatePlayerWardrobeCap;
import moe.plushie.armourers_workshop.common.network.messages.client.MessageClientUpdateWardrobeCap;
import moe.plushie.armourers_workshop.common.network.messages.server.MessageServerClientCommand;
import moe.plushie.armourers_workshop.common.network.messages.server.MessageServerGameProfile;
import moe.plushie.armourers_workshop.common.network.messages.server.MessageServerLibraryFileList;
import moe.plushie.armourers_workshop.common.network.messages.server.MessageServerLibrarySendSkin;
import moe.plushie.armourers_workshop.common.network.messages.server.MessageServerSendSkinData;
import moe.plushie.armourers_workshop.common.network.messages.server.MessageServerSyncConfig;
import moe.plushie.armourers_workshop.common.network.messages.server.MessageServerSyncPlayerWardrobeCap;
import moe.plushie.armourers_workshop.common.network.messages.server.MessageServerSyncSkinCap;
import moe.plushie.armourers_workshop.common.network.messages.server.MessageServerSyncWardrobeCap;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public final class PacketHandler {

    public static final SimpleNetworkWrapper networkWrapper = NetworkRegistry.INSTANCE.newSimpleChannel(LibModInfo.CHANNEL);
    private static int packetId = 0;

    public static void init() {
        // Client messages.
        registerMessage(MessageClientUpdateWardrobeCap.class, MessageClientUpdateWardrobeCap.class, Side.SERVER);
        registerMessage(MessageClientUpdatePlayerWardrobeCap.class, MessageClientUpdatePlayerWardrobeCap.class, Side.SERVER);

        registerMessage(MessageClientLoadArmour.class, MessageClientLoadArmour.class, Side.SERVER);
        registerMessage(MessageClientKeyPress.class, MessageClientKeyPress.class, Side.SERVER);
        registerMessage(MessageClientRequestSkinData.class, MessageClientRequestSkinData.class, Side.SERVER);
        registerMessage(MessageClientSkinPart.class, MessageClientSkinPart.class, Side.SERVER);
        registerMessage(MessageClientToolPaintBlock.class, MessageClientToolPaintBlock.class, Side.SERVER);
        registerMessage(MessageClientRequestGameProfile.class, MessageClientRequestGameProfile.class, Side.SERVER);

        // Client GUI messages.
        registerMessage(MessageClientGuiLoadSaveArmour.class, MessageClientGuiLoadSaveArmour.class, Side.SERVER);
        registerMessage(MessageClientGuiColourUpdate.class, MessageClientGuiColourUpdate.class, Side.SERVER);
        registerMessage(MessageClientGuiButton.class, MessageClientGuiButton.class, Side.SERVER);
        registerMessage(MessageClientGuiSetSkin.class, MessageClientGuiSetSkin.class, Side.SERVER);
        registerMessage(MessageClientGuiToolOptionUpdate.class, MessageClientGuiToolOptionUpdate.class, Side.SERVER);
        registerMessage(MessageClientGuiSetArmourerSkinProps.class, MessageClientGuiSetArmourerSkinProps.class, Side.SERVER);
        registerMessage(MessageClientGuiBipedRotations.class, MessageClientGuiBipedRotations.class, Side.SERVER);
        registerMessage(MessageClientGuiSetArmourerSkinType.class, MessageClientGuiSetArmourerSkinType.class, Side.SERVER);
        registerMessage(MessageClientGuiUpdateTileProperties.class, MessageClientGuiUpdateTileProperties.class, Side.SERVER);
        registerMessage(MessageClientGuiAdminPanel.class, MessageClientGuiAdminPanel.class, Side.SERVER);
        registerMessage(MessageClientGuiSkinLibraryCommand.class, MessageClientGuiSkinLibraryCommand.class, Side.SERVER);
        registerMessage(MessageClientGuiArmourerBlockUtil.class, MessageClientGuiArmourerBlockUtil.class, Side.SERVER);
        registerMessage(MessageClientGuiUpdateMannequin.Handler.class, MessageClientGuiUpdateMannequin.class, Side.SERVER);

        // Server messages.
        registerMessage(MessageServerSyncSkinCap.class, MessageServerSyncSkinCap.class, Side.CLIENT);
        registerMessage(MessageServerSyncWardrobeCap.class, MessageServerSyncWardrobeCap.class, Side.CLIENT);
        registerMessage(MessageServerSyncPlayerWardrobeCap.class, MessageServerSyncPlayerWardrobeCap.class, Side.CLIENT);
        registerMessage(MessageServerLibraryFileList.class, MessageServerLibraryFileList.class, Side.CLIENT);
        registerMessage(MessageServerSendSkinData.class, MessageServerSendSkinData.class, Side.CLIENT);
        registerMessage(MessageServerClientCommand.class, MessageServerClientCommand.class, Side.CLIENT);
        registerMessage(MessageServerLibrarySendSkin.class, MessageServerLibrarySendSkin.class, Side.CLIENT);
        registerMessage(MessageServerSyncConfig.class, MessageServerSyncConfig.class, Side.CLIENT);
        registerMessage(MessageServerGameProfile.class, MessageServerGameProfile.class, Side.CLIENT);
        
        DataSerializers.registerSerializer(EntityMannequin.BIPED_ROTATIONS_SERIALIZER);
        DataSerializers.registerSerializer(EntityMannequin.TEXTURE_DATA_SERIALIZER);
    }

    private static <REQ extends IMessage, REPLY extends IMessage> void registerMessage(Class<? extends IMessageHandler<REQ, REPLY>> messageHandler, Class<REQ> requestMessageType, Side side) {
        networkWrapper.registerMessage(messageHandler, requestMessageType, packetId, side);
        packetId++;
    }

    private static class DelayedPacket {
        IMessage message;
        EntityPlayerMP player;
        int delay;

        public DelayedPacket(IMessage message, EntityPlayerMP player, int delay) {
            this.message = message;
            this.player = player;
            this.delay = delay;
        }
    }
}
