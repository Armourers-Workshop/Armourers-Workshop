package moe.plushie.armourers_workshop.common.data;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.nbt.NBTTagCompound;

public class BipedRotations {
    
    private static final String TAG_HEAD = "head";
    private static final String TAG_CHEST = "chest";
    private static final String TAG_LEFT_ARM = "leftArm";
    private static final String TAG_RIGHT_ARM = "rightArm";
    private static final String TAG_LEFT_LEG = "LeftLeg";
    private static final String TAG_RIGHT_LEG = "RightLeg";
    private static final String TAG_IS_CHILD = "isChild";
    
    public BipedPart head;
    public BipedPart chest;
    public BipedPart leftArm;
    public BipedPart rightArm;
    public BipedPart leftLeg;
    public BipedPart rightLeg;
    public boolean isChild;
    public boolean hasCustomHead;
    
    //head - x: -90 - 90 y: -90 - 90 :z -20 - 20
    //left arm - x: -90 - 180 y: -45 - 90 :z -45 - 45
    //right arm - x: -90 - 180 y: -45 - 90 :z -45 - 45
    //left leg - x: -90 - 90 y: -45 - 45 :z -45 - 45
    //right leg - x: -90 - 90 y: -45 - 45 :z -45 - 45
    
    public BipedRotations() {
        head = new BipedPart(TAG_HEAD);
        chest = new BipedPart(TAG_CHEST);
        leftArm = new BipedPart(TAG_LEFT_ARM);
        rightArm = new BipedPart(TAG_RIGHT_ARM);
        leftLeg = new BipedPart(TAG_LEFT_LEG);
        rightLeg = new BipedPart(TAG_RIGHT_LEG);
        resetRotations();
    }
    
    public BipedPart getPartForIndex(int index) {
        switch (index) {
        case 0:
            return head;
        case 1:
            return chest;
        case 2:
            return leftArm;
        case 3:
            return rightArm;
        case 4:
            return leftLeg;
        case 5:
            return rightLeg;
        default:
            return null;
        }
    }
    
    public void resetRotations() {
        head.setRotationsDegrees(0, 0, 0);
        chest.setRotationsDegrees(0, 0, 0);
        leftArm.setRotationsDegrees(0, -1, -10);
        rightArm.setRotationsDegrees(0, 1, 10);
        leftLeg.setRotationsDegrees(0, 0, 0);
        rightLeg.setRotationsDegrees(0, 0, 0);
    }
    
    public void applyRotationsToBiped(ModelBiped modelBiped) {
        head.applyRotationsToBipedPart(modelBiped.bipedHead);
        head.applyRotationsToBipedPart(modelBiped.bipedHeadwear);
        chest.applyRotationsToBipedPart(modelBiped.bipedBody);
        leftArm.applyRotationsToBipedPart(modelBiped.bipedLeftArm);
        rightArm.applyRotationsToBipedPart(modelBiped.bipedRightArm);
        leftLeg.applyRotationsToBipedPart(modelBiped.bipedLeftLeg);
        rightLeg.applyRotationsToBipedPart(modelBiped.bipedRightLeg);
        modelBiped.isChild = isChild;
    }
    
    public void loadNBTData(NBTTagCompound compound) {
        head.loadNBTData(compound);
        chest.loadNBTData(compound);
        leftArm.loadNBTData(compound);
        rightArm.loadNBTData(compound);
        leftLeg.loadNBTData(compound);
        rightLeg.loadNBTData(compound);
        this.isChild = compound.getBoolean(TAG_IS_CHILD);
    }
    
    public NBTTagCompound saveNBTData(NBTTagCompound compound) {
        head.saveNBTData(compound);
        chest.saveNBTData(compound);
        leftArm.saveNBTData(compound);
        rightArm.saveNBTData(compound);
        leftLeg.saveNBTData(compound);
        rightLeg.saveNBTData(compound);
        compound.setBoolean(TAG_IS_CHILD, this.isChild);
        return compound;
    }
    
    public void readFromBuf(ByteBuf buf) {
        head.readFromBuf(buf);
        chest.readFromBuf(buf);
        leftArm.readFromBuf(buf);
        rightArm.readFromBuf(buf);
        leftLeg.readFromBuf(buf);
        rightLeg.readFromBuf(buf);
        this.isChild = buf.readBoolean();
    }
    
