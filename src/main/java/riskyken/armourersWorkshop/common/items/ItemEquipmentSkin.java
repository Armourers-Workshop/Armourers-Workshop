package riskyken.armourersWorkshop.common.items;

import java.util.List;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import buildcraft.api.robots.IRobotOverlayItem;
import cpw.mods.fml.common.Optional;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;
import riskyken.armourersWorkshop.api.common.skin.type.ISkinType;
import riskyken.armourersWorkshop.client.handler.EquipmentRenderHandler;
import riskyken.armourersWorkshop.client.lib.LibItemResources;
import riskyken.armourersWorkshop.client.model.ClientModelCache;
import riskyken.armourersWorkshop.client.settings.Keybindings;
import riskyken.armourersWorkshop.common.lib.LibItemNames;
import riskyken.armourersWorkshop.common.skin.cubes.Cube;
import riskyken.armourersWorkshop.common.skin.cubes.CubeGlass;
import riskyken.armourersWorkshop.common.skin.cubes.CubeGlassGlowing;
import riskyken.armourersWorkshop.common.skin.cubes.CubeGlowing;
import riskyken.armourersWorkshop.common.skin.data.Skin;
import riskyken.armourersWorkshop.common.skin.data.SkinPointer;
import riskyken.armourersWorkshop.common.skin.type.SkinTypeRegistry;
import riskyken.armourersWorkshop.utils.EquipmentNBTHelper;
import riskyken.armourersWorkshop.utils.TranslateUtils;

@Optional.Interface(iface = "buildcraft.api.robots.IRobotOverlayItem", modid = "BuildCraft|Core")
public class ItemEquipmentSkin extends AbstractModItem implements IRobotOverlayItem {

    public ItemEquipmentSkin() {
        super(LibItemNames.EQUIPMENT_SKIN, false);
    }
    
    public ISkinType getSkinType(ItemStack stack) {
        return EquipmentNBTHelper.getSkinTypeFromStack(stack);
    }
    
    public static void addTooltipToSkinItem(ItemStack stack, EntityPlayer player, List tooltip, boolean showAdvancedItemTooltips) {
        String cRed = EnumChatFormatting.RED.toString();
        
        boolean isEquipmentSkin = stack.getItem() == ModItems.equipmentSkin;
        boolean isEquipmentContainer = stack.getItem() instanceof AbstractModItemArmour;
        
        if (EquipmentNBTHelper.stackHasSkinData(stack)) {
            SkinPointer skinData = EquipmentNBTHelper.getSkinPointerFromStack(stack);
            
            if (!isEquipmentSkin & !skinData.lockSkin & !isEquipmentContainer) {
                return;
            }
            
            if (!isEquipmentSkin) {
                tooltip.add(TranslateUtils.translate("item.armourersworkshop:rollover.hasSkin"));
            }
            
            if (ClientModelCache.INSTANCE.isEquipmentInCache(skinData.skinId)) {
                Skin data = ClientModelCache.INSTANCE.getEquipmentItemData(skinData.skinId);
                if (!data.getCustomName().trim().isEmpty()) {
                    tooltip.add(TranslateUtils.translate("item.armourersworkshop:rollover.skinName", data.getCustomName()));
                }
                if (!data.getAuthorName().trim().isEmpty()) {
                    tooltip.add(TranslateUtils.translate("item.armourersworkshop:rollover.skinAuthor", data.getAuthorName()));
                }
                
                if (GuiScreen.isShiftKeyDown()) {
                    if (skinData.skinType != null) {
                        String localSkinName = SkinTypeRegistry.INSTANCE.getLocalizedSkinTypeName(skinData.skinType);
                        tooltip.add(TranslateUtils.translate("item.armourersworkshop:rollover.skinType", localSkinName));
                    }
                    tooltip.add(TranslateUtils.translate("item.armourersworkshop:rollover.skinId", skinData.skinId));
                    tooltip.add(TranslateUtils.translate("item.armourersworkshop:rollover.skinTotalCubes", data.getTotalCubes()));
                    tooltip.add(TranslateUtils.translate("item.armourersworkshop:rollover.skinNumCubes", data.getTotalOfCubeType(Cube.class)));
                    tooltip.add(TranslateUtils.translate("item.armourersworkshop:rollover.skinNumCubesGlowing", data.getTotalOfCubeType(CubeGlowing.class)));
                    tooltip.add(TranslateUtils.translate("item.armourersworkshop:rollover.skinNumCubesGlass", data.getTotalOfCubeType(CubeGlass.class)));
                    tooltip.add(TranslateUtils.translate("item.armourersworkshop:rollover.skinNumCubesGlassGlowing", data.getTotalOfCubeType(CubeGlassGlowing.class)));
                } else {
                    tooltip.add(TranslateUtils.translate("item.armourersworkshop:rollover.skinHoldShiftForInfo"));
                }
                if (skinData.skinId != data.lightHash()) {
                    tooltip.add(TranslateUtils.translate("item.armourersworkshop:rollover.skinIdError1"));
                    tooltip.add(TranslateUtils.translate("item.armourersworkshop:rollover.skinIdError2"));
                    tooltip.add(TranslateUtils.translate("item.armourersworkshop:rollover.skinIdError3", data.requestId, data.lightHash()));
                }
            } else {
                tooltip.add(TranslateUtils.translate("item.armourersworkshop:rollover.skindownloading"));
            }
            String keyName = Keyboard.getKeyName(Keybindings.openCustomArmourGui.getKeyCode());
            if (isEquipmentSkin) {
                tooltip.add(TranslateUtils.translate("item.armourersworkshop:rollover.skinOpenWardrobe", keyName));
            }
        } else {
            if (EquipmentNBTHelper.stackHasLegacySkinData(stack)) {
                tooltip.add(TranslateUtils.translate("item.armourersworkshop:rollover.skinOldType"));
            } else {
                if (isEquipmentSkin) {
                    tooltip.add(TranslateUtils.translate("item.armourersworkshop:rollover.skinInvalidItem"));
                }
            }
        }
    }
    
