package its_meow.claimit.proxy;

import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public interface ISidedProxy {
	
	void preInit(FMLPreInitializationEvent e);
	
	void init(FMLInitializationEvent e);
	
	void postInit(FMLPostInitializationEvent e);
	
}
