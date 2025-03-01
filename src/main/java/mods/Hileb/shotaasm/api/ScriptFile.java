package mods.Hileb.shotaasm.api;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nullable;
import java.io.BufferedReader;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public record ScriptFile(String name, String text, Multimap<String, String> property, HashMap<String, Object> data) {

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
                    if (!map.containsKey("compiler")) map.put("compiler", "javaShota");

                    byte[] b1 = IOUtils.toByteArray(reader, StandardCharsets.UTF_8);
                    byte[] b2 = new byte[line.length() + 1 + b1.length];
                    System.arraycopy(line.getBytes(), 0, b2, 0, line.length());
                    b2[line.length()] = '\n';
                    System.arraycopy(b1, line.length() + 1, b2, 0, b1.length);

                    return new ScriptFile(
                            name,
                            new String(b2),
                            map, new HashMap<>()
                    );
                }
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }
}
