package moe.plushie.armourers_workshop.core.skin.data;

import com.google.common.collect.Iterables;
import moe.plushie.armourers_workshop.core.api.ISkinPaintType;
import moe.plushie.armourers_workshop.core.api.common.skin.ISkinDye;
import moe.plushie.armourers_workshop.core.bake.BakedSkinTexture;
import moe.plushie.armourers_workshop.core.bake.ColorDescriptor;
import moe.plushie.armourers_workshop.core.skin.painting.PaintColor;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class Palette implements ISkinDye {

    public final static Palette EMPTY = new Palette();

//    private static final String TAG_SKIN_DYE = "dyeData";
//    private static final String TAG_DYE = "dye";
//    private static final String TAG_NAME = "name";
//    private static final String TAG_RED = "r";
//    private static final String TAG_GREEN = "g";
//    private static final String TAG_BLUE = "b";
//    private static final String TAG_TYPE = "t";

    private final HashMap<ISkinPaintType, PaintColor> colors = new HashMap<>();
    private HashMap<ISkinPaintType, PaintColor> resolvedColors;

    private ResourceLocation texture;
    private Palette reference;
    private int hashCode;

    public Palette() {
    }

    public Palette copy() {
        Palette palette = new Palette();
        palette.colors.putAll(colors);
        return palette;
    }

    public boolean isEmpty() {
        if (reference != null && !reference.isEmpty()) {
            return false;
        }
        return colors.isEmpty();
    }

    public boolean isTextureReady() {
        return texture != null;
    }

    public PaintColor getResolvedColor(ISkinPaintType paintType) {
        if (resolvedColors == null) {
            resolvedColors = getResolvedColors();
        }
        return resolvedColors.get(paintType);
    }

    public BakedSkinTexture getTextureReader() {
        return null;
    }

    public ResourceLocation getTexture() {
        return this.texture;
    }

    public void setTexture(ResourceLocation texture) {
        this.texture = texture;
        this.hashCode = 0;
    }

    public Palette getReference() {
        if (reference != null) {
            return reference;
        }
        return Palette.EMPTY;
    }

    public void setReference(Palette reference) {
        if (!Objects.equals(this.reference, reference)) {
            this.reference = reference;
            this.resolvedColors = null;
            this.hashCode = 0;
        }
    }

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
//    public byte[] getDyeColour(int index) {
//        return dyes[index];
//    }
//
//    @Override
//    public String getDyeName(int index) {
//        return names[index];
//    }
//
//    @Override
//    public boolean haveDyeInSlot(int index) {
//        return hasDye[index];
//    }
//
//    @Override
//    public boolean hasName(int index) {
//        return !StringUtils.isNullOrEmpty(names[index]);
//    }

//    @Override
//    public void addDye(byte[] rgbt, String name) {
//        if (rgbt.length != 4) {
//            SkinLog.warn("Something tried to set an invalid dye colour.");
//            Thread.dumpStack();
//            return;
//        }
//        for (int i = 0; i < hasDye.length; i++) {
//            if (!hasDye[i]) {
//                dyes[i] = rgbt;
//                hasDye[i] = true;
//                names[i] = name;
//                break;
//            }
//        }
//    }
//
//    @Override
//    public void addDye(byte[] rgbt) {
//        addDye(rgbt, null);
//    }
//
//    @Override
//    public void addDye(int index, byte[] rgbt, String name) {
//        if (rgbt.length != 4) {
//            SkinLog.warn("Something tried to set an invalid dye colour.");
//            Thread.dumpStack();
//            return;
//        }
//        dyes[index] = rgbt;
//        hasDye[index] = true;
//        names[index] = name;
//    }
//
//    @Override
//    public void addDye(int index, byte[] rgbt) {
//        addDye(index, rgbt, null);
//    }
//
//    @Override
//    public void removeDye(int index) {
//        dyes[index] = new byte[]{(byte) 0, (byte) 0, (byte) 0, (byte) 255};
//        hasDye[index] = false;
//        names[index] = null;
//    }
//
//    @Override
//    public int getNumberOfDyes() {
//        int count = 0;
//        for (int i = 0; i < MAX_SKIN_DYES; i++) {
//            if (hasDye[i]) {
//                count++;
//            }
//        }
//        return count;
//    }

//    @Override
//    public void writeToBuf(ByteBuf buf) {
//        for (int i = 0; i < MAX_SKIN_DYES; i++) {
//            buf.writeBoolean(hasDye[i]);
//            if (hasDye[i]) {
//                buf.writeBytes(dyes[i]);
//                if (!StringUtils.isNullOrEmpty(names[i])) {
//                    buf.writeBoolean(true);
//                    // TODO: IMP
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
//                    // TODO: IMP
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
        Palette palette = (Palette) o;
        return colors.equals(palette.colors) && Objects.equals(texture, palette.texture) && Objects.equals(reference, palette.reference);
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
        return "SkinDye [colors=" + colors + "]";
    }
}
