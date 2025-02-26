package mods.Hileb.shotaasm.api;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nullable;
import java.io.BufferedReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public record ScriptFile(String name, String text, Multimap<String, String> property) {

    @Nullable
    public static ScriptFile create(String name, String rawText){
        try (BufferedReader reader = new BufferedReader(new StringReader(rawText))) {
            List<String> lines = new ArrayList<>();
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (StringUtils.startsWith(line, "#")) {
                    lines.add(line.substring(1));
                } else {
                    Multimap<String, String> map = HashMultimap.create();
                    for (String s : lines) {
                        int i = s.indexOf(' ');
                        if (i > 0) {
                            map.put(s.substring(0, i), s.substring(i + 1));
                        }
                    }
                    if (!map.containsKey("compiler")) map.put("compiler", "shota");
                    return new ScriptFile(
                            name,
                            line + "\n" + reader.lines().collect(Collectors.joining("\n")),
                            map
                    );
                }
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }
}
