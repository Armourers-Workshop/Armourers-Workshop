package moe.plushie.armourers_workshop.common.skin.advanced.value;

import moe.plushie.armourers_workshop.common.skin.advanced.AdvancedSkinRegistry.AdvancedSkinMathValue;

public class SkinValueMathMultiply extends AdvancedSkinMathValue<Float> {

    public SkinValueMathMultiply() {
        super("multiply");
        // TODO Auto-generated constructor stub
    }
    
    @Override
    public Float getValue(Object... data) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Class getType() {
        return Float.class;
    }

    @Override
    public int getInputCount() {
        return 2;
    }

    @Override
    public Class getInputType(int index) {
        if (index == 0) {
            return Float.class;
        }
        if (index == 1) {
            return Float.class;
        }
        return null;
    }

}
