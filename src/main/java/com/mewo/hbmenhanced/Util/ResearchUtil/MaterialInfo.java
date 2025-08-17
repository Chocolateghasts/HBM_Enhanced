package com.mewo.hbmenhanced.Util.ResearchUtil;

import com.mewo.hbmenhanced.Util.ResearchUtil.ResearchRegistry.*;

public class MaterialInfo {
    public final MaterialType type;
    public final MaterialRarity rarity;
    public final String material;

    public MaterialInfo(ResearchRegistry.MaterialType type, ResearchRegistry.MaterialRarity rarity, String material) {
        this.type = type;
        this.rarity = rarity;
        this.material = material;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MaterialInfo)) return false;
        MaterialInfo that = (MaterialInfo) o;
        return type == that.type &&
                rarity == that.rarity &&
                material.equals(that.material);
    }

    @Override
    public int hashCode() {
        int result = type.hashCode();
        result = 31 * result + rarity.hashCode();
        result = 31 * result + material.hashCode();
        return result;
    }
}
