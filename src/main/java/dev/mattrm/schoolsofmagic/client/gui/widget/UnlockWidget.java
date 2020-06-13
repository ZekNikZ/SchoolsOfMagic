package dev.mattrm.schoolsofmagic.client.gui.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.mattrm.schoolsofmagic.client.data.unlocks.UnlockNode;
import dev.mattrm.schoolsofmagic.client.gui.Bounds;
import dev.mattrm.schoolsofmagic.client.gui.SchoolUnlocksView;
import dev.mattrm.schoolsofmagic.common.data.unlocks.UnlockState;
import dev.mattrm.schoolsofmagic.common.data.unlocks.types.UnlockType;
import net.minecraft.item.ItemStack;

public class UnlockWidget extends AbstractWidget {
    private static final int WIDGET_SIZE = 26;

    private UnlockNode node;
    private SchoolUnlocksView unlocksView;

    public UnlockWidget(UnlockNode node, SchoolUnlocksView screen) {
        super(new Bounds(node.getX1(), node.getX2(), node.getY1(), node.getY2()), screen);

        this.node = node;
        this.unlocksView = screen;
    }

    @Override
    public void preRender() {
        if (node.getData().getSchool().equals(this.unlocksView.getCurrentSchool())) {
            final int X_SPACING = 10;
            final int Y_SPACING = 10;
            final int COLOR_INACTIVE = 0xFF000000;
            final int COLOR_ACTIVE = 0xFFFFFFFF;
            final int xOffset = ((int) this.unlocksView.getScrollX());
            final int yOffset = ((int) this.unlocksView.getScrollY());

            if (!node.getChildren().isEmpty()) {
                // Draw outlet line
                this.vLine(node.getX1() + WIDGET_SIZE / 2 + xOffset, node.getY2() + yOffset - WIDGET_SIZE / 2, node.getY2() + Y_SPACING / 2 + yOffset, node.getChildren().stream().noneMatch(n -> n.getState() == UnlockState.NOT_AVAILABLE) && node.getState() == UnlockState.UNLOCKED ? COLOR_ACTIVE : COLOR_INACTIVE);

                // Draw child connections
                node.getChildren().forEach(child -> {
                    boolean ILeadToThisChild = child.getState() != UnlockState.NOT_AVAILABLE && node.getState() == UnlockState.UNLOCKED;
                    int COLOR = ILeadToThisChild ? COLOR_ACTIVE : COLOR_INACTIVE;
                    if (ILeadToThisChild) {
                        RenderSystem.pushMatrix();
                        RenderSystem.translatef(0.0f, 0.0f, 1.0f);
                    }
                    this.vLine(node.getX1() + WIDGET_SIZE / 2 + xOffset, node.getY2() + Y_SPACING / 2 + yOffset - 2, child.getY1() - Y_SPACING / 2 + yOffset, COLOR);
                    this.hLine(node.getX1() + WIDGET_SIZE / 2 + xOffset, child.getX1() + WIDGET_SIZE / 2 + xOffset, child.getY1() - Y_SPACING / 2 + yOffset, COLOR);
                    this.vLine(child.getX1() + WIDGET_SIZE / 2 + xOffset, child.getY1() - Y_SPACING / 2 + yOffset, child.getY1() + yOffset + WIDGET_SIZE / 2, COLOR);
                    if (ILeadToThisChild) {
                        RenderSystem.popMatrix();
                    }
                });
            }
        }
    }

    @Override
    public void render() {
        if (node.getData().getSchool().equals(this.unlocksView.getCurrentSchool())) {
            UnlockType type = node.getData().getType();
            ItemStack preview = node.getData().getIconItemStack();

            final int x = node.getX1() + ((int) this.unlocksView.getScrollX());
            final int y = node.getY1() + ((int) this.unlocksView.getScrollY());

            RenderSystem.pushMatrix();
            RenderSystem.translatef(0.0f, 0.0f, 2.0f);
            RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.enableBlend();
            this.screen.bindTexture(type.getClientTexture());
            this.blit(x, y, type.getClientTextureX(), type.getClientTextureY() + WIDGET_SIZE * node.getState().getTextureIndex(this.unlocksView.getFlashState()), WIDGET_SIZE, WIDGET_SIZE);
            RenderSystem.popMatrix();

            final int ITEM_OFFSET = (WIDGET_SIZE - 16) / 2;
            this.screen.renderItemStack(preview, x + ITEM_OFFSET, y + ITEM_OFFSET);
        }
    }

    @Override
    public void onHover(double relMouseX, double relMouseY) {
        if (node.getData().getSchool().equals(this.unlocksView.getCurrentSchool())) {
            this.unlocksView.setCurrentHover(this.node);
        }
    }
}
