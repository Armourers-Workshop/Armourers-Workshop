package moe.plushie.armourers_workshop.api.client;

import java.util.function.Supplier;

public interface IRenderAttachable {

    void attachRenderTask(Supplier<Runnable> provider);
}
