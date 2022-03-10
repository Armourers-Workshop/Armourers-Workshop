package moe.plushie.armourers_workshop.core.item;

import moe.plushie.armourers_workshop.core.AWConfig;
import moe.plushie.armourers_workshop.core.base.AWItems;
import moe.plushie.armourers_workshop.core.render.bake.BakedSkin;
import moe.plushie.armourers_workshop.core.render.bake.SkinBakery;
import moe.plushie.armourers_workshop.core.skin.Skin;
import moe.plushie.armourers_workshop.core.skin.SkinDescriptor;
import moe.plushie.armourers_workshop.core.skin.SkinLoader;
import moe.plushie.armourers_workshop.core.skin.SkinTypes;
import moe.plushie.armourers_workshop.core.skin.cube.SkinCubes;
import moe.plushie.armourers_workshop.core.utils.AWKeyBindings;
import moe.plushie.armourers_workshop.core.utils.TranslateUtils;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.util.Strings;

import javax.annotation.Nullable;
import java.util.ArrayList;

@SuppressWarnings("NullableProblems")
public class SkinItem extends Item {

    public SkinItem(Item.Properties properties) {
        super(properties);
    }

    @OnlyIn(Dist.CLIENT)
    public static ArrayList<ITextComponent> getTooltip(ItemStack itemStack) {
        boolean isItemOwner = itemStack.getItem() == AWItems.SKIN;
        ArrayList<ITextComponent> tooltip = new ArrayList<>();
        SkinDescriptor descriptor = SkinDescriptor.of(itemStack);
        if (descriptor.isEmpty()) {
            if (isItemOwner) {
                tooltip.add(TranslateUtils.subtitle("item.armourers_workshop.rollover.skinInvalidItem"));
            }
            return tooltip;
        }
        BakedSkin bakedSkin = SkinBakery.getInstance().loadSkin(descriptor);
        if (bakedSkin == null) {
            tooltip.add(TranslateUtils.subtitle("item.armourers_workshop.rollover.skindownloading", descriptor.getIdentifier()));
            return tooltip;
        }
        Skin skin = bakedSkin.getSkin();

        if (!isItemOwner) {
            tooltip.add(TranslateUtils.subtitle("item.armourers_workshop.rollover.hasSkin"));
            if (AWConfig.tooltipSkinName && Strings.isNotBlank(skin.getCustomName())) {
                tooltip.add(TranslateUtils.subtitle("item.armourers_workshop.rollover.skinName", skin.getCustomName().trim()));
            }
        }

        if (AWConfig.tooltipSkinAuthor && Strings.isNotBlank(skin.getAuthorName())) {
            tooltip.add(TranslateUtils.subtitle("item.armourers_workshop.rollover.skinAuthor", skin.getAuthorName().trim()));
        }

        if (AWConfig.tooltipSkinType) {
            TextComponent textComponent = TranslateUtils.subtitle("skinType." + skin.getType().getRegistryName());
            tooltip.add(TranslateUtils.subtitle("item.armourers_workshop.rollover.skinType", textComponent));
        }

        if (AWConfig.tooltipFlavour && Strings.isNotBlank(skin.getFlavourText())) {
            tooltip.add(TranslateUtils.title("item.armourers_workshop.rollover.flavour", skin.getFlavourText().trim()));
        }

        if (AWConfig.tooltipDebug && Screen.hasShiftDown()) {
            tooltip.add(TranslateUtils.subtitle("item.armourers_workshop.rollover.skinIdentifier", descriptor.getIdentifier()));
            tooltip.add(TranslateUtils.subtitle("item.armourers_workshop.rollover.skinTotalCubes", skin.getTotalCubes()));
            tooltip.add(TranslateUtils.subtitle("item.armourers_workshop.rollover.skinNumCubes", skin.getTotalOfCubeType(SkinCubes.SOLID)));
            tooltip.add(TranslateUtils.subtitle("item.armourers_workshop.rollover.skinNumCubesGlowing", skin.getTotalOfCubeType(SkinCubes.GLOWING)));
            tooltip.add(TranslateUtils.subtitle("item.armourers_workshop.rollover.skinNumCubesGlass", skin.getTotalOfCubeType(SkinCubes.GLASS)));
            tooltip.add(TranslateUtils.subtitle("item.armourers_workshop.rollover.skinNumCubesGlassGlowing", skin.getTotalOfCubeType(SkinCubes.GLASS_GLOWING)));
            tooltip.add(TranslateUtils.subtitle("item.armourers_workshop.rollover.skinPaintData", skin.hasPaintData()));
            tooltip.add(TranslateUtils.subtitle("item.armourers_workshop.rollover.skinMarkerCount", skin.getMarkerCount()));
//            tooltip.add(TranslateUtils.translate("item.armourers_workshop.rollover.skinDyeCount", skin.getSkinDye().getNumberOfDyes()));
//            tooltip.add(TranslateUtils.translate("item.armourers_workshop.rollover.skinProperties"));
//            for (String prop : skin.getProperties().getPropertiesList()) {
//                tooltip.add(TranslateUtils.literal(" " + prop));
//            }
        } else if (AWConfig.tooltipDebug) {
            tooltip.add(TranslateUtils.subtitle("item.armourers_workshop.rollover.skinHoldShiftForInfo"));
        }

        // Skin ID error.
//        if (identifier.hasLocalId()) {
//            if (identifier.getSkinLocalId() != data.lightHash()) {
//        tooltip.add(TranslateUtils.translate("item.armourers_workshop.rollover.skinIdError1"));
//        tooltip.add(TranslateUtils.translate("item.armourers_workshop.rollover.skinIdError2"));
//            }
//        }

        if (AWConfig.tooltipOpenWardrobe && isItemOwner) {
            ITextComponent keyName = AWKeyBindings.OPEN_WARDROBE_KEY.getTranslatedKeyMessage();
            tooltip.add(TranslateUtils.subtitle("item.armourers_workshop.rollover.skinOpenWardrobe", keyName));
        }

        return tooltip;
    }

