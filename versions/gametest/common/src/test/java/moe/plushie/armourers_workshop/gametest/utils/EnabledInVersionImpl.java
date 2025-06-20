package moe.plushie.armourers_workshop.gametest.utils;

import org.junit.jupiter.api.extension.ConditionEvaluationResult;
import org.junit.jupiter.api.extension.ExecutionCondition;
import org.junit.jupiter.api.extension.ExtensionContext;

public class EnabledInVersionImpl implements ExecutionCondition {

    private final String version;
    private final int versionNumber;

    private EnabledInVersionImpl() {
        this.version = System.getProperty("junit.dli.task.minecraft", "0");
        this.versionNumber = parseVersion(version, 0);
    }

    @Override
    public ConditionEvaluationResult evaluateExecutionCondition(ExtensionContext context) {
        // get the annotation from the target method or target class.
        // format: [a.b.c, a.b.c)
        var limit = context.getElement().map(it -> it.getAnnotation(EnabledInVersion.class)).map(EnabledInVersion::value).orElseGet(() -> context.getTestClass().map(it -> it.getAnnotation(EnabledInVersion.class)).map(EnabledInVersion::value).orElse(""));
        var parts = limit.split("\\s*,\\s*");
        if (parts.length != 2) {
            return ConditionEvaluationResult.disabled("Not a version correctly!");
        }
        var lower = parseVersion(parts[0], 0);
        var upper = parseVersion(parts[1], Integer.MAX_VALUE);
        if (versionNumber < lower || versionNumber > upper) {
            return ConditionEvaluationResult.disabled("Not supported in current version!");
        }
        return ConditionEvaluationResult.enabled("Enabled in version " + version);
    }

    // example: 1.18.2-SNAPSHOT) => 1.18.2 => 1|18|02 => 11802 - 1 => 11801
    public static int parseVersion(String version, int undefined) {
        // check the limiter offset.
        var offset = 0;
        if (version.startsWith("(") || version.endsWith(")")) {
            if (version.length() == 1) {
                return undefined;
            }
            offset = 1;
        }
        // remove limiter: 1.18.2-SNAPSHOT
        // remove -SNAPSHOT part: 1.18.2
        // split version part to major, minor, patch: 1, 18, 2
        var versions = version.replaceAll("[()\\[\\]]", "").split("-")[0].split("\\.");

        // combine the all version part: 11802
        // apply the limiter offset: 11801
        var major = Integer.parseInt(versions.length > 0 ? versions[0] : "0");
        var minor = Integer.parseInt(versions.length > 1 ? versions[1] : "0");
        var patch = Integer.parseInt(versions.length > 2 ? versions[2] : "0");
        return Integer.parseInt(String.format("%d%02d%02d", major, minor, patch)) - offset;
    }
}
