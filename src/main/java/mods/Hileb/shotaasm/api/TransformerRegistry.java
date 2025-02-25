package mods.Hileb.shotaasm.api;


import top.outlands.foundation.TransformerDelegate;

import net.minecraft.launchwrapper.IClassNameTransformer;
import net.minecraft.launchwrapper.IClassTransformer;
import top.outlands.foundation.boot.TransformerHolder;

import java.util.*;
import java.util.function.*;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;
import top.outlands.foundation.IExplicitTransformer;

public class TransformerRegistry {

    public static void registerExplicitTransformer(final int priorty, final IExplicitTransformer transformer, String... targets) {
        TransformerDelegate.registerExplicitTransformer(new IExplicitTransformer() {
            
            @Override
            public byte[] transform(byte[] basicClass) {
                return transformer.transform(basicClass);
            }

            @Override
            public int getPriority() {
                return priorty;
            }
        }, targets);
    }

    public static void registerASMExplicitTransformer(final int priorty, final Consumer<ClassNode> transformer, String... targets) {
        TransformerDelegate.registerExplicitTransformer(new IExplicitTransformer() {
            
            @Override
            public byte[] transform(byte[] basicClass) {
                ClassReader classReader = new ClassReader(basicClass);
                ClassNode classNode = new ClassNode();
                classReader.accept(classNode, 0);
                transformer.accept(classNode);
                ClassWriter classWriter = new ClassWriter(0);
                classNode.accept(classWriter);
                return classWriter.toByteArray();
            }

            @Override
            public int getPriority() {
                return priorty;
            }
        }, targets);
    }

    public static void registerASMExplicitTransformer(final Consumer<ClassNode> transformer, String... targets) {
        TransformerDelegate.registerExplicitTransformer(new IExplicitTransformer() {
            @Override
            public byte[] transform(byte[] basicClass) {
                ClassReader classReader = new ClassReader(basicClass);
                ClassNode classNode = new ClassNode();
                classReader.accept(classNode, 0);
                transformer.accept(classNode);
                ClassWriter classWriter = new ClassWriter(0);
                classNode.accept(classWriter);
                return classWriter.toByteArray();
            }
        }, targets);
    }

    public static void registerExplicitTransformer(IExplicitTransformer transformer, String... targets) {
        TransformerDelegate.registerExplicitTransformer(transformer, targets);
    }

    public static void registerClassTransformer(IClassTransformer transformer) {
        TransformerDelegate.registerTransformer(transformer);
    }

    public static void unregisterClassTransformer(IClassTransformer transformer) {
        TransformerDelegate.unRegisterTransformer(transformer);
    }
}