package mods.Hileb.scriptasm.api;

public class ScriptContext {
    public static void initialize() {}

    public static boolean isClassExist(String name) {
        return net.minecraft.launchwrapper.Launch.classLoader.isClassExist(name);
    }
    
    public static java.util.Set<String> supportedCompilers() {
        return mods.Hileb.scriptasm.ScriptLoader.compilers.keySet();
    }
}
