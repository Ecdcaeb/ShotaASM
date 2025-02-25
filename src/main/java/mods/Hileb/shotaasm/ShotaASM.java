package mods.Hileb.shotaasm;

import net.minecraftforge.fml.common.ModMetadata;
import org.apache.logging.log4j.*;

import net.minecraftforge.fml.common.DummyModContainer;
import net.minecraftforge.fml.relauncher.*;

import java.util.*;

import java.io.CharArrayReader;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import org.apache.commons.io.IOUtils;

@IFMLLoadingPlugin.Name(ShotaASM.NAME)
@IFMLLoadingPlugin.MCVersion(net.minecraftforge.common.ForgeVersion.mcVersion)
public class ShotaASM implements IFMLLoadingPlugin {
    public static final String NAME = "ShotaASM";
    public static final String MOD_ID = "shotaasm"
    public static final ModMetadata MOD_METADATA = MetaDataDecoder.decodeMcModInfo(ShotaASM.class.getResourceAsStream("mcmod.info")).get(MOD_ID);
    public static final Logger LOGGER = LogManager.getLogger(NAME);

    public static File source = null;

    @Override
    public String[] getASMTransformerClass() {
        return null;
    }

    @Override
    public String getModContainerClass() {
        return "mods.Hileb.shotaasm.ShotaASM$Container";
    }

    @Override
    public String getSetupClass() {
        return "mods.Hileb.shotaasm.ShotaASM$Setup";
    }

    @Override
    public void injectData(Map<String, Object> map) {
        source = (File) data.get("coremodLocation");
    }

    @Override
    public String getAccessTransformerClass() {
        return null;
    }

    public static class Container extends DummyModContainer{
        
        public Container(){
            super(MOD_METADATA);
        }

        @Override
        public boolean registerBus(EventBus bus, LoadController controller) {
            return true;
        }

        @Override
        public File getSource() {
            return source;
        }

    }

    public static class Setup implements IFMLCallHook {

        public void injectData(Map<String,Object> data) {

        }

        public Void call() {
            File root = new File(Launch.minecraftHome, "shotaasm");
            if (!root.exists()) {
                root.mkdirs();
            }
            
            List<Runnable> list = new ArrayList<>();
            for (File file : root.listFiles()) {
                byte[] text = IOUtils.toByteArray(Files.newBufferedReader(file.toPath()), StandardCharsets.UTF_8);
                String name = file.getName();
                list.add(ScriptLoader.loadScript(name, text));
            }
            list.forEach(Runnable::run);
        }

    }

}