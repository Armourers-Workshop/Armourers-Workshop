package riskyken.armourersWorkshop.common.items;

import java.util.List;

import org.lwjgl.input.Keyboard;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import riskyken.armourersWorkshop.api.common.skin.type.ISkinType;
import riskyken.armourersWorkshop.client.lib.LibItemResources;
import riskyken.armourersWorkshop.client.settings.Keybindings;
import riskyken.armourersWorkshop.client.skin.ClientSkinCache;
import riskyken.armourersWorkshop.common.blocks.ModBlocks;
import riskyken.armourersWorkshop.common.config.ConfigHandler;
import riskyken.armourersWorkshop.common.lib.LibItemNames;
import riskyken.armourersWorkshop.common.skin.cubes.CubeRegistry;
import riskyken.armourersWorkshop.common.skin.data.Skin;
import riskyken.armourersWorkshop.common.skin.data.SkinPointer;
import riskyken.armourersWorkshop.common.skin.type.SkinTypeRegistry;
import riskyken.armourersWorkshop.common.tileentities.TileEntitySkinnable;
import riskyken.armourersWorkshop.utils.SkinNBTHelper;
import riskyken.armourersWorkshop.utils.SkinUtils;
import riskyken.armourersWorkshop.utils.TranslateUtils;

public class ItemSkin extends AbstractModItem {

    public ItemSkin() {
        super(LibItemNames.EQUIPMENT_SKIN, false);
    }
    
