package dev.mattrm.schoolsofmagic.client.gui.widget;

import dev.mattrm.schoolsofmagic.client.gui.Bounds;
import dev.mattrm.schoolsofmagic.client.gui.WidgetScreen;
import net.minecraft.client.gui.AbstractGui;

public abstract class AbstractWidget extends AbstractGui {
    public final Bounds bounds;
    protected final WidgetScreen screen;

    public AbstractWidget(Bounds bounds, WidgetScreen screen) {
        this.bounds = bounds;
        this.screen = screen;
    }

    public void preRender() {

    }

    public void render() {

    }

    public void postRender() {

    }

    public void onHover(double relMouseX, double relMouseY) {

    }

    public void onClick(double relMouseX, double relMouseY, int button) {

    }

    public final boolean inBounds(double mouseX, double mouseY) {
        return bounds.in(mouseX, mouseY);
    }
}
