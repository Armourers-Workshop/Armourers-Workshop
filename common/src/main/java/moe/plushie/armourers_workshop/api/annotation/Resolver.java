package moe.plushie.armourers_workshop.api.annotation;

import moe.plushie.armourers_workshop.core.utils.Evaluator;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;
import org.spongepowered.asm.service.MixinService;
import org.spongepowered.asm.util.Annotations;

import java.util.List;
import java.util.Set;

@SuppressWarnings("unused")
public class Resolver implements IMixinConfigPlugin {

    private final Evaluator evaluator = new Evaluator();

    @Override
    public void onLoad(String mixinPackage) {

    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        // we need check the mixin condition?
        var condition = getCondition(mixinClassName);
        if (condition != null && !condition.isEmpty()) {
            return evaluator.eval(condition);
        }
        return true;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {

    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Nullable
    private String getCondition(String className) {
        try {
            var classNode = MixinService.getService().getBytecodeProvider().getClassNode(className);
            var condition = Annotations.getVisible(classNode, Conditional.class);
            if (condition == null) {
                return null;
            }
            return Annotations.getValue(condition, "value", String.class);
        } catch (Exception e) {
            return null;
        }
    }
}
