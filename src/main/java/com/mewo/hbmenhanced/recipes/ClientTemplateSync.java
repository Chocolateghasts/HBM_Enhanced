package com.mewo.hbmenhanced.recipes;

import com.mewo.hbmenhanced.Util.ResearchTemplate;

import java.util.*;

public class ClientTemplateSync {
    public static int version = -1;
    public static Set<ResearchTemplate> templates = new HashSet<>();

    private static List<ResearchTemplate> getByType(String type) {
        List<ResearchTemplate> tmp = new ArrayList<>();
        for (ResearchTemplate template : templates) {
            if (template.getType().equalsIgnoreCase(type)) {
                tmp.add(template);
            }
        }
        System.out.println("Got templates for type " + type);
        System.out.println("Templates are: " + Arrays.toString(tmp.toArray()));
        return tmp;
    }

    public static void addTemplate(ResearchTemplate template) {
        templates.add(template);
    }

    public static void removeTemplate(ResearchTemplate template) {
        templates.remove(template);
    }


    public static List<ResearchTemplate> getAssemblyTemplates() {
        return getByType("a");
    }

    public static List<ResearchTemplate> getChemicalTemplates() {
        return getByType("b");
    }

    public static List<ResearchTemplate> getCrucibleTemplates() {
        return getByType("c");
    }

}
