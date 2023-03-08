package moe.plushie.armourers_workshop.core.skin;

import moe.plushie.armourers_workshop.api.skin.ISkin;
import moe.plushie.armourers_workshop.api.skin.ISkinPartType;
import moe.plushie.armourers_workshop.api.skin.ISkinType;
import moe.plushie.armourers_workshop.core.skin.data.SkinMarker;
import moe.plushie.armourers_workshop.core.skin.part.SkinPart;
import moe.plushie.armourers_workshop.core.skin.property.SkinProperties;
import moe.plushie.armourers_workshop.core.skin.property.SkinProperty;
import moe.plushie.armourers_workshop.utils.ThreadUtils;
import moe.plushie.armourers_workshop.utils.math.Rectangle3i;
import moe.plushie.armourers_workshop.utils.texture.SkinPaintData;
import net.minecraft.core.BlockPos;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class Skin implements ISkin {

    private final int id = ThreadUtils.SKIN_COUNTER.incrementAndGet();

    private final SkinProperties properties;
    private final ISkinType skinType;
    private final ArrayList<SkinPart> parts;

    //    public SkinModelTexture skinModelTexture;
    private final SkinPaintData paintData;

    public Skin(ISkinType skinType, SkinProperties properties, SkinPaintData paintData, ArrayList<SkinPart> skinParts) {
        this.properties = properties;
        this.skinType = skinType;
        this.paintData = paintData;
        this.parts = skinParts;
    }

    public int getId() {
        return id;
    }

    public SkinProperties getProperties() {
        return properties;
    }

    public HashMap<BlockPos, Rectangle3i> getBlockBounds() {
        if (skinType != SkinTypes.BLOCK) {
            return null;
        }
        HashMap<BlockPos, Rectangle3i> blockBounds = new HashMap<>();
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
        for (int i = 0; i < parts.size(); i++) {
            count += parts.get(i).getModelCount();
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

    @Override
    public List<SkinPart> getParts() {
        return parts;
    }


    public boolean isModelOverridden(ISkinPartType partType) {
        for (SkinPart part : parts) {
            if (part.getType() == partType) {
                return part.getType().isModelOverridden(properties);
            }
        }
        return false;
    }

    public boolean requiresAdvanceFeatures() {
//        for (SkinPart skinPart : getParts()) {
//            if (skinPart.getType() == SkinPartTypes.ADVANCED) {
//                return true;
//            }
//            if (skinPart.getTransform() != SkinTransform.IDENTIFIER) {
//                return true;
//            }
//        }
        return false;
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
        return null;
    }

    public static class Builder {

        private final ISkinType skinType;
        private final ArrayList<SkinPart> skinParts = new ArrayList<>();

        private SkinPaintData paintData;
        private SkinProperties properties = SkinProperties.EMPTY;
        private Object blobs;

        public Builder(ISkinType skinType) {
            this.skinType = skinType;
        }

        public Builder properties(SkinProperties properties) {
            this.properties = properties;
            return this;
        }

        public Builder paintData(SkinPaintData paintData) {
            this.paintData = paintData;
            return this;
        }

        public Builder parts(Collection<SkinPart> parts) {
            this.skinParts.addAll(parts);
            return this;
        }

        public Builder blobs(Object blobs) {
            this.blobs = blobs;
            return this;
        }

        public Skin build() {
            updatePropertiesIfNeeded();
            bindPropertiesIfNeeded();
//            test();
            return new Skin(skinType, properties, paintData, skinParts);
        }

//        private void test() {
//            String name = properties.get(SkinProperty.ALL_CUSTOM_NAME);
//            if (!name.equals("TAS") || skinParts.size() != 1) {
//                return;
//            }
//            SkinPart skinPart = skinParts.get(0);
//            for (int i = 0; i < 2; ++i) {
//                SkinPart.Builder builder = new SkinPart.Builder(skinPart.getType());
//                builder.id(skinParts.size());
//                builder.parent(skinPart.getId());
//                builder.cubes(skinPart.getCubeData());
//                builder.transform(SkinTransform.createRotationTransform(new Vector3f(30 * (i + 1), 0, 0)));
//                skinParts.add(builder.build());
//            }
//        }

        private void updatePropertiesIfNeeded() {
            // Update skin properties.
            if (properties.get(SkinProperty.MODEL_OVERRIDE)) {
                if (skinType == SkinTypes.ARMOR_HEAD) {
                    properties.put(SkinProperty.MODEL_OVERRIDE_HEAD, true);
                }
                if (skinType == SkinTypes.ARMOR_CHEST) {
                    properties.put(SkinProperty.MODEL_OVERRIDE_CHEST, true);
                    properties.put(SkinProperty.MODEL_OVERRIDE_ARM_LEFT, true);
                    properties.put(SkinProperty.MODEL_OVERRIDE_ARM_RIGHT, true);
                }
                if (skinType == SkinTypes.ARMOR_LEGS) {
                    properties.put(SkinProperty.MODEL_OVERRIDE_LEG_LEFT, true);
                    properties.put(SkinProperty.MODEL_OVERRIDE_LEG_RIGHT, true);
                }
                if (skinType == SkinTypes.ARMOR_FEET) {
                    properties.put(SkinProperty.MODEL_OVERRIDE_LEG_LEFT, true);
                    properties.put(SkinProperty.MODEL_OVERRIDE_LEG_RIGHT, true);
                }
                properties.remove(SkinProperty.MODEL_OVERRIDE);
            }
            if (properties.get(SkinProperty.MODEL_HIDE_OVERLAY)) {
                if (skinType == SkinTypes.ARMOR_HEAD) {
                    properties.put(SkinProperty.MODEL_HIDE_OVERLAY_HEAD, true);
                }
                if (skinType == SkinTypes.ARMOR_CHEST) {
                    properties.put(SkinProperty.MODEL_HIDE_OVERLAY_CHEST, true);
                    properties.put(SkinProperty.MODEL_HIDE_OVERLAY_ARM_LEFT, true);
                    properties.put(SkinProperty.MODEL_HIDE_OVERLAY_ARM_RIGHT, true);
                }
                if (skinType == SkinTypes.ARMOR_LEGS) {
                    properties.put(SkinProperty.MODEL_HIDE_OVERLAY_LEG_LEFT, true);
                    properties.put(SkinProperty.MODEL_HIDE_OVERLAY_LEG_RIGHT, true);
                }
                if (skinType == SkinTypes.ARMOR_FEET) {
                    properties.put(SkinProperty.MODEL_HIDE_OVERLAY_LEG_LEFT, true);
                    properties.put(SkinProperty.MODEL_HIDE_OVERLAY_LEG_RIGHT, true);
                }
                properties.remove(SkinProperty.MODEL_HIDE_OVERLAY);
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
                    SkinProperties stub = SkinProperties.create(properties, skinIndex);
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
