package mods.Hileb.shotaasm.impl;

import com.google.common.collect.HashMultimap;

public class EventHandler {
    private static final HashMultimap<String, Runnable> eventTasks = HashMultimap.create();

    public static void executeEvent(String evt) {
        eventTasks.get(evt).forEach(Runnable::run);
        eventTasks.removeAll(evt);
    }

    public static void addEvent(String evt, Runnable r) {
        eventTasks.put(evt, r);
    }
}