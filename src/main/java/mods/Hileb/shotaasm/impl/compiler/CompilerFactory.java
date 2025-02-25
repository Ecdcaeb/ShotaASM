package mods.Hileb.shotaasm.impl.compiler;

import com.google.common.collect.Iterables;
import mods.Hileb.shotaasm.impl.compiler.virtual.VirtualFileManager;
import mods.Hileb.shotaasm.impl.compiler.virtual.VirtualJavaFileObject;
import mods.Hileb.shotaasm.ShotaASM;

import javax.tools.*;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

public class CompilerFactory{
    public List<File> classpath = null;

    public CompilerFactory(URL[] classpath) {
        this.classpath = new ArrayList<>();
        for(URL url : classpath) {
            if ("file".equals(url.getProtocol())) {
                try {
                    this.classpath.add(new File(url.toURI()));
                } catch (URISyntaxException e) {}
            }
        }

        if (ShotaASM.DEBUG) {
            ShotaASM.LOGGER.info("Java Compiler Factory Debug.");
            String s0 = "";
            for (File s : this.classpath) {
                s0 = s0 + s;
            }
            ShotaASM.LOGGER.info("Classpath : {}", s0);
        }
    }

    public Compiler compiler(){
        return new Compiler(this.classpath);
    }
}