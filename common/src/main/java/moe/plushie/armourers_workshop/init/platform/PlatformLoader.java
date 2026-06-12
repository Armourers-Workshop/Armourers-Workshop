package moe.plushie.armourers_workshop.init.platform;

public abstract class PlatformLoader {

    private static final String NAME = detect();

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
        var name = clazz.getName();
        name = name.replace(".platform.", ".platform." + platform + ".");
        name = name.replaceFirst("^(.+)\\.([^$]+?)(\\$.+?)?$", "$1.$2Impl$3");
        return name;
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
