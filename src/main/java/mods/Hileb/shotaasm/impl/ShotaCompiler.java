package mods.Hileb.shotaasm.impl;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Iterables;
import mods.Hileb.shotaasm.api.IScriptCompiler;
import mods.Hileb.shotaasm.api.ScriptFile;
import mods.Hileb.shotaasm.impl.compiler.*;
import net.minecraft.launchwrapper.Launch;

import java.io.File;
import java.net.MalformedURLException;
import java.security.CodeSigner;
import java.security.CodeSource;
import java.util.*;

import java.lang.reflect.InvocationTargetException;

public class ShotaCompiler implements IScriptCompiler {
    private static final HashMultimap<String, Runnable> eventTasks = HashMultimap.create();

    public static void executeEvent(String evt) {
        eventTasks.get(evt).forEach(Runnable::run);
        eventTasks.removeAll(evt);
    }

    @Override
    public String name() {
        return "javaShota";
    }

    @Override
    public Runnable compile(final ScriptFile file) {
        CompilerFactory CompilerFactory = new CompilerFactory(Launch.classLoader.getURLs());

        if (file.property().containsKey("event")) {
            String event = Iterables.getFirst(file.property().get("event"), null);
            if (event != null) {
                eventTasks.put(event, () -> ShotaCompiler.this.compile(file));
                return () -> {};
            } else return null;
        } else {
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
                    .append("    }\n}");
            try {
                Compiler compiler = CompilerFactory.compiler();
                compiler.addSource(name, builder.toString());
                CompileError error = compiler.compile();
                if (error != null) throw new RuntimeException(error);
                else {
                    for (Map.Entry<String, byte[]> entry : compiler.getClasses().entrySet()) {
                        Launch.classLoader.defineClass(entry.getKey(), entry.getValue(), new CodeSource(((File) file.data().get("file")).toURI().toURL(), (CodeSigner[]) null));
                    }
                    return (Runnable) Class.forName(name, true, Launch.classLoader).getConstructor().newInstance();
                }
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                     NoSuchMethodException |
                     ClassNotFoundException | MalformedURLException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
