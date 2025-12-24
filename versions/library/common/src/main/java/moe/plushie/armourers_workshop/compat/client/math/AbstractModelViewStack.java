package moe.plushie.armourers_workshop.compat.client.math;

public class AbstractModelViewStack extends AbstractModelViewStackImpl {

    private static final AbstractModelViewStack INSTANCE = new AbstractModelViewStack();

    public static AbstractModelViewStack getInstance() {
        return INSTANCE;
    }
}

