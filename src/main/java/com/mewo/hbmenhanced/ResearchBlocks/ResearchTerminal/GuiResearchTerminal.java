package com.mewo.hbmenhanced.ResearchBlocks.ResearchTerminal;

import com.mewo.hbmenhanced.OpenComputers.ResearchNode;
import com.mewo.hbmenhanced.OpenComputers.ResearchTree;
import com.mewo.hbmenhanced.Packets.PacketResearchTree;
import com.mewo.hbmenhanced.Packets.PacketResearchTreeRequest;
import com.mewo.hbmenhanced.ResearchBlocks.Util.ClientResearchSync;
import com.mewo.hbmenhanced.ResearchBlocks.Util.Gui.GuiResearchCategoryButton;
import com.mewo.hbmenhanced.items.ItemRenderer;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.util.*;

import static com.mewo.hbmenhanced.ResearchBlocks.Util.Gui.UtilGuiTerminal.*;
import static com.mewo.hbmenhanced.hbmenhanced.network;

public class GuiResearchTerminal extends GuiScreen {
    public static final ResourceLocation background = new ResourceLocation("hbmenhanced", "textures/gui/research_tree/background.png");
    public ResearchTree tree;
    private int guiLeft, guiTop;
    public String team = "";

    public static final int NORMAL_NODE_X = 1;
    public static final int NORMAL_NODE_Y = 229;
    public static final int RARE_NODE_X = 29;
    public static final int RARE_NODE_Y = 229;
    public static final int SPECIAL_NODE_X = 59;
    public static final int SPECIAL_NODE_Y = 229;

    public static final int VIEWPORT_LEFT = 20;
    public static final int VIEWPORT_TOP = 29;
    public static final float MAX_ZOOM = 4.0f;
    public static final float MIN_ZOOM = 0.5f;
    private static final float LERP_SPEED = 0.625f;

    private static final int CATEGORY_BUTTON_Y = 7;

    private int scrollX;
    private int scrollY;
    private float zoom = 1.0f;
    private boolean isDragging = false;
    private int dragStartX, dragStartY;
    private int scrollStartX, scrollStartY;
    private float currentScrollX, currentScrollY = 0;
    private float currentZoom = 1.0f;
    private float targetZoom = 1.0f;
    private float targetScrollX, targetScrollY = 0;

    public final ItemRenderer itemRenderer = new ItemRenderer();

    public String[] researchCategories = {
            "basic", "advanced", "nuclear", "weaponry", "space", "industrial", "chemical"
    };
    public String currentCategory = "basic";

    @Override
    @SuppressWarnings("unchecked")
    public void initGui() {
        super.initGui();
        EntityClientPlayerMP playerMP = Minecraft.getMinecraft().thePlayer;
        String team = playerMP.getEntityData().getString("hbmenhanced:team");
        PacketResearchTreeRequest pkt = PacketResearchTreeRequest.versionCheck(team, ClientResearchSync.getVersion());
        network.sendToServer(pkt);
        this.team = team;
        int texWidth = 256;
        int texHeight = 192;
        guiLeft = (this.width - texWidth) / 2;
        guiTop = (this.height - texHeight) / 2;

        int catX = guiLeft + 23;
        int currentId = 0;
        int padding = 3;
        for (String cat : researchCategories) {
            GuiResearchCategoryButton btn = new GuiResearchCategoryButton(currentId, catX, guiTop + CATEGORY_BUTTON_Y, cat, fontRendererObj);
            buttonList.add(btn);
            catX += padding + btn.width;
            currentId++;
        }
    }

    public GuiResearchTerminal(String team) {
        this.tree = ClientResearchSync.getTree(team);
    }

    private boolean isInViewport(int x, int y, int width, int height) {
        int left = guiLeft + VIEWPORT_LEFT;
        int top = guiTop + VIEWPORT_TOP;
        int right = left + 234;
        int bottom = top + 154;

        return !(x + width < left || x > right || y + height < top || y > bottom);
    }

