package riskyken.armourersWorkshop.common;

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
    
    public BipedPart head;
    public BipedPart chest;
    public BipedPart leftArm;
    public BipedPart rightArm;
    public BipedPart leftLeg;
    public BipedPart rightLeg;
    
    public BipedRotations() {
        head = new BipedPart(TAG_HEAD);
        chest = new BipedPart(TAG_CHEST);
        leftArm = new BipedPart(TAG_LEFT_ARM);
        rightArm = new BipedPart(TAG_RIGHT_ARM);
        leftLeg = new BipedPart(TAG_LEFT_LEG);
        rightLeg = new BipedPart(TAG_RIGHT_LEG);
    }
    
    public void applyRotationsToBiped(ModelBiped modelBiped) {
        head.applyRotationsToBipedPart(modelBiped.bipedHead);
        head.applyRotationsToBipedPart(modelBiped.bipedHeadwear);
        chest.applyRotationsToBipedPart(modelBiped.bipedBody);
        leftArm.applyRotationsToBipedPart(modelBiped.bipedLeftArm);
        rightArm.applyRotationsToBipedPart(modelBiped.bipedRightArm);
        leftLeg.applyRotationsToBipedPart(modelBiped.bipedLeftLeg);
        rightLeg.applyRotationsToBipedPart(modelBiped.bipedRightLeg);
    }
    
    public void loadNBTData(NBTTagCompound compound) {
        head.loadNBTData(compound);
        chest.loadNBTData(compound);
        leftArm.loadNBTData(compound);
        rightArm.loadNBTData(compound);
        leftLeg.loadNBTData(compound);
        rightLeg.loadNBTData(compound);
    }
    
    public void saveNBTData(NBTTagCompound compound) {
        head.saveNBTData(compound);
        chest.saveNBTData(compound);
        leftArm.saveNBTData(compound);
        rightArm.saveNBTData(compound);
        leftLeg.saveNBTData(compound);
        rightLeg.saveNBTData(compound);
    }
    
    public void readFromBuf(ByteBuf buf) {
        head.readFromBuf(buf);
        chest.readFromBuf(buf);
        leftArm.readFromBuf(buf);
        rightArm.readFromBuf(buf);
        leftLeg.readFromBuf(buf);
        rightLeg.readFromBuf(buf);
    }
    
    public void writeToBuf(ByteBuf buf) {
        head.writeToBuf(buf);
        chest.writeToBuf(buf);
        leftArm.writeToBuf(buf);
        rightArm.writeToBuf(buf);
        leftLeg.writeToBuf(buf);
        rightLeg.writeToBuf(buf);
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
        
        public void loadNBTData(NBTTagCompound compound) {
            this.rotationX = compound.getFloat(TAG_ROTATION_X + this.partName);
            this.rotationY = compound.getFloat(TAG_ROTATION_Y + this.partName);
            this.rotationZ = compound.getFloat(TAG_ROTATION_Z + this.partName);
        }
        
        public void saveNBTData(NBTTagCompound compound) {
            compound.setFloat(TAG_ROTATION_X + this.partName, this.rotationX);
            compound.setFloat(TAG_ROTATION_Y + this.partName, this.rotationY);
            compound.setFloat(TAG_ROTATION_Z + this.partName, this.rotationZ);
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
    }
}
