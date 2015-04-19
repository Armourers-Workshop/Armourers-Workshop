package riskyken.armourersWorkshop.common.network;

import riskyken.armourersWorkshop.common.lib.LibModInfo;
import riskyken.armourersWorkshop.common.network.messages.MessageClientGuiBipedRotations;
import riskyken.armourersWorkshop.common.network.messages.MessageClientGuiButton;
import riskyken.armourersWorkshop.common.network.messages.MessageClientGuiColourUpdate;
import riskyken.armourersWorkshop.common.network.messages.MessageClientGuiLoadSaveArmour;
import riskyken.armourersWorkshop.common.network.messages.MessageClientGuiSetArmourerCustomName;
import riskyken.armourersWorkshop.common.network.messages.MessageClientGuiSetSkin;
import riskyken.armourersWorkshop.common.network.messages.MessageClientGuiToolOptionUpdate;
import riskyken.armourersWorkshop.common.network.messages.MessageClientGuiUpdateNakedInfo;
import riskyken.armourersWorkshop.common.network.messages.MessageClientKeyPress;
import riskyken.armourersWorkshop.common.network.messages.MessageClientLoadArmour;
import riskyken.armourersWorkshop.common.network.messages.MessageClientRequestEquipmentDataData;
import riskyken.armourersWorkshop.common.network.messages.MessageServerAddEquipmentInfo;
import riskyken.armourersWorkshop.common.network.messages.MessageServerClientCommand;
import riskyken.armourersWorkshop.common.network.messages.MessageServerLibraryFileList;
import riskyken.armourersWorkshop.common.network.messages.MessageServerRemoveEquipmentInfo;
import riskyken.armourersWorkshop.common.network.messages.MessageServerSendEquipmentData;
import riskyken.armourersWorkshop.common.network.messages.MessageServerUpdateSkinInfo;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;

public class PacketHandler {

    public static final SimpleNetworkWrapper networkWrapper = NetworkRegistry.INSTANCE.newSimpleChannel(LibModInfo.CHANNEL);

    public static void init() {
        networkWrapper.registerMessage(MessageClientGuiColourUpdate.class, MessageClientGuiColourUpdate.class, 0, Side.SERVER);
        networkWrapper.registerMessage(MessageClientGuiButton.class, MessageClientGuiButton.class, 1, Side.SERVER);
        networkWrapper.registerMessage(MessageServerAddEquipmentInfo.class, MessageServerAddEquipmentInfo.class, 2, Side.CLIENT);
        networkWrapper.registerMessage(MessageServerRemoveEquipmentInfo.class, MessageServerRemoveEquipmentInfo.class, 3, Side.CLIENT);
        networkWrapper.registerMessage(MessageClientLoadArmour.class, MessageClientLoadArmour.class, 4, Side.SERVER);
        networkWrapper.registerMessage(MessageClientGuiSetSkin.class, MessageClientGuiSetSkin.class, 5, Side.SERVER);
        networkWrapper.registerMessage(MessageServerLibraryFileList.class, MessageServerLibraryFileList.class, 6, Side.CLIENT);
        networkWrapper.registerMessage(MessageClientGuiLoadSaveArmour.class, MessageClientGuiLoadSaveArmour.class, 7, Side.SERVER);
        networkWrapper.registerMessage(MessageClientKeyPress.class, MessageClientKeyPress.class, 8, Side.SERVER);
        networkWrapper.registerMessage(MessageClientGuiToolOptionUpdate.class, MessageClientGuiToolOptionUpdate.class, 9, Side.SERVER);
        networkWrapper.registerMessage(MessageClientGuiUpdateNakedInfo.class, MessageClientGuiUpdateNakedInfo.class, 10, Side.SERVER);
        networkWrapper.registerMessage(MessageServerUpdateSkinInfo.class, MessageServerUpdateSkinInfo.class, 11, Side.CLIENT);
        networkWrapper.registerMessage(MessageClientRequestEquipmentDataData.class, MessageClientRequestEquipmentDataData.class, 12, Side.SERVER);
        networkWrapper.registerMessage(MessageServerSendEquipmentData.class, MessageServerSendEquipmentData.class, 13, Side.CLIENT);
        networkWrapper.registerMessage(MessageClientGuiSetArmourerCustomName.class, MessageClientGuiSetArmourerCustomName.class, 14, Side.SERVER);
        networkWrapper.registerMessage(MessageClientGuiBipedRotations.class, MessageClientGuiBipedRotations.class, 15, Side.SERVER);
        networkWrapper.registerMessage(MessageServerClientCommand.class, MessageServerClientCommand.class, 16, Side.CLIENT);
    }
}
