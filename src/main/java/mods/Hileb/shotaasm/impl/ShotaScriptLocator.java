package mods.Hileb.shotaasm.impl;

import mods.Hileb.shotaasm.ShotaASM;
import mods.Hileb.shotaasm.ScriptLoader;
import mods.Hileb.shotaasm.api.IScriptLocator;
import mods.Hileb.shotaasm.api.ScriptFile;
import net.minecraft.launchwrapper.Launch;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ShotaScriptLocator implements IScriptLocator {

    @Override
    public Collection<ScriptFile> getScripts() {
        File root = new File(Launch.minecraftHome, "shotaasm");
        if (!root.exists()) {
            root.mkdirs();
        }

        List<ScriptFile> list = new ArrayList<>();
        for (File file : root.listFiles()) {
            if (file.isFile()) {
                try {
                    String name = file.getName();
                    ScriptFile scriptFile = ScriptFile.create(name, new String(IOUtils.toByteArray(Files.newBufferedReader(file.toPath()), StandardCharsets.UTF_8)));
                    if (scriptFile == null) {
                        ShotaASM.LOGGER.error("error when locate script for {}", file.getAbsolutePath());
                        continue;
                    }
                    scriptFile.data().put("file", file);
                    list.add(scriptFile);
                } catch (IOException e) {
                    throw new RuntimeException("Could not read file", e);
                }
            }
        }
        return list;
    }
}
