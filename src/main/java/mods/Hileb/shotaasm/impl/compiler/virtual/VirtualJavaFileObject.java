package mods.Hileb.shotaasm.impl.compiler.virtual;

import javax.tools.SimpleJavaFileObject;
import java.io.ByteArrayOutputStream;
import java.net.URI;
import java.io.IOException;

public class VirtualJavaFileObject extends SimpleJavaFileObject {

    private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

    private final String content;

    public VirtualJavaFileObject(String className, String content) {
        super(URI.create("string:///" + className.replace(".", "/") + Kind.SOURCE.extension), Kind.SOURCE);
        this.content = content;
    }

    public byte[] getBytecode() {
        return outputStream.toByteArray();
    }

    @Override
    public ByteArrayOutputStream openOutputStream() {
        return outputStream;
    }

    @Override
    public CharSequence getCharContent(boolean ignoreEncodingErrors) throws IOException {
        return content;
    }
}