    private boolean isLineInViewport(int x1, int y1, int x2, int y2) {
        int left = guiLeft + VIEWPORT_LEFT;
        int top = guiTop + VIEWPORT_TOP;
        int right = left + 234;
        int bottom = top + 154;

        int minX = Math.min(x1, x2);
        int maxX = Math.max(x1, x2);
        int minY = Math.min(y1, y2);
        int maxY = Math.max(y1, y2);

        return !(maxX < left || minX > right || maxY < top || minY > bottom);
    }



    private void bindBackgroundTexture() {
        this.mc.getTextureManager().bindTexture(background);
    }

    private void drawBackground() {
        bindBackgroundTexture();

        int texWidth = 256;
        int texHeight = 192;

        int x = (this.width - texWidth) / 2;
        int y = (this.height - texHeight) / 2;

        drawTexturedModalRect(x, y, 0, 0, texWidth, texHeight);
    }

    private void clampTargetScrollAndZoom() {
        targetZoom = Math.max(MIN_ZOOM, Math.min(targetZoom, MAX_ZOOM));

        int contentWidth = 700;
        int contentHeight = 600;

        int viewportWidth = 234;
        int viewportHeight = 154;

        float visibleWorldWidth = viewportWidth / targetZoom;
        float visibleWorldHeight = viewportHeight / targetZoom;

        targetScrollX = Math.max(0, Math.min(targetScrollX, (int)(contentWidth - visibleWorldWidth)));
        targetScrollY = Math.max(0, Math.min(targetScrollY, (int)(contentHeight - visibleWorldHeight)));
    }

    private float lerp(float start, float end, float speed) {
        return start + (end - start) * speed;
    }

    private void drawArrowHead(int x, int y, String direction) {
        int u = 0;
        int v = 0;
        int w = 0;
        int h = 0;

        switch (direction) {
            case "LEFT":   u = 107; v = 224; w = 8; h = 12;      break;
            case "RIGHT":  u = 115; v = 224; w = 8; h = 12;      break;
            case "UP":     u = 124; v = 223; w = 12; h = 8;      break;
            case "DOWN":   u = 124; v = 231; w = 12; h = 8;      break;
        }
//        System.out.println("Drawing arrow of dir: " + direction);
//        System.out.println("Arrow X: " + x);
//        System.out.println("Arrow Y: " + y);
//        System.out.println("Arrow U: " + u);
//        System.out.println("Arrow V: " + v);
        drawTexturedModalRect(x, y, u, v, w, h);
    }

    private void enableScissor() {
        int scaleFactor = Minecraft.getMinecraft().gameSettings.guiScale;
        if (scaleFactor == 0) scaleFactor = 1;

        int left = guiLeft + VIEWPORT_LEFT+1;  // 20
        int top = guiTop + VIEWPORT_TOP+1;     // 29

        int width = 215;   // 235 - 20
        int height = 126;  // 155 - 29

        int scissorX = left * scaleFactor;
        int scissorY = (this.height - (top + height)) * scaleFactor;
        int scissorWidth = width * scaleFactor;
        int scissorHeight = height * scaleFactor;

        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        GL11.glScissor(scissorX, scissorY, scissorWidth, scissorHeight);
    }


    private void disableScissor() {
        GL11.glDisable(GL11.GL_SCISSOR_TEST);
    }


