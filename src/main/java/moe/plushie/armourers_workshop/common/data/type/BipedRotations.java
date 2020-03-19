package moe.plushie.armourers_workshop.common.data.type;

import java.util.Arrays;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagFloat;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants.NBT;

public class BipedRotations {

    private static final String TAG_ROTATION_DATA = "rotation_data";
    private static final String TAG_CHILD = "child";

    private float[][] rotationData;
    private boolean child;

    public BipedRotations() {
        rotationData = new float[BipedPart.values().length][3];
        child = false;
        resetRotations();
    }

    public void resetRotations() {
        setPartRotations(BipedPart.HEAD, 0F, 0F, 0F);
        setPartRotations(BipedPart.CHEST, 0F, 0F, 0F);
        setPartRotations(BipedPart.LEFT_ARM, 0F, (float) Math.toRadians(-1F), (float) Math.toRadians(-5F));
        setPartRotations(BipedPart.RIGHT_ARM, 0F, (float) Math.toRadians(1F), (float) Math.toRadians(5F));
        setPartRotations(BipedPart.LEFT_LEG, 0F, 0F, 0F);
        setPartRotations(BipedPart.RIGHT_LEG, 0F, 0F, 0F);
    }

    public void setChild(boolean child) {
        this.child = child;
    }

    public boolean isChild() {
        return child;
    }

    public void setPartRotations(BipedPart bipedPart, float[] rotationData) {
        setPartRotations(bipedPart, rotationData[0], rotationData[1], rotationData[2]);
    }

    public void setPartRotations(BipedPart bipedPart, float x, float y, float z) {
        rotationData[bipedPart.ordinal()][0] = x;
        rotationData[bipedPart.ordinal()][1] = y;
        rotationData[bipedPart.ordinal()][2] = z;
    }

    public float[] getPartRotations(BipedPart bipedPart) {
        return rotationData[bipedPart.ordinal()];
    }

    public void applyRotationsToBiped(ModelPlayer modelBiped) {
        modelBiped.isChild = child;
        applyRotationsToBipedPart(modelBiped.bipedHead, getPartRotations(BipedPart.HEAD));
        applyRotationsToBipedPart(modelBiped.bipedHeadwear, getPartRotations(BipedPart.HEAD));

        applyRotationsToBipedPart(modelBiped.bipedBody, getPartRotations(BipedPart.CHEST));
        applyRotationsToBipedPart(modelBiped.bipedBodyWear, getPartRotations(BipedPart.CHEST));

        applyRotationsToBipedPart(modelBiped.bipedLeftArm, getPartRotations(BipedPart.LEFT_ARM));
        applyRotationsToBipedPart(modelBiped.bipedLeftArmwear, getPartRotations(BipedPart.LEFT_ARM));

        applyRotationsToBipedPart(modelBiped.bipedRightArm, getPartRotations(BipedPart.RIGHT_ARM));
        applyRotationsToBipedPart(modelBiped.bipedRightArmwear, getPartRotations(BipedPart.RIGHT_ARM));

        applyRotationsToBipedPart(modelBiped.bipedLeftLeg, getPartRotations(BipedPart.LEFT_LEG));
        applyRotationsToBipedPart(modelBiped.bipedLeftLegwear, getPartRotations(BipedPart.LEFT_LEG));

        applyRotationsToBipedPart(modelBiped.bipedRightLeg, getPartRotations(BipedPart.RIGHT_LEG));
        applyRotationsToBipedPart(modelBiped.bipedRightLegwear, getPartRotations(BipedPart.RIGHT_LEG));
    }

    public void applyRotationsToBiped(ModelBiped modelBiped) {
        modelBiped.isChild = child;
        applyRotationsToBipedPart(modelBiped.bipedHead, getPartRotations(BipedPart.HEAD));
        applyRotationsToBipedPart(modelBiped.bipedHeadwear, getPartRotations(BipedPart.HEAD));

        applyRotationsToBipedPart(modelBiped.bipedBody, getPartRotations(BipedPart.CHEST));

        applyRotationsToBipedPart(modelBiped.bipedLeftArm, getPartRotations(BipedPart.LEFT_ARM));

        applyRotationsToBipedPart(modelBiped.bipedRightArm, getPartRotations(BipedPart.RIGHT_ARM));

        applyRotationsToBipedPart(modelBiped.bipedLeftLeg, getPartRotations(BipedPart.LEFT_LEG));

        applyRotationsToBipedPart(modelBiped.bipedRightLeg, getPartRotations(BipedPart.RIGHT_LEG));
    }

