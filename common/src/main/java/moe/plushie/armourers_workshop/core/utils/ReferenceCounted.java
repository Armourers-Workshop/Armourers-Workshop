package moe.plushie.armourers_workshop.core.utils;

public abstract class ReferenceCounted {

    private volatile int refCount = 0;

    protected void init() {
    }

    protected void dispose() {
    }

    /**
     * Increases the reference count by {@code 1}.
     */
    public final void retain() {
        var oldRefCount = refCount;
        refCount = oldRefCount + 1;
        if (oldRefCount == 0) {
            init();
        }
    }

    /**
     * Decreases the reference count by 1 and deallocates this object if the reference count reaches at 0.
     */
    public final void release() {
        var oldRefCount = refCount;
        if (oldRefCount <= 0) {
            return;
        }
        refCount = oldRefCount - 1;
        if (oldRefCount == 1) {
            dispose();
        }
    }

    /**
     * Returns the reference count of this object.  If {@code 0}, it means this object has been deallocated.
     */
    public final int refCnt() {
        return refCount;
    }
}
