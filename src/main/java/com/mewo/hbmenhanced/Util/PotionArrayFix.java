package com.mewo.hbmenhanced.Util;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import net.minecraft.potion.Potion;

public class PotionArrayFix {

    public static void expandPotionArray() {
        try {
            Field field = Potion.class.getDeclaredField("potionTypes");
            field.setAccessible(true);

            Potion[] old = (Potion[]) field.get(null);
            Potion[] newer = new Potion[256];

            System.arraycopy(old, 0, newer, 0, old.length);

            Field modifiers = Field.class.getDeclaredField("modifiers");
            modifiers.setAccessible(true);
            modifiers.setInt(field, field.getModifiers() & ~Modifier.FINAL);

            field.set(null, newer);

        } catch (Exception e) {
            throw new RuntimeException("Failed to expand potion array", e);
        }
    }
}
