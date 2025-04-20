package mods.Hileb.shotaasm.impl.rml;

import mods.Hileb.shotaasm.ShotaASM;
import mods.Hileb.shotaasm.ScriptLoader;
import mods.Hileb.shotaasm.api.IScriptLocator;
import mods.Hileb.shotaasm.api.ScriptFile;
import mods.Hileb.shotaasm.api.ShotaContext;

import net.minecraft.launchwrapper.Launch;
import net.minecraft.util.ResourceLocation;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.FilenameUtils;

import org.apache.logging.log4j.message.FormattedMessage;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import rml.loader.ResourceModLoader;
import rml.loader.api.mods.module.ModuleType;
import rml.loader.api.mods.ContainerHolder;
import rml.jrx.utils.file.FileHelper;

public class ShotaRMLLocator implements IScriptLocator {

    @Override
    public Collection<ScriptFile> getScripts() {
        final List<ScriptFile> list = new ArrayList<>();

        if (ShotaContext.isClassExist("rml.loader.ResourceModLoader")) {
            ResourceModLoader.loadModuleFindAssets(ModuleType.valueOf(new ResourceLocation("shotaasm", "shotaasm")), (containerHolder, module, root, file) -> {
                String relative = root.relativize(file).toString();
                String name = FilenameUtils.removeExtension(relative).replaceAll("\\\\", "/");
                ResourceLocation key = new ResourceLocation(containerHolder.getContainer().getModId(), name);

                try
                {
                    ScriptFile scriptFile = ScriptFile.create(key.toString().replace(':', '$').replace('/', '$'), new String(FileHelper.getByteSource(file).read()));
                    scriptFile.data().put("rml_container", containerHolder);
                    list.add(scriptFile);
                } catch (Exception e) {
                    ErrorHandler.error(
                        Objects.requireNonNull(module, "module").moduleType, 
                        containerHolder, e, "Could not read file {}", key);
                }
            });
        }
        return list;
    }

    private static class ErrorHandler{
        public static void runThrow(Throwable throwable, String msg, Object... args){
            throw new RuntimeException(new FormattedMessage(msg, args).getFormattedMessage(), throwable);
        }

        public static boolean isForced(ContainerHolder containerHolder, ModuleType moduleType){
            return containerHolder.hasModule(moduleType) && containerHolder.getModules().get(moduleType).forceLoaded;
        }

        public static void error(ModuleType moduleType, ContainerHolder containerHolder, Throwable throwable, String msg, Object... args){
            if (isForced(containerHolder, moduleType)) runThrow(throwable, msg, args);
            else ShotaASM.LOGGER.error(new FormattedMessage(msg, args).getFormattedMessage(), throwable);
        }
    }

}
