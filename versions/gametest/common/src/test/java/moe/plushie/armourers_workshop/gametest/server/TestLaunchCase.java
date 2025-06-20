package moe.plushie.armourers_workshop.gametest.server;

import moe.plushie.armourers_workshop.gametest.utils.Asynchronous;
import org.junit.jupiter.api.Test;

import static moe.plushie.armourers_workshop.gametest.utils.AssertLog.assertPrintLogRE;


@Asynchronous
public class TestLaunchCase {

    @Test
    public void testLaunchComplete() {
        assertPrintLogRE("Done (*s)! For help, type \"help\"");
    }
}
