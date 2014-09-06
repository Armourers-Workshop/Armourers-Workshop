package riskyken.armourersWorkshop.common.network;

import riskyken.armourersWorkshop.common.lib.LibModInfo;
import riskyken.armourersWorkshop.common.network.messages.MessageClientArmourUpdate;
import riskyken.armourersWorkshop.common.network.messages.MessageClientGuiButton;
import riskyken.armourersWorkshop.common.network.messages.MessageClientGuiColourUpdate;
import riskyken.armourersWorkshop.common.network.messages.MessageClientGuiLoadSaveArmour;
import riskyken.armourersWorkshop.common.network.messages.MessageClientGuiSetSkin;
import riskyken.armourersWorkshop.common.network.messages.MessageClientLoadArmour;
import riskyken.armourersWorkshop.common.network.messages.MessageServerAddArmourData;
import riskyken.armourersWorkshop.common.network.messages.MessageServerLibraryFileList;
import riskyken.armourersWorkshop.common.network.messages.MessageServerRemoveArmourData;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;

public class PacketHandler {

    public static final SimpleNetworkWrapper networkWrapper = NetworkRegistry.INSTANCE.newSimpleChannel(LibModInfo.CHANNEL);

    public static void init() {
        networkWrapper.registerMessage(MessageClientGuiColourUpdate.class, MessageClientGuiColourUpdate.class, 0, Side.SERVER);
        networkWrapper.registerMessage(MessageClientGuiButton.class, MessageClientGuiButton.class, 1, Side.SERVER);
        networkWrapper.registerMessage(MessageServerAddArmourData.class, MessageServerAddArmourData.class, 2, Side.CLIENT);
        networkWrapper.registerMessage(MessageServerRemoveArmourData.class, MessageServerRemoveArmourData.class, 3, Side.CLIENT);
        networkWrapper.registerMessage(MessageClientArmourUpdate.class, MessageClientArmourUpdate.class, 4, Side.SERVER);
        networkWrapper.registerMessage(MessageClientLoadArmour.class, MessageClientLoadArmour.class, 5, Side.SERVER);
        networkWrapper.registerMessage(MessageClientGuiSetSkin.class, MessageClientGuiSetSkin.class, 6, Side.SERVER);
        networkWrapper.registerMessage(MessageServerLibraryFileList.class, MessageServerLibraryFileList.class, 7, Side.CLIENT);
        networkWrapper.registerMessage(MessageClientGuiLoadSaveArmour.class, MessageClientGuiLoadSaveArmour.class, 8, Side.SERVER);
    }
}