    public void writeToBuf(ByteBuf buf) {
        head.writeToBuf(buf);
        chest.writeToBuf(buf);
        leftArm.writeToBuf(buf);
        rightArm.writeToBuf(buf);
        leftLeg.writeToBuf(buf);
        rightLeg.writeToBuf(buf);
        buf.writeBoolean(this.isChild);
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((chest == null) ? 0 : chest.hashCode());
        result = prime * result + ((head == null) ? 0 : head.hashCode());
        result = prime * result + (isChild ? 1231 : 1237);
        result = prime * result + (hasCustomHead ? 1231 : 1237);
        result = prime * result + ((leftArm == null) ? 0 : leftArm.hashCode());
        result = prime * result + ((leftLeg == null) ? 0 : leftLeg.hashCode());
        result = prime * result + ((rightArm == null) ? 0 : rightArm.hashCode());
        result = prime * result + ((rightLeg == null) ? 0 : rightLeg.hashCode());
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
        if (chest == null) {
            if (other.chest != null)
                return false;
        } else if (!chest.equals(other.chest))
            return false;
        if (head == null) {
            if (other.head != null)
                return false;
        } else if (!head.equals(other.head))
            return false;
        if (isChild != other.isChild)
            return false;
        if (leftArm == null) {
            if (other.leftArm != null)
                return false;
        } else if (!leftArm.equals(other.leftArm))
            return false;
        if (leftLeg == null) {
            if (other.leftLeg != null)
                return false;
        } else if (!leftLeg.equals(other.leftLeg))
            return false;
        if (rightArm == null) {
            if (other.rightArm != null)
                return false;
        } else if (!rightArm.equals(other.rightArm))
            return false;
        if (rightLeg == null) {
            if (other.rightLeg != null)
                return false;
        } else if (!rightLeg.equals(other.rightLeg))
            return false;
        return true;
    }
    
    public class BipedPart {
        private static final String TAG_ROTATION_X = "rotationX";
        private static final String TAG_ROTATION_Y = "rotationY";
        private static final String TAG_ROTATION_Z = "rotationZ";
        
        private final String partName;
        public float rotationX;
        public float rotationY;
        public float rotationZ;
        
        public BipedPart(String partName) {
            this.partName = partName;
        }
        
        public void applyRotationsToBipedPart(ModelRenderer modelRenderer) {
            modelRenderer.rotateAngleX = this.rotationX;
            modelRenderer.rotateAngleY = this.rotationY;
            modelRenderer.rotateAngleZ = this.rotationZ;
        }
        
        public void setRotations(float x, float y, float z) {
            this.rotationX = x;
            this.rotationY = y;
            this.rotationZ = z;
        }
        
        public void setRotationsDegrees(float x, float y, float z) {
            this.rotationX = (float) Math.toRadians(x);
            this.rotationY = (float) Math.toRadians(y);
            this.rotationZ = (float) Math.toRadians(z);
        }
        
        public void loadNBTData(NBTTagCompound compound) {
            this.rotationX = compound.getFloat(TAG_ROTATION_X + this.partName);
            this.rotationY = compound.getFloat(TAG_ROTATION_Y + this.partName);
            this.rotationZ = compound.getFloat(TAG_ROTATION_Z + this.partName);
        }
        
        public NBTTagCompound saveNBTData(NBTTagCompound compound) {
            compound.setFloat(TAG_ROTATION_X + this.partName, this.rotationX);
            compound.setFloat(TAG_ROTATION_Y + this.partName, this.rotationY);
            compound.setFloat(TAG_ROTATION_Z + this.partName, this.rotationZ);
            return compound;
        }
        
        public void writeToBuf(ByteBuf buf) {
            buf.writeFloat(this.rotationX);
            buf.writeFloat(this.rotationY);
            buf.writeFloat(this.rotationZ);
        }
        
        public void readFromBuf(ByteBuf buf) {
            this.rotationX = buf.readFloat();
            this.rotationY = buf.readFloat();
            this.rotationZ = buf.readFloat();
        }
        
        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((partName == null) ? 0 : partName.hashCode());
            result = prime * result + Float.floatToIntBits(rotationX);
            result = prime * result + Float.floatToIntBits(rotationY);
            result = prime * result + Float.floatToIntBits(rotationZ);
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
            BipedPart other = (BipedPart) obj;
            if (partName == null) {
                if (other.partName != null)
                    return false;
            } else if (!partName.equals(other.partName))
                return false;
            if (Float.floatToIntBits(rotationX) != Float
                    .floatToIntBits(other.rotationX))
                return false;
            if (Float.floatToIntBits(rotationY) != Float
                    .floatToIntBits(other.rotationY))
                return false;
            if (Float.floatToIntBits(rotationZ) != Float
                    .floatToIntBits(other.rotationZ))
                return false;
            return true;
        }
        
        private BipedRotations getOuterType() {
            return BipedRotations.this;
        }
    }
}
