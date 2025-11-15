package moe.plushie.armourers_workshop.core.utils;


import org.apache.commons.lang3.tuple.Pair;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class OpenFieldTransformer {

    private final Map<Class<?>, List<Field>> scannedFields = new ConcurrentHashMap<>();
    private final Map<Pair<Class<?>, Class<?>>, List<Field>> pairedClasses = new ConcurrentHashMap<>();

    /**
     * Apply the transform from src to dest.
     */
    public void apply(Object src, Object dest) {
        try {
            // ignore when src or dest is null.
            if (src == null || dest == null) {
                return;
            }
            var srcClass = src.getClass();
            var destClass = dest.getClass();
            var fields = pairedClasses.computeIfAbsent(Pair.of(srcClass, destClass), this::build);
            for (var field : fields) {
                apply(src, dest, field);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected abstract void apply(Object src, Object dest, Field field) throws Exception;

    protected abstract boolean shouldApply(Field field);


    private List<Field> build(Pair<Class<?>, Class<?>> pair) {
        // search the left class chain.
        var leftClasses = new ArrayList<Class<?>>();
        for (var cls = pair.getLeft(); cls != null; cls = cls.getSuperclass()) {
            leftClasses.add(0, cls);
        }

        // search the right class chain.
        var rightClasses = new ArrayList<Class<?>>();
        for (var cls = pair.getRight(); cls != null; cls = cls.getSuperclass()) {
            rightClasses.add(0, cls);
        }

        // search the common class.
        for (int i = 1; i < leftClasses.size() && i < rightClasses.size(); ++i) {
            if (leftClasses.get(i) != rightClasses.get(i)) {
                return search(leftClasses.get(i - 1));
            }
        }

        return Collections.emptyList();
    }

    private List<Field> search(Class<?> clazz) {
        var fields = scannedFields.get(clazz);
        if (fields != null) {
            return fields;
        }
        fields = new ArrayList<>();

        // add the all parent fields.
        var superClass = clazz.getSuperclass();
        if (superClass != null && superClass != Object.class) {
            fields.addAll(search(superClass));
        }

        // search the field with reflect.
        for (var field : clazz.getDeclaredFields()) {
            if (shouldApply(field)) {
                fields.add(field);
            }
        }

        scannedFields.put(clazz, fields);
        return fields;
    }
}
