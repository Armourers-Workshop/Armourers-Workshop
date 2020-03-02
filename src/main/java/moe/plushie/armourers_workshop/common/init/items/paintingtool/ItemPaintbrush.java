package moe.plushie.armourers_workshop.common.init.items.paintingtool;

import java.util.ArrayList;
import java.util.List;

import moe.plushie.armourers_workshop.api.common.painting.IPaintType;
import moe.plushie.armourers_workshop.api.common.painting.IPantableBlock;
import moe.plushie.armourers_workshop.common.holiday.ModHolidays;
import moe.plushie.armourers_workshop.common.init.sounds.ModSounds;
import moe.plushie.armourers_workshop.common.lib.LibItemNames;
import moe.plushie.armourers_workshop.common.lib.LibModInfo;
import moe.plushie.armourers_workshop.common.painting.tool.IConfigurableTool;
import moe.plushie.armourers_workshop.common.painting.tool.ToolOption;
import moe.plushie.armourers_workshop.common.painting.tool.ToolOptions;
import moe.plushie.armourers_workshop.common.world.undo.UndoManager;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemPaintbrush extends AbstractPaintingTool implements IConfigurableTool {
    
    public ItemPaintbrush() {
        super(LibItemNames.PAINTBRUSH);
        setSortPriority(20);
    }
    
    @Override
    public void playToolSound(EntityPlayer player, World world, BlockPos pos, ItemStack stack) {
        SoundEvent soundEvent = ModSounds.PAINT;
        if (ModHolidays.APRIL_FOOLS.isHolidayActive()) {
            soundEvent = ModSounds.BOI;
        }
        if (ToolOptions.FULL_BLOCK_MODE.getValue(stack)) {
            world.playSound(null, pos, soundEvent, SoundCategory.BLOCKS, 1.0F, world.rand.nextFloat() * 0.2F + 0.9F);
        } else {
            world.playSound(null, pos, soundEvent, SoundCategory.BLOCKS, 1.0F, world.rand.nextFloat() * 0.2F + 1.5F);
        }
    }
    
    @Override
    public void usedOnBlockSide(ItemStack stack, EntityPlayer player, World world, BlockPos pos, Block block, EnumFacing face, boolean spawnParticles) {
        int colour = getToolColour(stack);
        IPaintType paintType = getToolPaintType(stack);
        if (!world.isRemote) {
            IPantableBlock worldColourable = (IPantableBlock) block;
            int oldColour = worldColourable.getColour(world, pos, face);
            byte oldPaintType = (byte) worldColourable.getPaintType(world, pos, face).getId();
            UndoManager.blockPainted(player, world, pos, oldColour, oldPaintType, face);
            ((IPantableBlock)block).setColour(world, pos, colour, face);
            ((IPantableBlock)block).setPaintType(world, pos, paintType, face);
        } else {
            if (spawnParticles) {
                spawnPaintParticles(world, pos, face, colour);
            }
        }
    }
    
    @Override
    public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        addOpenSettingsInformation(stack, worldIn, tooltip, flagIn);
    }
    
    @Override
    public void getToolOptions(ArrayList<ToolOption<?>> toolOptionList) {
        toolOptionList.add(ToolOptions.FULL_BLOCK_MODE);
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public void registerModels() {
        ModelLoader.setCustomMeshDefinition(this, new ItemMeshDefinition() {
            @Override
            public ModelResourceLocation getModelLocation(ItemStack stack) {
                if (ToolOptions.FULL_BLOCK_MODE.getValue(stack)) {
                    return new ModelResourceLocation(new ResourceLocation(LibModInfo.ID, getTranslationKey()), "normal");
                } else {
                    return new ModelResourceLocation(new ResourceLocation(LibModInfo.ID, getTranslationKey() + "-small"), "normal");
                }
            }
        });
        ModelBakery.registerItemVariants(this, new ModelResourceLocation(new ResourceLocation(LibModInfo.ID, getTranslationKey()), "normal"), new ModelResourceLocation(new ResourceLocation(LibModInfo.ID, getTranslationKey() + "-small"), "normal"));
    }
}
