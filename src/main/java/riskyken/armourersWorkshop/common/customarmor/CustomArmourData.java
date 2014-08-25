package riskyken.armourersWorkshop.common.customarmor;

import io.netty.buffer.ByteBuf;

import java.util.ArrayList;

public class CustomArmourData {

    private ArrayList<ArmourBlockData> armourData;
    private ArmourerType type;
    private ArmourPart part;

    public CustomArmourData(ArrayList armourData, ArmourerType type,
            ArmourPart part) {
        this.armourData = armourData;
        this.type = type;
        this.part = part;
    }

    public CustomArmourData(ByteBuf buf) {
        readFromBuf(buf);
    }

    public ArmourerType getArmourType() {
        return this.type;
    }

    public ArmourPart getArmourPart() {
        return this.part;
    }

    public ArrayList<ArmourBlockData> getArmourData() {
        return armourData;
    }

    public void writeToBuf(ByteBuf buf) {
        buf.writeByte(type.ordinal());
        buf.writeByte(part.ordinal());
        buf.writeInt(armourData.size());
        for (int i = 0; i < armourData.size(); i++) {
            armourData.get(i).writeToBuf(buf);
        }
    }

    private void readFromBuf(ByteBuf buf) {
        type = ArmourerType.getOrdinal(buf.readByte());
        part = ArmourPart.getOrdinal(buf.readByte());
        int size = buf.readInt();
        armourData = new ArrayList<ArmourBlockData>();
        for (int i = 0; i < size; i++) {
            armourData.add(new ArmourBlockData(buf));
        }
    }
}
