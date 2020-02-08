package moe.plushie.armourers_workshop.common.addons;

public class AddonRealFirstPerson2 extends ModAddon {

    public AddonRealFirstPerson2() {
        super("rfp2", "Real First Person 2");
    }

    public boolean isModRender() {
        if (!isModLoaded()) {
            return false;
        }
        StackTraceElement[] traceElements = Thread.currentThread().getStackTrace();
        for (int i = 0; i < traceElements.length; i++) {
            StackTraceElement traceElement = traceElements[i];
            if (traceElement.toString().contains("rfp2")) {
                return true;
            }
        }
        return false;
    }
}
