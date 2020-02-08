package moe.plushie.armourers_workshop.common.addons;

public class AddonRealFirstPerson extends ModAddon {

    public AddonRealFirstPerson() {
        super("rfpr", "Real First-Person Render");
    }

    public boolean isRealFirstPersonRender() {
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
