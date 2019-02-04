package its_meow.claimit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import its_meow.claimit.claim.ClaimEventHandler;
import its_meow.claimit.claim.ClaimManager;
import its_meow.claimit.claim.ClaimPermissions;
import its_meow.claimit.command.CommandClaimIt;
import its_meow.claimit.command.ConfirmationManager;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

@Mod(modid = Ref.MOD_ID, name = Ref.NAME, version = Ref.VERSION, acceptedMinecraftVersions = Ref.acceptedMCV, updateJSON = Ref.updateJSON, acceptableRemoteVersions = "*")
public class ClaimIt {

	@Instance(Ref.MOD_ID) 
	public static ClaimIt mod;

	public static CreativeTab tab = new CreativeTab("ClaimIt");

	public static Logger logger;

	@EventHandler
	public void serverLoad(FMLServerStartingEvent event) {
		ClaimManager.getManager().clearAdmins();
		ClaimManager.getManager().deserialize();
		ConfirmationManager.getManager().removeAllConfirms();
		event.registerServerCommand(new CommandClaimIt());
	}

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		logger = LogManager.getLogger("claimit");
		ClaimPermissions.register();
		MinecraftForge.EVENT_BUS.register(new ClaimEventHandler());
	}

	@EventHandler
	public void init(FMLInitializationEvent e) {
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent e) {
	}

	public static class CreativeTab extends CreativeTabs {

		public CreativeTab(String tab) {
			super(tab);
		}

		@Override
		public ItemStack getTabIconItem() {
			return new ItemStack(Blocks.BARRIER);
		}


	}

}
