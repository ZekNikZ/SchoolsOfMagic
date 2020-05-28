package dev.mattrm.schoolsofmagic.common.block;

import dev.mattrm.schoolsofmagic.GlobalConstants;
import net.minecraft.block.Block;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS =  new DeferredRegister<Block>(ForgeRegistries.BLOCKS, GlobalConstants.MODID);

    public static final RegistryObject<Block> STUDY_TABLE = BLOCKS.register("study_table", StudyTableBlockBase::new);
    public static final RegistryObject<Block> MAGICAL_WORKBENCH = BLOCKS.register("magical_workbench", MagicalWorkbenchBlock::new);
}
