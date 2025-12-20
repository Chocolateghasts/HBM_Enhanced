package com.mewo.hbmenhanced.potion;

import net.minecraft.potion.Potion;

public class ModPotions {

    public static Potion ballCancer;
    public static Potion ballsFallenOff;
    public static Potion noBalls;

    public static void init() {
        ballCancer = new PotionBallCancer(32);
        ballsFallenOff = new PotionBallsFallenOff(33);
        noBalls = new PotionNoBalls(34);
    }
}
