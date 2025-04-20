package mods.Hileb.shotaasm.impl.groovy;

import mods.Hileb.shotaasm.api.IScriptCompiler;
import mods.Hileb.shotaasm.api.ScriptFile;

public class GroovyShotaCompiler implements IScriptCompiler {

    @Override
    public String name() {
        return "groovyShota";
    }

    @Override
    public Runnable compile(final ScriptFile file) {
        if (ShotaContext.isClassExist("groovy.lang.GroovyClassLoader")) {
            if (file.property().containsKey("event")) {
                String event = Iterables.getFirst(file.property().get("event"), null);
                if (event != null) {
                    eventTasks.put(event, () -> GroovyShotaCompiler.this.compile(file));
                    return () -> {};
                } else throw new RuntimeException("Could not understand the first event property is null. At " + file.name());
            } else {
                String singleName = file.name().substring(0, file.name().lastIndexOf('.')).replace('.', '_') + file.hashCode();
                String name = "mods.Hileb.shotaasm.dynamic.groovy" + singleName;
                StringBuilder builder = new StringBuilder();
                for (String s : file.property().get("import")) {
                    builder.append("import ").append(s).append(";\n");
                }
                builder.append("\n").append(file.text());
                try {
                    return ShotaGroovySandbox.compile(name, builder.toString());
                } catch (Throwable e) {
                    throw new RuntimeException("Unable to compile for " + file.name(), e);
                }
            }
        } else {
            throw new RuntimeException("Groovy Support not found. But " + file.name() + " required the compiler of groovyShota.", e);
        }
    }
}
