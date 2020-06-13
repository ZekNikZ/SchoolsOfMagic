package dev.mattrm.schoolsofmagic.client.data;

import dev.mattrm.schoolsofmagic.common.data.schools.School;
import dev.mattrm.schoolsofmagic.common.data.schools.types.SchoolType;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

// TODO: finish
public class ClientSchoolManager extends ClientDataManager<School, SchoolType, Map<ResourceLocation, School>> {
    private static final Logger LOGGER = LogManager.getLogger();

    @Override
    public void loadData(Map<ResourceLocation, School> data) {
        super.loadData(data);
    }

    public School getSchool(ResourceLocation id) {
        return this.data.get(id);
    }

    public List<School> getAllSchools() {
        return new ArrayList<>(this.data.values());
    }
}
