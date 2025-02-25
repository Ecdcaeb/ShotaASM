package mods.somebody.example;

import java.lang.String;

import net.minecraftforge.fml.common.Mod;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(modid = Example.MODID)
public class Example {
    public static final String MODID = "modid";
    
    public static Logger LOGGER = LogManager.getLogger(MODID);

}