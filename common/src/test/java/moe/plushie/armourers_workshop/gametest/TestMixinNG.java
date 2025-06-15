package moe.plushie.armourers_workshop.gametest;

import moe.plushie.armourers_workshop.core.utils.Executors;
import moe.plushie.armourers_workshop.init.environment.EnvironmentExecutor;
import moe.plushie.armourers_workshop.init.environment.EnvironmentType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class TestMixinNG {

    @Test
    public void testItem() {
        Assertions.assertTrue(true, "Not yet implemented!");
    }

    @Test
    public void testSetupEvent() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        EnvironmentExecutor.didSetup(EnvironmentType.COMMON, () -> () -> {
            Executors.sleep(1000);
            latch.countDown();
        });
        boolean completed = latch.await(60, TimeUnit.SECONDS);
        Assertions.assertTrue(completed, "Callback was not invoked in time");
    }
}
