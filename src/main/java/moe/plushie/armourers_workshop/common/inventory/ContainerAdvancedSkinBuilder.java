package moe.plushie.armourers_workshop.common.inventory;

import java.util.ArrayList;

import moe.plushie.armourers_workshop.ArmourersWorkshop;
import moe.plushie.armourers_workshop.api.common.skin.data.ISkinIdentifier;
import moe.plushie.armourers_workshop.api.common.skin.type.ISkinType;
import moe.plushie.armourers_workshop.client.gui.advanced_skin_builder.GuiAdvancedSkinBuilder;
import moe.plushie.armourers_workshop.common.library.LibraryFile;
import moe.plushie.armourers_workshop.common.network.messages.client.MessageClientGuiButton.IButtonPress;
import moe.plushie.armourers_workshop.common.skin.advanced.AdvancedPartNode;
import moe.plushie.armourers_workshop.common.skin.cache.CommonSkinCache;
import moe.plushie.armourers_workshop.common.skin.data.Skin;
import moe.plushie.armourers_workshop.common.skin.data.SkinDescriptor;
import moe.plushie.armourers_workshop.common.skin.data.SkinPart;
import moe.plushie.armourers_workshop.common.skin.data.SkinProperties;
import moe.plushie.armourers_workshop.common.tileentities.TileEntityAdvancedSkinBuilder;
import moe.plushie.armourers_workshop.utils.SkinNBTHelper;
import moe.plushie.armourers_workshop.utils.UtilItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;

public class ContainerAdvancedSkinBuilder extends ModTileContainer<TileEntityAdvancedSkinBuilder> implements IButtonPress {

    public ContainerAdvancedSkinBuilder(InventoryPlayer invPlayer, TileEntityAdvancedSkinBuilder tileEntity) {
        super(invPlayer, tileEntity);
        addPlayerSlots(8, 40);
    }

    @Override
    public void buttonPressed(EntityPlayer player, byte buttonId) {
        if (buttonId == 0) {
            saveAdvancedSkin(player);
        }
    }

    private void saveAdvancedSkin(EntityPlayer player) {
        ArmourersWorkshop.getLogger().info("Making advanced part.");
        
        GuiAdvancedSkinBuilder guiBuilder;
        GuiScreen screen = Minecraft.getMinecraft().currentScreen;
        if (screen != null && screen instanceof GuiAdvancedSkinBuilder) {
            guiBuilder = (GuiAdvancedSkinBuilder) screen;
        } else {
            return;
        }

        SkinProperties properties = new SkinProperties();
        ISkinType skinType = guiBuilder.getSkinType();
        AdvancedPartNode advancedPartNode = guiBuilder.convertTreeToAdvancedPartNode();
        ArrayList<ISkinIdentifier> skinIdentifiers = guiBuilder.readSkinsFromTree();
        ArrayList<SkinPart> skinParts = new ArrayList<SkinPart>();

        SkinProperties.PROP_ALL_AUTHOR_NAME.setValue(properties, player.getName());
        if (player.getGameProfile() != null && player.getGameProfile().getId() != null) {
            SkinProperties.PROP_ALL_AUTHOR_UUID.setValue(properties, player.getGameProfile().getId().toString());
        }
        // SkinProperties.PROP_ALL_CUSTOM_NAME.setValue(properties, "");
        // SkinProperties.PROP_ALL_FLAVOUR_TEXT.setValue(properties, "");

        for (ISkinIdentifier identifier : skinIdentifiers) {
            Skin skin = CommonSkinCache.INSTANCE.getSkin(identifier, false);
            if (skin != null) {
                SkinPart skinPart = skin.getParts().get(0);
                if (!skinParts.contains(skinPart)) {
                    skinParts.add(skinPart);
                }
            }
        }

        ArmourersWorkshop.getLogger().info("Parts found: " + skinParts.size());

        Skin skin = new Skin(properties, skinType, null, skinParts);
        skin.setAdvancedPartData(advancedPartNode);
        
        ArmourersWorkshop.getLogger().info(advancedPartNode);
        
        CommonSkinCache.INSTANCE.addEquipmentDataToCache(skin, (LibraryFile) null);
        ItemStack skinStack = SkinNBTHelper.makeEquipmentSkinStack(new SkinDescriptor(skin));

        UtilItems.spawnItemAtEntity(player, skinStack, false);
    }

    public void setSkinType(ISkinType skinType) {

    }

    public ISkinType getSkinType() {
        return null;
    }

    public void setAdvancedPartNode(AdvancedPartNode advancedPartNode) {

    }

    public AdvancedPartNode getAdvancedPartNode() {
        return null;

    }
}
