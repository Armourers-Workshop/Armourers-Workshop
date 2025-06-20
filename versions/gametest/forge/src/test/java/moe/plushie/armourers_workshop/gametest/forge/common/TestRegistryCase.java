package moe.plushie.armourers_workshop.gametest.forge.common;

import moe.plushie.armourers_workshop.gametest.utils.Asynchronous;
import moe.plushie.armourers_workshop.gametest.utils.EnabledInVersion;
import org.junit.jupiter.api.Test;

import static moe.plushie.armourers_workshop.gametest.utils.AssertLog.assertPrintLog;

@Asynchronous
public class TestRegistryCase {

    @Test
    @EnabledInVersion("[1.21, )")
    public void testDataAttachmentTypeRegisters() {
        assertPrintLog("Registering Data Attachment Type 'armourers_workshop:entity-skin-provider'");
    }
}
