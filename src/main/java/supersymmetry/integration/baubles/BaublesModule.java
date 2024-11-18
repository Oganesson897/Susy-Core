package supersymmetry.integration.baubles;

import java.util.Collections;
import java.util.List;

import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import org.jetbrains.annotations.NotNull;

import baubles.api.BaubleType;
import gregtech.api.modules.GregTechModule;
import gregtech.common.items.MetaItems;
import gregtech.integration.IntegrationSubmodule;
import supersymmetry.Supersymmetry;
import supersymmetry.api.SusyLog;
import supersymmetry.common.item.behavior.ArmorBaubleBehavior;
import supersymmetry.modules.SuSyModules;

@GregTechModule(
                moduleID = SuSyModules.MODULE_BAUBLES,
                containerID = Supersymmetry.MODID,
                modDependencies = "baubles",
                name = "SuSy Baubles Integration",
                description = "SuSy Baubles Integration Module")
public class BaublesModule extends IntegrationSubmodule {

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void registerItems(RegistryEvent.Register<Item> event) {
        MetaItems.SEMIFLUID_JETPACK.addComponents(new ArmorBaubleBehavior(BaubleType.BODY));
        MetaItems.ELECTRIC_JETPACK.addComponents(new ArmorBaubleBehavior(BaubleType.BODY));
        MetaItems.ELECTRIC_JETPACK_ADVANCED.addComponents(new ArmorBaubleBehavior(BaubleType.BODY));
        MetaItems.NIGHTVISION_GOGGLES.addComponents(new ArmorBaubleBehavior(BaubleType.HEAD));
    }

    @NotNull
    @Override
    public List<Class<?>> getEventBusSubscribers() {
        return Collections.singletonList(BaublesModule.class);
    }

    @Override
    public void init(FMLInitializationEvent event) {
        SusyLog.logger.info("Baubles found. Enabling integration...");
    }
}
