package mods.Hileb;

import net.minecraftforge.fml.relauncher.*;
import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraft.launchwrapper.Launch;

@IFMLLoadingPlugin.Name(ShotaASM.NAME)
@IFMLLoadingPlugin.MCVersion(net.minecraftforge.common.ForgeVersion.mcVersion)
public class SASMEnrtypoint implements IFMLLoadingPlugin{
    static {
        if (!Launch.classLoader.isClassExist("mods.Hileb.shotaasm.ShotaASM")) {
            TransformerDelegate.registerTransformer(new Transformer());
        }
    }

    private IFMLLoadingPlugin instance;

    public SASMEnrtypoint() {
        try {
            instance = Class.forName("mods.Hileb.shotaasm.ShotaASM", true, Launch.classLoader).getConstructor().newInstance();
        } catch (Throwable t) {
            return new RuntimeException("Could not launch ShotaASM!!", t);
        }
    }

    @Override
    public String[] getASMTransformerClass() {
        return instance.getASMTransformerClass();
    }

    @Override
    public String getModContainerClass() {
        return instance.getModContainerClass();
    }

    @Override
    public String getSetupClass() {
        return instance.getSetupClass();
    }

    @Override
    public void injectData(Map<String, Object> map) {
        instance.injectData(map);
    }

    @Override
    public String getAccessTransformerClass() {
        return instance.getAccessTransformerClass();
    }

    public static Transformer implements IClassTransformer {
        public byte[] transform(String s1, String s2, byte[] bc) {
            if(s2.startsWith("mods.Hileb.scriptasm.")) {
                ClassWriter writer;
                new ClassReader(is).accept(new ClassRemapper(writer = new ClassWriter(0), CurseforgeRemapper.INSTANCE), 0);
                return writer.toByteArray();
            } else return bc;
        }
    }

    static class CurseforgeRemapper extends Remapper {
        private static final CurseforgeRemapper INSTANCE = new CurseforgeRemapper();

        @Override
        public String map(String typeName) {
            return typeName.replace("_script_", "shota").replace("_Script_", "shota");
        }
    }
}