package com.mewo.hbmenhanced.Util;

import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.Map;
import com.mewo.hbmenhanced.ResearchManager.PointManager.ResearchType;
public class ResearchMap {

    public static final Map<String, ResearchValue> keywordMap = new LinkedHashMap<>();
    private static ResearchValue mapOf(Object... entries) {
        EnumMap<ResearchType, Integer> map = new EnumMap<>(ResearchType.class);
        for (int i = 0; i < entries.length; i += 2) {
            ResearchType type = (ResearchType) entries[i];
            int value = (int) entries[i + 1];
            map.put(type, value);
        }
        return new ResearchValue(map);
    }

    static {
        keywordMap.put("cadmium", mapOf(ResearchType.STRUCTURAL, 35));
        keywordMap.put("technetium", mapOf(ResearchType.STRUCTURAL, 35));
        keywordMap.put("iron", mapOf(ResearchType.STRUCTURAL, 10));

        keywordMap.put("steel", mapOf(
                ResearchType.MACHINERY, 40,
                ResearchType.STRUCTURAL, 40,
                ResearchType.SPACE, 25));

        keywordMap.put("uranium 238", mapOf(ResearchType.WEAPONRY, 45));
        keywordMap.put("uranium 235", mapOf(ResearchType.NUCLEAR, 50));
        keywordMap.put("uranium 233", mapOf(ResearchType.NUCLEAR, 40));

        keywordMap.put("plutonium 240", mapOf(ResearchType.NUCLEAR, 40));
        keywordMap.put("plutonium 241", mapOf(ResearchType.NUCLEAR, 75));
        keywordMap.put("plutonium 238", mapOf(ResearchType.NUCLEAR, 60));
        keywordMap.put("plutonium 239", mapOf(
                ResearchType.EXPLOSIVES, 75,
                ResearchType.NUCLEAR, 25));
        keywordMap.put("plutonium fuel", mapOf(ResearchType.NUCLEAR, 60));
        keywordMap.put("plutonium reactor", mapOf(ResearchType.NUCLEAR, 70));
        keywordMap.put("plutonium", mapOf(ResearchType.NUCLEAR, 25));

        keywordMap.put("thorium 232", mapOf(ResearchType.NUCLEAR, 40));
        keywordMap.put("thorium fuel", mapOf(ResearchType.NUCLEAR, 50));

        keywordMap.put("digamma", mapOf(ResearchType.EXOTIC, 100));

        keywordMap.put("americium 241", mapOf(ResearchType.NUCLEAR, 60));
        keywordMap.put("americium 242", mapOf(ResearchType.NUCLEAR, 60));
        keywordMap.put("americium reactor grade", mapOf(ResearchType.NUCLEAR, 70));
        keywordMap.put("americium fuel", mapOf(ResearchType.NUCLEAR, 70));

        keywordMap.put("radium", mapOf(ResearchType.NUCLEAR, 50));
        keywordMap.put("polonium", mapOf(ResearchType.NUCLEAR, 60));
        keywordMap.put("gold 198", mapOf(ResearchType.NUCLEAR, 30));
        keywordMap.put("lead 209", mapOf(ResearchType.NUCLEAR, 30));
        keywordMap.put("strontium 90", mapOf(ResearchType.NUCLEAR, 40));
        keywordMap.put("cobalt 60", mapOf(ResearchType.NUCLEAR, 50));
        keywordMap.put("neptunium", mapOf(ResearchType.NUCLEAR, 60));
        keywordMap.put("curium", mapOf(ResearchType.NUCLEAR, 20));

        keywordMap.put("iridium", mapOf(ResearchType.MACHINERY, 5));
        keywordMap.put("bscco", mapOf(ResearchType.MACHINERY, 5));
        keywordMap.put("stainless steel", mapOf(ResearchType.MACHINERY, 30));
        keywordMap.put("nickel", mapOf(ResearchType.MACHINERY, 25));
        keywordMap.put("bismuth arsenic bronze", mapOf(ResearchType.MACHINERY, 35));
        keywordMap.put("arsenic", mapOf(ResearchType.MACHINERY, 20));
        keywordMap.put("calcium", mapOf(ResearchType.CHEMICAL, 10));
        keywordMap.put("tantalium", mapOf(ResearchType.ELECTRONICS, 40));
        keywordMap.put("beryllium", mapOf(ResearchType.MACHINERY, 45));
        keywordMap.put("cobalt", mapOf(ResearchType.MACHINERY, 30));

        keywordMap.put("boron", mapOf(
                ResearchType.MACHINERY, 25,
                ResearchType.NUCLEAR, 30));
        keywordMap.put("graphite", mapOf(
                ResearchType.MACHINERY, 20,
                ResearchType.NUCLEAR, 30));

        keywordMap.put("firebrick", mapOf(ResearchType.MACHINERY, 15));
        keywordMap.put("high speed steel", mapOf(ResearchType.MACHINERY, 50));
        keywordMap.put("durasteel", mapOf(ResearchType.MACHINERY, 50));

        keywordMap.put("polymer", mapOf(ResearchType.CHEMICAL, 30));
        keywordMap.put("bakelite", mapOf(ResearchType.CHEMICAL, 30));
        keywordMap.put("latex", mapOf(ResearchType.CHEMICAL, 15));
        keywordMap.put("rubber", mapOf(ResearchType.CHEMICAL, 20));
        keywordMap.put("hard plastic", mapOf(ResearchType.CHEMICAL, 25));
        keywordMap.put("pvc", mapOf(ResearchType.CHEMICAL, 25));

        keywordMap.put("tungsten", mapOf(ResearchType.MACHINERY, 60));
        keywordMap.put("crystalline fullerite", mapOf(ResearchType.EXOTIC, 80));
        keywordMap.put("advanced alloy", mapOf(ResearchType.MACHINERY, 100));
        keywordMap.put("minecraft grade copper", mapOf(ResearchType.MACHINERY, 40));
        keywordMap.put("gallium arsenide", mapOf(ResearchType.ELECTRONICS, 60));
        keywordMap.put("lanthanium", mapOf(ResearchType.ELECTRONICS, 70));
        keywordMap.put("titanium", mapOf(
                ResearchType.MACHINERY, 80,
                ResearchType.STRUCTURAL, 60));

        keywordMap.put("desh", mapOf(ResearchType.MACHINERY, 90));
        keywordMap.put("ferrouranium", mapOf(ResearchType.MACHINERY, 30));
        keywordMap.put("starmetal", mapOf(ResearchType.MACHINERY, 100));
        keywordMap.put("niobium", mapOf(ResearchType.ELECTRONICS, 30));
        keywordMap.put("bismuth", mapOf(ResearchType.ELECTRONICS, 65));
        keywordMap.put("schrabidium", mapOf(ResearchType.NUCLEAR, 40));
        keywordMap.put("magnetized tungsten", mapOf(ResearchType.MACHINERY, 60));
        keywordMap.put("ferric schrabidate", mapOf(ResearchType.MACHINERY, 50));
        keywordMap.put("solinium", mapOf(ResearchType.NUCLEAR, 90));
        keywordMap.put("actinium", mapOf(ResearchType.NUCLEAR, 25));
        keywordMap.put("australium", mapOf(ResearchType.NUCLEAR, 80));
        keywordMap.put("saturnite", mapOf(ResearchType.MACHINERY, 70));
        keywordMap.put("euphenium", mapOf(ResearchType.MACHINERY, 200));
        keywordMap.put("dineutronium", mapOf(ResearchType.MACHINERY, 250));
        keywordMap.put("electronium", mapOf(ResearchType.MACHINERY, 300));
        keywordMap.put("osmiridium", mapOf(ResearchType.MACHINERY, 250));
        keywordMap.put("hafnium", mapOf(ResearchType.MACHINERY, 100));
        keywordMap.put("chinesium", mapOf(ResearchType.STRUCTURAL, -9999999));
    }
}