    private void drawPath(ResearchNode start, ResearchNode end) {
        if (!start.category.equalsIgnoreCase(end.category)) return;
        int startX = start.x + 13;
        int startY = start.y + 13;
        int endX = end.x + 13;
        int endY = end.y + 13;

        int screenStartX = guiLeft + VIEWPORT_LEFT + (int)((startX - currentScrollX) * currentZoom);
        int screenStartY = guiTop + VIEWPORT_TOP + (int)((startY - currentScrollY) * currentZoom);
        int screenEndX = guiLeft + VIEWPORT_LEFT + (int)((endX - currentScrollX) * currentZoom);
        int screenEndY = guiTop + VIEWPORT_TOP + (int)((endY - currentScrollY) * currentZoom);

        List<List<int[]>> squaredPaths = getSquaredPaths(screenStartX, screenStartY, screenEndX, screenEndY);
        List<int[]> chosenPath = squaredPaths.get(0);

        List<int[]> cleanedPath = new ArrayList<>();
        int[] last = null;
        for (int[] p : chosenPath) {
            if (last == null || p[0] != last[0] || p[1] != last[1]) {
                cleanedPath.add(p);
            }
            last = p;
        }
        chosenPath = cleanedPath;

        final int NODE_W = 26;
        final int NODE_H = 26;
        int[] secondLast = chosenPath.get(chosenPath.size() - 2);
        int[] lastPoint = chosenPath.get(chosenPath.size() - 1);

        if (lastPoint[0] > secondLast[0]) {
            lastPoint[0] = guiLeft + VIEWPORT_LEFT + (int)(((end.x) - currentScrollX) * currentZoom);
            lastPoint[1] = guiTop + VIEWPORT_TOP + (int)(((end.y + NODE_H / 2) - currentScrollY) * currentZoom);
        } else if (lastPoint[0] < secondLast[0]) {
            lastPoint[0] = guiLeft + VIEWPORT_LEFT + (int)(((end.x + NODE_W) - currentScrollX) * currentZoom);
            lastPoint[1] = guiTop + VIEWPORT_TOP + (int)(((end.y + NODE_H / 2) - currentScrollY) * currentZoom);
        } else if (lastPoint[1] > secondLast[1]) {
            lastPoint[0] = guiLeft + VIEWPORT_LEFT + (int)(((end.x + NODE_W / 2) - currentScrollX) * currentZoom);
            lastPoint[1] = guiTop + VIEWPORT_TOP + (int)(((end.y) - currentScrollY) * currentZoom);
        } else if (lastPoint[1] < secondLast[1]) {
            lastPoint[0] = guiLeft + VIEWPORT_LEFT + (int)(((end.x + NODE_W / 2) - currentScrollX) * currentZoom);
            lastPoint[1] = guiTop + VIEWPORT_TOP + (int)(((end.y + NODE_H) - currentScrollY) * currentZoom);
        }

        for (int i = 0; i < chosenPath.size() - 1; i++) {
            int[] p1 = chosenPath.get(i);
            int[] p2 = chosenPath.get(i + 1);

            int x1 = p1[0];
            int y1 = p1[1];
            int x2 = p2[0];
            int y2 = p2[1];
            if (!isLineInViewport(x1, y1, x2, y2)) {
                return;
            }

            if (y1 == y2) {
                drawHorizontalLine(Math.min(x1, x2), Math.max(x1, x2), y1, 0xFFFFFFFF);
            } else if (x1 == x2) {
                drawVerticalLine(x1, Math.min(y1, y2), Math.max(y1, y2), 0xFFFFFFFF);
            } else {
                System.err.println("Non-orthogonal segment detected!");
            }

            if (i == chosenPath.size() - 2) {
                if (x2 > x1) {
                    drawArrowHead(x2 - 8, y2 - 6, "RIGHT");
                } else if (x2 < x1) {
                    drawArrowHead(x2, y2 - 6, "LEFT");
                } else if (y2 > y1) {
                    drawArrowHead(x2 - 6, y2 - 7, "DOWN");
                } else if (y2 < y1) {
                    drawArrowHead(x2 - 6, y2, "UP");
                }
            }
        }
        resetColor();
    }