    @Override
    public int getRenderPasses(int metadata) {
        return 2;
    }
    
    @Override
    public boolean requiresMultipleRenderPasses() {
        return true;
    }
    
    @SideOnly(Side.CLIENT)
    private IIcon loadingIcon;
    
    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister register) {
        this.itemIcon = register.registerIcon(LibItemResources.TEMPLATE_BLANK);
        this.loadingIcon = register.registerIcon(LibItemResources.TEMPLATE_LOADING);
    }
    
    @Override
    public IIcon getIcon(ItemStack stack, int pass) {
        if (pass == 1) {
            return this.loadingIcon;
        }
        
        if (EquipmentNBTHelper.stackHasSkinData(stack)) {
            SkinPointer skinData = EquipmentNBTHelper.getSkinPointerFromStack(stack);
            if (skinData.skinType != null) {
                if (skinData.skinType.getIcon() != null) {
                    return skinData.skinType.getIcon();
                }
            }
        }
        
        return this.itemIcon;
    }

    @Optional.Method(modid = "BuildCraft|Core")
    @Override
    public boolean isValidRobotOverlay(ItemStack stack) {
        if (!EquipmentNBTHelper.stackHasSkinData(stack)) {
            return false;
        }
        SkinPointer skinPointer = EquipmentNBTHelper.getSkinPointerFromStack(stack);
        if (skinPointer.getSkinType() != SkinTypeRegistry.skinHead) {
            return false;
        }
        return true;
    }
    
    @Optional.Method(modid = "BuildCraft|Core")
    @SideOnly(Side.CLIENT)
    @Override
    public void renderRobotOverlay(ItemStack stack, TextureManager textureManager) {
        GL11.glPushMatrix();
        GL11.glScalef(1.0125F, 1.0125F, 1.0125F);
        GL11.glTranslatef(0.0F, -0.25F, 0.0F);
        GL11.glRotatef(180F, 0F, 0F, 1F);
        GL11.glRotatef(-90F, 0F, 1F, 0F);
        EquipmentRenderHandler.INSTANCE.renderSkinWithHelper(stack);
        GL11.glPopMatrix();
    }
}
