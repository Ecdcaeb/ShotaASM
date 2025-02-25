package mods.Hileb.shotaasm.api;

public interface IScriptCompiler {
    String name();
    Runnable compile(ScriptFile file);
}
