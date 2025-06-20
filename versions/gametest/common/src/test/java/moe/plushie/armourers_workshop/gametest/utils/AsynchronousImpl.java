package moe.plushie.armourers_workshop.gametest.utils;

import com.google.common.collect.ImmutableList;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.util.List;

public class AsynchronousImpl implements BeforeTestExecutionCallback, AfterTestExecutionCallback {

    private static final List<Executor> EXECUTORS = ImmutableList.<Executor>builder()
            .add(AssertLog.getExecutor())
            .build();

    @Override
    public void beforeTestExecution(ExtensionContext context) throws Exception {
        for (var executor : EXECUTORS) {
            executor.beforeTestExecution(context);
        }
    }

    @Override
    public void afterTestExecution(ExtensionContext context) throws Exception {
        for (var executor : EXECUTORS) {
            executor.afterTestExecution(context);
        }
    }

    public interface Executor extends BeforeTestExecutionCallback, AfterTestExecutionCallback {
    }
}
