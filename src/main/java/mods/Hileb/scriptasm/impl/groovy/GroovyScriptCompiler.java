package mods.Hileb.scriptasm.impl.groovy;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Iterables;

import mods.Hileb.scriptasm.api.ScriptContext;
import mods.Hileb.scriptasm.api.IScriptCompiler;
import mods.Hileb.scriptasm.api.ScriptFile;
import mods.Hileb.scriptasm.impl.EventHandler;

public class GroovyScriptCompiler implements IScriptCompiler {

    @Override
    public String name() {
        return "groovyScript";
    }

    @Override
    public Runnable compile(final ScriptFile file) {
        if (ScriptContext.isClassExist("groovy.lang.GroovyClassLoader")) {
            if (file.property().containsKey("event")) {
                String event = Iterables.getFirst(file.property().get("event"), null);
                if (event != null) {
                    EventHandler.addEvent(event, () -> GroovyScriptCompiler.this.compile(file));
                    return () -> {};
                } else throw new RuntimeException("Could not understand the first event property is null. At " + file.name());
            } else {
                String singleName = file.name().substring(0, file.name().lastIndexOf('.')).replace('.', '_') + file.hashCode();
                String name = "mods.Hileb.scriptasm.dynamic.groovy" + singleName;
                StringBuilder builder = new StringBuilder();
                for (String s : file.property().get("import")) {
                    builder.append("import ").append(s).append(";\n");
                }
                builder.append("\n").append(file.text());
                try {
                    return ScriptGroovySandbox.compileScript(name, builder.toString());
                } catch (Throwable e) {
                    throw new RuntimeException("Unable to compile for " + file.name(), e);
                }
            }
        } else {
            throw new RuntimeException("Groovy Support not found. But " + file.name() + " required the compiler of groovyScript.");
        }
    }
}
