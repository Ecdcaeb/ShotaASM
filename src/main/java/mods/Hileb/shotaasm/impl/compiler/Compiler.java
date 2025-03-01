package mods.Hileb.shotaasm.impl.compiler;

import com.google.common.collect.Iterables;
import mods.Hileb.shotaasm.impl.compiler.virtual.VirtualFileManager;
import mods.Hileb.shotaasm.impl.compiler.virtual.VirtualJavaFileObject;

import javax.tools.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Compiler {

    private final URL[] cp;

    public Compiler(){
        this.cp = null;
    }

    public Compiler(URL[] cp) {
        this.cp = cp;
    }

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

        if (cp != null) {
            try {
                List<Path> paths = new LinkedList<>();
                Iterables.addAll(paths, standardJavaFileManager.getLocationAsPaths(StandardLocation.CLASS_PATH));
                for (URL url : cp) {
                    paths.add(Paths.get(url.toURI()));
                }
                standardJavaFileManager.setLocationFromPaths(StandardLocation.CLASS_PATH, paths);
            } catch (URISyntaxException | IOException e) {
                return new CompileError("Could not load classpath", e);
            }
        }

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
