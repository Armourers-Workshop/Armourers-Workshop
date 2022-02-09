package moe.plushie.armourers_workshop.core.skin.data;

import com.google.common.collect.Iterables;
import moe.plushie.armourers_workshop.core.api.ISkinPaintType;
import moe.plushie.armourers_workshop.core.api.common.skin.ISkinDye;
import moe.plushie.armourers_workshop.core.skin.painting.SkinPaintTypes;

import java.util.ArrayList;
import java.util.HashMap;

public class SkinDye implements ISkinDye {

    public final static SkinDye EMPTY = new SkinDye();

    private static final String TAG_SKIN_DYE = "dyeData";
    private static final String TAG_DYE = "dye";
    private static final String TAG_NAME = "name";
    private static final String TAG_RED = "r";
    private static final String TAG_GREEN = "g";
    private static final String TAG_BLUE = "b";
    private static final String TAG_TYPE = "t";


    private final HashMap<ISkinPaintType, Integer> colors = new HashMap<>();
    private HashMap<ISkinPaintType, PaintColor> resolvedColors;

//    private byte[][] dyes;
//    private boolean[] hasDye;
//    private String[] names;

    public SkinDye() {
//        dyes = new byte[MAX_SKIN_DYES][4];
//        hasDye = new boolean[MAX_SKIN_DYES];
//        names = new String[MAX_SKIN_DYES];
    }

//    public SkinDye(ISkinDye skinDye) {
//        this();
////        for (int i = 0; i < MAX_SKIN_DYES; i++) {
////            if (skinDye.haveDyeInSlot(i)) {
////                addDye(i, skinDye.getDyeColour(i));
////            }
////        }
//    }

    public void clear() {
        colors.clear();
        resolvedColors = null;
    }

    public boolean isEmpty() {
        return colors.isEmpty();
    }

    public PaintColor getResolvedColor(ISkinPaintType paintType) {
        return getResolvedColors().get(paintType);
    }

    public Integer getColor(ISkinPaintType paintType) {
        return colors.get(paintType);
    }

    public void setColor(ISkinPaintType paintType, int color) {
        colors.put(paintType, color);
        resolvedColors = null;
    }

    public void add(SkinDye dye) {
        if (dye.colors.isEmpty()) {
            return; // not any changes
        }
        colors.putAll(dye.colors);
        resolvedColors = null;
        if (colors.equals(dye.colors)) {
            resolvedColors = dye.getResolvedColors();
        }
    }

    private HashMap<ISkinPaintType, PaintColor> getResolvedColors() {
        if (resolvedColors != null) {
            return resolvedColors;
        }
        resolvedColors = new HashMap<>();
        if (colors.isEmpty()) {
            return resolvedColors;
        }
        // build all item dependencies
        HashMap<ISkinPaintType, ArrayList<ISkinPaintType>> dependencies = new HashMap<>();
        colors.forEach((key, value) -> {
            PaintColor color = new PaintColor(value);
            if (color.getPaintType().getDyeType() != null) {
                dependencies.computeIfAbsent(color.getPaintType(), k -> new ArrayList<>()).add(key);
            } else {
                resolvedColors.put(key, color);
            }
        });
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
        SkinDye dye = (SkinDye) o;
        return colors.equals(dye.colors);
    }

    @Override
    public int hashCode() {
        return colors.hashCode();
    }

    @Override
    public String toString() {
        return "SkinDye [colors=" + colors + "]";
    }

    public static class PaintColor {
        private final int rgb;
        private final ISkinPaintType paintType;

        public PaintColor(int value) {
            this.paintType = SkinPaintTypes.byId(value >> 24 & 0xff);
            this.rgb = 0xff000000 | value;
        }

        public int getRGB() {
            return rgb;
        }

        public ISkinPaintType getPaintType() {
            return paintType;
        }
    }
}
