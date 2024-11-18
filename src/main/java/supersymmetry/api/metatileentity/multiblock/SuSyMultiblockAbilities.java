package supersymmetry.api.metatileentity.multiblock;

import net.minecraftforge.items.IItemHandlerModifiable;

import gregtech.api.metatileentity.multiblock.MultiblockAbility;

public class SuSyMultiblockAbilities {

    public static final MultiblockAbility<IItemHandlerModifiable> PRIMITIVE_IMPORT_ITEMS = new MultiblockAbility<>(
            "primitive_import_items");
    public static final MultiblockAbility<IItemHandlerModifiable> PRIMITIVE_EXPORT_ITEMS = new MultiblockAbility<>(
            "primitive_export_items");
}
