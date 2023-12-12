package moe.plushie.armourers_workshop.core.skin;

import moe.plushie.armourers_workshop.api.skin.ISkin;
import moe.plushie.armourers_workshop.api.skin.ISkinType;
import moe.plushie.armourers_workshop.core.data.transform.SkinItemTransforms;
import moe.plushie.armourers_workshop.core.skin.part.SkinPart;
import moe.plushie.armourers_workshop.core.skin.property.SkinProperties;
import moe.plushie.armourers_workshop.core.skin.property.SkinProperty;
import moe.plushie.armourers_workshop.core.skin.property.SkinSettings;
import moe.plushie.armourers_workshop.core.skin.serializer.SkinSerializer;
import moe.plushie.armourers_workshop.utils.ThreadUtils;
import moe.plushie.armourers_workshop.utils.math.Rectangle3i;
import moe.plushie.armourers_workshop.utils.texture.SkinPaintData;
import moe.plushie.armourers_workshop.utils.texture.SkinPreviewData;
import net.minecraft.core.BlockPos;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class Skin implements ISkin {

    private final int id = ThreadUtils.SKIN_COUNTER.incrementAndGet();

    private final SkinSettings settings;
    private final SkinProperties properties;
    private final ISkinType skinType;
    private final List<SkinPart> parts;

    private Object blobs;

    private int version;
    private boolean previewMode = false;

    private final SkinPaintData paintData;
    private final SkinPreviewData previewData;

    public Skin(ISkinType skinType, SkinProperties properties, SkinSettings settings, SkinPaintData paintData, SkinPreviewData previewData, Collection<SkinPart> skinParts) {
        this.properties = properties;
        this.settings = settings;
        this.skinType = skinType;
        this.paintData = paintData;
        this.previewData = previewData;
        this.parts = new ArrayList<>(skinParts);
    }

    public int getId() {
        return id;
    }

    public SkinSettings getSettings() {
        return settings;
    }

    public SkinProperties getProperties() {
        return properties;
    }

    public HashMap<BlockPos, Rectangle3i> getBlockBounds() {
        HashMap<BlockPos, Rectangle3i> blockBounds = new HashMap<>();
        if (skinType != SkinTypes.BLOCK) {
            return blockBounds;
        }
        for (SkinPart part : getParts()) {
            HashMap<BlockPos, Rectangle3i> partBlockBounds = part.getBlockBounds();
            if (partBlockBounds != null) {
                blockBounds.putAll(partBlockBounds);
            }
        }
        return blockBounds;
    }

    public int getModelCount() {
        int count = 0;
        for (SkinPart part : parts) {
            count += part.getModelCount();
        }
        return count;
    }

    public int getPartCount() {
        return parts.size();
    }

    @Override
    public ISkinType getType() {
        return skinType;
    }

    @Nullable
    public SkinPaintData getPaintData() {
        return paintData;
    }

    public SkinPreviewData getPreviewData() {
        return previewData;
    }

    @Override
    public List<SkinPart> getParts() {
        return parts;
    }

    public SkinItemTransforms getItemTransforms() {
        return settings.getItemTransforms();
    }

    public String getCustomName() {
        return properties.get(SkinProperty.ALL_CUSTOM_NAME);
    }

    public String getAuthorName() {
        return properties.get(SkinProperty.ALL_AUTHOR_NAME);
    }

    public String getFlavourText() {
        return properties.get(SkinProperty.ALL_FLAVOUR_TEXT);
    }

    public int getVersion() {
        return version;
    }

    @Override
    public String toString() {
        String returnString = "Skin [properties=" + properties + ", type=" + skinType.getRegistryName();
        if (paintData != null) {
            returnString += ", paintData=" + paintData;
        }
        returnString += "]";
        return returnString;
    }

    public Collection<SkinMarker> getMarkers() {
        ArrayList<SkinMarker> markers = new ArrayList<>();
        for (SkinPart part : parts) {
            markers.addAll(part.getMarkers());
        }
        return markers;
    }

    public Object getBlobs() {
        return blobs;
    }

    public static class Builder {

        private final ISkinType skinType;
        private final ArrayList<SkinPart> skinParts = new ArrayList<>();

        private SkinPaintData paintData;
        private SkinPreviewData previewData;
        private SkinSettings settings = new SkinSettings();
        private SkinProperties properties = SkinProperties.EMPTY;
        private Object blobs;
        private int version = SkinSerializer.Versions.V13;

        public Builder(ISkinType skinType) {
            this.skinType = skinType;
            // for outfit skin, editable is not allowed by default.
            if (this.skinType == SkinTypes.OUTFIT) {
                this.settings.setEditable(false);
            }
        }

        public Builder properties(SkinProperties properties) {
            if (properties != null) {
                this.properties = properties;
            }
            return this;
        }

        public Builder settings(SkinSettings settings) {
            if (settings != null) {
                this.settings = settings;
            }
            return this;
        }

        public Builder paintData(SkinPaintData paintData) {
            this.paintData = paintData;
            return this;
        }

        public Builder previewData(SkinPreviewData previewData) {
            this.previewData = previewData;
            return this;
        }

        public Builder parts(Collection<SkinPart> parts) {
            if (parts != null) {
                this.skinParts.addAll(parts);
            }
            return this;
        }

        public Builder blobs(Object blobs) {
            this.blobs = blobs;
            return this;
        }

        public Builder version(int version) {
            this.version = version;
            return this;
        }

        public Skin build() {
            updateSettingIfNeeded();
            updatePropertiesIfNeeded();
            bindPropertiesIfNeeded();
            Skin skin = new Skin(skinType, properties, settings, paintData, previewData, skinParts);
            skin.version = version;
            skin.blobs = blobs;
            return skin;
        }

        private void updateSettingIfNeeded() {
            // when skin only provided preview data and not any part data,
            // this indicates the skin is in preview mod
            settings.setPreviewMode(previewData != null && skinParts.isEmpty());
        }

        private void updatePropertiesIfNeeded() {
            // Update skin properties.
            if (properties.get(SkinProperty.OVERRIDE_MODEL_ALL)) {
                if (skinType == SkinTypes.ARMOR_HEAD) {
                    properties.put(SkinProperty.OVERRIDE_MODEL_HEAD, true);
                }
                if (skinType == SkinTypes.ARMOR_CHEST) {
                    properties.put(SkinProperty.OVERRIDE_MODEL_CHEST, true);
                    properties.put(SkinProperty.OVERRIDE_MODEL_LEFT_ARM, true);
                    properties.put(SkinProperty.OVERRIDE_MODEL_RIGHT_ARM, true);
                }
                if (skinType == SkinTypes.ARMOR_LEGS) {
                    properties.put(SkinProperty.OVERRIDE_MODEL_LEFT_LEG, true);
                    properties.put(SkinProperty.OVERRIDE_MODEL_RIGHT_LEG, true);
                }
                if (skinType == SkinTypes.ARMOR_FEET) {
                    properties.put(SkinProperty.OVERRIDE_MODEL_LEFT_LEG, true);
                    properties.put(SkinProperty.OVERRIDE_MODEL_RIGHT_LEG, true);
                }
                properties.remove(SkinProperty.OVERRIDE_MODEL_ALL);
            }
            if (properties.get(SkinProperty.OVERRIDE_OVERLAY_ALL)) {
                if (skinType == SkinTypes.ARMOR_HEAD) {
                    properties.put(SkinProperty.OVERRIDE_OVERLAY_HAT, true);
                }
                if (skinType == SkinTypes.ARMOR_CHEST) {
                    properties.put(SkinProperty.OVERRIDE_OVERLAY_JACKET, true);
                    properties.put(SkinProperty.OVERRIDE_OVERLAY_LEFT_SLEEVE, true);
                    properties.put(SkinProperty.OVERRIDE_OVERLAY_RIGHT_SLEEVE, true);
                }
                if (skinType == SkinTypes.ARMOR_LEGS) {
                    properties.put(SkinProperty.OVERRIDE_OVERLAY_LEFT_PANTS, true);
                    properties.put(SkinProperty.OVERRIDE_OVERLAY_RIGHT_PANTS, true);
                }
                if (skinType == SkinTypes.ARMOR_FEET) {
                    properties.put(SkinProperty.OVERRIDE_OVERLAY_LEFT_PANTS, true);
                    properties.put(SkinProperty.OVERRIDE_OVERLAY_RIGHT_PANTS, true);
                }
                properties.remove(SkinProperty.OVERRIDE_OVERLAY_ALL);
            }
        }

        private void bindPropertiesIfNeeded() {
            // bind properties to part.
            for (SkinPart part : skinParts) {
                part.setProperties(properties);
            }
            String skinIndexs = properties.get(SkinProperty.OUTFIT_PART_INDEXS);
            if (skinIndexs != null && !skinIndexs.equals("")) {
                String[] split = skinIndexs.split(":");
                int partIndex = 0;
                for (int skinIndex = 0; skinIndex < split.length; ++skinIndex) {
                    SkinProperties stub = properties.slice(skinIndex);
                    int count = Integer.parseInt(split[skinIndex]);
                    while (partIndex < count) {
                        if (partIndex < skinParts.size()) {
                            SkinPart skinPart = skinParts.get(partIndex);
                            skinPart.setProperties(stub);
                        }
                        partIndex += 1;
                    }
                    partIndex = count;
                }
            }
        }
    }
}
