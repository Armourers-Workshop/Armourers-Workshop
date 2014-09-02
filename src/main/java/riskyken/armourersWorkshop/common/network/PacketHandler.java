package riskyken.armourersWorkshop.common.network;

import riskyken.armourersWorkshop.common.lib.LibModInfo;
import riskyken.armourersWorkshop.common.network.messages.MessageClientArmourUpdate;
import riskyken.armourersWorkshop.common.network.messages.MessageClientGuiButton;
import riskyken.armourersWorkshop.common.network.messages.MessageClientGuiColourUpdate;
import riskyken.armourersWorkshop.common.network.messages.MessageServerAddArmourData;
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
    }
}
