package moe.plushie.armourers_workshop.common.addons;

import net.minecraftforge.fml.common.Loader;

public class AddonRealFirstPerson extends ModAddon {

    public AddonRealFirstPerson() {
        super("rfpr", "Real First-Person Render");
    }
    
    @Override
    protected boolean setIsModLoaded() {
        if (Loader.isModLoaded("rfpr")) {
            return true;
        }
        if (Loader.isModLoaded("realrender")) {
            return true;
        }
        return false;
    }

    public boolean isModRender() {
        if (!isModLoaded()) {
            return false;
        }
        StackTraceElement[] traceElements = Thread.currentThread().getStackTrace();
        for (int i = 0; i < traceElements.length; i++) {
            StackTraceElement traceElement = traceElements[i];
            if (traceElement.toString().contains("realrender") | traceElement.toString().contains("rfpf")) {
                return true;
            }
        }
        return false;
    }
}
