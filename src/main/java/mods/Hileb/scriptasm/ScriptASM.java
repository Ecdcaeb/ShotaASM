package mods.Hileb.scriptasm;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import mods.Hileb.scriptasm.api.ScriptContext;
import mods.Hileb.scriptasm.impl.ScriptCompiler;
import mods.Hileb.scriptasm.impl.EventHandler;
import net.minecraftforge.fml.common.LoadController;
import net.minecraftforge.fml.common.ModMetadata;
import net.minecraftforge.fml.common.event.FMLStateEvent;
import org.apache.logging.log4j.*;

import net.minecraftforge.fml.common.DummyModContainer;
import net.minecraftforge.fml.relauncher.*;

import java.util.*;

import java.io.File;

@IFMLLoadingPlugin.Name(ScriptASM.NAME)
@IFMLLoadingPlugin.MCVersion(net.minecraftforge.common.ForgeVersion.mcVersion)
public class ScriptASM implements IFMLLoadingPlugin {
    public static final String NAME = Reference.MOD_NAME;
    public static final String MOD_ID = Reference.MOD_ID;
    public static final ModMetadata MOD_METADATA = MetaDataDecoder.decodeMcModInfo(ScriptASM.class.getResourceAsStream("/mcmod.info")).get(MOD_ID);
    public static final Logger LOGGER = LogManager.getLogger(NAME);
    public static boolean DEBUG = Boolean.parseBoolean(System.getProperty("foundation.dump", "false"));

    public static File source = null;

    @Override
    public String[] getASMTransformerClass() {
        return null;
    }

    @Override
    public String getModContainerClass() {
        return "mods.Hileb.scriptasm.ScriptASM$Container";
    }

    @Override
    public String getSetupClass() {
        return "mods.Hileb.scriptasm.ScriptASM$Setup";
    }

    @Override
    public void injectData(Map<String, Object> map) {
        source = (File) map.get("coremodLocation");
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
            bus.register(this);
            return true;
        }

        @Override
        public File getSource() {
            return source;
        }

        @Subscribe
        public void onFMLState(FMLStateEvent event) {
            EventHandler.executeEvent(event.getEventType());
        }

    }

    public static class Setup implements IFMLCallHook {

        public void injectData(Map<String,Object> data) {

        }

        public Void call() {
            ScriptContext.initialize();
            ScriptLoader.initialize();
            ScriptLoader.loadScripts().forEach(Runnable::run);
            return null;
        }

    }

}