package moe.plushie.armourers_workshop.common.item;

import moe.plushie.armourers_workshop.core.bake.BakedSkin;
import moe.plushie.armourers_workshop.core.config.SkinConfig;
import moe.plushie.armourers_workshop.core.skin.cube.SkinCubes;
import moe.plushie.armourers_workshop.core.skin.data.Skin;
import moe.plushie.armourers_workshop.core.skin.data.SkinDescriptor;
import moe.plushie.armourers_workshop.core.utils.Keybindings;
import moe.plushie.armourers_workshop.core.utils.SkinCore;
import moe.plushie.armourers_workshop.core.utils.TranslateUtils;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.util.Strings;

import java.util.ArrayList;

public class SkinItem extends Item {

    public SkinItem(Item.Properties properties) {
        super(properties);
    }

    @OnlyIn(Dist.CLIENT)
    public static ArrayList<ITextComponent> getTooltip(ItemStack itemStack) {
        boolean isOwner = itemStack.getItem() == SkinItems.SKIN.get();
        ArrayList<ITextComponent> tooltip = new ArrayList<>();
        SkinDescriptor descriptor = SkinDescriptor.of(itemStack);
        if (descriptor.isEmpty()) {
            if (isOwner) {
                tooltip.add(TranslateUtils.translate("item.armourers_workshop.rollover.skinInvalidItem"));
            }
            return tooltip;
        }
        BakedSkin bakedSkin = SkinCore.bakery.loadSkin(descriptor);
        if (bakedSkin == null) {
            tooltip.add(TranslateUtils.translate("item.armourers_workshop.rollover.skindownloading", descriptor.getIdentifier()));
            return tooltip;
        }

        Skin skin = bakedSkin.getSkin();

        if (!isOwner) {
            tooltip.add(TranslateUtils.translate("item.armourers_workshop.rollover.hasSkin"));
            if (SkinConfig.tooltipSkinName && Strings.isNotBlank(skin.getCustomName())) {
                tooltip.add(TranslateUtils.translate("item.armourers_workshop.rollover.skinName", skin.getCustomName().trim()));
            }
        }

        if (SkinConfig.tooltipSkinAuthor && Strings.isNotBlank(skin.getAuthorName())) {
            tooltip.add(TranslateUtils.translate("item.armourers_workshop.rollover.skinAuthor", skin.getAuthorName().trim()));
        }

        if (SkinConfig.tooltipSkinType) {
            TextComponent textComponent = TranslateUtils.translate("skinType." + skin.getType().getRegistryName());
            tooltip.add(TranslateUtils.translate("item.armourers_workshop.rollover.skinType", textComponent));
        }

        if (SkinConfig.tooltipFlavour && Strings.isNotBlank(skin.getFlavourText())) {
            tooltip.add(TranslateUtils.translate("item.armourers_workshop.rollover.flavour", skin.getFlavourText().trim()));
        }

        if (SkinConfig.tooltipDebug && Screen.hasShiftDown()) {
            tooltip.add(TranslateUtils.translate("item.armourers_workshop.rollover.skinIdentifier", descriptor.getIdentifier()));
            tooltip.add(TranslateUtils.translate("item.armourers_workshop.rollover.skinTotalCubes", skin.getTotalCubes()));
            tooltip.add(TranslateUtils.translate("item.armourers_workshop.rollover.skinNumCubes", skin.getTotalOfCubeType(SkinCubes.SOLID)));
            tooltip.add(TranslateUtils.translate("item.armourers_workshop.rollover.skinNumCubesGlowing", skin.getTotalOfCubeType(SkinCubes.GLOWING)));
            tooltip.add(TranslateUtils.translate("item.armourers_workshop.rollover.skinNumCubesGlass", skin.getTotalOfCubeType(SkinCubes.GLASS)));
            tooltip.add(TranslateUtils.translate("item.armourers_workshop.rollover.skinNumCubesGlassGlowing", skin.getTotalOfCubeType(SkinCubes.GLASS_GLOWING)));
            tooltip.add(TranslateUtils.translate("item.armourers_workshop.rollover.skinPaintData", skin.hasPaintData()));
            tooltip.add(TranslateUtils.translate("item.armourers_workshop.rollover.skinMarkerCount", skin.getMarkerCount()));
//                    tooltip.add(TranslateUtils.translate("item.armourers_workshop.rollover.skinDyeCount", skin.getSkinDye().getNumberOfDyes()));
            tooltip.add(TranslateUtils.translate("item.armourers_workshop.rollover.skinProperties"));
            for (String prop : skin.getProperties().getPropertiesList()) {
                tooltip.add(TranslateUtils.literal(" " + prop));
            }
        } else if (SkinConfig.tooltipDebug) {
            tooltip.add(TranslateUtils.translate("item.armourers_workshop.rollover.skinHoldShiftForInfo"));
        }

        // Skin ID error.
//        if (identifier.hasLocalId()) {
//            if (identifier.getSkinLocalId() != data.lightHash()) {
//        tooltip.add(TranslateUtils.translate("item.armourers_workshop.rollover.skinIdError1"));
//        tooltip.add(TranslateUtils.translate("item.armourers_workshop.rollover.skinIdError2"));
//            }
//        }

        if (SkinConfig.tooltipOpenWardrobe) {
            String keyName = Keybindings.OPEN_WARDROBE.getTranslatedKeyMessage().getContents().toUpperCase();
            tooltip.add(TranslateUtils.translate("item.armourers_workshop.rollover.skinOpenWardrobe", keyName));
        }

        return tooltip;
    }

    @Override
    public ITextComponent getName(ItemStack itemStack) {
        Skin skin = SkinCore.loader.getSkin(itemStack);
        if (skin != null) {
            return new StringTextComponent(skin.getCustomName());
        }
        return TranslateUtils.translate("item.armourers_workshop.rollover.skinInvalidItem");
    }
}
