package moe.plushie.armourers_workshop.builder.item;

import moe.plushie.armourers_workshop.ArmourersWorkshop;
import moe.plushie.armourers_workshop.api.common.IItemColorProvider;
import moe.plushie.armourers_workshop.api.common.IItemModelProperty;
import moe.plushie.armourers_workshop.api.common.IItemPropertiesProvider;
import moe.plushie.armourers_workshop.api.common.IItemTintColorProvider;
import moe.plushie.armourers_workshop.api.painting.IBlockPaintViewer;
import moe.plushie.armourers_workshop.api.painting.IPaintColor;
import moe.plushie.armourers_workshop.api.painting.IPaintingToolProperty;
import moe.plushie.armourers_workshop.builder.item.impl.IPaintToolAction;
import moe.plushie.armourers_workshop.builder.item.tooloption.ToolOptions;
import moe.plushie.armourers_workshop.builder.other.SkinCubePaintingEvent;
import moe.plushie.armourers_workshop.core.data.color.PaintColor;
import moe.plushie.armourers_workshop.core.item.impl.IPaintProvider;
import moe.plushie.armourers_workshop.core.item.impl.IPaintToolPicker;
import moe.plushie.armourers_workshop.api.registry.IRegistryObject;
import moe.plushie.armourers_workshop.core.skin.painting.SkinPaintTypes;
import moe.plushie.armourers_workshop.init.ModSounds;
import moe.plushie.armourers_workshop.utils.ColorUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class PaintbrushItem extends AbstractPaintToolItem implements IItemTintColorProvider, IItemPropertiesProvider, IItemColorProvider, IBlockPaintViewer, IPaintToolPicker {

    public PaintbrushItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        InteractionResult resultType = usePickTool(context);
        if (resultType.consumesAction()) {
            return resultType;
        }
        return super.useOn(context);
    }

    @Override
    public InteractionResult usePickTool(Level world, BlockPos pos, Direction dir, BlockEntity tileEntity, UseOnContext context) {
        if (tileEntity instanceof IPaintProvider) {
            setItemColor(context.getItemInHand(), ((IPaintProvider) tileEntity).getColor());
            return InteractionResult.sidedSuccess(world.isClientSide);
        }
        return InteractionResult.PASS;
    }

    @Override
    public void createToolProperties(Consumer<IPaintingToolProperty<?>> builder) {
        builder.accept(ToolOptions.FULL_BLOCK_MODE);
    }

    @Override
    public IPaintToolAction createPaintToolAction(UseOnContext context) {
        IPaintColor paintColor = getItemColor(context.getItemInHand(), PaintColor.WHITE);
        return new SkinCubePaintingEvent.SetAction(paintColor);
    }

    @Override
    public void createModelProperties(BiConsumer<ResourceLocation, IItemModelProperty> builder) {
        builder.accept(ArmourersWorkshop.getResource("small"), (itemStack, world, entity) -> ToolOptions.FULL_BLOCK_MODE.get(itemStack) ? 0 : 1);
    }

    @Override
    public void appendColorHoverText(ItemStack itemStack, List<Component> tooltips) {
        IPaintColor paintColor = getItemColor(itemStack, PaintColor.WHITE);
        tooltips.addAll(ColorUtils.getColorTooltips(paintColor, true));
    }

    @Override
    public void setItemColor(ItemStack itemStack, IPaintColor paintColor) {
        ColorUtils.setColor(itemStack, paintColor);
    }

    @Override
    public IPaintColor getItemColor(ItemStack itemStack) {
        return ColorUtils.getColor(itemStack);
    }

    @Override
    public int getTintColor(ItemStack itemStack, int index) {
        if (index == 1) {
            return ColorUtils.getDisplayRGB(itemStack);
        }
        return 0xffffffff;
    }

    @Override
    public boolean isFoil(ItemStack itemStack) {
        IPaintColor paintColor = getItemColor(itemStack, PaintColor.WHITE);
        return paintColor.getPaintType() != SkinPaintTypes.NORMAL;
    }

    //    @Override
//    public void playParticle(UseOnContext context) {
////        byte[] rtbt = PaintingHelper.intToBytes(colour);
////        for (int i = 0; i < 3; i++) {
////            ParticlePaintSplash particle = new ParticlePaintSplash(world, pos, rtbt[0], rtbt[1], rtbt[2], facing);
////            ModParticleManager.spawnParticle(particle);
////        }
////        context.getLevel().addParticle();
////        void addParticle(IParticleData p_195594_1_, double p_195594_2_, double p_195594_4_, double p_195594_6_, double p_195594_8_, double p_195594_10_, double p_195594_12_);
//
//        // tool color
//        IPaintColor paintColor = ColorUtils.getColor(context.getItemInHand());
//        if (paintColor == null) {
//            paintColor = PaintColor.WHITE;
//        }
//        ILevel world = context.getLevel();
//        if (world.isClientSide()) {
//            return;
//        }
//        Direction face = context.getClickedFace();
//        Vector3d pos = Vector3d.atCenterOf(context.getClickedPos());
//        ServerWorld serverWorld = (ServerWorld)world;
//        PaintSplashParticleData data = new PaintSplashParticleData(face, paintColor);
//        serverWorld.sendParticles(data, pos.x(), pos.y(), pos.z(), 3, 0, 0, 0, 1);
//
////        if (!p_196262_2_.isClientSide) {
////            ServerWorld serverworld = (ServerWorld)p_196262_2_;
////
////            for(int i = 0; i < 2; ++i) {
////                serverworld.sendParticles(ParticleTypes.SPLASH, (double)p_196262_3_.getX() + p_196262_2_.random.nextDouble(), (double)(p_196262_3_.getY() + 1), (double)p_196262_3_.getZ() + p_196262_2_.random.nextDouble(), 1, 0.0D, 0.0D, 0.0D, 1.0D);
////                serverworld.sendParticles(ParticleTypes.BUBBLE, (double)p_196262_3_.getX() + p_196262_2_.random.nextDouble(), (double)(p_196262_3_.getY() + 1), (double)p_196262_3_.getZ() + p_196262_2_.random.nextDouble(), 1, 0.0D, 0.01D, 0.0D, 0.2D);
////            }
////        }
//    }

    //    @Override
//    public void usedOnBlockSide(ItemStack stack, EntityPlayer player, Level world, BlockPos pos, Block block, EnumFacing face, boolean spawnParticles) {
//        int colour = getToolColour(stack);
//        IPaintType paintType = getToolPaintType(stack);
//        if (!world.isRemote) {
//            IPantableBlock worldColourable = (IPantableBlock) block;
//            int oldColour = worldColourable.getColour(world, pos, face);
//            byte oldPaintType = (byte) worldColourable.getPaintType(world, pos, face).getId();
//            UndoManager.blockPainted(player, world, pos, oldColour, oldPaintType, face);
//            ((IPantableBlock)block).setColour(world, pos, colour, face);
//            ((IPantableBlock)block).setPaintType(world, pos, paintType, face);
//        } else {
//            if (spawnParticles) {
//                spawnPaintParticles(world, pos, face, colour);
//            }
//        }
//    }
    @Override
    public IRegistryObject<SoundEvent> getItemSoundEvent(UseOnContext context) {
        return ModSounds.PAINT;
    }
}