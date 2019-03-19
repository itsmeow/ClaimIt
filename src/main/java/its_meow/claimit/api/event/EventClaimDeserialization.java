package its_meow.claimit.api.event;

import its_meow.claimit.api.claim.ClaimArea;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

/** Fired right before a claim is about to be added to the claims list - after all the data has been loaded.
 * Feel free to modify fields on the claim. Canceling the event will not add the claim to the claims list.
 * Be warned - this will eventually result in loss of the claim data as during serialization all data is overwritten.
 * If you wish to modify data before it is saved, subscribe to {@link EventClaimSerialization}
 **/
@Cancelable
public class EventClaimDeserialization extends Event {
	
	private final ClaimArea claim;
	
	public EventClaimDeserialization(ClaimArea claim) {
		this.claim = claim;
	}
	
	public ClaimArea getClaim() { 
		return claim; 
	}
	
}