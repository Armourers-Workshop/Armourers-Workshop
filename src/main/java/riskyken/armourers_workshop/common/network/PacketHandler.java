package riskyken.armourers_workshop.common.network;

import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import riskyken.armourers_workshop.common.lib.LibModInfo;
import riskyken.armourers_workshop.common.network.messages.client.MessageClientGuiAdminPanel;
import riskyken.armourers_workshop.common.network.messages.client.MessageClientGuiArmourerBlockUtil;
import riskyken.armourers_workshop.common.network.messages.client.MessageClientGuiBipedRotations;
import riskyken.armourers_workshop.common.network.messages.client.MessageClientGuiButton;
import riskyken.armourers_workshop.common.network.messages.client.MessageClientGuiColourUpdate;
import riskyken.armourers_workshop.common.network.messages.client.MessageClientGuiHologramProjector;
import riskyken.armourers_workshop.common.network.messages.client.MessageClientGuiLoadSaveArmour;
import riskyken.armourers_workshop.common.network.messages.client.MessageClientGuiMannequinData;
import riskyken.armourers_workshop.common.network.messages.client.MessageClientGuiMiniArmourerCubeEdit;
import riskyken.armourers_workshop.common.network.messages.client.MessageClientGuiSetArmourerSkinProps;
import riskyken.armourers_workshop.common.network.messages.client.MessageClientGuiSetArmourerSkinType;
import riskyken.armourers_workshop.common.network.messages.client.MessageClientGuiSetSkin;
import riskyken.armourers_workshop.common.network.messages.client.MessageClientGuiSkinLibraryCommand;
import riskyken.armourers_workshop.common.network.messages.client.MessageClientGuiToolOptionUpdate;
import riskyken.armourers_workshop.common.network.messages.client.MessageClientKeyPress;
import riskyken.armourers_workshop.common.network.messages.client.MessageClientLoadArmour;
import riskyken.armourers_workshop.common.network.messages.client.MessageClientRequestGameProfile;
import riskyken.armourers_workshop.common.network.messages.client.MessageClientRequestSkinData;
import riskyken.armourers_workshop.common.network.messages.client.MessageClientSkinPart;
import riskyken.armourers_workshop.common.network.messages.client.MessageClientSkinWardrobeUpdate;
import riskyken.armourers_workshop.common.network.messages.client.MessageClientToolPaintBlock;
import riskyken.armourers_workshop.common.network.messages.server.MessageServerClientCommand;
import riskyken.armourers_workshop.common.network.messages.server.MessageServerEntitySkinData;
import riskyken.armourers_workshop.common.network.messages.server.MessageServerGameProfile;
import riskyken.armourers_workshop.common.network.messages.server.MessageServerLibraryFileList;
import riskyken.armourers_workshop.common.network.messages.server.MessageServerLibrarySendSkin;
import riskyken.armourers_workshop.common.network.messages.server.MessageServerMiniArmourerCubeEdit;
import riskyken.armourers_workshop.common.network.messages.server.MessageServerMiniArmourerSkinData;
import riskyken.armourers_workshop.common.network.messages.server.MessageServerPlayerLeftTrackingRange;
import riskyken.armourers_workshop.common.network.messages.server.MessageServerSendSkinData;
import riskyken.armourers_workshop.common.network.messages.server.MessageServerSkinInfoUpdate;
import riskyken.armourers_workshop.common.network.messages.server.MessageServerSkinWardrobeUpdate;
import riskyken.armourers_workshop.common.network.messages.server.MessageServerSyncConfig;

public class PacketHandler {

    public static final SimpleNetworkWrapper networkWrapper = NetworkRegistry.INSTANCE.newSimpleChannel(LibModInfo.CHANNEL);
    private static int packetId = 0;
    
    public static void init() {
        // Client messages.
        registerMessage(MessageClientLoadArmour.class, MessageClientLoadArmour.class, Side.SERVER);
        registerMessage(MessageClientKeyPress.class, MessageClientKeyPress.class, Side.SERVER);
        registerMessage(MessageClientSkinWardrobeUpdate.class, MessageClientSkinWardrobeUpdate.class, Side.SERVER);
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
        registerMessage(MessageClientGuiMiniArmourerCubeEdit.class, MessageClientGuiMiniArmourerCubeEdit.class, Side.SERVER);
        registerMessage(MessageClientGuiMannequinData.class, MessageClientGuiMannequinData.class, Side.SERVER);
        registerMessage(MessageClientGuiAdminPanel.class, MessageClientGuiAdminPanel.class, Side.SERVER);
        registerMessage(MessageClientGuiSkinLibraryCommand.class, MessageClientGuiSkinLibraryCommand.class, Side.SERVER);
        registerMessage(MessageClientGuiArmourerBlockUtil.class, MessageClientGuiArmourerBlockUtil.class, Side.SERVER);
        registerMessage(MessageClientGuiHologramProjector.class, MessageClientGuiHologramProjector.class, Side.SERVER);
        
        //Server messages.
        registerMessage(MessageServerSkinInfoUpdate.class, MessageServerSkinInfoUpdate.class, Side.CLIENT);
        registerMessage(MessageServerPlayerLeftTrackingRange.class, MessageServerPlayerLeftTrackingRange.class, Side.CLIENT);
        registerMessage(MessageServerLibraryFileList.class, MessageServerLibraryFileList.class, Side.CLIENT);
        registerMessage(MessageServerSkinWardrobeUpdate.class, MessageServerSkinWardrobeUpdate.class, Side.CLIENT);
        registerMessage(MessageServerSendSkinData.class, MessageServerSendSkinData.class, Side.CLIENT);
        registerMessage(MessageServerClientCommand.class, MessageServerClientCommand.class, Side.CLIENT);
        registerMessage(MessageServerEntitySkinData.class, MessageServerEntitySkinData.class, Side.CLIENT);
        registerMessage(MessageServerLibrarySendSkin.class, MessageServerLibrarySendSkin.class, Side.CLIENT);
        registerMessage(MessageServerMiniArmourerSkinData.class, MessageServerMiniArmourerSkinData.class, Side.CLIENT);
        registerMessage(MessageServerMiniArmourerCubeEdit.class, MessageServerMiniArmourerCubeEdit.class, Side.CLIENT);
        registerMessage(MessageServerSyncConfig.class, MessageServerSyncConfig.class, Side.CLIENT);
        registerMessage(MessageServerGameProfile.class, MessageServerGameProfile.class, Side.CLIENT);
    }
    
    private static <REQ extends IMessage, REPLY extends IMessage> void registerMessage(Class<? extends IMessageHandler<REQ, REPLY>> messageHandler, Class<REQ> requestMessageType, Side side) {
        networkWrapper.registerMessage(messageHandler, requestMessageType, packetId, side);
        packetId++;
    }
}
