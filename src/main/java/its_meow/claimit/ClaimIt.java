package its_meow.claimit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import its_meow.claimit.common.claim.ClaimEventHandler;
import its_meow.claimit.common.claim.ClaimManager;
import its_meow.claimit.common.command.CommandClaimIt;
import its_meow.claimit.proxy.CommonProxy;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

@Mod(modid = Ref.MOD_ID, name = Ref.NAME, version = Ref.VERSION, acceptedMinecraftVersions = Ref.acceptedMCV, updateJSON = Ref.updateJSON, acceptableRemoteVersions = "*")
public class ClaimIt {
	
	@Instance(Ref.MOD_ID) 
	public static ClaimIt mod;
	
	@SidedProxy(clientSide = Ref.CLIENT_PROXY_C, serverSide = Ref.SERVER_PROXY_C)
	public static CommonProxy proxy;

	public static CreativeTab tab = new CreativeTab("ClaimIt");

	public static Logger logger;
	
	@EventHandler
	public void serverLoad(FMLServerStartingEvent event)
	{
		event.registerServerCommand(new CommandClaimIt());
		ClaimManager.getManager().deserialize();
	}
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		logger = LogManager.getLogger("claimit");
		proxy.preInit(event);
		MinecraftForge.EVENT_BUS.register(new ClaimEventHandler());
	}

	@EventHandler
	public void init(FMLInitializationEvent e) {
		proxy.init(e);
	}
	
	@EventHandler
	public void postInit(FMLPostInitializationEvent e) {
		proxy.postInit(e);
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
