package dev.mattrm.schoolsofmagic.client.data;

import dev.mattrm.schoolsofmagic.common.data.schools.School;
import dev.mattrm.schoolsofmagic.common.data.unlocks.Unlock;
import dev.mattrm.schoolsofmagic.common.data.unlocks.types.UnlockType;
import net.minecraft.util.ResourceLocation;

import java.util.Map;

// TODO: finish
public class ClientUnlockNodesManager extends ClientDataManager<Unlock, UnlockType, Map<School, Map<ResourceLocation, Unlock>>> {
    Map<School, Map<ResourceLocation, Unlock>> unlocks;

    @Override
    public void loadData(Map<School, Map<ResourceLocation, Unlock>> data) {
        
    }
}
