package mods.Hileb.scriptasm.api;

public interface IScriptCompiler {
    String name();
    Runnable compile(ScriptFile file);
}
