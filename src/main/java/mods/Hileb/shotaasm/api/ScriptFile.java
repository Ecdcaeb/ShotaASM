package mods.Hileb.shotaasm.api;

import mods.Hileb.shotaasm.ShotaASM;
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
    public static ScriptFile create(String name, String rawText) {
        try (BufferedReader reader = new BufferedReader(new StringReader(rawText))) {
            List<String> commentLines = new ArrayList<>();
            StringBuilder nonCommentText = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (StringUtils.startsWith(line, "#")) {
                    commentLines.add(line.substring(1));
                } else {
                    nonCommentText.append(line).append("\n");
                }
            }

            if(nonCommentText.length() > 0 && nonCommentText.charAt(nonCommentText.length()-1) == '\n'){
                nonCommentText.deleteCharAt(nonCommentText.length()-1);
            }

            Multimap<String, String> map = HashMultimap.create();
            for (String s : commentLines) {
                int i = s.indexOf(' ');
                if (i > 0) {
                    map.put(s.substring(0, i), s.substring(i + 1));
                }
            }
            if (!map.containsKey("compiler")) {
                map.put("compiler", "javaShota");
            }

            if (ShotaASM.DEBUG) {
                ShotaASM.LOGGER.info("Script Debug : {}", name);
                ShotaASM.LOGGER.info("Processed Text : \n {}", nonCommentText.toString());
            }

            return new ScriptFile(name, nonCommentText.toString(), map, new HashMap<>());
        } catch (Exception e) {
            ShotaASM.LOGGER.error("Error creating ScriptFile for '{}': {}", name, e.getMessage(), e);
            return null;
        }
    }
}
