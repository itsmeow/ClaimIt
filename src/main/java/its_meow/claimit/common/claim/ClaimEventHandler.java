package its_meow.claimit.common.claim;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.EntityInteract;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.event.world.BlockEvent.FarmlandTrampleEvent;
import net.minecraftforge.event.world.BlockEvent.PlaceEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber
public class ClaimEventHandler {

	
	@SubscribeEvent
	public void onWorldLoad(WorldEvent.Load e) {
		if(!e.getWorld().isRemote) {
			ClaimManager.getManager().deserialize(e.getWorld());
		}
	}
	
	@SubscribeEvent
	public void onWorldSave(WorldEvent.Save e) {
		if(!e.getWorld().isRemote) {
			ClaimManager.getManager().serialize(e.getWorld());
		}
	}
	
	@SubscribeEvent
	public void onBlockBroken(BreakEvent e) {
		World world = e.getWorld();
		BlockPos pos = e.getPos();
		ClaimManager cm = ClaimManager.getManager();
		ClaimArea claim = cm.getClaimAtLocation(world, pos);
		if(claim != null) {
			EntityPlayer player = e.getPlayer();
			// First make sure online UUID doesn't match
			if(claim.getOwner() != player.getUUID(player.getGameProfile())) {
				// If online UUID doesn't match then make sure offline doesn't either
				if(claim.getOwnerOffline() != player.getOfflineUUID(player.getName())) {
					e.setCanceled(true);
				}
			}
		}
	}

	@SubscribeEvent
	public void onBlockPlaced(PlaceEvent e) {
		World world = e.getWorld();
		BlockPos pos = e.getPos();
		ClaimManager cm = ClaimManager.getManager();
		ClaimArea claim = cm.getClaimAtLocation(world, pos);
		if(claim != null) {
			EntityPlayer player = e.getPlayer();
			// First make sure online UUID doesn't match
			if(claim.getOwner() != player.getUUID(player.getGameProfile())) {
				// If online UUID doesn't match then make sure offline doesn't either
				if(claim.getOwnerOffline() != player.getOfflineUUID(player.getName())) {
					if(!player.capabilities.isCreativeMode) {
						player.addItemStackToInventory(new ItemStack(e.getItemInHand().getItem(), 1));
					}
					
					e.setCanceled(true);
				}
			}
		}
	}

	@SubscribeEvent
	public void onFarmTrample(FarmlandTrampleEvent e) {
		World world = e.getWorld();
		BlockPos pos = e.getPos();
		ClaimManager cm = ClaimManager.getManager();
		ClaimArea claim = cm.getClaimAtLocation(world, pos);
		if(claim != null) {
			Entity ent = e.getEntity();
			if(ent instanceof EntityPlayer) {
				EntityPlayer player = (EntityPlayer) ent;
				// First make sure online UUID doesn't match
				if(claim.getOwner() != player.getUUID(player.getGameProfile())) {
					// If online UUID doesn't match then make sure offline doesn't either
					if(claim.getOwnerOffline() != player.getOfflineUUID(player.getName())) {
						e.setCanceled(true);
					}
				}
			} else {
				e.setCanceled(true);
			}
		}
	}
	
	@SubscribeEvent
	public void onPlayerAttackAnimal(AttackEntityEvent e) {
		World world = e.getEntity().getEntityWorld();
		BlockPos pos = e.getEntity().getPosition();
		ClaimManager cm = ClaimManager.getManager();
		ClaimArea claim = cm.getClaimAtLocation(world, pos);
		if(claim != null) {
			EntityPlayer player = e.getEntityPlayer();
			// First make sure online UUID doesn't match
			if(claim.getOwner() != player.getUUID(player.getGameProfile())) {
				// If online UUID doesn't match then make sure offline doesn't either
				if(claim.getOwnerOffline() != player.getOfflineUUID(player.getName())) {
					e.setCanceled(true);
				}
			}
		}
	}
	
	@SubscribeEvent
	public void onEntityRightClicked(EntityInteract e) {
		World world = e.getEntity().getEntityWorld();
		BlockPos pos = e.getEntity().getPosition();
		ClaimManager cm = ClaimManager.getManager();
		ClaimArea claim = cm.getClaimAtLocation(world, pos);
		if(claim != null) {
			EntityPlayer player = e.getEntityPlayer();
			// First make sure online UUID doesn't match
			if(claim.getOwner() != player.getUUID(player.getGameProfile())) {
				// If online UUID doesn't match then make sure offline doesn't either
				if(claim.getOwnerOffline() != player.getOfflineUUID(player.getName())) {
					e.setCanceled(true);
				}
			}
		}
	}
	
	@SubscribeEvent
	public void onRightClickBlock(PlayerInteractEvent e) {
		World world = e.getEntity().getEntityWorld();
		BlockPos pos = e.getEntity().getPosition();
		ClaimManager cm = ClaimManager.getManager();
		ClaimArea claim = cm.getClaimAtLocation(world, pos);
		if(claim != null) {
			EntityPlayer player = e.getEntityPlayer();
			// First make sure online UUID doesn't match
			if(claim.getOwner() != player.getUUID(player.getGameProfile())) {
				// If online UUID doesn't match then make sure offline doesn't either
				if(claim.getOwnerOffline() != player.getOfflineUUID(player.getName())) {
					e.setCanceled(true);
				}
			}
		}
	}
	
	@SubscribeEvent
	public void onBlockExploded(net.minecraftforge.event.entity.EntityMobGriefingEvent e) {
		World world = e.getEntity().getEntityWorld();
		BlockPos pos = e.getEntity().getPosition();
		ClaimManager cm = ClaimManager.getManager();
		ClaimArea claim = cm.getClaimAtLocation(world, pos);
		if(claim != null) {
			e.setCanceled(true);
		}
	}
	
	@SubscribeEvent
	public void onPlayerAttack(net.minecraftforge.event.entity.player.AttackEntityEvent e) {
		World world = e.getEntity().getEntityWorld();
		BlockPos pos = e.getEntity().getPosition();
		ClaimManager cm = ClaimManager.getManager();
		ClaimArea claim = cm.getClaimAtLocation(world, pos);
		if(claim != null || cm.getClaimAtLocation(world, e.getEntityPlayer().getPosition()) != null) {
			e.setCanceled(true);
		}
	}

}