    @OnlyIn(Dist.CLIENT)
    public static float getIconIndex(ItemStack itemStack, @Nullable ClientWorld world, @Nullable LivingEntity entity) {
        SkinDescriptor descriptor = SkinDescriptor.of(itemStack);
        BakedSkin bakedSkin = SkinBakery.getInstance().loadSkin(descriptor);
        if (bakedSkin != null) {
            return 0;
        }
        return descriptor.getType().getId();
    }

    @Override
    public ActionResultType useOn(ItemUseContext context) {
        ItemStack itemStack = context.getItemInHand();
        SkinDescriptor descriptor = SkinDescriptor.of(itemStack);
        if (descriptor.getType() == SkinTypes.BLOCK) {
            return this.place(itemStack, descriptor, new BlockItemUseContext(context));
        }
        return ActionResultType.PASS;
    }

    public ActionResultType place(ItemStack itemStack, SkinDescriptor descriptor, BlockItemUseContext context) {
        if (!context.canPlace()) {
            return ActionResultType.FAIL;
        }
//        } else {
//            BlockItemUseContext blockitemusecontext = this.updatePlacementContext(p_195942_1_);
//            if (blockitemusecontext == null) {
//                return ActionResultType.FAIL;
//            } else {
//                BlockState blockstate = this.getPlacementState(blockitemusecontext);
//                if (blockstate == null) {
//                    return ActionResultType.FAIL;
//                } else if (!this.placeBlock(blockitemusecontext, blockstate)) {
//                    return ActionResultType.FAIL;
//                } else {
//                    BlockPos blockpos = blockitemusecontext.getClickedPos();
//                    World world = blockitemusecontext.getLevel();
//                    PlayerEntity playerentity = blockitemusecontext.getPlayer();
//                    ItemStack itemstack = blockitemusecontext.getItemInHand();
//                    BlockState blockstate1 = world.getBlockState(blockpos);
//                    Block block = blockstate1.getBlock();
//                    if (block == blockstate.getBlock()) {
//                        blockstate1 = this.updateBlockStateFromTag(blockpos, world, itemstack, blockstate1);
//                        this.updateCustomBlockEntityTag(blockpos, world, playerentity, itemstack, blockstate1);
//                        block.setPlacedBy(world, blockpos, blockstate1, playerentity, itemstack);
//                        if (playerentity instanceof ServerPlayerEntity) {
//                            CriteriaTriggers.PLACED_BLOCK.trigger((ServerPlayerEntity)playerentity, blockpos, itemstack);
//                        }
//                    }
//
//                    SoundType soundtype = blockstate1.getSoundType(world, blockpos, p_195942_1_.getPlayer());
//                    world.playSound(playerentity, blockpos, this.getPlaceSound(blockstate1, world, blockpos, p_195942_1_.getPlayer()), SoundCategory.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);
//                    if (playerentity == null || !playerentity.abilities.instabuild) {
//                        itemstack.shrink(1);
//                    }
//
//                    return ActionResultType.sidedSuccess(world.isClientSide);
//                }
//            }
//        }
        return ActionResultType.PASS;
    }


    @Override
    public ITextComponent getName(ItemStack itemStack) {
        Skin skin = SkinLoader.getInstance().getSkin(itemStack);
        if (skin != null && !skin.getCustomName().trim().isEmpty()) {
            return new StringTextComponent(skin.getCustomName());
        }
        return super.getName(itemStack);
    }
}
