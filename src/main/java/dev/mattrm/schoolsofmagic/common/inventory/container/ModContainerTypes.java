package dev.mattrm.schoolsofmagic.common.inventory.container;

import dev.mattrm.schoolsofmagic.GlobalConstants;
import net.minecraft.inventory.container.ContainerType;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModContainerTypes {
    public static final DeferredRegister<ContainerType<?>> CONTAINER_TYPES = new DeferredRegister<>(ForgeRegistries.CONTAINERS, GlobalConstants.MODID);

    public static final RegistryObject<ContainerType<MagicalWorkbenchContainer>> MAGICAL_WORKBENCH = CONTAINER_TYPES
            .register("magical_workbench", () -> IForgeContainerType.create(MagicalWorkbenchContainer::new));
}
