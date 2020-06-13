package dev.mattrm.schoolsofmagic.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.mattrm.schoolsofmagic.SchoolsOfMagicMod;
import dev.mattrm.schoolsofmagic.client.data.unlocks.UnlockNode;
import dev.mattrm.schoolsofmagic.client.gui.widget.UnlockWidget;
import dev.mattrm.schoolsofmagic.common.data.schools.School;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SchoolUnlocksView extends WidgetScreen {
    private double scrollX = 0;
    private double scrollY = 0;
    private double zoom = 1;
    private School currentSchool;
    private UnlockNode currentHover;
    private UUID uuid;
    private boolean canBuy;
    private long flashTimer = System.currentTimeMillis();
    private boolean flashState = false;
    private IUnlockViewContainer container;

    public <T extends WidgetScreen & IUnlockViewContainer> SchoolUnlocksView(int x, int y, int width, int height, UUID player, T parent, boolean canBuy) {
        super(new StringTextComponent("School Unlocks View"), x, y, width, height, parent);
        this.uuid = player;
        this.canBuy = canBuy;

        this.createUnlockWidgets();
        this.container = parent;
    }

    public double getScrollX() {
        return scrollX;
    }

    public double getScrollY() {
        return scrollY;
    }

    public double getZoom() {
        return zoom;
    }

    public School getCurrentSchool() {
        return currentSchool;
    }

    public void setCurrentSchool(School currentSchool) {
        this.currentSchool = currentSchool;
    }

    public UnlockNode getCurrentHover() {
        return currentHover;
    }

    public void setCurrentHover(UnlockNode currentHover) {
        this.currentHover = currentHover;
    }

    void reloadNodes() {
        SchoolsOfMagicMod.getInstance().getClientUnlockManager().reloadAllStates(this.uuid, this.container.getPoints());
    }

    void reloadCurrentNodes() {
        SchoolsOfMagicMod.getInstance().getClientUnlockManager().reloadAllStates(this.uuid, this.container.getPoints(), this.currentSchool);
    }

    public UUID getUuid() {
        return uuid;
    }

    public boolean isCanBuy() {
        return canBuy;
    }

    public boolean getFlashState() {
        return flashState;
    }

    @Override
    public void renderRelative(int relMouseX, int relMouseY, float partialTicks) {
        RenderSystem.pushMatrix();
        RenderSystem.enableDepthTest();
        RenderSystem.translatef(0.0F, 0.0F, 950.0F);
        RenderSystem.colorMask(false, false, false, false);
        fill(4680, 2260, -4680, -2260, -16777216);
        RenderSystem.colorMask(true, true, true, true);
        RenderSystem.translatef(0.0F, 0.0F, -950.0F);
        RenderSystem.depthFunc(518);
        fill(0, 0, this.screenWidth, this.screenHeight, -16777216);
        RenderSystem.depthFunc(515);

        RenderSystem.pushMatrix();
        RenderSystem.scaled(this.zoom, this.zoom, 1.0);
        this.renderInside();
        this.renderNodes();
        RenderSystem.popMatrix();

        RenderSystem.depthFunc(518);
        RenderSystem.translatef(0.0F, 0.0F, -950.0F);
        RenderSystem.colorMask(false, false, false, false);
        fill(4680, 2260, -4680, -2260, -16777216);
        RenderSystem.colorMask(true, true, true, true);
        RenderSystem.translatef(0.0F, 0.0F, 950.0F);
        RenderSystem.depthFunc(515);
        RenderSystem.popMatrix();


        if (System.currentTimeMillis() - flashTimer >= 500) {
            flashTimer = System.currentTimeMillis();
            flashState = !flashState;
        }
    }

    private void renderInside() {
        ResourceLocation bg = this.currentSchool.getJournalBackground();

        final int TILE_SIZE = 16;
        final int TILES_ACROSS = ((int) (this.screenWidth / TILE_SIZE / this.zoom));
        final int TILES_DOWN = ((int) (this.screenHeight / TILE_SIZE / this.zoom));
        final int SCROLL_X = MathHelper.floor(this.scrollX / 2) % TILE_SIZE;
        final int SCROLL_Y = MathHelper.floor(this.scrollY / 2) % TILE_SIZE;

        RenderSystem.pushMatrix();
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.enableBlend();

        if (bg != null) {
            this.minecraft.getTextureManager().bindTexture(bg);
        } else {
            this.minecraft.getTextureManager().bindTexture(TextureManager.RESOURCE_LOCATION_EMPTY);
        }

        for (int i = -1; i <= TILES_ACROSS + 2; i++) {
            for (int j = -1; j <= TILES_DOWN + 3; j++) {
                blit(SCROLL_X + i * TILE_SIZE, SCROLL_Y + j * TILE_SIZE, 0, 0, TILE_SIZE, TILE_SIZE, TILE_SIZE, TILE_SIZE);
            }
        }

        RenderSystem.popMatrix();
    }

    private void renderNodes() {
        this.renderWidgets(UnlockWidget.class);
    }

    private void createUnlockWidgets() {
        SchoolsOfMagicMod.getInstance().getClientUnlockManager().getAllNodes().values().forEach(node -> {
            this.addWidget(new UnlockWidget(node, this));
        });
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        this.zoom = MathHelper.clamp(this.zoom + delta / 10, 0.5, 1);
        return true;
    }

    @Override
    public boolean mouseDragged(double p_mouseDragged_1_, double p_mouseDragged_3_, int p_mouseDragged_5_, double p_mouseDragged_6_, double p_mouseDragged_8_) {
        this.scrollX += p_mouseDragged_6_ / zoom;
        this.scrollY += p_mouseDragged_8_ / zoom;
        return super.mouseDragged(p_mouseDragged_1_, p_mouseDragged_3_, p_mouseDragged_5_, p_mouseDragged_6_, p_mouseDragged_8_);
    }

    public void renderHoverTooltips(int mouseX, int mouseY, int mouseActX, int mouseActY) {
        // Render hover tooltips
        if (!(0 <= mouseX && mouseX <= this.screenWidth && 0 <= mouseY && mouseY <= this.screenHeight)) {
            return;
        }

        final int relMouseX = (int) (mouseX / zoom - scrollX);
        final int relMouseY = (int) (mouseY / zoom - scrollY);
        this.currentHover = null;
        this.checkWidgetHover(UnlockWidget.class, relMouseX, relMouseY);
        if (this.currentHover != null) {
            ITextComponent title = this.currentHover.getData().getName();
            ITextComponent desc = this.currentHover.getData().getDescription();

            List<String> toolip = new ArrayList<>();
            toolip.add(title.getFormattedText());
            toolip.add(desc.getFormattedText());

            this.renderTooltip(toolip, mouseActX, mouseActY);
        }
    }
}
