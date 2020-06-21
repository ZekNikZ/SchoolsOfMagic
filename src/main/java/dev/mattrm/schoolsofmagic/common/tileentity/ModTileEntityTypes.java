package dev.mattrm.schoolsofmagic.common.tileentity;

import dev.mattrm.schoolsofmagic.GlobalConstants;
import dev.mattrm.schoolsofmagic.common.block.ModBlocks;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

//@ObjectHolder(GlobalConstants.MODID)
public class ModTileEntityTypes {
    public static final DeferredRegister<TileEntityType<?>> TILE_ENTITY_TYPES = new DeferredRegister<>(ForgeRegistries.TILE_ENTITIES, GlobalConstants.MODID);

    public static final RegistryObject<TileEntityType<MagicalWorkbenchTileEntity>> MAGICAL_WORKBENCH = TILE_ENTITY_TYPES.register("magical_workbench", () -> TileEntityType.Builder.create(MagicalWorkbenchTileEntity::new, ModBlocks.MAGICAL_WORKBENCH.get()).build(null));

    public static final RegistryObject<TileEntityType<StudyTableTileEntity>> STUDY_TABLE = TILE_ENTITY_TYPES.register("study_table", () -> TileEntityType.Builder.create(StudyTableTileEntity::new, ModBlocks.STUDY_TABLE.get()).build(null));
}
