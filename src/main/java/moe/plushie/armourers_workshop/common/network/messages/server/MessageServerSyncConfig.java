package moe.plushie.armourers_workshop.common.network.messages.server;

import java.util.UUID;

import io.netty.buffer.ByteBuf;
import moe.plushie.armourers_workshop.common.addons.ModAddonManager;
import moe.plushie.armourers_workshop.common.config.ConfigHandler;
import moe.plushie.armourers_workshop.common.network.ByteBufHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Sent from the server to a client when they connect to sync configs.
 * @author RiskyKen
 *
 */
public class MessageServerSyncConfig implements IMessage, IMessageHandler<MessageServerSyncConfig, IMessage> {
    
    private boolean allowClientsToDownloadSkins;
    private boolean allowClientsToUploadSkins;
    private String[] itemOverrides;
    private boolean libraryShowsModelPreviews;
    private boolean lockDyesOnSkins;
    private boolean instancedDyeTable;
    private boolean enableRecoveringSkins;
    private UUID playerId;
    
    public MessageServerSyncConfig(EntityPlayer player) {
        this();
        playerId = player.getUniqueID();
    }
    
    public MessageServerSyncConfig() {
        this.allowClientsToDownloadSkins = ConfigHandler.allowClientsToDownloadSkins;
        this.allowClientsToUploadSkins = ConfigHandler.allowClientsToUploadSkins;
        this.itemOverrides = ModAddonManager.getItemOverrides().toArray(new String[ModAddonManager.getItemOverrides().size()]);
        this.libraryShowsModelPreviews = ConfigHandler.libraryShowsModelPreviews;
        this.lockDyesOnSkins = ConfigHandler.lockDyesOnSkins;
        this.instancedDyeTable = ConfigHandler.instancedDyeTable;
        this.enableRecoveringSkins = ConfigHandler.enableRecoveringSkins;
    }
    
    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeBoolean(allowClientsToDownloadSkins);
        buf.writeBoolean(allowClientsToUploadSkins);
        ByteBufHelper.writeStringArrayToBuf(buf, itemOverrides);
        buf.writeBoolean(libraryShowsModelPreviews);
        buf.writeBoolean(lockDyesOnSkins);
        buf.writeBoolean(instancedDyeTable);
        buf.writeBoolean(enableRecoveringSkins);
        if (playerId == null) {
            buf.writeBoolean(false);
        } else {
            buf.writeBoolean(true);
            ByteBufHelper.writeUUID(buf, playerId);
        }
    }
    
    
    @Override
    public void fromBytes(ByteBuf buf) {
        allowClientsToDownloadSkins = buf.readBoolean();
        allowClientsToUploadSkins = buf.readBoolean();
        itemOverrides = ByteBufHelper.readStringArrayFromBuf(buf);
        libraryShowsModelPreviews = buf.readBoolean();
        lockDyesOnSkins = buf.readBoolean();
        instancedDyeTable = buf.readBoolean();
        enableRecoveringSkins = buf.readBoolean();
        if (buf.readBoolean()) {
            playerId = ByteBufHelper.readUUID(buf);
        }
    }

    @Override
    public IMessage onMessage(MessageServerSyncConfig message, MessageContext ctx) {
        setConfigsOnClient(message);
        return null;
    }
    
    @SideOnly(Side.CLIENT)
    private void setConfigsOnClient(MessageServerSyncConfig message) {
        ConfigHandler.allowClientsToDownloadSkins = message.allowClientsToDownloadSkins;
        ConfigHandler.allowClientsToUploadSkins = message.allowClientsToUploadSkins;
        ModAddonManager.setOverridesFromServer(message.itemOverrides);
        ConfigHandler.libraryShowsModelPreviews = message.libraryShowsModelPreviews;
        ConfigHandler.lockDyesOnSkins = message.lockDyesOnSkins;
        ConfigHandler.remotePlayerId = message.playerId;
        ConfigHandler.instancedDyeTable = message.instancedDyeTable;
        ConfigHandler.enableRecoveringSkins = message.enableRecoveringSkins ;
    }
}
