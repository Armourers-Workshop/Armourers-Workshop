package moe.plushie.armourers_workshop.api.common;

import java.util.function.Consumer;

public interface IConfigurableTool {

    void createToolProperties(Consumer<IConfigurableToolProperty<?>> builder);

}
