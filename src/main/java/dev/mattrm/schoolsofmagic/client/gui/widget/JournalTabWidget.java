package dev.mattrm.schoolsofmagic.client.gui.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.mattrm.schoolsofmagic.GlobalConstants;
import dev.mattrm.schoolsofmagic.client.gui.Bounds;
import dev.mattrm.schoolsofmagic.client.gui.JournalScreen;
import dev.mattrm.schoolsofmagic.common.data.schools.School;
import net.minecraft.util.ResourceLocation;

public class JournalTabWidget extends AbstractWidget {
    public static final int TAB_WIDTH = 32;
    public static final int TAB_HEIGHT = 28;
    private static final ResourceLocation TABS = new ResourceLocation(GlobalConstants.MODID, "textures/gui/journal/tabs.png");

    private final Side side;
    private final School school;
    private final int x;
    private final int y;
    private JournalScreen container;

    public JournalTabWidget(Side side, School school, int x, int y, JournalScreen screen) {
        super(new Bounds(x, x + TAB_WIDTH, y, y + TAB_HEIGHT), screen);

        this.side = side;
        this.school = school;
        this.x = x;
        this.y = y;
        this.container = screen;
    }

    @Override
    public void render() {
        // Render tab
        this.screen.bindTexture(TABS);
        this.blit(this.x, this.y, side.getXStart(), this.container.getCurrentSchool() == school ? TAB_HEIGHT : 0, TAB_WIDTH, TAB_HEIGHT);

        RenderSystem.pushMatrix();
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.enableBlend();
        this.screen.bindTexture(school.getIcon());
        blit(this.x + 7, this.y + 6, 0, 0, 16, 16, 16, 16);
        RenderSystem.popMatrix();
    }

    @Override
    public void onHover(double relMouseX, double relMouseY) {
        this.container.hoveredSchool = this.school;
    }

    public enum Side {
        LEFT(32),
        RIGHT(128);
        private int xStart;

        public int getXStart() {
            return xStart;
        }

        Side(int xStart) {
            this.xStart = xStart;
        }
    }

    @Override
    public void onClick(double relMouseX, double relMouseY, int button) {
        if (button == 0) {
            System.out.println("HERE");
            this.container.setCurrentSchool(this.school);
        }
    }
}
