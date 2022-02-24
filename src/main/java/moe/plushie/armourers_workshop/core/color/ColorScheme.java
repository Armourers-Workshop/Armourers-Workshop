package moe.plushie.armourers_workshop.core.color;

import com.google.common.collect.Iterables;
import moe.plushie.armourers_workshop.core.api.ISkinPaintType;
import moe.plushie.armourers_workshop.core.api.common.skin.ISkinDye;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

@SuppressWarnings("unused")
public class ColorScheme implements ISkinDye {

    public final static ColorScheme EMPTY = new ColorScheme();

    private final HashMap<ISkinPaintType, PaintColor> colors = new HashMap<>();
    private HashMap<ISkinPaintType, PaintColor> resolvedColors;

    private ColorScheme reference;
    private ResourceLocation texture;

    private int hashCode;

    public ColorScheme() {
    }

    public ColorScheme copy() {
        ColorScheme scheme = new ColorScheme();
        scheme.colors.putAll(colors);
        scheme.reference = reference;
        scheme.texture = texture;
        return scheme;
    }

    public boolean isEmpty() {
        if (reference != null && !reference.isEmpty()) {
            return false;
        }
        return colors.isEmpty();
    }

    @Nullable
    public PaintColor getColor(ISkinPaintType paintType) {
        PaintColor color = colors.get(paintType);
        if (color != null) {
            return color;
        }
        if (reference != null) {
            return reference.getColor(paintType);
        }
        return null;
    }

    public void setColor(ISkinPaintType paintType, PaintColor color) {
        colors.put(paintType, color);
        resolvedColors = null;
        hashCode = 0;
    }

    public PaintColor getResolvedColor(ISkinPaintType paintType) {
        if (resolvedColors == null) {
            resolvedColors = getResolvedColors();
        }
        return resolvedColors.get(paintType);
    }

    public void setTexture(ResourceLocation texture) {
        this.texture = texture;
    }

    public ResourceLocation getTexture() {
        return texture;
    }

    public ColorScheme getReference() {
        if (reference != null) {
            return reference;
        }
        return ColorScheme.EMPTY;
    }

    public void setReference(ColorScheme reference) {
        if (!Objects.equals(this.reference, reference)) {
            this.reference = reference;
            this.resolvedColors = null;
            this.hashCode = 0;
        }
    }

