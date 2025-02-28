package mods.Hileb.shotaasm.impl;

import mods.Hileb.shotaasm.api.IScriptCompiler;
import mods.Hileb.shotaasm.api.ScriptFile;
import mods.Hileb.shotaasm.impl.compiler.*;
import net.minecraft.launchwrapper.Launch;
import java.util.*;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import java.lang.reflect.InvocationTargetException;

public class ShotaCompiler implements IScriptCompiler {

    static {
        mods.Hileb.shotaasm.dynamic.DynamicPackageInitializer.initialize();
    }

    @Override
    public String name() {
        return "javaShota";
    }

    @Override
    public Runnable compile(ScriptFile file) {
        String singleName = file.name().substring(0, file.name().lastIndexOf('.')).replace('.', '_');
        String name = "mods.Hileb.shotaasm.dynamic." + singleName;
        StringBuilder builder = new StringBuilder("package mods.Hileb.shotaasm.dynamic;");
        for (String s : file.property().get("import")) {
            builder.append("import ").append(s).append(";\n");
        }
        builder
                .append("import mods.Hileb.shotaasm.api.TransformerRegistry;")
                .append("import mods.Hileb.shotaasm.api.ShotaContext;")
                .append("import org.objectweb.asm.*;")
                .append("import org.objectweb.asm.tree.*;")
                .append("import org.objectweb.asm.util.*;")
                .append("import org.objectweb.asm.commons.*;")
                .append("import org.objectweb.asm.signature.*;")
                .append("import static org.objectweb.asm.Opcodes.*;")
                .append("public class ").append(singleName).append(" implements Runnable { ")
                .append("    public void run() { ")
                .append(file.text())
                .append("}}");
        try {
            Compiler compiler = new Compiler();
            compiler.addSource(name, builder.toString());
            CompileError error = compiler.compile();
            if (error != null) throw new RuntimeException(error);
            else {
                for (Map.Entry<String, byte[]> entry : compiler.getClasses().entrySet()) {
                    Launch.classLoader.defineClass(entry.getKey(), entry.getValue());
                }
                return (Runnable) Class.forName(name, true, Launch.classLoader).getConstructor().newInstance();
            }
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
