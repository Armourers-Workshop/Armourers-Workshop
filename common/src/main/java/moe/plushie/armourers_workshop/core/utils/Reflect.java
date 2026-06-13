package moe.plushie.armourers_workshop.core.utils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Objects;

public class Reflect {

    public static Receiver of(Object object) {
        return new Receiver(object);
    }

    public static Receiver forName(String name) {
        try {
            return of(Class.forName(name));
        } catch (ClassNotFoundException e) {
            return of(null);
        }
    }

    @SuppressWarnings("unchecked")
    public static class Receiver {

        private final Object owned;
        private final Lookup members;

        public Receiver(Object owned) {
            this.owned = owned;
            this.members = Lookup.of(owned);
        }

        public <T> T invoke(String name, Object... args) {
            try {
                var method = members.findMethod(name, args);
                if (method != null) {
                    return (T) method.invoke(sel(method.getModifiers()), args);
                }
                return null;
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }

        public <T> T get(String name) {
            try {
                var field = members.findField(name);
                if (field != null) {
                    return (T) field.get(sel(field.getModifiers()));
                }
                return null;
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }

        public <T> void set(String name, T value) {
            try {
                var field = members.findField(name);
                if (field != null) {
                    field.set(sel(field.getModifiers()), value);
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }

        private Object sel(int modifiers) {
            if (Modifier.isStatic(modifiers)) {
                return null;
            }
            return owned;
        }
    }

    private static class Lookup {

        private static final Lookup NULL = new Lookup(null);
        private static final IdentityHashMap<Class<?>, Lookup> INSTANCES = new IdentityHashMap<>();

        private final HashMap<String, Field> fields = new HashMap<>();
        private final HashMap<String, List<Method>> methods = new HashMap<>();

        private final ArrayList<Class<?>> classes = new ArrayList<>();

        public Lookup(Class<?> target) {
            // collect all classes.
            for (var clazz = target; clazz != null; clazz = clazz.getSuperclass()) {
                classes.add(clazz);
            }
            // collect all interfaces.
            for (var i = 0; i < classes.size(); ++i) {
                var clazz = classes.get(i);
                for (var iface : clazz.getInterfaces()) {
                    if (!classes.contains(iface)) {
                        classes.add(iface);
                    }
                }
            }
            // collect all fields/methods.
            for (var clazz : classes) {
                // collect declared fields for class.
                for (var field : clazz.getDeclaredFields()) {
                    if (Modifier.isPublic(field.getModifiers())) { // only support public fields.
                        var name = field.getName();
                        fields.putIfAbsent(name, field);
                    }
                }
                // collect declared methods for class.
                for (var method : clazz.getDeclaredMethods()) {
                    if (Modifier.isPublic(method.getModifiers()) && !Modifier.isAbstract(method.getModifiers())) { // only support public methods.
                        var name = method.getName();
                        var candidates = methods.computeIfAbsent(name, key -> new ArrayList<>());
                        if (candidates.stream().noneMatch(it -> matches(it, method))) {
                            candidates.add(method);
                        }
                    }
                }
            }
        }

        public static synchronized Lookup of(Object receiver) {
            if (receiver == null) {
                return NULL;
            }
            if (receiver instanceof Class<?> clazz) {
                return INSTANCES.computeIfAbsent(clazz, Lookup::new);
            }
            return INSTANCES.computeIfAbsent(receiver.getClass(), Lookup::new);
        }

        public Field findField(String name) {
            return fields.get(name);
        }

        public Method findMethod(String name, Object... args) {
            var candidates = methods.get(name);
            if (candidates == null) {
                return null;
            }
            if (candidates.size() == 1) {
                return candidates.get(0);
            }
            for (var candidate : candidates) {
                // must match the parameter count.
                var types = candidate.getParameterTypes();
                if (types.length != args.length) {
                    continue;
                }
                // check the argument types.
                var index = 0;
                for (index = 0; index < types.length; ++index) {
                    var type = types[index];
                    var arg = args[index];
                    if (arg == null && type.isPrimitive()) {
                        break; // primitive types can't be null.
                    }
                    if (arg != null && !wrap(type).isInstance(arg)) {
                        break; // invalid argument type.
                    }
                }
                // the target is matched?
                if (index == types.length) {
                    return candidate;
                }
            }
            return candidates.get(0); // return the first method.
        }

        private boolean matches(Method method1, Method method2) {
            return Objects.equals(method1.getReturnType(), method2.getReturnType()) &&
                    Arrays.equals(method1.getParameterTypes(), method2.getParameterTypes());
        }

        private Class<?> wrap(Class<?> clazz) {
            if (clazz == boolean.class) {
                return Boolean.class;
            }
            if (clazz == byte.class) {
                return Byte.class;
            }
            if (clazz == short.class) {
                return Short.class;
            }
            if (clazz == int.class) {
                return Integer.class;
            }
            if (clazz == long.class) {
                return Long.class;
            }
            if (clazz == float.class) {
                return Float.class;
            }
            if (clazz == double.class) {
                return Double.class;
            }
            if (clazz == char.class) {
                return Character.class;
            }
            return clazz;
        }
    }
}
