package its_meow.claimit.api.event;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

/** Called right before a claim tag is written to disk. You can modify and attach data here. 
 * Canceling will result in the data not being written to disk - and loss of the claim data.
 * If you wish to read data or modify data, subscribe to {@link EventClaimDeserialization}
 * **/
@Cancelable
public class EventClaimSerialization extends Event {
	
	private final NBTTagCompound data;
	
	public EventClaimSerialization(NBTTagCompound data) {
		this.data = data;
	}
	
	public NBTTagCompound getNBTTag() {
		return data;
	}
	
}
