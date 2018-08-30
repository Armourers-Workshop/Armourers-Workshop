package riskyken.armourersWorkshop.common.items;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.input.Keyboard;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDispenser;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import net.minecraft.dispenser.IBehaviorDispenseItem;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import riskyken.armourersWorkshop.api.common.skin.type.ISkinType;
import riskyken.armourersWorkshop.client.settings.Keybindings;
import riskyken.armourersWorkshop.client.skin.cache.ClientSkinCache;
import riskyken.armourersWorkshop.common.blocks.BlockLocation;
import riskyken.armourersWorkshop.common.blocks.BlockSkinnable;
import riskyken.armourersWorkshop.common.blocks.ModBlocks;
import riskyken.armourersWorkshop.common.config.ConfigHandlerClient;
import riskyken.armourersWorkshop.common.lib.LibItemNames;
import riskyken.armourersWorkshop.common.skin.ExPropsPlayerSkinData;
import riskyken.armourersWorkshop.common.skin.cubes.CubeRegistry;
import riskyken.armourersWorkshop.common.skin.data.Skin;
import riskyken.armourersWorkshop.common.skin.data.SkinIdentifier;
import riskyken.armourersWorkshop.common.skin.data.SkinPointer;
import riskyken.armourersWorkshop.common.skin.data.SkinProperties;
import riskyken.armourersWorkshop.common.skin.type.SkinTypeRegistry;
import riskyken.armourersWorkshop.common.tileentities.TileEntitySkinnable;
import riskyken.armourersWorkshop.common.tileentities.TileEntitySkinnableChild;
import riskyken.armourersWorkshop.utils.SkinNBTHelper;
import riskyken.armourersWorkshop.utils.SkinUtils;
import riskyken.armourersWorkshop.utils.TranslateUtils;
import riskyken.armourersWorkshop.utils.UtilPlayer;

public class ItemSkin extends AbstractModItem {