    private void drawNode(String nodeId) {
        ResearchNode node = tree.getNode(nodeId);
        if (node == null) return;
        int screenX = guiLeft + VIEWPORT_LEFT + (int)((node.x - currentScrollX) * currentZoom);
        int screenY = guiTop + VIEWPORT_TOP + (int)((node.y - currentScrollY) * currentZoom);
        int nodeSize = (int)(26 * currentZoom);

        if (!isInViewport(screenX, screenY, nodeSize, nodeSize)) {
            return;  // skip rendering this node
        }
        bindBackgroundTexture();
        resetColor();
        GL11.glPushAttrib(GL11.GL_CURRENT_BIT);



        try {
            String background = node.backgroundId;
            int textureX = 0, textureY = 0;
            switch (background) {
                case "default": textureX = NORMAL_NODE_X; textureY = NORMAL_NODE_Y; break;
                case "rare":    textureX = RARE_NODE_X;   textureY = RARE_NODE_Y;   break;
                case "special": textureX = SPECIAL_NODE_X; textureY = SPECIAL_NODE_Y; break;
                default: return;
            }

            float[] bgColor = getColorForNode(node, tree);
            setColor(bgColor);

            int x = guiLeft + VIEWPORT_LEFT + (int)((node.x - currentScrollX) * currentZoom);
            int y = guiTop + VIEWPORT_TOP + (int)((node.y - currentScrollY) * currentZoom);
            int nativeSize = 26;

            GL11.glPushMatrix();
            GL11.glTranslatef(x, y, 0);
            GL11.glScalef(currentZoom, currentZoom, 1f);

            bindBackgroundTexture();
            drawTexturedModalRect(0, 0, textureX, textureY, nativeSize, nativeSize);

            GL11.glPopMatrix();

            resetColor();

            // Draw item icon
            if (node.iconId != null) {
                String[] itemId = node.iconId.split(":");
                if (itemId.length >= 2) {
                    String modId = itemId[0];
                    String name = itemId[1];

                    Item item = GameRegistry.findItem(modId, name);
                    if (item != null) {
                        ItemStack stack = new ItemStack(item, 1, 0);
                        GL11.glPushMatrix();
                        GL11.glTranslatef(x + 5 * currentZoom, y + 5 * currentZoom, 0);
                        GL11.glScalef(currentZoom, currentZoom, 1f);
                        itemRenderer.renderItemInGUI(stack, 0, 0);
                        bindBackgroundTexture();
                        GL11.glPopMatrix();
                    }
                }
            }
        } finally {
            resetColor();
            GL11.glPopAttrib();
            bindBackgroundTexture();
        }
    }


    public void checkHover(int mouseX, int mouseY) {
        List<String> nodes = tree.getNodesForCategory(currentCategory);
        for (String nodeId : nodes) {
            ResearchNode node = tree.getNode(nodeId);
            if (node != null) {
                int nodeX = guiLeft + VIEWPORT_LEFT + (int)((node.x - currentScrollX) * currentZoom);
                int nodeY = guiTop + VIEWPORT_TOP + (int)((node.y - currentScrollY) * currentZoom);
                int size = (int)(26 * currentZoom);

                if (mouseX >= nodeX && mouseX <= nodeX + size &&
                        mouseY >= nodeY && mouseY <= nodeY + size) {

                    List<String> tooltip = new ArrayList<>();
                    tooltip.add(EnumChatFormatting.AQUA + node.name);
                    tooltip.add(EnumChatFormatting.GRAY + node.description);

                    if (node.requirements != null && !node.requirements.isEmpty()) {
                        StringBuilder reqBuilder = new StringBuilder("Requires: ");
                        boolean first = true;
                        for (Map.Entry<String, Integer> entry : node.requirements.entrySet()) {
                            if (!first) reqBuilder.append(", ");
                            first = false;
                            String formattedKey = entry.getKey().substring(0, 1).toUpperCase() + entry.getKey().substring(1).toLowerCase();
                            reqBuilder.append(entry.getValue()).append(" ").append(formattedKey);
                        }
                        tooltip.add(reqBuilder.toString());
                    }

                    if (node.dependencies != null && node.dependencies.length > 0) {
                        StringBuilder depBuilder = new StringBuilder("Needs: ");
                        boolean first = true;
                        for (String dep : node.dependencies) {
                            ResearchNode depNode = tree.getNode(dep);
                            String depName = depNode != null ? depNode.name : dep;
                            if (!first) depBuilder.append(", ");
                            first = false;
                            depBuilder.append(depName);
                        }
                        tooltip.add(depBuilder.toString());
                    }

                    tooltip.add(node.isUnlocked ? EnumChatFormatting.GREEN + "Unlocked" : EnumChatFormatting.RED + "Locked");
                    drawHoveringText(tooltip, mouseX, mouseY, fontRendererObj);
                    bindBackgroundTexture();
                    resetColor();
                    break;
                }
            }
        }
        resetColor();
    }


