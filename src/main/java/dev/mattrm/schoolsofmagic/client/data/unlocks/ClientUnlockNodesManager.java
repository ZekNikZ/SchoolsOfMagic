package dev.mattrm.schoolsofmagic.client.data.unlocks;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import dev.mattrm.schoolsofmagic.client.data.ClientDataManager;
import dev.mattrm.schoolsofmagic.common.data.schools.School;
import dev.mattrm.schoolsofmagic.common.data.unlocks.Unlock;
import dev.mattrm.schoolsofmagic.common.data.unlocks.types.UnlockType;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

// TODO: finish
public class ClientUnlockNodesManager extends ClientDataManager<Unlock, UnlockType, Map<School, Map<ResourceLocation, Unlock>>> {
    private static final Logger LOGGER = LogManager.getLogger();
    public static final int WIDGET_SIZE = 26;

    private Map<ResourceLocation, Unlock> allUnlocks;

    private Map<ResourceLocation, UnlockNode> allNodes;
    private Map<School, List<List<UnlockNode>>> trees;

    private UUID currentlyTracking;
    private int latestPoints;

    public Unlock getUnlock(ResourceLocation id) {
        return allUnlocks.get(id);
    }

    public List<UnlockNode> getSchool(School school) {
        return this.allNodes.values().stream().filter(n -> n.getData().getSchool().getId() == school.getId()).collect(Collectors.toList());
    }

    public Map<ResourceLocation, Unlock> getAllUnlocks() {
        return allUnlocks;
    }

    public Map<ResourceLocation, UnlockNode> getAllNodes() {
        return allNodes;
    }

    public UUID getCurrentlyTracking() {
        return currentlyTracking;
    }

    public void reloadAllStates(ResourceLocation advancement) {
        if (currentlyTracking != null) {
//            this.allNodes.values().stream().filter(n -> n.getData().getLinkedAdvancement().equals(advancement)).forEach(n -> n.reloadState(this.currentlyTracking, this.latestPoints));
            reloadAllStates(this.currentlyTracking, this.latestPoints);
        }
    }

    public void reloadAllStates(UUID player, int points, School school) {
        this.currentlyTracking = player;
        this.latestPoints = points;
        this.getSchool(school).forEach(node -> node.reloadState(player, points));
    }

    public void reloadAllStates(UUID player, int points) {
        this.currentlyTracking = player;
        this.latestPoints = points;
        this.allNodes.values().forEach(node -> node.reloadState(player, points));
    }

    @Override
    public void loadData(Map<School, Map<ResourceLocation, Unlock>> data) {
        LOGGER.info("Loading unlocks from the server...");
        super.loadData(data);

        this.allUnlocks = Maps.newHashMap();
        this.data.values().forEach(m -> m.forEach((key, value) -> this.allUnlocks.put(key, value)));
        LOGGER.info("Loaded {} unlocks", this.allUnlocks.size());

        LOGGER.info("Constructing unlock trees...");
        this.constructUnlockTree();
        LOGGER.info("Constructed {} unlock trees", this.trees.size());

        // TODO: remove, debug
        this.allNodes.values().forEach(n -> {
            LOGGER.debug("Handled node '{}' ('{}'): x={}, y={}, w={}:{}", n.getData().getSchool().getId(), n.getData().getId(), n.getX1(), n.getY1(), n.getWidthofDirectChildren(), n.getChildren().stream().mapToInt(UnlockNode::getWidthofDirectChildren).sum());
        });
    }