    public ItemSkin() {
        super(LibItemNames.EQUIPMENT_SKIN, false);
        BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(this, dispenserBehavior);
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
        String cRed = TextFormatting.RED.toString();
        
        boolean isEquipmentSkin = stack.getItem() == ModItems.equipmentSkin;
        boolean isEquipmentContainer = stack.getItem() instanceof AbstractModItemArmour;
        
        if (SkinNBTHelper.stackHasSkinData(stack)) {
            SkinPointer skinData = SkinNBTHelper.getSkinPointerFromStack(stack);
            SkinIdentifier identifier = skinData.getIdentifier();
            
            if (!isEquipmentSkin & !skinData.lockSkin & !isEquipmentContainer) {
                return;
            }
            
            if (!isEquipmentSkin) {
                tooltip.add(TranslateUtils.translate("item.armourersworkshop:rollover.hasSkin"));
            }
            
            if (ClientSkinCache.INSTANCE.isSkinInCache(skinData)) {
                Skin data = ClientSkinCache.INSTANCE.getSkin(skinData);
                if (stack.getItem() != ModItems.equipmentSkin & !data.getCustomName().trim().isEmpty()) {
                    tooltip.add(TranslateUtils.translate("item.armourersworkshop:rollover.skinName", data.getCustomName()));
                }
                if (!data.getAuthorName().trim().isEmpty()) {
                    tooltip.add(TranslateUtils.translate("item.armourersworkshop:rollover.skinAuthor", data.getAuthorName()));
                }
                if (skinData.getIdentifier().getSkinType() != null) {
                    String localSkinName = SkinTypeRegistry.INSTANCE.getLocalizedSkinTypeName(skinData.getIdentifier().getSkinType());
                    tooltip.add(TranslateUtils.translate("item.armourersworkshop:rollover.skinType", localSkinName));
                }
                if (ConfigHandlerClient.showSkinTooltipDebugInfo) {
                    if (GuiScreen.isShiftKeyDown()) {
                        tooltip.add(TranslateUtils.translate("item.armourersworkshop:rollover.skinIdentifier"));
                        if (identifier.hasLocalId()) {
                            tooltip.add("  " + TranslateUtils.translate("item.armourersworkshop:rollover.skinId", identifier.getSkinLocalId()));
                        }
                        if (identifier.hasLibraryFile()) {
                            tooltip.add("  " + TranslateUtils.translate("item.armourersworkshop:rollover.skinLibraryFile", identifier.getSkinLibraryFile().getFullName()));
                        }
                        if (identifier.hasGlobalId()) {
                            tooltip.add("  " + TranslateUtils.translate("item.armourersworkshop:rollover.skinGlobalId", identifier.getSkinGlobalId()));
                        }
                        tooltip.add(TranslateUtils.translate("item.armourersworkshop:rollover.skinTotalCubes", data.getTotalCubes()));
                        tooltip.add(TranslateUtils.translate("item.armourersworkshop:rollover.skinNumCubes", data.getTotalOfCubeType(CubeRegistry.INSTANCE.getCubeFormId((byte) 0))));
                        tooltip.add(TranslateUtils.translate("item.armourersworkshop:rollover.skinNumCubesGlowing", data.getTotalOfCubeType(CubeRegistry.INSTANCE.getCubeFormId((byte) 1))));
                        tooltip.add(TranslateUtils.translate("item.armourersworkshop:rollover.skinNumCubesGlass", data.getTotalOfCubeType(CubeRegistry.INSTANCE.getCubeFormId((byte) 2))));
                        tooltip.add(TranslateUtils.translate("item.armourersworkshop:rollover.skinNumCubesGlassGlowing", data.getTotalOfCubeType(CubeRegistry.INSTANCE.getCubeFormId((byte) 3))));
                        tooltip.add(TranslateUtils.translate("item.armourersworkshop:rollover.skinPaintData", data.hasPaintData()));
                        tooltip.add(TranslateUtils.translate("item.armourersworkshop:rollover.skinMarkerCount", data.getMarkerCount()));
                        tooltip.add(TranslateUtils.translate("item.armourersworkshop:rollover.skinDyeCount", skinData.getSkinDye().getNumberOfDyes()));
                        tooltip.add(TranslateUtils.translate("item.armourersworkshop:rollover.skinProperties"));
                        ArrayList<String> props = data.getProperties().getPropertiesList();
                        for (int i = 0; i < props.size(); i++) {
                            tooltip.add("  " + props.get(i));
                        }
                    } else {
                        tooltip.add(TranslateUtils.translate("item.armourersworkshop:rollover.skinHoldShiftForInfo"));
                    }
                }
                if (identifier.hasLocalId()) {
                    if (identifier.getSkinLocalId() != data.lightHash()) {
                        tooltip.add(TranslateUtils.translate("item.armourersworkshop:rollover.skinIdError1"));
                        tooltip.add(TranslateUtils.translate("item.armourersworkshop:rollover.skinIdError2"));
                        tooltip.add(TranslateUtils.translate("item.armourersworkshop:rollover.skinIdError3", data.requestId, data.lightHash()));
                    }
                }
                
            } else {
                tooltip.add(TranslateUtils.translate("item.armourersworkshop:rollover.skindownloading", identifier.toString()));
                if (identifier.hasLocalId()) {
                    tooltip.add("  " + TranslateUtils.translate("item.armourersworkshop:rollover.skinId", identifier.getSkinLocalId()));
                }
                if (identifier.hasLibraryFile()) {
                    tooltip.add("  " + TranslateUtils.translate("item.armourersworkshop:rollover.skinLibraryFile", identifier.getSkinLibraryFile().getFullName()));
                }
                if (identifier.hasGlobalId()) {
                    tooltip.add("  " + TranslateUtils.translate("item.armourersworkshop:rollover.skinGlobalId", identifier.getSkinGlobalId()));
                }
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
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
        Block block = world.getBlock(x, y, z);
        SkinPointer skinPointer = SkinNBTHelper.getSkinPointerFromStack(stack);
        if (skinPointer != null && skinPointer.getIdentifier().getSkinType() == SkinTypeRegistry.skinBlock) {
            Skin skin = SkinUtils.getSkinDetectSide(skinPointer, false, true);
            if (skin != null) {
                EnumFacing dir = EnumFacing.getFront(side);
                Block replaceBlock = world.getBlock(x + dir.offsetX, y + dir.offsetY, z + dir.offsetZ);
                if (replaceBlock.isReplaceable(world, x + dir.offsetX, y + dir.offsetY, z + dir.offsetZ)) {
                    placeSkinAtLocation(world, player, side, stack, x + dir.offsetX, y + dir.offsetY, z + dir.offsetZ, skinPointer);
                    return true;
                }
            }
        }
        return false;
    }
    
    @Override
    public ItemStack onItemRightClick(ItemStack itemStack, World world, EntityPlayer player) {
        SkinPointer skinPointer = SkinNBTHelper.getSkinPointerFromStack(itemStack);
        if (!world.isRemote) {
            if (skinPointer != null) {
                if (equipSkin(player, itemStack.copy())) {
                    itemStack.stackSize--;
                }
            }
        }
        return itemStack;
    }
    
    private boolean equipSkin(EntityPlayer player, ItemStack itemStack) {
        ExPropsPlayerSkinData equipmentData = ExPropsPlayerSkinData.get(player);
        if (equipmentData.setStackInNextFreeSlot(itemStack)) {
            return true;
        }
        return false;
    }
    
    private boolean canPlaceSkinAtLocation(World world, EntityPlayer player, int side, ItemStack stack, int x, int y, int z, SkinPointer skinPointer) {
        if (!player.canPlayerEdit(x, y, z, side, stack)) {
            return false;
        }
        if (stack.stackSize == 0) {
            return false;
        }
        if (y == 255) {
            return false;
        }
        if (!world.canPlaceEntityOnSide(world.getBlock(x, y, z), x, y, z, false, side, null, stack)) {
            return false;
        }
        
        return true;
    }
    
    private boolean canPlaceChildren(World world, EntityPlayer player, int side, ItemStack stack, int x, int y, int z, Skin skin, SkinPointer skinPointer, ArrayList<BlockLocation> relatedBlocks) {
        EnumFacing dir = UtilPlayer.getDirectionSide(player).getOpposite();
        for (int ix = 0; ix < 3; ix++) {
            for (int iy = 0; iy < 3; iy++) {
                for (int iz = 0; iz < 3; iz++) {
                    float[] bounds = TileEntitySkinnable.getBlockBounds(skin, -ix + 2, iy, iz, dir);
                    if (bounds != null) {
                        int childX = x;
                        int childY = y;
                        int childZ = z;
                        
                        childX += ix - 1 - dir.offsetX * 1;
                        childY += iy;
                        childZ += iz - 1 - dir.offsetZ * 1;
                        
                        relatedBlocks.add(new BlockLocation(childX, childY, childZ));
                        
                        Block replaceBlock = world.getBlock(childX, childY, childZ);
                        if (!replaceBlock.isReplaceable(world, childX, childY, childZ)) {
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }
    
    private boolean placeSkinAtLocation(World world, EntityPlayer player, int side, ItemStack stack, int x, int y, int z, SkinPointer skinPointer) {
        if (!canPlaceSkinAtLocation(world, player, side, stack, x, y, z, skinPointer)) {
            return false;
        }
        
        EnumFacing dir = UtilPlayer.getDirectionSide(player).getOpposite();
        
        
        Skin skin = SkinUtils.getSkinDetectSide(stack, false, true);
        if (skin == null) {
            return false;
        }
        
        BlockSkinnable targetBlock = (BlockSkinnable) ModBlocks.skinnable;
        if (SkinProperties.PROP_BLOCK_GLOWING.getValue(skin.getProperties())) {
            targetBlock = (BlockSkinnable) ModBlocks.skinnableGlowing;
        }
        
        int meta = targetBlock.convertDirectionToMetadata(dir);
        
        boolean multiblock = SkinProperties.PROP_BLOCK_MULTIBLOCK.getValue(skin.getProperties());
        ArrayList<BlockLocation> relatedBlocks = new ArrayList<BlockLocation>();
        if (multiblock) {
            if (!canPlaceChildren(world, player, side, stack, x, y, z, skin, skinPointer, relatedBlocks)) {
                return false;
            }
            placeChildren(world, player, side, x, y, z, skin, skinPointer, relatedBlocks);
        }
        
        world.setBlock(x, y, z, targetBlock, meta, 2);
        world.setTileEntity(x, y, z, ((ITileEntityProvider)targetBlock).createNewTileEntity(world, 0));
        TileEntitySkinnable te = (TileEntitySkinnable) world.getTileEntity(x, y, z);
        te.setSkinPointer(skin, skinPointer);
        targetBlock.onBlockPlacedBy(world, x, y, z, player, stack);
        targetBlock.onPostBlockPlaced(world, x, y, z, meta);
        te.setRelatedBlocks(relatedBlocks);
        //targetBlock.setFacingDirection(world, x, y, z, dir);
        stack.stackSize--;
        world.playSoundEffect((float)x + 0.5F, (float)y + 0.5F, (float)z + 0.5F, "dig.stone", 1, 1);
        return true;
    }
    
    private void placeChildren(World world, EntityPlayer player, int side, int x, int y, int z, Skin skin, SkinPointer skinPointer, ArrayList<BlockLocation> relatedBlocks) {
        for (int ix = 0; ix < 3; ix++) {
            for (int iy = 0; iy < 3; iy++) {
                for (int iz = 0; iz < 3; iz++) {
                    placeChild(world, player, side, x, y, z, ix, iy, iz, skin, skinPointer, relatedBlocks);
                }
            }
        }
    }
    
    private void placeChild(World world, EntityPlayer player, int side, int x, int y, int z, int ix, int iy, int iz, Skin skin, SkinPointer skinPointer, ArrayList<BlockLocation> relatedBlocks) {
        EnumFacing dir = UtilPlayer.getDirectionSide(player).getOpposite();
        
        BlockSkinnable targetBlock = (BlockSkinnable) ModBlocks.skinnableChild;
        
        int meta = targetBlock.convertDirectionToMetadata(dir);
        if (SkinProperties.PROP_BLOCK_GLOWING.getValue(skin.getProperties())) {
            targetBlock = (BlockSkinnable) ModBlocks.skinnableChildGlowing;
        }
        
        float[] bounds = TileEntitySkinnable.getBlockBounds(skin, -ix + 2, iy, iz, dir);
        if (bounds != null) {
            int childX = x;
            int childY = y;
            int childZ = z;
            
            childX += ix - 1 - dir.offsetX * 1;
            childY += iy;
            childZ += iz - 1 - dir.offsetZ * 1;
            
            world.setBlock(childX, childY, childZ, targetBlock, meta, 2);
            world.setTileEntity(childX, childY, childZ, targetBlock.createTileEntity(world, meta));
            
            TileEntitySkinnableChild te = (TileEntitySkinnableChild) world.getTileEntity(childX, childY, childZ);
            te.setSkinPointer(skin, skinPointer);
            te.setParentLocation(x, y, z);
            te.setRelatedBlocks(relatedBlocks);
        }
    }
    
    private static final IBehaviorDispenseItem dispenserBehavior = new BehaviorDefaultDispenseItem() {
        
        @Override
        protected ItemStack dispenseStack(IBlockSource blockSource, ItemStack itemStack) {
            if (!SkinNBTHelper.stackHasSkinData(itemStack)) {
                return super.dispenseStack(blockSource, itemStack);
            }
            EnumFacing enumfacing = BlockDispenser.func_149937_b(blockSource.getBlockMetadata());
            int x = blockSource.getXInt() + enumfacing.getFrontOffsetX();
            int y = blockSource.getYInt() + enumfacing.getFrontOffsetY();
            int z = blockSource.getZInt() + enumfacing.getFrontOffsetZ();
            AxisAlignedBB axisalignedbb = AxisAlignedBB.getBoundingBox((double)x, (double)y, (double)z, (double)(x + 1), (double)(y + 1), (double)(z + 1));
            List list = blockSource.getWorld().getEntitiesWithinAABB(EntityLivingBase.class, axisalignedbb);
            
            for (int i = 0; i < list.size(); i++) {
                EntityLivingBase entitylivingbase = (EntityLivingBase)list.get(i);
                if (entitylivingbase instanceof EntityPlayer) {
                    EntityPlayer player = (EntityPlayer) entitylivingbase;
                    ExPropsPlayerSkinData equipmentData = ExPropsPlayerSkinData.get(player);
                    if (equipmentData.setStackInNextFreeSlot(itemStack.copy())) {
                        --itemStack.stackSize;
                        return itemStack;
                    }
                }
            }
            
            return super.dispenseStack(blockSource, itemStack);
        }
    };
}
