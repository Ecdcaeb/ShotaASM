package mods.Hileb.shotaasm.impl.compiler.virtual;

import javax.tools.*;
import java.io.IOException;
import java.util.Map;
import java.util.stream.Collectors;

public class VirtualFileManager extends ForwardingJavaFileManager<StandardJavaFileManager> {
    private final Map<String, VirtualJavaFileObject> sources;

    public VirtualFileManager(StandardJavaFileManager javaFileManager, Map<String, VirtualJavaFileObject> sources){
        super(javaFileManager);
        this.sources = sources;
    }

    public Map<String, byte[]> getClasses() {
        return sources.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, v -> v.getValue().getBytecode()));
    }


    @Override
    public JavaFileObject getJavaFileForOutput(Location location, String className, JavaFileObject.Kind kind, FileObject sibling) throws IOException, IOException {
        if (JavaFileObject.Kind.CLASS == kind) {
            VirtualJavaFileObject virtualJavaFileObject = new VirtualJavaFileObject(className, null);
            sources.put(className, virtualJavaFileObject);
            return virtualJavaFileObject;
        } else {
            return super.getJavaFileForOutput(location, className, kind, sibling);
        }
    }

}
