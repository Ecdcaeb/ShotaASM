package mods.Hileb.shotaasm.api;

public class ShotaContext {
    public static void initialize() {}

    public static boolean isClassExist(String name) {
        return net.minecraft.launchwrapper.Launch.classLoader.isClassExist(name);
    }
}
