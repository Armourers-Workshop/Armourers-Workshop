package moe.plushie.armourers_workshop.api.painting;

import java.util.function.Consumer;

public interface IPaintingTool {

    void createToolProperties(Consumer<IPaintingToolProperty<?>> builder);

}
