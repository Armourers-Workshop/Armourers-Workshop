package moe.plushie.armourers_workshop.init.platform;

public abstract class PlatformLoader {

    public static final String NAME = detect();

    public static <T> T load(Class<T> clazz) {
        try {
            var implementClass = clazz.getClassLoader().loadClass(resolve(clazz, NAME));
            // noinspection unchecked
            return (T) implementClass.getDeclaredConstructor().newInstance();
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }

    private static String resolve(Class<?> clazz, String platform) {
        return clazz.getName().replaceFirst("^(.+)\\.(.+?)$", String.format("$1.%s.$2Impl", platform));
    }

    private static String detect() {
        var clazz = PlatformLoader.class;
        var classLoader = clazz.getClassLoader();
        var platforms = new String[]{"forge", "fabric"};
        for (var platform : platforms) {
            var filePath = resolve(clazz, platform).replace('.', '/') + ".class";
            if (classLoader.getResource(filePath) != null) {
                return platform;
            }
        }
        throw new IllegalStateException("Could not find any implementation of " + clazz.getName());
    }
}
