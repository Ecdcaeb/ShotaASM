package mods.Hileb.shotaasm.gradle;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.jar.*;

public class CurseforgeTransformer {

    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            System.err.println("Usage: CurseforgeTransformer <input-jar> <output-jar>");
            System.exit(1);
        }
        
        Path inputJar = Paths.get(args[0]);
        Path outputJar = Paths.get(args[1]);
        
        System.out.println("Transforming JAR: " + inputJar);
        System.out.println("Output JAR: " + outputJar);
        
        transformJar(inputJar, outputJar);
    }

    public static void transformJar(Path inputJarPath, Path outputJarPath) throws IOException {
        try (JarFile jarFile = new JarFile(inputJarPath.toFile());
             JarOutputStream jos = new JarOutputStream(Files.newOutputStream(outputJarPath))) {
            
            Manifest manifest = jarFile.getManifest();
            if (manifest != null) {
                jos.putNextEntry(new JarEntry(Manifest.MANIFEST_NAME));
                manifest.write(jos);
                jos.closeEntry();
            }

            Enumeration<JarEntry> entries = jarFile.entries();
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                if (entry.isDirectory()) continue;
                
                jos.putNextEntry(new JarEntry(entry.getName().replace("shota", "_script_")));
                
                if (entry.getName().endsWith(".class")) {
                    try (InputStream is = jarFile.getInputStream(entry)) {
                        jos.write(transformClass(is));
                    }
                } 

                else {
                    try (InputStream is = jarFile.getInputStream(entry)) {
                        is.transferTo(jos);
                    }
                }
                jos.closeEntry();
            }
        }
    }

    private static byte[] transformClass(InputStream clazz) {
        ClassWriter writer;
        new ClassReader(is).accept(new ClassRemapper(writer = new ClassWriter(0), CurseforgeRemapper.INSTANCE), 0);
        return writer.toByteArray();
    }

    static class CurseforgeRemapper extends Remapper {
        private static final CurseforgeRemapper INSTANCE = new CurseforgeRemapper();

        @Override
        public String map(String typeName) {
            return typeName.replace("shota", "_script_").replace("Shota", "_Script_");
        }
    }
}