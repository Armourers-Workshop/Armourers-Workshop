package moe.plushie.armourers_workshop.builder.client.gui.advancedbuilder.panel;

import com.apple.library.coregraphics.CGRect;
import com.apple.library.coregraphics.CGSize;
import com.apple.library.uikit.UICheckBox;
import com.apple.library.uikit.UIImage;
import com.apple.library.uikit.UIScrollView;
import moe.plushie.armourers_workshop.builder.client.gui.advancedbuilder.document.DocumentEditor;
import moe.plushie.armourers_workshop.builder.client.gui.widget.PropertySettingView;
import moe.plushie.armourers_workshop.core.skin.SkinType;
import moe.plushie.armourers_workshop.core.skin.SkinTypes;
import moe.plushie.armourers_workshop.core.skin.property.SkinProperties;
import moe.plushie.armourers_workshop.core.skin.property.SkinProperty;
import moe.plushie.armourers_workshop.core.skin.serializer.document.SkinDocumentNode;
import moe.plushie.armourers_workshop.core.utils.Collections;
import moe.plushie.armourers_workshop.init.ModTextures;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

public class AdvancedSettingPanel extends AdvancedPanel {

    private static final Map<SkinType, Collection<SkinProperty<?>>> VALUES = Collections.immutableMap(builder -> {
        builder.put(SkinTypes.OUTFIT, Collections.newList(
                SkinProperty.OVERRIDE_MODEL_HEAD,
                SkinProperty.OVERRIDE_MODEL_CHEST,
                SkinProperty.OVERRIDE_MODEL_LEFT_ARM,
                SkinProperty.OVERRIDE_MODEL_RIGHT_ARM,
                SkinProperty.OVERRIDE_MODEL_LEFT_LEG,
                SkinProperty.OVERRIDE_MODEL_RIGHT_LEG,
                SkinProperty.OVERRIDE_OVERLAY_HAT,
                SkinProperty.OVERRIDE_OVERLAY_CLOAK,
                SkinProperty.OVERRIDE_OVERLAY_JACKET,
                SkinProperty.OVERRIDE_OVERLAY_LEFT_SLEEVE,
                SkinProperty.OVERRIDE_OVERLAY_RIGHT_SLEEVE,
                SkinProperty.OVERRIDE_OVERLAY_LEFT_PANTS,
                SkinProperty.OVERRIDE_OVERLAY_RIGHT_PANTS,
                SkinProperty.OVERRIDE_EQUIPMENT_HELMET,
                SkinProperty.OVERRIDE_EQUIPMENT_CHESTPLATE,
                SkinProperty.OVERRIDE_EQUIPMENT_LEGGINGS,
                SkinProperty.OVERRIDE_EQUIPMENT_BOOTS,
                SkinProperty.LIMIT_LEGS_LIMBS,
                SkinProperty.USE_OVERLAY_COLOR,
                SkinProperty.OVERRIDE_ENTITY_SIZE,
                SkinProperty.OVERRIDE_ENTITY_SIZE_WIDTH,
                SkinProperty.OVERRIDE_ENTITY_SIZE_HEIGHT,
                SkinProperty.OVERRIDE_ENTITY_SIZE_EYE_HEIGHT
        ));
        builder.put(SkinTypes.ARMOR_HEAD, Collections.newList(
                SkinProperty.OVERRIDE_MODEL_HEAD,
                SkinProperty.OVERRIDE_OVERLAY_HAT,
                SkinProperty.OVERRIDE_EQUIPMENT_HELMET,
                SkinProperty.USE_OVERLAY_COLOR
        ));
        builder.put(SkinTypes.ARMOR_CHEST, Collections.newList(
                SkinProperty.OVERRIDE_MODEL_CHEST,
                SkinProperty.OVERRIDE_MODEL_LEFT_ARM,
                SkinProperty.OVERRIDE_MODEL_RIGHT_ARM,
                SkinProperty.OVERRIDE_OVERLAY_CLOAK,
                SkinProperty.OVERRIDE_OVERLAY_JACKET,
                SkinProperty.OVERRIDE_OVERLAY_LEFT_SLEEVE,
                SkinProperty.OVERRIDE_OVERLAY_RIGHT_SLEEVE,
                SkinProperty.OVERRIDE_EQUIPMENT_CHESTPLATE,
                SkinProperty.USE_OVERLAY_COLOR
        ));
        builder.put(SkinTypes.ARMOR_FEET, Collections.newList(
                SkinProperty.OVERRIDE_MODEL_LEFT_LEG,
                SkinProperty.OVERRIDE_MODEL_RIGHT_LEG,
                SkinProperty.OVERRIDE_OVERLAY_LEFT_PANTS,
                SkinProperty.OVERRIDE_OVERLAY_RIGHT_PANTS,
                SkinProperty.OVERRIDE_EQUIPMENT_BOOTS,
                SkinProperty.USE_OVERLAY_COLOR
        ));
        builder.put(SkinTypes.ARMOR_LEGS, Collections.newList(
                SkinProperty.OVERRIDE_MODEL_LEFT_LEG,
                SkinProperty.OVERRIDE_MODEL_RIGHT_LEG,
                SkinProperty.OVERRIDE_OVERLAY_LEFT_PANTS,
                SkinProperty.OVERRIDE_OVERLAY_RIGHT_PANTS,
                SkinProperty.OVERRIDE_EQUIPMENT_LEGGINGS,
                SkinProperty.LIMIT_LEGS_LIMBS,
                SkinProperty.USE_OVERLAY_COLOR
        ));
        builder.put(SkinTypes.ARMOR_WINGS, Collections.newList(
                SkinProperty.USE_OVERLAY_COLOR
        ));
        builder.put(SkinTypes.ITEM_SWORD, Collections.newList());
        builder.put(SkinTypes.ITEM_SHIELD, Collections.newList());
        builder.put(SkinTypes.ITEM_BOW, Collections.newList());
        builder.put(SkinTypes.ITEM_TRIDENT, Collections.newList());
        builder.put(SkinTypes.ITEM_PICKAXE, Collections.newList());
        builder.put(SkinTypes.ITEM_AXE, Collections.newList());
        builder.put(SkinTypes.ITEM_SHOVEL, Collections.newList());
        builder.put(SkinTypes.ITEM_HOE, Collections.newList());
        builder.put(SkinTypes.BLOCK, Collections.newList(
                SkinProperty.BLOCK_GLOWING,
                SkinProperty.BLOCK_LADDER,
                SkinProperty.BLOCK_NO_COLLISION,
                SkinProperty.BLOCK_SEAT,
                SkinProperty.BLOCK_MULTIBLOCK,
                SkinProperty.BLOCK_BED,
                SkinProperty.BLOCK_ENDER_INVENTORY,
                SkinProperty.BLOCK_INVENTORY,
                SkinProperty.BLOCK_INVENTORY_WIDTH,
                SkinProperty.BLOCK_INVENTORY_HEIGHT
        ));
        builder.put(SkinTypes.HORSE, Collections.newList(
                SkinProperty.OVERRIDE_MODEL_HEAD,
                SkinProperty.OVERRIDE_MODEL_CHEST,
                SkinProperty.OVERRIDE_MODEL_LEFT_FRONT_LEG,
                SkinProperty.OVERRIDE_MODEL_RIGHT_FRONT_LEG,
                SkinProperty.OVERRIDE_MODEL_LEFT_HIND_LEG,
                SkinProperty.OVERRIDE_MODEL_RIGHT_HIND_LEG,
                SkinProperty.OVERRIDE_MODEL_TAIL,
                SkinProperty.OVERRIDE_EQUIPMENT_CHESTPLATE,
                SkinProperty.USE_OVERLAY_COLOR,
                SkinProperty.OVERRIDE_ENTITY_SIZE,
                SkinProperty.OVERRIDE_ENTITY_SIZE_WIDTH,
                SkinProperty.OVERRIDE_ENTITY_SIZE_HEIGHT,
                SkinProperty.OVERRIDE_ENTITY_SIZE_EYE_HEIGHT
        ));
        builder.put(SkinTypes.MINECART, Collections.newList(
                SkinProperty.USE_OVERLAY_COLOR,
                SkinProperty.OVERRIDE_ENTITY_SIZE,
                SkinProperty.OVERRIDE_ENTITY_SIZE_WIDTH,
                SkinProperty.OVERRIDE_ENTITY_SIZE_HEIGHT,
                SkinProperty.OVERRIDE_ENTITY_SIZE_EYE_HEIGHT
        ));
        builder.put(SkinTypes.BOAT, Collections.newList(
                SkinProperty.USE_OVERLAY_COLOR,
                SkinProperty.OVERRIDE_ENTITY_SIZE,
                SkinProperty.OVERRIDE_ENTITY_SIZE_WIDTH,
                SkinProperty.OVERRIDE_ENTITY_SIZE_HEIGHT,
                SkinProperty.OVERRIDE_ENTITY_SIZE_EYE_HEIGHT
        ));
    });


