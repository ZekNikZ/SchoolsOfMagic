package dev.mattrm.schoolsofmagic.client.data.unlocks;

import dev.mattrm.schoolsofmagic.SchoolsOfMagicMod;
import dev.mattrm.schoolsofmagic.common.cache.AdvancementCache;
import dev.mattrm.schoolsofmagic.common.data.JsonDataProxy;
import dev.mattrm.schoolsofmagic.common.data.unlocks.Unlock;
import dev.mattrm.schoolsofmagic.common.data.unlocks.UnlockState;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UnlockNode extends JsonDataProxy<Unlock> {
    private UnlockState state;
    private List<UnlockNode> parents;
    private List<UnlockNode> children;
    private int priority;
    private int subpriority;
    private int x1;
    private int x2;
    private int y1;
    private int y2;
    private int level;
    private int widthofDirectChildren;

    public UnlockNode(Unlock data) {
        super(data);

        this.parents = new ArrayList<>();
        this.children = new ArrayList<>();
        this.priority = data.getPriority();
        this.widthofDirectChildren = 1;
        this.state = UnlockState.NOT_AVAILABLE;
    }

    public int getWidthofDirectChildren() {
        return widthofDirectChildren;
    }

    public void setWidthofDirectChildren(int widthofDirectChildren) {
        this.widthofDirectChildren = widthofDirectChildren;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public List<UnlockNode> getParents() {
        return parents;
    }

    public List<UnlockNode> getChildren() {
        return children;
    }

    public int getPriority() {
        return priority;
    }

    public int getSubpriority() {
        return subpriority;
    }

    public int getX1() {
        return x1;
    }

    public int getX2() {
        return x2;
    }

    public int getY1() {
        return y1;
    }

    public int getY2() {
        return y2;
    }

    public void setState(UnlockState state) {
        this.state = state;
    }

    public void setParents(List<UnlockNode> parents) {
        this.parents = parents;
    }

    public void addParent(UnlockNode parent) {
        this.parents.add(parent);
    }

    public void setChildren(List<UnlockNode> children) {
        this.children = children;
    }

    public void addChild(UnlockNode child) {
        this.children.add(child);
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public void setSubpriority(int subpriority) {
        this.subpriority = subpriority;
    }

    public void setX1(int x1) {
        this.x1 = x1;
    }

    public void setX2(int x2) {
        this.x2 = x2;
    }

    public void setX(int x) {
        this.x1 = x;
        this.x2 = x + ClientUnlockNodesManager.WIDGET_SIZE;
    }

    public void setY1(int y1) {
        this.y1 = y1;
    }

    public void setY2(int y2) {
        this.y2 = y2;
    }

    public void setY(int y) {
        this.y1 = y;
        this.y2 = y + ClientUnlockNodesManager.WIDGET_SIZE;
    }

    @Override
    protected void loadData(ResourceLocation id) {
        this.setData(SchoolsOfMagicMod.getInstance().getClientUnlockManager().getUnlock(id));
    }

    public UnlockState getState() {
        return state;
    }

    public UnlockState reloadState(UUID player, int points) {
        if (AdvancementCache.getClientInstance().get(player, this.getData().getLinkedAdvancement())) {
            return this.state = UnlockState.UNLOCKED;
        } else if (this.parents.stream().anyMatch(p -> p.getState() == UnlockState.UNLOCKED)) {
            if (points >= this.getData().getPoints()) {
                return this.state = UnlockState.CAN_PURCHASE;
            } else {
                return this.state = UnlockState.NOT_UNLOCKED;
            }
        } else {
            return this.state = UnlockState.NOT_AVAILABLE;
        }
    }
}
