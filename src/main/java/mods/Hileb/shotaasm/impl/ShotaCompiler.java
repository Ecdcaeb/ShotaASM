package mods.Hileb.shotaasm.impl;

import mods.Hileb.shotaasm.api.IScriptCompiler;
import mods.Hileb.shotaasm.api.ScriptFile;
import mods.Hileb.shotaasm.impl.compiler.Compiler;
import net.minecraft.launchwrapper.Launch;

import java.lang.reflect.InvocationTargetException;

public class ShotaCompiler implements IScriptCompiler {
    @Override
    public String name() {
        return "shota";
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
                .append("import top.outlands.foundation.TransformerDelegate;")
                .append("import org.objectweb.asm.*;")
                .append("import org.objectweb.asm.tree.*;")
                .append("import org.objectweb.asm.util.*;")
                .append("import org.objectweb.asm.commons.*;")
                .append("import org.objectweb.asm.signature.*;")
                .append("import static org.objectweb.asm.Opcodes.*;")
                .append("public ").append(singleName).append(" implements Runnable { ")
                .append("    public void run() { ")
                .append(file.text())
                .append("}}");
        try {
            return (Runnable) Launch.classLoader.defineClass(name, Compiler.compileSingle(name, builder.toString()))
                    .getConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }
}
