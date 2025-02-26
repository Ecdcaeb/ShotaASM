package mods.Hileb.shotaasm.impl.compiler;

public class CompileError extends Exception {
    public CompileError(String read){
        super(read);
    }

    public CompileError(String read, Throwable throwable){
        super(read, throwable);
    }

    public CompileError(Throwable throwable){
        super(throwable);
    }
}