    private HashMap<ISkinPaintType, PaintColor> getResolvedColors() {
        HashMap<ISkinPaintType, PaintColor> resolvedColors = new HashMap<>();
        HashMap<ISkinPaintType, ArrayList<ISkinPaintType>> dependencies = new HashMap<>();
        // build all item dependencies
        Iterables.concat(colors.entrySet(), getReference().colors.entrySet()).forEach(e -> {
            ISkinPaintType paintType = e.getKey();
            PaintColor color = e.getValue();
            if (color.getPaintType().getDyeType() != null) {
                dependencies.computeIfAbsent(color.getPaintType(), k -> new ArrayList<>()).add(paintType);
            } else {
                resolvedColors.put(paintType, color);
            }
        });
        if (resolvedColors.isEmpty()) {
            return resolvedColors;
        }
        // merge all items whens dependencies
        dependencies.forEach((key, value) -> Iterables.tryFind(dependencies.values(), v -> v.contains(key)).toJavaUtil().ifPresent(target -> {
            if (target != value) {
                target.addAll(value);
            }
            value.clear(); // clear to prevent infinite loop occurs
        }));
        dependencies.forEach((key, value) -> value.forEach(paintType -> resolvedColors.put(paintType, resolvedColors.get(key))));
        return resolvedColors;
    }



//    @Override
//    public void writeToBuf(ByteBuf buf) {
//        for (int i = 0; i < MAX_SKIN_DYES; i++) {
//            buf.writeBoolean(hasDye[i]);
//            if (hasDye[i]) {
//                buf.writeBytes(dyes[i]);
//                if (!StringUtils.isNullOrEmpty(names[i])) {
//                    buf.writeBoolean(true);
////                    ByteBufUtils.writeUTF8String(buf, names[i]);
//                } else {
//                    buf.writeBoolean(false);
//                }
//            }
//        }
//    }
//
//    @Override
//    public void readFromBuf(ByteBuf buf) {
//        for (int i = 0; i < MAX_SKIN_DYES; i++) {
//            hasDye[i] = buf.readBoolean();
//            if (hasDye[i]) {
//                buf.readBytes(dyes[i]);
//                if (buf.readBoolean()) {
////                    names[i] = ByteBufUtils.readUTF8String(buf);
//                }
//            }
//        }
//    }
//
//    // TODO: IMP
//    public NBTTagCompound writeToCompound(NBTTagCompound compound) {
//        NBTTagCompound dyeCompound = new NBTTagCompound();
//        for (int i = 0; i < MAX_SKIN_DYES; i++) {
//            if (hasDye[i]) {
//                dyeCompound.setByte(TAG_DYE + i + TAG_RED, dyes[i][0]);
//                dyeCompound.setByte(TAG_DYE + i + TAG_GREEN, dyes[i][1]);
//                dyeCompound.setByte(TAG_DYE + i + TAG_BLUE, dyes[i][2]);
//                dyeCompound.setByte(TAG_DYE + i + TAG_TYPE, dyes[i][3]);
//                if (!StringUtils.isNullOrEmpty(names[i])) {
//                    dyeCompound.setString(TAG_NAME + i, names[i]);
//                }
//            }
//        }
//        compound.setTag(TAG_SKIN_DYE, dyeCompound);
//        return compound;
//    }
//
//    public void readFromCompound(NBTTagCompound compound) {
//        NBTTagCompound dyeCompound = compound.getCompoundTag(TAG_SKIN_DYE);
//        for (int i = 0; i < MAX_SKIN_DYES; i++) {
//            // Load old dye code.
//            if (dyeCompound.hasKey(TAG_DYE + i, Constants.NBT.TAG_BYTE_ARRAY)) {
//                dyes[i] = dyeCompound.getByteArray(TAG_DYE + i);
//
//                if (dyes[i].length == 4) {
//                    hasDye[i] = true;
//                } else {
//                    dyes[i] = new byte[] {0,0,0,0};
//                }
//                if (dyeCompound.hasKey(TAG_NAME + i, NBT.TAG_STRING)) {
//                    names[i] = dyeCompound.getString(TAG_NAME + i);
//                }
//            }
//            // End old dye loading code.
//            if (dyeCompound.hasKey(TAG_DYE + i + TAG_RED, Constants.NBT.TAG_BYTE)) {
//                if (dyeCompound.hasKey(TAG_DYE + i + TAG_GREEN, Constants.NBT.TAG_BYTE)) {
//                    if (dyeCompound.hasKey(TAG_DYE + i + TAG_BLUE, Constants.NBT.TAG_BYTE)) {
//                        if (dyeCompound.hasKey(TAG_DYE + i + TAG_TYPE, Constants.NBT.TAG_BYTE)) {
//                            dyes[i] = new byte[] {0,0,0,0};
//                            hasDye[i] = true;
//                            dyes[i][0] = dyeCompound.getByte(TAG_DYE + i + TAG_RED);
//                            dyes[i][1] = dyeCompound.getByte(TAG_DYE + i + TAG_GREEN);
//                            dyes[i][2] = dyeCompound.getByte(TAG_DYE + i + TAG_BLUE);
//                            dyes[i][3] = dyeCompound.getByte(TAG_DYE + i + TAG_TYPE);
//                            if (dyeCompound.hasKey(TAG_NAME + i, NBT.TAG_STRING)) {
//                                names[i] = dyeCompound.getString(TAG_NAME + i);
//                            }
//                        }
//                    }
//                }
//            }
//        }
//    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (o.hashCode() != this.hashCode()) return false;
        ColorScheme scheme = (ColorScheme) o;
        return colors.equals(scheme.colors) && Objects.equals(texture, scheme.texture) && Objects.equals(reference, scheme.reference);
    }

    @Override
    public int hashCode() {
        if (hashCode == 0) {
            hashCode = Objects.hash(colors, texture, reference);
            if (hashCode == 0) {
                hashCode = ~hashCode;
            }
        }
        return hashCode;
    }

    @Override
    public String toString() {
        return "[" + colors + "]";
    }
}
