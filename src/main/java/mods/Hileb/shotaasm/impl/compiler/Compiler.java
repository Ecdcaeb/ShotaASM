package mods.Hileb.shotaasm.impl.compiler;

import mods.Hileb.shotaasm.impl.compiler.virtual.VirtualFileManager;
import mods.Hileb.shotaasm.impl.compiler.virtual.VirtualJavaFileObject;

import javax.tools.*;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class Compiler {

    private final Map<String, VirtualJavaFileObject> javaFileObjectMap = new HashMap<>();
    private Map<String, byte[]> results = new HashMap<>();

    private DiagnosticListener<VirtualJavaFileObject> listener;


    public void addSource(String name, String content) {
        javaFileObjectMap.put(name, new VirtualJavaFileObject(name, content));
    }

    public Map<String, byte[]> getClasses() {
        return results;
    }

    public static byte[] compileSingle(String canonicalName, String text){
        Compiler compiler = new Compiler();
        compiler.addSource(canonicalName, text);
        CompileError compileError = compiler.compile();
        if (compileError != null) return null;
        return compiler.getClasses().get(canonicalName);
    }

    public static byte[] compileSingle(String canonicalName, String text, Writer errorTracer){
        Compiler compiler = new Compiler();
        compiler.addSource(canonicalName, text);
        CompileError compileError = compiler.compile();
        if (compileError != null) {
            compileError.printStackTrace(new PrintWriter(errorTracer));
            return null;
        }
        return compiler.getClasses().get(canonicalName);
    }

    @SuppressWarnings("unchecked")
    public CompileError compile() {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        StandardJavaFileManager standardJavaFileManager = compiler.getStandardFileManager(null, null, StandardCharsets.UTF_8);
        VirtualFileManager fileManager = new VirtualFileManager(standardJavaFileManager, this.javaFileObjectMap);
        try {
            StringWriter stringWriter = new StringWriter();
            JavaCompiler.CompilationTask task = compiler.getTask(stringWriter, fileManager, (DiagnosticListener<? super JavaFileObject>)(Object) listener, null, null, javaFileObjectMap.values());
            if (task.call() == Boolean.TRUE) {
                this.results = fileManager.getClasses();
                return null;
            } else return new CompileError(stringWriter.toString());
        } catch (Exception e) {
            return new CompileError(e);
        }
    }

    public void setListener(DiagnosticListener<VirtualJavaFileObject> listener) {
        this.listener = listener;
    }
}
