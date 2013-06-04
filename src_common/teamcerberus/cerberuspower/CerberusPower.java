package teamcerberus.cerberuspower;

import net.minecraftforge.common.Configuration;
import teamcerberus.cerberuscore.config.ConfigurationParser;
import teamcerberus.cerberuspower.core.ElectricityRegistry;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Init;
import cpw.mods.fml.common.Mod.PreInit;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;

@Mod(modid = CerberusPower.id, name = CerberusPower.id,
		version = CerberusPower.version,
		dependencies = "required-after:CerberusCore")
@NetworkMod(clientSideRequired = true, serverSideRequired = false)
public class CerberusPower {
	public final static String	id		= "CerberusPower";
	public final static String	version	= "@VERSION@";

	@PreInit
	public void preinit(FMLPreInitializationEvent e) {
		ConfigurationParser.Parse(this,
				new Configuration(e.getSuggestedConfigurationFile()));
		ElectricityRegistry.getInstance();
	}

	@Init
	public void init(FMLInitializationEvent e) {}
}