    public void applyRotationsToBipedPart(ModelRenderer modelRenderer, float[] partRotations) {
        modelRenderer.rotateAngleX = partRotations[0];
        modelRenderer.rotateAngleY = partRotations[1];
        modelRenderer.rotateAngleZ = partRotations[2];
    }

    public void getRotationsFromBiped(ModelBiped modelBiped) {
        setPartRotations(BipedPart.HEAD, getRotationsFromBipedPart(modelBiped.bipedHead));
        setPartRotations(BipedPart.CHEST, getRotationsFromBipedPart(modelBiped.bipedBody));
        setPartRotations(BipedPart.LEFT_ARM, getRotationsFromBipedPart(modelBiped.bipedLeftArm));
        setPartRotations(BipedPart.RIGHT_ARM, getRotationsFromBipedPart(modelBiped.bipedRightArm));
        setPartRotations(BipedPart.LEFT_LEG, getRotationsFromBipedPart(modelBiped.bipedLeftLeg));
        setPartRotations(BipedPart.RIGHT_LEG, getRotationsFromBipedPart(modelBiped.bipedRightLeg));
    }

    public float[] getRotationsFromBipedPart(ModelRenderer modelRenderer) {
        return new float[] { modelRenderer.rotateAngleX, modelRenderer.rotateAngleY, modelRenderer.rotateAngleZ };
    }

    public void loadNBTData(NBTTagCompound compound) {
        if (compound.hasKey(TAG_ROTATION_DATA, NBT.TAG_LIST)) {
            NBTTagList list = compound.getTagList(TAG_ROTATION_DATA, NBT.TAG_FLOAT);
            int totalRotations = BipedPart.values().length * 3;
            if (list.tagCount() >= totalRotations) {
                int counter = 0;
                for (BipedPart bipedPart : BipedPart.values()) {
                    NBTTagFloat rot1 = (NBTTagFloat) list.get(counter);
                    NBTTagFloat rot2 = (NBTTagFloat) list.get(counter + 1);
                    NBTTagFloat rot3 = (NBTTagFloat) list.get(counter + 2);
                    counter += 3;
                    setPartRotations(bipedPart, rot1.getFloat(), rot2.getFloat(), rot3.getFloat());
                }
            }
        }
        if (compound.hasKey(TAG_CHILD, NBT.TAG_BYTE)) {
            child = compound.getBoolean(TAG_CHILD);
        }
    }

    public NBTTagCompound saveNBTData(NBTTagCompound compound) {
        NBTTagList list = new NBTTagList();
        for (BipedPart bipedPart : BipedPart.values()) {
            float[] rots = getPartRotations(bipedPart);
            for (int i = 0; i < rots.length; i++) {
                list.appendTag(new NBTTagFloat(rots[i]));
            }
        }
        compound.setTag(TAG_ROTATION_DATA, list);
        compound.setBoolean(TAG_CHILD, child);
        return compound;
    }

    public void readFromBuf(ByteBuf buf) {
        for (BipedPart bipedPart : BipedPart.values()) {
            float rot1 = buf.readFloat();
            float rot2 = buf.readFloat();
            float rot3 = buf.readFloat();
            setPartRotations(bipedPart, rot1, rot2, rot3);
        }
        child = buf.readBoolean();
    }

    public void writeToBuf(ByteBuf buf) {
        for (BipedPart bipedPart : BipedPart.values()) {
            float[] rots = getPartRotations(bipedPart);
            for (int i = 0; i < rots.length; i++) {
                buf.writeFloat(rots[i]);
            }
        }
        buf.writeBoolean(child);
    }
    
    public BipedRotations copy() {
        BipedRotations rotations = new BipedRotations();
        rotations.loadNBTData(saveNBTData(new NBTTagCompound()));
        return rotations;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (child ? 1231 : 1237);
        result = prime * result + Arrays.deepHashCode(rotationData);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        BipedRotations other = (BipedRotations) obj;
        if (child != other.child)
            return false;
        if (!Arrays.deepEquals(rotationData, other.rotationData))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "BipedRotations [rotationData=" + Arrays.deepToString(rotationData) + ", child=" + child + "]";
    }

    public enum BipedPart {
        HEAD, CHEST, LEFT_ARM, RIGHT_ARM, LEFT_LEG, RIGHT_LEG
    }
}
