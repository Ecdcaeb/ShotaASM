package mods.Hileb.shotaasm.impl.groovy;

import org.codehaus.groovy.control.customizers.ImportCustomizer;
import org.codehaus.groovy.control.CompilerConfiguration;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import groovy.lang.Binding;
import groovy.lang.GroovyClassLoader;
import groovy.lang.Script;
import org.codehaus.groovy.runtime.InvokerHelper;
import mods.Hileb.shotaasm.api.ScriptFile;
import net.minecraft.launchwrapper.Launch;

import java.util.*;

public class ShotaGroovySandbox {

    ImportCustomizer importCustomizer = new ImportCustomizer();
    CompilerConfiguration config = new CompilerConfiguration();
    Map<String, Object> bindings = new Object2ObjectOpenHashMap<>();
    Binding binding = new Binding(this.bindings);
    GroovyClassLoader classLoader;

    public AbstractGroovySandbox() {
        importCustomizer = new ImportCustomizer();
        config = new CompilerConfiguration();

        importCustomizer.addImports(
            "mods.Hileb.shotaasm.api.TransformerRegistry",
            "mods.Hileb.shotaasm.api.ShotaContext"
        );
        importCustomizer.addStarImports(
            "org.objectweb.asm.*",
            "org.objectweb.asm.tree.*",
            "org.objectweb.asm.util.*",
            "org.objectweb.asm.commons.*",
            "org.objectweb.asm.signature.*"
        );
        importCustomizer.addStaticStars("org.objectweb.asm.Opcodes");
        config.addCompilationCustomizers(importCustomizer);
        classLoader = new GroovyClassLoader(Launch.classLoader, config, false);
    }

    public Class<?> compile(String name, String text) {
        return classLoader.parseClass(new String(this.getFile()), name);
    }

    public Runnable makeScript(Class<?> cls){
        final Script script = InvokerHelper.createScript(cls, binding);
        return script::run ;
    }

    static ShotaGroovySandbox box = null;
    public static Runnable compile(String name, String file){
        if (box == null) box = new ShotaGroovySandbox();
        return box.makeScript(box.compile(name, file));
    }
}