    private SkinProperties properties;
    private final ArrayList<UICheckBox> boxes = new ArrayList<>();
    private PropertySettingView settingView;
    private final UIScrollView scrollView = new UIScrollView(CGRect.ZERO);
    private CGSize cachedSize = CGSize.ZERO;

    public AdvancedSettingPanel(DocumentEditor editor) {
        super(editor);
        this.barItem.setImage(UIImage.of(ModTextures.TAB_ICONS).uv(208, 0).fixed(16, 16).build());
        this.setup();
    }

    @Override
    public void layoutSubviews() {
        super.layoutSubviews();
        var size = bounds().size();
        if (settingView != null && !cachedSize.equals(size)) {
            var rect = settingView.frame().copy();
            rect.setWidth(size.width - 20);
            settingView.setFrame(rect);
            cachedSize = size;
        }
    }

    private void setup() {
        scrollView.setFrame(bounds());
        scrollView.setAutoresizingMask(AutoresizingMask.flexibleWidth | AutoresizingMask.flexibleHeight);
        insertViewAtIndex(scrollView, 0);
        editor.connector().addListener(this::update);
    }

    private void update(SkinDocumentNode node) {
        if (properties == document.properties()) {
            return;
        }
        properties = document.properties();
        addProperties(document.type().skinType());
    }

    private void addProperties(SkinType skinType) {
        if (settingView != null) {
            settingView.removeFromSuperview();
        }
        var rect = scrollView.frame();
        var properties = VALUES.get(skinType);
        if (properties == null || properties.isEmpty()) {
            return;
        }
        settingView = new PropertySettingView(new CGRect(10, 10, rect.width - 20, 0), properties) {
            @Override
            public void beginEditing() {
                editor.beginEditing();
            }

            @Override
            public <T> void putValue(SkinProperty<T> property, T value) {
                document.put(property, value);
            }

            @Override
            public <T> T getValue(SkinProperty<T> property) {
                return document.get(property);
            }

            @Override
            public void endEditing() {
                editor.endEditing();
            }

        };
        scrollView.addSubview(settingView);
        scrollView.setContentSize(new CGSize(0, settingView.frame().maxY() + 10));
    }
}