    public ISkinType getSkinType(ItemStack stack) {
        return SkinNBTHelper.getSkinTypeFromStack(stack);
    }
    
    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        Skin skin = SkinUtils.getSkinDetectSide(stack, true, false);
        if (skin != null) {
            if (!skin.getCustomName().trim().isEmpty()) {
                return skin.getCustomName();
            }
        }
        return super.getItemStackDisplayName(stack);
    }
    
    public static void addTooltipToSkinItem(ItemStack stack, EntityPlayer player, List tooltip, boolean showAdvancedItemTooltips) {
        String cRed = EnumChatFormatting.RED.toString();
        
        boolean isEquipmentSkin = stack.getItem() == ModItems.equipmentSkin;
        boolean isEquipmentContainer = stack.getItem() instanceof AbstractModItemArmour;
        
        if (SkinNBTHelper.stackHasSkinData(stack)) {
            SkinPointer skinData = SkinNBTHelper.getSkinPointerFromStack(stack);
            
            if (!isEquipmentSkin & !skinData.lockSkin & !isEquipmentContainer) {
                return;
            }
            
            if (!isEquipmentSkin) {
                tooltip.add(TranslateUtils.translate("item.armourersworkshop:rollover.hasSkin"));
            }
            
            if (ClientSkinCache.INSTANCE.isSkinInCache(skinData.skinId)) {
                Skin data = ClientSkinCache.INSTANCE.getSkin(skinData.skinId);
                if (stack.getItem() != ModItems.equipmentSkin & !data.getCustomName().trim().isEmpty()) {
                    tooltip.add(TranslateUtils.translate("item.armourersworkshop:rollover.skinName", data.getCustomName()));
                }
                if (!data.getAuthorName().trim().isEmpty()) {
                    tooltip.add(TranslateUtils.translate("item.armourersworkshop:rollover.skinAuthor", data.getAuthorName()));
                }
                if (skinData.skinType != null) {
                    String localSkinName = SkinTypeRegistry.INSTANCE.getLocalizedSkinTypeName(skinData.skinType);
                    tooltip.add(TranslateUtils.translate("item.armourersworkshop:rollover.skinType", localSkinName));
                }
                if (ConfigHandler.showSkinTooltipDebugInfo) {
                    if (GuiScreen.isShiftKeyDown()) {
                        tooltip.add(TranslateUtils.translate("item.armourersworkshop:rollover.skinId", skinData.skinId));
                        tooltip.add(TranslateUtils.translate("item.armourersworkshop:rollover.skinTotalCubes", data.getTotalCubes()));
                        tooltip.add(TranslateUtils.translate("item.armourersworkshop:rollover.skinNumCubes", data.getTotalOfCubeType(CubeRegistry.INSTANCE.getCubeFormId((byte) 0))));
                        tooltip.add(TranslateUtils.translate("item.armourersworkshop:rollover.skinNumCubesGlowing", data.getTotalOfCubeType(CubeRegistry.INSTANCE.getCubeFormId((byte) 1))));
                        tooltip.add(TranslateUtils.translate("item.armourersworkshop:rollover.skinNumCubesGlass", data.getTotalOfCubeType(CubeRegistry.INSTANCE.getCubeFormId((byte) 2))));
                        tooltip.add(TranslateUtils.translate("item.armourersworkshop:rollover.skinNumCubesGlassGlowing", data.getTotalOfCubeType(CubeRegistry.INSTANCE.getCubeFormId((byte) 3))));
                        tooltip.add(TranslateUtils.translate("item.armourersworkshop:rollover.skinPaintData", data.hasPaintData()));
                        tooltip.add(TranslateUtils.translate("item.armourersworkshop:rollover.skinMarkerCount", data.getMarkerCount()));
                        tooltip.add(TranslateUtils.translate("item.armourersworkshop:rollover.skinDyeCount", skinData.getSkinDye().getNumberOfDyes()));
                    } else {
                        tooltip.add(TranslateUtils.translate("item.armourersworkshop:rollover.skinHoldShiftForInfo"));
                    }
                }
                
                if (skinData.skinId != data.lightHash()) {
                    tooltip.add(TranslateUtils.translate("item.armourersworkshop:rollover.skinIdError1"));
                    tooltip.add(TranslateUtils.translate("item.armourersworkshop:rollover.skinIdError2"));
                    tooltip.add(TranslateUtils.translate("item.armourersworkshop:rollover.skinIdError3", data.requestId, data.lightHash()));
                }
            } else {
                tooltip.add(TranslateUtils.translate("item.armourersworkshop:rollover.skindownloading", skinData.skinId));
            }
            String keyName = Keyboard.getKeyName(Keybindings.openCustomArmourGui.getKeyCode());
            if (isEquipmentSkin) {
                tooltip.add(TranslateUtils.translate("item.armourersworkshop:rollover.skinOpenWardrobe", keyName));
            }
        } else {
            if (SkinNBTHelper.stackHasLegacySkinData(stack)) {
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
        
        if (SkinNBTHelper.stackHasSkinData(stack)) {
            SkinPointer skinData = SkinNBTHelper.getSkinPointerFromStack(stack);
            if (skinData.skinType != null) {
                if (skinData.skinType.getIcon() != null) {
                    return skinData.skinType.getIcon();
                }
            }
        }
        
        return this.itemIcon;
    }
    
    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world,
            int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
        Block block = world.getBlock(x, y, z);
        
        SkinPointer skinPointer = SkinNBTHelper.getSkinPointerFromStack(stack);
        
        if (skinPointer != null && skinPointer.getSkinType() == SkinTypeRegistry.skinBlock) {
            ForgeDirection dir = ForgeDirection.getOrientation(side);
            Block replaceBlock = world.getBlock(x + dir.offsetX, y + dir.offsetY, z + dir.offsetZ);
            if (replaceBlock.isReplaceable(world, x + dir.offsetX, y + dir.offsetY, z + dir.offsetZ)) {
                placeSkinAtLocation(world, player, side, stack, x + dir.offsetX, y + dir.offsetY, z + dir.offsetZ, skinPointer);
                return true;
            }
        }

        return false;
    }
    
    private boolean placeSkinAtLocation(World world, EntityPlayer player, int side, ItemStack stack, int x, int y, int z, SkinPointer skinPointer) {
        if (!player.canPlayerEdit(x, y, z, side, stack)) {
            return false;
        }
        if (stack.stackSize == 0) {
            return false;
        }
        if (y == 255) {
            return false;
        }
        if (!world.canPlaceEntityOnSide(Blocks.stone, x, y, z, false, side, null, stack)) {
            return false;
        }
        int rotation = MathHelper.floor_double((double)(player.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
        world.setBlock(x, y, z, ModBlocks.skinnable, rotation, 2);
        world.setTileEntity(x, y, z, ((ITileEntityProvider)ModBlocks.skinnable).createNewTileEntity(world, 0));
        TileEntitySkinnable te = (TileEntitySkinnable) world.getTileEntity(x, y, z);
        te.setSkinPointer(skinPointer);
        stack.stackSize--;
        world.playSoundEffect((float)x + 0.5F, (float)y + 0.5F, (float)z + 0.5F, "dig.stone", 1, 1);
        return true;
    }
}
