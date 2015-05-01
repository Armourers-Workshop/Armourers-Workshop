package riskyken.armourersWorkshop.common.network;

import riskyken.armourersWorkshop.common.lib.LibModInfo;
import riskyken.armourersWorkshop.common.network.messages.client.MessageClientGuiBipedRotations;
import riskyken.armourersWorkshop.common.network.messages.client.MessageClientGuiButton;
import riskyken.armourersWorkshop.common.network.messages.client.MessageClientGuiColourUpdate;
import riskyken.armourersWorkshop.common.network.messages.client.MessageClientGuiLoadSaveArmour;
import riskyken.armourersWorkshop.common.network.messages.client.MessageClientGuiSetArmourerCustomName;
import riskyken.armourersWorkshop.common.network.messages.client.MessageClientGuiSetArmourerSkinType;
import riskyken.armourersWorkshop.common.network.messages.client.MessageClientGuiSetSkin;
import riskyken.armourersWorkshop.common.network.messages.client.MessageClientGuiToolOptionUpdate;
import riskyken.armourersWorkshop.common.network.messages.client.MessageClientGuiUpdateNakedInfo;
import riskyken.armourersWorkshop.common.network.messages.client.MessageClientKeyPress;
import riskyken.armourersWorkshop.common.network.messages.client.MessageClientLoadArmour;
import riskyken.armourersWorkshop.common.network.messages.client.MessageClientRequestEquipmentDataData;
import riskyken.armourersWorkshop.common.network.messages.server.MessageServerAddEquipmentInfo;
import riskyken.armourersWorkshop.common.network.messages.server.MessageServerClientCommand;
import riskyken.armourersWorkshop.common.network.messages.server.MessageServerEntityEquipmentData;
import riskyken.armourersWorkshop.common.network.messages.server.MessageServerLibraryFileList;
import riskyken.armourersWorkshop.common.network.messages.server.MessageServerLibrarySendSkin;
import riskyken.armourersWorkshop.common.network.messages.server.MessageServerRemoveEquipmentInfo;
import riskyken.armourersWorkshop.common.network.messages.server.MessageServerSendEquipmentData;
import riskyken.armourersWorkshop.common.network.messages.server.MessageServerUpdateSkinInfo;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;

public class PacketHandler {

    public static final SimpleNetworkWrapper networkWrapper = NetworkRegistry.INSTANCE.newSimpleChannel(LibModInfo.CHANNEL);
    private static int packetId = 0;
    
    public static void init() {
        registerMessage(MessageClientGuiColourUpdate.class, MessageClientGuiColourUpdate.class, Side.SERVER);
        registerMessage(MessageClientGuiButton.class, MessageClientGuiButton.class, Side.SERVER);
        registerMessage(MessageServerAddEquipmentInfo.class, MessageServerAddEquipmentInfo.class, Side.CLIENT);
        registerMessage(MessageServerRemoveEquipmentInfo.class, MessageServerRemoveEquipmentInfo.class, Side.CLIENT);
        registerMessage(MessageClientLoadArmour.class, MessageClientLoadArmour.class, Side.SERVER);
        registerMessage(MessageClientGuiSetSkin.class, MessageClientGuiSetSkin.class, Side.SERVER);
        registerMessage(MessageServerLibraryFileList.class, MessageServerLibraryFileList.class, Side.CLIENT);
        registerMessage(MessageClientGuiLoadSaveArmour.class, MessageClientGuiLoadSaveArmour.class, Side.SERVER);
        registerMessage(MessageClientKeyPress.class, MessageClientKeyPress.class, Side.SERVER);
        registerMessage(MessageClientGuiToolOptionUpdate.class, MessageClientGuiToolOptionUpdate.class, Side.SERVER);
        registerMessage(MessageClientGuiUpdateNakedInfo.class, MessageClientGuiUpdateNakedInfo.class, Side.SERVER);
        registerMessage(MessageServerUpdateSkinInfo.class, MessageServerUpdateSkinInfo.class, Side.CLIENT);
        registerMessage(MessageClientRequestEquipmentDataData.class, MessageClientRequestEquipmentDataData.class, Side.SERVER);
        registerMessage(MessageServerSendEquipmentData.class, MessageServerSendEquipmentData.class, Side.CLIENT);
        registerMessage(MessageClientGuiSetArmourerCustomName.class, MessageClientGuiSetArmourerCustomName.class, Side.SERVER);
        registerMessage(MessageClientGuiBipedRotations.class, MessageClientGuiBipedRotations.class, Side.SERVER);
        registerMessage(MessageServerClientCommand.class, MessageServerClientCommand.class, Side.CLIENT);
        registerMessage(MessageClientGuiSetArmourerSkinType.class, MessageClientGuiSetArmourerSkinType.class, Side.SERVER);
        registerMessage(MessageServerEntityEquipmentData.class, MessageServerEntityEquipmentData.class, Side.CLIENT);
        registerMessage(MessageServerLibrarySendSkin.class, MessageServerLibrarySendSkin.class, Side.CLIENT);
    }
    
    private static <REQ extends IMessage, REPLY extends IMessage> void registerMessage(Class<? extends IMessageHandler<REQ, REPLY>> messageHandler, Class<REQ> requestMessageType, Side side) {
        networkWrapper.registerMessage(messageHandler, requestMessageType, packetId, side);
        packetId++;
    }
}
