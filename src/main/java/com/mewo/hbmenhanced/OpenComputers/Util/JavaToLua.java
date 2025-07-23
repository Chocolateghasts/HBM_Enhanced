package com.mewo.hbmenhanced.OpenComputers.Util;

import com.mewo.hbmenhanced.OpenComputers.ResearchNode;
import com.mewo.hbmenhanced.Util.ResearchTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JavaToLua {
    public static Map<String, Object> nodeToLua(ResearchNode node) {
        Map<String, Object> tmp = new HashMap<>();
        tmp.put("id", node.id);
        tmp.put("name", node.name);
        tmp.put("category", node.category);
        tmp.put("description", node.description);
        tmp.put("level", node.level);
        tmp.put("unlocked", node.isUnlocked);
        tmp.put("dependencies", node.dependencies);
        tmp.put("requirements", node.requirements);
        List<Map<String, Object>> luaTemplates = new ArrayList<>();
        if (node.templates != null) {
            for (ResearchTemplate template : node.templates) {
                Map<String, Object> map = new HashMap<>();
                map.put("type", template.type);
                map.put("id", template.id);
                luaTemplates.add(map);
            }
        }
        tmp.put("templates", luaTemplates);
        return tmp;
    }
}
