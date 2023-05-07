package moe.plushie.armourers_workshop.utils;

import java.util.ArrayList;

public class WorkQueue {

    private boolean isPaused = true;
    private final ArrayList<Runnable> values = new ArrayList<>();

    public void pause() {
        isPaused = true;
    }

    public void resume() {
        isPaused = false;
        values.forEach(Runnable::run);
        values.clear();
    }

    public void submit(Runnable cmd) {
        if (isPaused) {
            values.add(cmd);
        } else {
            cmd.run();
        }
    }
}
