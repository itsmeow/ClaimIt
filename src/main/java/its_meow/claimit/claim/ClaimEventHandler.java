package its_meow.claimit.claim;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerPickupXpEvent;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.event.world.BlockEvent.FarmlandTrampleEvent;
import net.minecraftforge.event.world.BlockEvent.PlaceEvent;
import net.minecraftforge.event.world.ExplosionEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber
public class ClaimEventHandler {

	@SubscribeEvent
	public void onWorldSave(WorldEvent.Save e) {
		if(!e.getWorld().isRemote) {
			ClaimManager.getManager().serialize();
		}
	}
	
	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void onBlockRightClicked(PlayerInteractEvent.RightClickBlock e) {
		World world = e.getEntity().getEntityWorld();
		BlockPos pos = e.getPos();
		ClaimManager cm = ClaimManager.getManager();
		ClaimArea claim = cm.getClaimAtLocation(world, pos);
		if(claim != null && e.getItemStack().isEmpty()) {
			EntityPlayer player = e.getEntityPlayer();
			e.setCanceled(!claim.canUse(player));
		} else if(claim == null && e.getItemStack().getItem() == Items.SHEARS && !world.isRemote) { // Add a claim with shears
			EntityPlayer player = e.getEntityPlayer();
			EnumHand hand = e.getHand();
			ItemStack stack = player.getHeldItem(hand);
			NBTTagCompound data = stack.getTagCompound();
			if(data == null) {
				NBTTagCompound newTag = new NBTTagCompound();
				data = newTag;
				stack.setTagCompound(newTag);
			}
			boolean isInClaim = ClaimManager.getManager().isBlockInAnyClaim(world, pos);
			if(!isInClaim) {
				int[] posArray = {pos.getX(), pos.getZ()};
				if(data.hasKey("Corner1")) {
					player.sendMessage(new TextComponentString("§9Added corner 2 at §b" + posArray[0] + "§9, §b" + posArray[1]));
					int[] corner1 = data.getIntArray("Corner1");
					int[] corner2 = posArray;
					BlockPos c1 = new BlockPos(corner1[0], 0, corner1[1]);
					BlockPos c2 = new BlockPos(corner2[0], 0, corner2[1]);
					/* Not needed due to ClaimArea constructor
						if(c1.subtract(c2).getX() < 0 && c1.subtract(c2).getY() < 0) {
							BlockPos c = c1; // Swap values to make c1 the proper corner
							c1 = c2;
							c2 = c;
						}*/
					BlockPos sideL = c2.subtract(c1); // Subtract to get side lengths
					// Claim corners are automatically corrected to proper values by constructor
					ClaimArea newClaim;
					newClaim = new ClaimArea(player.dimension, c1.getX(), c1.getZ(), sideL.getX(), sideL.getZ(), player);
					boolean didClaim = ClaimManager.getManager().addClaim(newClaim); // Add claim
					player.sendMessage(new TextComponentString(didClaim ? "§aClaim added successfully!" : "§cThis claim overlaps another claim!"));
					// Remove data so a new claim can be made.
					data.removeTag("Corner1");
				} else {
					data.setIntArray("Corner1", posArray);
					player.sendMessage(new TextComponentString("§9Added corner 1 at §b" + posArray[0] + "§9, §b" + posArray[1]));
				}
			} else {
				data.removeTag("Corner1");
				player.sendMessage(new TextComponentString("§cYou cannot set a corner inside an existing claim!"));
			}
			
			
		}
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void onBlockBroken(BreakEvent e) {
		World world = e.getWorld();
		BlockPos pos = e.getPos();
		ClaimManager cm = ClaimManager.getManager();
		ClaimArea claim = cm.getClaimAtLocation(world, pos);
		if(claim != null) {
			EntityPlayer player = e.getPlayer();
			e.setCanceled(!claim.canModify(player));
		}
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void onBlockPlaced(PlaceEvent e) {
		World world = e.getWorld();
		BlockPos pos = e.getPos();
		ClaimManager cm = ClaimManager.getManager();
		ClaimArea claim = cm.getClaimAtLocation(world, pos);
		if(claim != null) {
			EntityPlayer player = e.getPlayer();
			if(!claim.canModify(player)) {
				/*if(!player.capabilities.isCreativeMode) {
					player.addItemStackToInventory(new ItemStack(e.getPlayer().getHeldItem(e.getHand()).getItem(), 1));
				}*/

				e.setCanceled(true);
			}
		}
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void onFarmTrample(FarmlandTrampleEvent e) {
		World world = e.getWorld();
		BlockPos pos = e.getPos();
		ClaimManager cm = ClaimManager.getManager();
		ClaimArea claim = cm.getClaimAtLocation(world, pos);
		if(claim != null) {
			Entity ent = e.getEntity();
			if(ent instanceof EntityPlayer) {
				EntityPlayer player = (EntityPlayer) ent;
				e.setCanceled(!claim.canModify(player));
			} else {
				e.setCanceled(true);
			}
		}
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void onPlayerAttackAnimal(AttackEntityEvent e) {
		World world = e.getEntityPlayer().getEntityWorld();
		BlockPos pos = e.getEntityPlayer().getPosition();
		ClaimManager cm = ClaimManager.getManager();
		ClaimArea claim = cm.getClaimAtLocation(world, pos);
		if(claim != null) {
			EntityPlayer player = e.getEntityPlayer();
			if(e.getTarget() instanceof EntityPlayer) {
				e.setCanceled(!claim.canPVP(player));
			} else {
				e.setCanceled(!claim.canEntity(player));
			}
		}
		ClaimArea claim2 = cm.getClaimAtLocation(world, e.getTarget().getPosition());
		if(claim2 != null) {
			if(e.getTarget() instanceof EntityPlayer) {
				e.setCanceled(!claim2.canPVP(e.getEntityPlayer()) || e.isCanceled());
			} else {
				e.setCanceled(!claim2.canEntity(e.getEntityPlayer()) || e.isCanceled());;
			}
		}
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void onEntityRightClicked(PlayerInteractEvent.EntityInteract e) {
		World world = e.getEntity().getEntityWorld();
		BlockPos pos = e.getEntity().getPosition();
		ClaimManager cm = ClaimManager.getManager();
		ClaimArea claim = cm.getClaimAtLocation(world, pos);
		if(claim != null) {
			EntityPlayer player = e.getEntityPlayer();
			e.setCanceled(!claim.canEntity(player) || !claim.canUse(player));
		}
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void onPlayerUse(PlayerInteractEvent.RightClickEmpty e) {
		World world = e.getEntity().getEntityWorld();
		BlockPos pos = e.getPos();
		ClaimManager cm = ClaimManager.getManager();
		ClaimArea claim = cm.getClaimAtLocation(world, pos);
		if(claim != null) {
			EntityPlayer player = e.getEntityPlayer();
			e.setCanceled(!claim.canUse(player));
		}
	}
	
	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void onPlayerUse(PlayerInteractEvent.RightClickItem e) {
		World world = e.getEntity().getEntityWorld();
		BlockPos pos = e.getPos();
		ClaimManager cm = ClaimManager.getManager();
		ClaimArea claim = cm.getClaimAtLocation(world, pos);
		if(claim != null) {
			EntityPlayer player = e.getEntityPlayer();
			e.setCanceled(!claim.canUse(player));
		}
	}
	
	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void onPlayerUse(PlayerInteractEvent.LeftClickEmpty e) {
		World world = e.getEntity().getEntityWorld();
		BlockPos pos = e.getPos();
		ClaimManager cm = ClaimManager.getManager();
		ClaimArea claim = cm.getClaimAtLocation(world, pos);
		if(claim != null) {
			EntityPlayer player = e.getEntityPlayer();
			e.setCanceled(!claim.canUse(player));
		}
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void onBlockExplodedByMob(net.minecraftforge.event.entity.EntityMobGriefingEvent e) {
		World world = e.getEntity().getEntityWorld();
		BlockPos pos = e.getEntity().getPosition();
		ClaimManager cm = ClaimManager.getManager();
		ClaimArea claim = cm.getClaimAtLocation(world, pos);
		if(claim != null) {
			e.setResult(Result.DENY);
		}
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void onPlayerAttack(net.minecraftforge.event.entity.player.AttackEntityEvent e) {
		World world = e.getEntity().getEntityWorld();
		BlockPos pos = e.getEntity().getPosition();
		ClaimManager cm = ClaimManager.getManager();
		ClaimArea claim = cm.getClaimAtLocation(world, pos);
		if(claim != null || cm.getClaimAtLocation(world, e.getEntityPlayer().getPosition()) != null) {
			EntityPlayer player = e.getEntityPlayer();
			if(e.getTarget() instanceof EntityPlayer) {
				e.setCanceled(!claim.canPVP(player));
			} else {
				e.setCanceled(!claim.canEntity(player));
			}
		}
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void onHurtEvent(LivingHurtEvent e) {
		EntityLivingBase entity = e.getEntityLiving();
		DamageSource source = e.getSource();
		if(entity != null && source != null) { // There is an actual damage happening
			if(source.getTrueSource() instanceof EntityPlayer || source.getImmediateSource() instanceof EntityPlayer) { // Damage is caused by a player either indirectly or directly
				EntityPlayer player = (EntityPlayer) source.getTrueSource(); // Get the player
				ClaimArea claim = ClaimManager.getManager().getClaimAtLocation(entity.getEntityWorld(), player.getPosition()); // Claim the damage-doer is in
				ClaimArea claim2 = ClaimManager.getManager().getClaimAtLocation(entity.getEntityWorld(), entity.getPosition()); // Claim the damaged is in
				if(entity instanceof EntityPlayer) { // whether the damaged is a player or not
					e.setCanceled((claim != null && !claim.canPVP(player)) || (claim2 != null && !claim2.canPVP(player))); // if either one disallows PVP block it
				} else {
					e.setCanceled((claim != null && !claim.canEntity(player)) || (claim2 != null && !claim2.canEntity(player))); // if either one disallows entity block it
				}
			}
			ClaimArea claim = ClaimManager.getManager().getClaimAtLocation(entity.getEntityWorld(), entity.getPosition());
			if(source == DamageSource.MAGIC && claim != null) {
				e.setCanceled(true);
			}
		}
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void onPlayerPickupXp(PlayerPickupXpEvent e) {
		World world = e.getEntity().getEntityWorld();
		BlockPos pos = e.getEntity().getPosition();
		ClaimManager cm = ClaimManager.getManager();
		ClaimArea claim = cm.getClaimAtLocation(world, pos);
		if(claim != null) {
			EntityPlayer player = e.getEntityPlayer();
			e.setCanceled(!claim.canEntity(player));
		}
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void onExplode(ExplosionEvent.Detonate e) {
		World world = e.getWorld();
		ClaimManager cm = ClaimManager.getManager();
		Set<BlockPos> removeList = new HashSet<BlockPos>();
		for(BlockPos pos : e.getAffectedBlocks()) {
			if(cm.getClaimAtLocation(world, pos) != null) {
				removeList.add(pos);
			}
		}
		Set<Entity> removeListE = new HashSet<Entity>();
		for(Entity ent : e.getAffectedEntities()) {
			BlockPos pos = ent.getPosition();
			if(cm.getClaimAtLocation(world, pos) != null) {
				removeListE.add(ent);
			}
		}
		// Avoid concurrent modification by seperating tasks
		for(BlockPos pos : removeList) {
			e.getAffectedBlocks().remove(pos);
		}

		for(Entity ent : removeListE) {
			e.getAffectedEntities().remove(ent);
		}
	}

}
