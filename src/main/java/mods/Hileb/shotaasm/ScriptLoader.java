package mods.Hileb.shotaasm;

import com.google.common.collect.Iterables;
import mods.Hileb.shotaasm.api.IScriptCompiler;
import mods.Hileb.shotaasm.api.IScriptLocator;
import net.minecraft.launchwrapper.Launch;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

public class ScriptLoader {
    private static Collection<IScriptLocator> locators;
    private static HashMap<String, IScriptCompiler> compilers = new HashMap<>();

    public static void initialize() {
       locators = loadServices(IScriptLocator.class).stream().map( aClass -> {
           try {
               return aClass.getConstructor().newInstance();
           } catch (InvocationTargetException | InstantiationException | IllegalAccessException |
                    NoSuchMethodException e) {
               throw new RuntimeException(e);
           }
       }).collect(Collectors.toSet());

       loadServices(IScriptCompiler.class).stream().map( aClass -> {
           try {
               return aClass.getConstructor().newInstance();
           } catch (InvocationTargetException | InstantiationException | IllegalAccessException |
                    NoSuchMethodException e) {
               throw new RuntimeException(e);
           }
       }).forEach((i) -> compilers.put(i.name(), i));
    }

    public static Collection<Runnable> loadScripts() {
        return locators.stream().map(IScriptLocator::getScripts).flatMap(Collection::stream)
                .filter(Objects::nonNull)
                .map((script) -> compilers.get(Iterables.getFirst(script.property().get("compiler"), null))
                        .compile(script)).collect(Collectors.toSet());
    }

    @SuppressWarnings("unchecked")
    public static <T> Collection<Class<? extends T>> loadServices(Class<T> cls) {
        HashSet<Class<?>> clss = new HashSet<>();
        try {
            Enumeration<URL> e = Launch.classLoader.getResources("META-INF/services/" + cls.getName());
            while (e.hasMoreElements()) {
                URL url = e.nextElement();
                try (InputStream stream = url.openStream()) {
                    clss.addAll(new BufferedReader(new InputStreamReader(stream)).lines().map(
                            s -> {
                                try {
                                    return Launch.classLoader.findClass(s);
                                } catch (ClassNotFoundException e2){
                                    throw new RuntimeException(e2);
                                }
                            }).collect(Collectors.toSet()));
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Could not load services " + cls.getName() , e);
        }
        return (Collection<Class<? extends T>>) (Object) clss;
    }
}