package moe.plushie.armourers_workshop.core.item;

import moe.plushie.armourers_workshop.core.base.AWConfig;
import moe.plushie.armourers_workshop.core.AWCore;
import moe.plushie.armourers_workshop.core.base.AWItems;
import moe.plushie.armourers_workshop.core.render.bake.BakedSkin;
import moe.plushie.armourers_workshop.core.skin.Skin;
import moe.plushie.armourers_workshop.core.skin.cube.SkinCubes;
import moe.plushie.armourers_workshop.core.skin.data.SkinDescriptor;
import moe.plushie.armourers_workshop.core.utils.AWKeyBindings;
import moe.plushie.armourers_workshop.core.utils.TranslateUtils;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextComponent;
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
                tooltip.add(TranslateUtils.translate("item.armourers_workshop.rollover.skinInvalidItem"));
            }
            return tooltip;
        }
        BakedSkin bakedSkin = AWCore.bakery.loadSkin(descriptor);
        if (bakedSkin == null) {
            tooltip.add(TranslateUtils.translate("item.armourers_workshop.rollover.skindownloading", descriptor.getIdentifier()));
            return tooltip;
        }
        Skin skin = bakedSkin.getSkin();

        if (!isItemOwner) {
            tooltip.add(TranslateUtils.translate("item.armourers_workshop.rollover.hasSkin"));
            if (AWConfig.tooltipSkinName && Strings.isNotBlank(skin.getCustomName())) {
                tooltip.add(TranslateUtils.translate("item.armourers_workshop.rollover.skinName", skin.getCustomName().trim()));
            }
        }

        if (AWConfig.tooltipSkinAuthor && Strings.isNotBlank(skin.getAuthorName())) {
            tooltip.add(TranslateUtils.translate("item.armourers_workshop.rollover.skinAuthor", skin.getAuthorName().trim()));
        }

        if (AWConfig.tooltipSkinType) {
            TextComponent textComponent = TranslateUtils.translate("skinType." + skin.getType().getRegistryName());
            tooltip.add(TranslateUtils.translate("item.armourers_workshop.rollover.skinType", textComponent));
        }

        if (AWConfig.tooltipFlavour && Strings.isNotBlank(skin.getFlavourText())) {
            tooltip.add(TranslateUtils.translate("item.armourers_workshop.rollover.flavour", skin.getFlavourText().trim()));
        }

        if (AWConfig.tooltipDebug && Screen.hasShiftDown()) {
            tooltip.add(TranslateUtils.translate("item.armourers_workshop.rollover.skinIdentifier", descriptor.getIdentifier()));
            tooltip.add(TranslateUtils.translate("item.armourers_workshop.rollover.skinTotalCubes", skin.getTotalCubes()));
            tooltip.add(TranslateUtils.translate("item.armourers_workshop.rollover.skinNumCubes", skin.getTotalOfCubeType(SkinCubes.SOLID)));
            tooltip.add(TranslateUtils.translate("item.armourers_workshop.rollover.skinNumCubesGlowing", skin.getTotalOfCubeType(SkinCubes.GLOWING)));
            tooltip.add(TranslateUtils.translate("item.armourers_workshop.rollover.skinNumCubesGlass", skin.getTotalOfCubeType(SkinCubes.GLASS)));
            tooltip.add(TranslateUtils.translate("item.armourers_workshop.rollover.skinNumCubesGlassGlowing", skin.getTotalOfCubeType(SkinCubes.GLASS_GLOWING)));
            tooltip.add(TranslateUtils.translate("item.armourers_workshop.rollover.skinPaintData", skin.hasPaintData()));
            tooltip.add(TranslateUtils.translate("item.armourers_workshop.rollover.skinMarkerCount", skin.getMarkerCount()));
//            tooltip.add(TranslateUtils.translate("item.armourers_workshop.rollover.skinDyeCount", skin.getSkinDye().getNumberOfDyes()));
//            tooltip.add(TranslateUtils.translate("item.armourers_workshop.rollover.skinProperties"));
//            for (String prop : skin.getProperties().getPropertiesList()) {
//                tooltip.add(TranslateUtils.literal(" " + prop));
//            }
        } else if (AWConfig.tooltipDebug) {
            tooltip.add(TranslateUtils.translate("item.armourers_workshop.rollover.skinHoldShiftForInfo"));
        }

        // Skin ID error.
//        if (identifier.hasLocalId()) {
//            if (identifier.getSkinLocalId() != data.lightHash()) {
//        tooltip.add(TranslateUtils.translate("item.armourers_workshop.rollover.skinIdError1"));
//        tooltip.add(TranslateUtils.translate("item.armourers_workshop.rollover.skinIdError2"));
//            }
//        }

        if (AWConfig.tooltipOpenWardrobe) {
            ITextComponent keyName = AWKeyBindings.OPEN_WARDROBE_KEY.getTranslatedKeyMessage();
            tooltip.add(TranslateUtils.translate("item.armourers_workshop.rollover.skinOpenWardrobe", keyName));
        }

        return tooltip;
    }

    @OnlyIn(Dist.CLIENT)
    public static float getIconIndex(ItemStack itemStack, @Nullable ClientWorld world, @Nullable LivingEntity entity) {
        SkinDescriptor descriptor = SkinDescriptor.of(itemStack);
        BakedSkin bakedSkin = AWCore.bakery.loadSkin(descriptor);
        if (bakedSkin != null) {
            return 0;
        }
        return descriptor.getType().getId();
    }


    @Override
    public ITextComponent getName(ItemStack itemStack) {
        Skin skin = AWCore.loader.getSkin(itemStack);
        if (skin != null && !skin.getCustomName().trim().isEmpty()) {
            return new StringTextComponent(skin.getCustomName());
        }
        return super.getName(itemStack);
    }
}