    private void constructUnlockTree() {
        this.trees = Maps.newHashMap();
        this.allNodes = Maps.newHashMap();

        // Create all of the nodes
        this.allUnlocks.forEach((k, v) -> this.allNodes.put(k, new UnlockNode(v)));

        // Link up all of the nodes
        this.allNodes.values().forEach(node -> node.setParents(node.getData().getParents().stream().map(p -> {
            UnlockNode parent = this.allNodes.get(p);
            parent.addChild(node);
            return parent;
        }).collect(Collectors.toList())));

        for (School school : this.allUnlocks.values().stream().map(Unlock::getSchool).collect(Collectors.toSet())) {
            List<List<UnlockNode>> levels = new ArrayList<>();

            // Construct levels
            List<UnlockNode> firstLevel = this.allNodes.values().stream().filter(n -> n.getData().getSchool() == school && n.getParents().isEmpty()).peek(n -> n.setLevel(0)).collect(Collectors.toList());
            Queue<UnlockNode> nodesToHandle = new LinkedList<>(firstLevel);
            levels.add(firstLevel);
            while (!nodesToHandle.isEmpty()) {
                UnlockNode currentNode = nodesToHandle.poll();
                int level = currentNode.getParents().stream().mapToInt(UnlockNode::getLevel).max().orElse(-1) + 1;
                currentNode.setLevel(level);
                while (levels.size() < level + 1) {
                    levels.add(new ArrayList<>());
                }
                if (level > 0) {
                    levels.get(level).add(currentNode);
                }
                nodesToHandle.addAll(currentNode.getChildren());
            }

            // Determine widths and constrain priorities
            AtomicInteger currentSubPriority = new AtomicInteger();
            for (int i = levels.size() - 1; i >= 0; i--) {
                int finalI = i;
                levels.get(i).forEach(node -> {
                    // TODO: set same priority only for parents on SAME LEVEL and figure out how to make this sort properly [OR just don't care]
                    node.setWidthofDirectChildren(node.getParents().size() > 1 ? 1 : Math.max(node.getChildren().stream().mapToInt(UnlockNode::getWidthofDirectChildren).sum(), 1));
                    if (node.getParents().size() > 1 && node.getParents().stream().mapToInt(UnlockNode::getLevel).distinct().count() == 1) {
                        int maxPriority = node.getParents().stream().mapToInt(UnlockNode::getPriority).max().orElse(0);
                        node.getParents().stream().sorted(Comparator.comparing(UnlockNode::getPriority)).forEach(n -> {
                            n.setPriority(maxPriority);
                            n.setSubpriority(currentSubPriority.getAndIncrement());
                        });
                    }
                });
            }

            // Sort x locations
//            levels.forEach(l -> l.sort(Comparator.comparing(UnlockNode::getPriority).thenComparing(UnlockNode::getSubpriority)));
            levels.forEach(l -> l.forEach(n -> n.getChildren().sort(Comparator.comparing(UnlockNode::getPriority).thenComparing(UnlockNode::getSubpriority))));

            // Compute x locations
            final int X_SPACING = 10;
            AtomicInteger x = new AtomicInteger();
            levels.get(0).forEach(node -> {
                int childWidth = node.getWidthofDirectChildren() * (WIDGET_SIZE + X_SPACING) - X_SPACING;
                node.setX(x.get() + (childWidth - WIDGET_SIZE) / 2);
                x.addAndGet(childWidth + X_SPACING);
            });
            levels.forEach(l -> l.forEach(node -> {
                if (node.getParents().size() > 1) {
                    node.setX((node.getParents().stream().mapToInt(UnlockNode::getX1).max().getAsInt() + node.getParents().stream().mapToInt(UnlockNode::getX1).min().getAsInt()) / 2);
                }

                int childWidth = node.getWidthofDirectChildren() * (WIDGET_SIZE + X_SPACING) - X_SPACING;
                AtomicInteger blockStart = new AtomicInteger(node.getX1() + (WIDGET_SIZE - childWidth) / 2);
                node.getChildren().forEach(child -> {
                    int blockWidth = child.getWidthofDirectChildren() * (WIDGET_SIZE + X_SPACING) - X_SPACING;
                    child.setX(blockStart.get() + (blockWidth - WIDGET_SIZE) / 2);
                    blockStart.addAndGet(blockWidth + X_SPACING);
                });
            }));

            // Compute y locations
            final int Y_SPACING = 10;
            AtomicInteger y = new AtomicInteger();
            levels.forEach(l -> {
                l.forEach(n -> n.setY(y.get()));
                y.addAndGet(Y_SPACING + WIDGET_SIZE);
            });

            this.trees.put(school, levels);
        }
    }

    public void resetTracking() {
        this.currentlyTracking = null;
    }
}