    public void checkNodeUnlock(int mouseX, int mouseY, int mouseButton) {
        List<String> nodes = tree.getNodesForCategory(currentCategory);
        for (String nodeId : nodes) {
            ResearchNode node = tree.getNode(nodeId);
            if (node != null) {
                int nodeX = guiLeft + VIEWPORT_LEFT + (int)((node.x - currentScrollX) * currentZoom);
                int nodeY = guiTop + VIEWPORT_TOP + (int)((node.y - currentScrollY) * currentZoom);
                int size = (int)(26 * currentZoom);
                if (mouseX >= nodeX && mouseX <= nodeX + size &&
                        mouseY >= nodeY && mouseY <= nodeY + size) {
                    if (mouseButton == 0) {
                        PacketResearchTree pkt = PacketResearchTree.nodeUnlock(nodeId, true);
                        network.sendToServer(pkt);
                        PacketResearchTreeRequest pkt1 = PacketResearchTreeRequest.node(this.team, nodeId);
                        network.sendToServer(pkt1);
                    }
                    break;
                }
            }
        }
    }


    public void drawPathsForCategory(String category) {
        List<String> nodes = tree.getNodesForCategory(category);
        for (String nodeId : nodes) {
            ResearchNode node = tree.getNode(nodeId);
            if (node != null) {
                for (String depId : node.dependencies) {
                    ResearchNode depNode = tree.getNode(depId);
                    if (depNode != null) {
                        drawPath(depNode, node);
                    }
                }
            }
        }
    }

    public void drawNodesForCategory(String category) {
        bindBackgroundTexture();
        List<String> nodes = tree.getNodesForCategory(category);
        for (String nodeId : nodes) {
            drawNode(nodeId);
        }
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button instanceof GuiResearchCategoryButton) {
            resetColor();
            currentCategory = ((GuiResearchCategoryButton) button).categoryId;
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        currentScrollX = lerp(currentScrollX, targetScrollX, LERP_SPEED);
        currentScrollY = lerp(currentScrollY, targetScrollY, LERP_SPEED);
        currentZoom = lerp(currentZoom, targetZoom, LERP_SPEED);
        drawBackground();

        if (tree == null || tree.nodes == null || tree.nodes.isEmpty()) return;

        enableScissor();

        drawPathsForCategory(currentCategory);
        resetColor();
        bindBackgroundTexture();
        drawNodesForCategory(currentCategory);

        disableScissor();
        super.drawScreen(mouseX, mouseY, partialTicks);
        checkHover(mouseX, mouseY);

    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);

        if (mouseButton == 1) {
            isDragging = true;
            dragStartX = mouseX;
            dragStartY = mouseY;
            scrollStartX = (int) currentScrollX;
            scrollStartY = (int) currentScrollY;
        } else {
            checkNodeUnlock(mouseX, mouseY, mouseButton);
        }
    }

    @Override
    protected void mouseMovedOrUp(int mouseX, int mouseY, int which) {
        super.mouseMovedOrUp(mouseX, mouseY, which);

        if (which == 1) {
            isDragging = false;
        }
    }

    @Override
    protected void mouseClickMove(int mouseX, int mouseY, int mouseButton, long time) {
        super.mouseClickMove(mouseX, mouseY, mouseButton, time);
        if (isDragging && mouseButton == 1) {
            int dx = mouseX - dragStartX;
            int dy = mouseY - dragStartY;

            targetScrollX = scrollStartX - (int)(dx / targetZoom);
            targetScrollY = scrollStartY - (int)(dy / targetZoom);

            clampTargetScrollAndZoom();
        }
    }

    @Override
    public void handleMouseInput() {
        super.handleMouseInput();
        int wheel = Mouse.getEventDWheel();
        if (wheel != 0) {
            float oldZoom = targetZoom;
            if (wheel > 0) {
                targetZoom *= 1.1f;
            } else {
                targetZoom /= 1.1f;
            }
            clampTargetScrollAndZoom();

            int mouseX = Mouse.getEventX() * this.width / this.mc.displayWidth;
            int mouseY = this.height - Mouse.getEventY() * this.height / this.mc.displayHeight - 1;

            float relX = (mouseX - guiLeft - VIEWPORT_LEFT) / oldZoom + targetScrollX;
            float relY = (mouseY - guiTop - VIEWPORT_TOP) / oldZoom + targetScrollY;

            targetScrollX = relX - (mouseX - guiLeft - VIEWPORT_LEFT) / targetZoom;
            targetScrollY = relY - (mouseY - guiTop - VIEWPORT_TOP) / targetZoom;

            clampTargetScrollAndZoom();
        }
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}