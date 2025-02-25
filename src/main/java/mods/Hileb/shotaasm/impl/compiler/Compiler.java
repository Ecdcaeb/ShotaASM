package mods.Hileb.shotaasm.impl.compiler;

import com.google.common.collect.Iterables;
import mods.Hileb.shotaasm.impl.compiler.virtual.VirtualFileManager;
import mods.Hileb.shotaasm.impl.compiler.virtual.VirtualJavaFileObject;

import javax.tools.*;
import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

public class Compiler {

    public List<File> classpath = null;
    
    private final Map<String, VirtualJavaFileObject> javaFileObjectMap = new HashMap<>();
    private Map<String, byte[]> results = new HashMap<>();

    private DiagnosticListener<VirtualJavaFileObject> listener;

    public Compiler(){
        this(null);
    }

    public Compiler(List<File> options) {
        this.classpath = options;
    }

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
        if (compiler == null) return new CompileError("Could not found JavaCompiler, might not run in jdk.");
        else {
            try(StandardJavaFileManager standardJavaFileManager = compiler.getStandardFileManager(null, null, StandardCharsets.UTF_8)) {
                if (this.classpath != null) 
                    standardJavaFileManager.setLocation(StandardLocation.CLASS_PATH, classpath);
                VirtualFileManager fileManager = new VirtualFileManager(standardJavaFileManager, this.javaFileObjectMap);
                
                try {
                    StringWriter stringWriter = new StringWriter();
                    JavaCompiler.CompilationTask task = compiler.getTask(stringWriter, fileManager, (DiagnosticListener<? super JavaFileObject>)(Object) listener, null, null, javaFileObjectMap.values());
                    if (task.call() == Boolean.TRUE) {
                        this.results = fileManager.getClasses();
                        return null;
                    } else return new CompileError(stringWriter.toString());
                } catch (Throwable e) {
                    return new CompileError(e);
                }
            } catch (Throwable e) {
                return new CompileError(e);
            }
        }
    }



    public void setListener(DiagnosticListener<VirtualJavaFileObject> listener) {
        this.listener = listener;
    }


}
