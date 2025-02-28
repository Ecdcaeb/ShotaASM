package mods.Hileb.shotaasm.dynamic;

import mods.Hileb.shotaasm.api.*;

public class DynamicPackageInitializer {
    public static void initialize() {
        IScriptCompiler.class.getName();
        IScriptLocator.class.getName();
        ScriptFile.class.getName();
        ShotaContext.class.getName();
        TransformerRegistry.class.getName();
    }
}