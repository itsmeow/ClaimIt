package its_meow.claimit.common.claim;

import java.util.HashSet;
import java.util.Set;

import its_meow.claimit.common.command.CommandClaimIt;
import its_meow.claimit.init.ItemRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.EntityInteract;
import net.minecraftforge.event.entity.player.PlayerPickupXpEvent;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.event.world.BlockEvent.FarmlandTrampleEvent;
import net.minecraftforge.event.world.BlockEvent.PlaceEvent;
import net.minecraftforge.event.world.ExplosionEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
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
			e.setCanceled(!claim.canModify(player));
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
			if(!claim.canModify(player)) {
				if(!player.capabilities.isCreativeMode) {
					player.addItemStackToInventory(new ItemStack(e.getItemInHand().getItem(), 1));
				}

				e.setCanceled(true);
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
				e.setCanceled(!claim.canModify(player));
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
			if(e.getTarget() instanceof EntityPlayer) {
				e.setCanceled(!claim.canPVP(player));
			} else {
				e.setCanceled(!claim.canEntity(player));
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
			e.setCanceled(!claim.canEntity(player) && !claim.canUse(player));
		}
	}

	@SubscribeEvent
	public void onRightClickBlock(PlayerInteractEvent e) {
		World world = e.getEntity().getEntityWorld();
		BlockPos pos = e.getPos();
		ClaimManager cm = ClaimManager.getManager();
		ClaimArea claim = cm.getClaimAtLocation(world, pos);
		if(claim != null && e.getItemStack().getItem() != ItemRegistry.claiminfotool) {
			EntityPlayer player = e.getEntityPlayer();
			e.setCanceled(!claim.canUse(player));
		}
	}

	@SubscribeEvent
	public void onBlockExplodedByMob(net.minecraftforge.event.entity.EntityMobGriefingEvent e) {
		World world = e.getEntity().getEntityWorld();
		BlockPos pos = e.getEntity().getPosition();
		ClaimManager cm = ClaimManager.getManager();
		ClaimArea claim = cm.getClaimAtLocation(world, pos);
		if(claim != null) {
			e.setResult(Result.DENY);
		}
	}

	@SubscribeEvent
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

	@SubscribeEvent
	public void onHurtEvent(LivingHurtEvent e) {
		EntityLivingBase entity = e.getEntityLiving();
		DamageSource source = e.getSource();
		if(entity != null && source != null) {
			if(source.getTrueSource() instanceof EntityPlayer && source.getImmediateSource() instanceof EntityPlayer) {
				EntityPlayer player = (EntityPlayer) source.getTrueSource();
				ClaimArea claim = ClaimManager.getManager().getClaimAtLocation(entity.getEntityWorld(), player.getPosition());
				if(claim != null) {
					e.setCanceled(!claim.canEntity(player));
				}
			}
		}
	}

	@SubscribeEvent
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

	@SubscribeEvent
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
