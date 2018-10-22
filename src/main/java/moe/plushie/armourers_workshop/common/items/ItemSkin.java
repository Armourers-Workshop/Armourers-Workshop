package moe.plushie.armourers_workshop.common.items;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.lwjgl.input.Keyboard;

import moe.plushie.armourers_workshop.api.common.skin.data.ISkinDescriptor;
import moe.plushie.armourers_workshop.api.common.skin.type.ISkinType;
import moe.plushie.armourers_workshop.client.config.ConfigHandlerClient;
import moe.plushie.armourers_workshop.client.settings.Keybindings;
import moe.plushie.armourers_workshop.client.skin.cache.ClientSkinCache;
import moe.plushie.armourers_workshop.common.blocks.BlockSkinnable;
import moe.plushie.armourers_workshop.common.blocks.ModBlocks;
import moe.plushie.armourers_workshop.common.capability.entityskin.EntitySkinCapability;
import moe.plushie.armourers_workshop.common.capability.entityskin.IEntitySkinCapability;
import moe.plushie.armourers_workshop.common.capability.wardrobe.player.IPlayerWardrobeCap;
import moe.plushie.armourers_workshop.common.capability.wardrobe.player.PlayerWardrobeCap;
import moe.plushie.armourers_workshop.common.lib.LibItemNames;
import moe.plushie.armourers_workshop.common.skin.cubes.CubeRegistry;
import moe.plushie.armourers_workshop.common.skin.data.Skin;
import moe.plushie.armourers_workshop.common.skin.data.SkinDescriptor;
import moe.plushie.armourers_workshop.common.skin.data.SkinIdentifier;
import moe.plushie.armourers_workshop.common.skin.data.SkinProperties;
import moe.plushie.armourers_workshop.common.skin.type.SkinTypeRegistry;
import moe.plushie.armourers_workshop.common.tileentities.TileEntitySkinnable;
import moe.plushie.armourers_workshop.common.tileentities.TileEntitySkinnableChild;
import moe.plushie.armourers_workshop.common.world.AsyncWorldUpdateBlock;
import moe.plushie.armourers_workshop.common.world.SyncWorldUpdater;
import moe.plushie.armourers_workshop.utils.SkinNBTHelper;
import moe.plushie.armourers_workshop.utils.SkinUtils;
import moe.plushie.armourers_workshop.utils.TranslateUtils;
import moe.plushie.armourers_workshop.utils.UtilPlayer;
import net.minecraft.block.BlockDispenser;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import net.minecraft.dispenser.IBehaviorDispenseItem;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemSkin extends AbstractModItem {

    public ItemSkin() {
        super(LibItemNames.SKIN, false);
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
    
    @SideOnly(Side.CLIENT)
    public static void addTooltipToSkinItem(ItemStack stack, EntityPlayer player, List tooltip, ITooltipFlag flagIn) {
        String cRed = TextFormatting.RED.toString();
        
        boolean isEquipmentSkin = stack.getItem() == ModItems.skin;
        boolean isEquipmentContainer = stack.getItem() instanceof AbstractModItemArmour;
        
        if (SkinNBTHelper.stackHasSkinData(stack)) {
            SkinDescriptor skinData = SkinNBTHelper.getSkinDescriptorFromStack(stack);
            SkinIdentifier identifier = skinData.getIdentifier();
            
            if (!isEquipmentSkin) {
                tooltip.add(TranslateUtils.translate("item.armourers_workshop:rollover.hasSkin"));
            }
            
            if (ClientSkinCache.INSTANCE.isSkinInCache(skinData)) {
                Skin data = ClientSkinCache.INSTANCE.getSkin(skinData);
                if (stack.getItem() != ModItems.skin & !data.getCustomName().trim().isEmpty()) {
                    tooltip.add(TranslateUtils.translate("item.armourers_workshop:rollover.skinName", data.getCustomName()));
                }
                if (!data.getAuthorName().trim().isEmpty()) {
                    tooltip.add(TranslateUtils.translate("item.armourers_workshop:rollover.skinAuthor", data.getAuthorName()));
                }
                if (skinData.getIdentifier().getSkinType() != null) {
                    String localSkinName = SkinTypeRegistry.INSTANCE.getLocalizedSkinTypeName(skinData.getIdentifier().getSkinType());
                    tooltip.add(TranslateUtils.translate("item.armourers_workshop:rollover.skinType", localSkinName));
                }
                if (ConfigHandlerClient.showSkinTooltipDebugInfo) {
                    if (GuiScreen.isShiftKeyDown()) {
                        tooltip.add(TranslateUtils.translate("item.armourers_workshop:rollover.skinIdentifier"));
                        if (identifier.hasLocalId()) {
                            tooltip.add("  " + TranslateUtils.translate("item.armourers_workshop:rollover.skinId", identifier.getSkinLocalId()));
                        }
                        if (identifier.hasLibraryFile()) {
                            tooltip.add("  " + TranslateUtils.translate("item.armourers_workshop:rollover.skinLibraryFile", identifier.getSkinLibraryFile().getFullName()));
                        }
                        if (identifier.hasGlobalId()) {
                            tooltip.add("  " + TranslateUtils.translate("item.armourers_workshop:rollover.skinGlobalId", identifier.getSkinGlobalId()));
                        }
                        tooltip.add(TranslateUtils.translate("item.armourers_workshop:rollover.skinTotalCubes", data.getTotalCubes()));
                        tooltip.add(TranslateUtils.translate("item.armourers_workshop:rollover.skinNumCubes", data.getTotalOfCubeType(CubeRegistry.INSTANCE.getCubeFormId((byte) 0))));
                        tooltip.add(TranslateUtils.translate("item.armourers_workshop:rollover.skinNumCubesGlowing", data.getTotalOfCubeType(CubeRegistry.INSTANCE.getCubeFormId((byte) 1))));
                        tooltip.add(TranslateUtils.translate("item.armourers_workshop:rollover.skinNumCubesGlass", data.getTotalOfCubeType(CubeRegistry.INSTANCE.getCubeFormId((byte) 2))));
                        tooltip.add(TranslateUtils.translate("item.armourers_workshop:rollover.skinNumCubesGlassGlowing", data.getTotalOfCubeType(CubeRegistry.INSTANCE.getCubeFormId((byte) 3))));
                        tooltip.add(TranslateUtils.translate("item.armourers_workshop:rollover.skinPaintData", data.hasPaintData()));
                        tooltip.add(TranslateUtils.translate("item.armourers_workshop:rollover.skinMarkerCount", data.getMarkerCount()));
                        tooltip.add(TranslateUtils.translate("item.armourers_workshop:rollover.skinDyeCount", skinData.getSkinDye().getNumberOfDyes()));
                        tooltip.add(TranslateUtils.translate("item.armourers_workshop:rollover.skinProperties"));
                        ArrayList<String> props = data.getProperties().getPropertiesList();
                        for (int i = 0; i < props.size(); i++) {
                            tooltip.add("  " + props.get(i));
                        }
                    } else {
                        tooltip.add(TranslateUtils.translate("item.armourers_workshop:rollover.skinHoldShiftForInfo"));
                    }
                }
                if (identifier.hasLocalId()) {
                    if (identifier.getSkinLocalId() != data.lightHash()) {
                        tooltip.add(TranslateUtils.translate("item.armourers_workshop:rollover.skinIdError1"));
                        tooltip.add(TranslateUtils.translate("item.armourers_workshop:rollover.skinIdError2"));
                        tooltip.add(TranslateUtils.translate("item.armourers_workshop:rollover.skinIdError3", data.requestId, data.lightHash()));
                    }
                }
                String flavour = SkinProperties.PROP_ALL_FLAVOUR_TEXT.getValue(data.getProperties()).trim();
                if (!StringUtils.isEmpty(flavour)) {
                    tooltip.add(TranslateUtils.translate("item.armourers_workshop:rollover.flavour", flavour));
                }
                
            } else {
                tooltip.add(TranslateUtils.translate("item.armourers_workshop:rollover.skindownloading", identifier.toString()));
                if (identifier.hasLocalId()) {
                    tooltip.add("  " + TranslateUtils.translate("item.armourers_workshop:rollover.skinId", identifier.getSkinLocalId()));
                }
                if (identifier.hasLibraryFile()) {
                    tooltip.add("  " + TranslateUtils.translate("item.armourers_workshop:rollover.skinLibraryFile", identifier.getSkinLibraryFile().getFullName()));
                }
                if (identifier.hasGlobalId()) {
                    tooltip.add("  " + TranslateUtils.translate("item.armourers_workshop:rollover.skinGlobalId", identifier.getSkinGlobalId()));
                }
            }
            String keyName = Keyboard.getKeyName(Keybindings.OPEN_WARDROBE.getKeyCode());
            if (isEquipmentSkin) {
                tooltip.add(TranslateUtils.translate("item.armourers_workshop:rollover.skinOpenWardrobe", keyName));
            }
        } else {
            
            if (isEquipmentSkin) {
                tooltip.add(TranslateUtils.translate("item.armourers_workshop:rollover.skinInvalidItem"));
            }
            
        }
    }
    
    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        ItemStack stack = player.getHeldItem(hand);
        IBlockState state = worldIn.getBlockState(pos);
        ISkinDescriptor descriptor = SkinNBTHelper.getSkinDescriptorFromStack(stack);
        if (descriptor != null && descriptor.getIdentifier().getSkinType() == SkinTypeRegistry.skinBlock) {
            Skin skin = SkinUtils.getSkinDetectSide(descriptor, false, true);
            if (skin != null) {
                IBlockState replaceBlock = worldIn.getBlockState(pos.offset(facing));
                if (replaceBlock.getBlock().isReplaceable(worldIn, pos.offset(facing))) {
                    placeSkinAtLocation(worldIn, player, facing, stack, pos.offset(facing), skin, descriptor);
                    return EnumActionResult.SUCCESS;
                }
            }
            return EnumActionResult.FAIL;
        }
        return EnumActionResult.PASS;
    }
    
    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
        ItemStack itemStack = playerIn.getHeldItem(handIn);
        IPlayerWardrobeCap wardrobeCap = PlayerWardrobeCap.get(playerIn);
        IEntitySkinCapability skinCapability = EntitySkinCapability.get(playerIn);
        ISkinDescriptor descriptor = SkinNBTHelper.getSkinDescriptorFromStack(itemStack);
        if (wardrobeCap == null | skinCapability == null |  descriptor == null) {
            return new ActionResult<ItemStack>(EnumActionResult.FAIL, itemStack);
        }
        ISkinType skinType = descriptor.getIdentifier().getSkinType();
        
        
        for (int i = 0; i < wardrobeCap.getUnlockedSlotsForSkinType(skinType); i++) {
            ISkinDescriptor descriptor2 = skinCapability.getSkinDescriptor(skinType, i);
            if (descriptor2 == null) {
                if (!worldIn.isRemote) {
                    skinCapability.setSkinStack(skinType, i, itemStack);
                    skinCapability.setSkinDescriptor(skinType, i, descriptor);
                    skinCapability.syncToPlayer((EntityPlayerMP) playerIn);
                    skinCapability.syncToAllTracking();
                    itemStack.shrink(1);
                }
                return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, itemStack);
            }
        }
        
        return new ActionResult<ItemStack>(EnumActionResult.FAIL, itemStack);
    }
    
    private boolean placeSkinAtLocation(World world, EntityPlayer player, EnumFacing facing, ItemStack stack, BlockPos pos, Skin skin, ISkinDescriptor descriptor) {
        if (!canPlaceSkinAtLocation(world, player, facing, stack, pos, descriptor)) {
            return false;
        }
        if (world.isRemote) {
            return true;
        }
        EnumFacing dir = UtilPlayer.getDirectionSide(player).getOpposite();
        
        BlockSkinnable targetBlock = (BlockSkinnable) ModBlocks.skinnable;
        if (SkinProperties.PROP_BLOCK_GLOWING.getValue(skin.getProperties())) {
            targetBlock = (BlockSkinnable) ModBlocks.skinnableGlowing;
        }
        
        boolean multiblock = SkinProperties.PROP_BLOCK_MULTIBLOCK.getValue(skin.getProperties());
        ArrayList<BlockPos> relatedBlocks = new ArrayList<BlockPos>();
        if (multiblock) {
            if (!canPlaceChildren(world, player, facing, stack, pos, skin, descriptor, relatedBlocks)) {
                return false;
            }
            placeChildren(world, player, facing, pos, skin, descriptor, relatedBlocks);
        }
        
        IBlockState state = targetBlock.getDefaultState().withProperty(BlockSkinnable.STATE_FACING, dir);
        
        
        
        //world.setBlockState(pos, state, 2);
        
        TileEntitySkinnable te = new TileEntitySkinnable();
        te.setSkinPointer(skin, descriptor);
        te.setRelatedBlocks(relatedBlocks);
        //world.setTileEntity(pos, te);
        SyncWorldUpdater.addWorldUpdate(new AsyncWorldUpdateBlock(state, pos, world).setTileEntity(te).setDelay(1));

        

        //targetBlock.onBlockPlacedBy(world, pos, state, player, stack);
        //targetBlock.onPostBlockPlaced(world, pos, meta);

        //targetBlock.setFacingDirection(world, x, y, z, dir);
        stack.shrink(1);
        
        SoundType soundType = state.getBlock().getSoundType(state, world, pos, player);
        
        world.playSound(player, pos, soundType.getPlaceSound(), SoundCategory.BLOCKS, (soundType.getVolume() + 1.0F) / 2.0F, soundType.getPitch() * 0.8F);
        return true;
    }
    
    private boolean canPlaceSkinAtLocation(World world, EntityPlayer player, EnumFacing facing, ItemStack stack, BlockPos pos, ISkinDescriptor descriptor) {
        if (!player.canPlayerEdit(pos, facing, stack)) {
            return false;
        }
        if (stack.getCount() == 0) {
            return false;
        }
        if (!world.isValid(pos)) {
            return false;
        }
        if (!world.mayPlace(world.getBlockState(pos).getBlock(), pos, true, facing, player)) {
            return false;
        }
        return true;
    }
    
    private boolean canPlaceChildren(World world, EntityPlayer player, EnumFacing facing, ItemStack stack, BlockPos pos, Skin skin, ISkinDescriptor descriptor, ArrayList<BlockPos> relatedBlocks) {
        EnumFacing dir = UtilPlayer.getDirectionSide(player).getOpposite();
        for (int ix = 0; ix < 3; ix++) {
            for (int iy = 0; iy < 3; iy++) {
                for (int iz = 0; iz < 3; iz++) {
                    float[] bounds = TileEntitySkinnable.getBlockBounds(skin, -ix + 2, iy, iz, dir);
                    if (bounds != null) {
                        BlockPos childPos = pos.add(ix - 1 - dir.getXOffset() * 1, iy, iz - 1 - dir.getZOffset() * 1);
                        relatedBlocks.add(childPos);
                        
                        IBlockState replaceState = world.getBlockState(childPos);
                        if (!replaceState.getBlock().isReplaceable(world, childPos)) {
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }
    
    private void placeChildren(World world, EntityPlayer player, EnumFacing facing, BlockPos pos, Skin skin, ISkinDescriptor descriptor, ArrayList<BlockPos> relatedBlocks) {
        for (int ix = 0; ix < 3; ix++) {
            for (int iy = 0; iy < 3; iy++) {
                for (int iz = 0; iz < 3; iz++) {
                    placeChild(world, player, facing, pos, ix, iy, iz, skin, descriptor, relatedBlocks);
                }
            }
        }
    }
    
    private void placeChild(World world, EntityPlayer player, EnumFacing facing, BlockPos pos, int ix, int iy, int iz, Skin skin, ISkinDescriptor descriptor, ArrayList<BlockPos> relatedBlocks) {
        EnumFacing dir = UtilPlayer.getDirectionSide(player).getOpposite();
        
        BlockSkinnable targetBlock = (BlockSkinnable) ModBlocks.skinnableChild;
        
        IBlockState state = targetBlock.getDefaultState().withProperty(BlockSkinnable.STATE_FACING, dir);
        if (SkinProperties.PROP_BLOCK_GLOWING.getValue(skin.getProperties())) {
            targetBlock = (BlockSkinnable) ModBlocks.skinnableChildGlowing;
        }
        
        float[] bounds = TileEntitySkinnable.getBlockBounds(skin, -ix + 2, iy, iz, dir);
        if (bounds != null) {
            BlockPos childPos = pos.add(ix - 1 - dir.getXOffset() * 1, iy, iz - 1 - dir.getZOffset() * 1);
            world.setBlockState(childPos, state, 2);
            TileEntitySkinnableChild te = new TileEntitySkinnableChild();
            te.setSkinPointer(skin, descriptor);
            te.setParentLocation(pos);
            te.setRelatedBlocks(relatedBlocks);
            world.setTileEntity(childPos, te);
        }
    }
    
    private static final IBehaviorDispenseItem dispenserBehavior = new BehaviorDefaultDispenseItem() {
        
        @Override
        protected ItemStack dispenseStack(IBlockSource blockSource, ItemStack itemStack) {
            if (!SkinNBTHelper.stackHasSkinData(itemStack)) {
                return super.dispenseStack(blockSource, itemStack);
            }
            /*
            IBlockState state = blockSource.getBlockState();
            EnumFacing enumfacing = BlockDispenser.func_149937_b(blockSource.getBlockState());
            double x = blockSource.getX() + enumfacing.getXOffset();
            double y = blockSource.getY() + enumfacing.getYOffset();
            double z = blockSource.getZ() + enumfacing.getZOffset();
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
            */
            return super.dispenseStack(blockSource, itemStack);
        }
    };
}
