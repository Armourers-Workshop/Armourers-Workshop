package moe.plushie.armourers_workshop.compat.core.data;

import moe.plushie.armourers_workshop.api.annotation.Available;
import moe.plushie.armourers_workshop.api.core.IDataSerializable;

@Available("[16, )")
public abstract class AbstractSavedData extends AbstractSavedDataImpl implements IDataSerializable.Mutable {

    protected void abi$setDirty() {
        super.setDirty();
    }

    protected void abi$setDirty(boolean bl) {
        super.setDirty(bl);
    }

    protected boolean abi$isDirty() {
        return super.isDirty();
    }

    @Override
    public final void setDirty() {
        abi$setDirty();
    }

    @Override
    public final void setDirty(boolean bl) {
        abi$setDirty(bl);
    }

    @Override
    public final boolean isDirty() {
        return abi$isDirty();
    }
}
