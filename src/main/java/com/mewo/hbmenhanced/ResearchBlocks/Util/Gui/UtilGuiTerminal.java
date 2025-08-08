package com.mewo.hbmenhanced.ResearchBlocks.Util.Gui;

import com.mewo.hbmenhanced.OpenComputers.ResearchNode;
import com.mewo.hbmenhanced.OpenComputers.ResearchTree;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

public class UtilGuiTerminal {

    public static float[] getColorForNode(ResearchNode node, ResearchTree tree) {
        if (node.isUnlocked) {
            return new float[] {0.0f, 1.0f, 0.0f, 1.0f};
        } else if (tree.canUnlock(node.id)) {
            return new float[] {0.75f, 0.75f, 0.75f, 1.0f};
        } else {
            return new float[] {0.25f, 0.25f, 0.25f, 1.0f};
        }
    }

    public static void setColor(float[] colors) {
        if (colors == null || colors.length != 4) {
            resetColor(); // Reset if invalid colors
            return;
        }
        GL11.glColor4f(colors[0], colors[1], colors[2], colors[3]);
    }

    public static void resetColor() {
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
    }

    public static List<List<int[]>> getSquaredPaths(int x1, int y1, int x2, int y2) {
        List<int[]> path1 = new ArrayList<>();
        path1.add(new int[] {x1, y1});
        path1.add(new int[] {x2, y1});
        path1.add(new int[] {x2, y2});

        List<int[]> path2 = new ArrayList<>();
        path2.add(new int[] {x1, y1});
        path2.add(new int[] {x1, y2});
        path2.add(new int[] {x2, y2});

        List<List<int[]>> result = new ArrayList<>();
        result.add(path1);
        result.add(path2);

        return result;
    }
}
