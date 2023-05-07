package moe.plushie.armourers_workshop.core.data;

import moe.plushie.armourers_workshop.utils.ThreadUtils;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.concurrent.Executor;

public class BackendExecutor implements Executor {

    private boolean isPaused = false;

    private final Executor executor = ThreadUtils.newFixedThreadPool(1, "AW-SKIN-LD/PRE");
    private final ArrayList<Runnable> pending = new ArrayList<>();

    @Override
    public void execute(@NotNull Runnable command) {
        if (isPaused) {
            pending.add(command);
        } else {
            executor.execute(command);
        }
    }

    public void pause() {
        isPaused = true;
    }

    public void resume() {
        isPaused = false;
        pending.forEach(executor::execute);
        pending.clear();
    }
}
