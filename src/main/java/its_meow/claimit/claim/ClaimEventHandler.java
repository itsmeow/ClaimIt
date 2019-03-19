package its_meow.claimit.claim;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import its_meow.claimit.Ref;
import its_meow.claimit.api.claim.ClaimArea;
import its_meow.claimit.api.claim.ClaimManager;
import its_meow.claimit.config.ClaimConfig;
import net.minecraft.block.BlockGrass;
import net.minecraft.block.BlockTallGrass;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.common.util.BlockSnapshot;
import net.minecraftforge.event.entity.EntityMountEvent;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.living.LivingDestroyBlockEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.event.entity.player.ArrowNockEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.BonemealEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerPickupXpEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.ExplosionEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber(modid = Ref.MOD_ID)
public class ClaimEventHandler {
	
	@SubscribeEvent
	public static void onWorldSave(WorldEvent.Save e) {
		if(!e.getWorld().isRemote) {
			ClaimManager.getManager().serialize();
		}
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public static void onBlockRightClicked(PlayerInteractEvent.RightClickBlock e) {
		World world = e.getEntity().getEntityWorld();
		BlockPos pos = e.getPos();
		ClaimManager cm = ClaimManager.getManager();
		ClaimArea claim = cm.getClaimAtLocation(world, pos);
		if(claim != null) {
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
					if(newClaim.getSideLengthX() >= 1 && newClaim.getSideLengthZ() >= 1) {
						boolean didClaim = ClaimManager.getManager().addClaim(newClaim); // Add claim
						player.sendMessage(new TextComponentString(didClaim ? "§aClaim added successfully!" : "§cThis claim overlaps another claim!"));
					} else {
						player.sendMessage(new TextComponentString("§cYour claim must have a length of at least 2 in both directions!"));
					}
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
	public static void onBlockBroken(BlockEvent.BreakEvent e) {
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
	public static void onBlockPlaced(BlockEvent.PlaceEvent e) {
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
	public static void onFarmTrample(BlockEvent.FarmlandTrampleEvent e) {
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
	public static void onPlayerAttackAnimal(AttackEntityEvent e) {
		World world = e.getEntityPlayer().getEntityWorld();
		BlockPos pos = e.getEntityPlayer().getPosition();
		ClaimManager cm = ClaimManager.getManager();
		ClaimArea claim = cm.getClaimAtLocation(world, pos);
		if(claim != null) {
			EntityPlayer player = e.getEntityPlayer();
			if(e.getTarget() instanceof EntityPlayer) {
				e.setCanceled(!claim.canPVP(player)  || ClaimConfig.forceNoPVPInClaim);
			} else {
				e.setCanceled(!claim.canEntity(player));
			}
		}
		ClaimArea claim2 = cm.getClaimAtLocation(world, e.getTarget().getPosition());
		if(claim2 != null) {
			if(e.getTarget() instanceof EntityPlayer) {
				e.setCanceled(!claim2.canPVP(e.getEntityPlayer()) || e.isCanceled()  || ClaimConfig.forceNoPVPInClaim);
			} else {
				e.setCanceled(!claim2.canEntity(e.getEntityPlayer()) || e.isCanceled());;
			}
		}
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public static void onEntityRightClicked(PlayerInteractEvent.EntityInteract e) {
		World world = e.getEntity().getEntityWorld();
		BlockPos pos = e.getEntity().getPosition();
		BlockPos pos2 = e.getPos();
		ClaimManager cm = ClaimManager.getManager();
		EntityPlayer player = e.getEntityPlayer();
		ClaimArea claim = cm.getClaimAtLocation(world, pos);
		ClaimArea claim2 = cm.getClaimAtLocation(world, pos2);
		if(claim != null) {
			e.setCanceled(!claim.canEntity(player) || !claim.canUse(player));
		}
		if(claim2 != null) {
			e.setCanceled(!claim2.canEntity(player) || !claim2.canUse(player) || e.isCanceled());
		}
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public static void onPlayerUse(PlayerInteractEvent.RightClickEmpty e) {
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
	public static void onPlayerUse(PlayerInteractEvent.RightClickItem e) {
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
	public static void onMobGrief(net.minecraftforge.event.entity.EntityMobGriefingEvent e) {
		World world = e.getEntity().getEntityWorld();
		BlockPos pos = e.getEntity().getPosition();
		ClaimManager cm = ClaimManager.getManager();
		ClaimArea claim = cm.getClaimAtLocation(world, pos);
		if(claim != null) {
			e.setResult(Result.DENY);
		}
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public static void onPlayerAttack(net.minecraftforge.event.entity.player.AttackEntityEvent e) {
		World world = e.getEntity().getEntityWorld();
		BlockPos pos = e.getEntity().getPosition();
		ClaimManager cm = ClaimManager.getManager();
		ClaimArea claim = cm.getClaimAtLocation(world, pos);
		if(claim != null || cm.getClaimAtLocation(world, e.getEntityPlayer().getPosition()) != null) {
			EntityPlayer player = e.getEntityPlayer();
			if(e.getTarget() instanceof EntityPlayer) {
				e.setCanceled(!claim.canPVP(player)  || ClaimConfig.forceNoPVPInClaim);
			} else {
				e.setCanceled(!claim.canEntity(player));
			}
		}
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public static void onHurtEvent(LivingHurtEvent e) {
		EntityLivingBase entity = e.getEntityLiving();
		DamageSource source = e.getSource();
		if(entity != null && source != null) { // There is an actual damage happening
			if(source.getTrueSource() instanceof EntityPlayer || source.getImmediateSource() instanceof EntityPlayer) { // Damage is caused by a player either indirectly or directly
				EntityPlayer player = (EntityPlayer) source.getTrueSource(); // Get the player
				ClaimArea claim = ClaimManager.getManager().getClaimAtLocation(entity.getEntityWorld(), player.getPosition()); // Claim the damage-doer is in
				ClaimArea claim2 = ClaimManager.getManager().getClaimAtLocation(entity.getEntityWorld(), entity.getPosition()); // Claim the damaged is in
				if(entity instanceof EntityPlayer) { // whether the damaged is a player or not
					e.setCanceled((claim != null && !claim.canPVP(player)) || (claim2 != null && !claim2.canPVP(player))  || ClaimConfig.forceNoPVPInClaim); // if either one disallows PVP block it
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
	public static void onPlayerPickupXp(PlayerPickupXpEvent e) {
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
	public static void onExplode(ExplosionEvent.Detonate e) {
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

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public static void onBlockBrokenLiving(LivingDestroyBlockEvent e) {
		World world = e.getEntityLiving().getEntityWorld();
		BlockPos pos = e.getPos();
		ClaimManager cm = ClaimManager.getManager();
		ClaimArea claim = cm.getClaimAtLocation(world, pos);
		if(claim != null) {
			if(!(e.getEntityLiving() instanceof EntityPlayer)) {
				e.setCanceled(!claim.isPermissionToggled(ClaimPermissions.LIVING_MODIFY));
			} else if(e.getEntityLiving() instanceof EntityPlayer){
				EntityPlayer player = (EntityPlayer) e.getEntityLiving();
				e.setCanceled(!claim.canModify(player));
			}
		}
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public static void onEntityMount(EntityMountEvent e) {
		if(e.isMounting()) {
			if(e.getEntityMounting() instanceof EntityPlayer) {
				EntityPlayer player = (EntityPlayer) e.getEntityMounting();
				ClaimArea claim = ClaimManager.getManager().getClaimAtLocation(e.getWorldObj(), e.getEntityBeingMounted().getPosition());
				if(claim != null) {
					e.setCanceled(!claim.canEntity(player));
				}
			}
		}
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public static void onItemThrow(ItemTossEvent e) {
		World world = e.getPlayer().world;
		EntityPlayer player = e.getPlayer();
		BlockPos pos = player.getPosition();
		ClaimArea claim = ClaimManager.getManager().getClaimAtLocation(world, pos);
		if(claim != null) {
			if(!claim.isPermissionToggled(ClaimPermissions.DROP_ITEM)) {
				if(!claim.canUse(player)) {
					e.setCanceled(true);
					player.addItemStackToInventory(e.getEntityItem().getItem()); // Re-add items because canceling event deletes items
				}
			}
		}
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public static void onItemPickup(EntityItemPickupEvent e) {
		World world = e.getEntityPlayer().world;
		EntityPlayer player = e.getEntityPlayer();
		BlockPos pos = e.getItem().getPosition();
		ClaimArea claim = ClaimManager.getManager().getClaimAtLocation(world, pos);
		if(claim != null) {
			if(!claim.isPermissionToggled(ClaimPermissions.PICKUP_ITEM)) {
				if(!claim.canUse(player)) {
					e.setCanceled(true);
				}
			}
		}
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public static void onBonemeal(BonemealEvent e) {
		if(e.getBlock() instanceof BlockGrass) {
			int nearby = 0;
			for(ClaimArea claimI : ClaimManager.getManager().getClaimsList()) {
				if(claimI.getDimensionID() == e.getWorld().provider.getDimension()) {
					int xDistance = Math.abs(claimI.getMainPosition().getX() - e.getPos().getX());
					int zDistance = Math.abs(claimI.getMainPosition().getZ() - e.getPos().getZ());
					if(xDistance < 4 || zDistance < 4) {
						nearby++;
					}
				}
			}
			e.setCanceled(nearby > 0);
		}
		ClaimArea originalClaim = ClaimManager.getManager().getClaimAtLocation(e.getWorld(), e.getPos());
		if(e.isCanceled()) {
			Random rand = new Random();
			BlockPos blockpos = e.getPos().up();

			for (int i = 0; i < 128; ++i)
			{
				BlockPos blockpos1 = blockpos;
				int j = 0;

				while (true)
				{
					if (j >= i / 16)
					{
						ClaimArea claimAtLoc = ClaimManager.getManager().getClaimAtLocation(e.getWorld(), blockpos1);
						if (e.getWorld().isAirBlock(blockpos1) && (claimAtLoc == null || (claimAtLoc  == originalClaim && claimAtLoc.canModify(e.getEntityPlayer()))))
						{
							if (rand.nextInt(8) == 0) {
								e.getWorld().getBiome(blockpos1).plantFlower(e.getWorld(), rand, blockpos1);
							}
							else
							{
								IBlockState iblockstate1 = Blocks.TALLGRASS.getDefaultState().withProperty(BlockTallGrass.TYPE, BlockTallGrass.EnumType.GRASS);

								if (Blocks.TALLGRASS.canBlockStay(e.getWorld(), blockpos1, iblockstate1))
								{
									e.getWorld().setBlockState(blockpos1, iblockstate1, 3);
								}
							}
						}

						break;
					}

					blockpos1 = blockpos1.add(rand.nextInt(3) - 1, (rand.nextInt(3) - 1) * rand.nextInt(3) / 2, rand.nextInt(3) - 1);

					if (e.getWorld().getBlockState(blockpos1.down()).getBlock() != Blocks.GRASS || e.getWorld().getBlockState(blockpos1).isNormalCube())
					{
						break;
					}

					++j;
				}
			}
		}
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public static void onMultiPlace(BlockEvent.MultiPlaceEvent e) {
		World world = e.getWorld();
		EntityPlayer player = e.getPlayer();
		for(BlockSnapshot snap : e.getReplacedBlockSnapshots()) {
			BlockPos pos = snap.getPos();
			ClaimArea claim = ClaimManager.getManager().getClaimAtLocation(world, pos);
			if(claim != null) {
				e.setCanceled(!claim.canModify(player) || e.isCanceled());
			}
		}
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public static void onBowUse(ArrowNockEvent e) {
		World world = e.getWorld();
		EntityPlayer player = e.getEntityPlayer();
		BlockPos pos = player.getPosition();
		ClaimArea claim = ClaimManager.getManager().getClaimAtLocation(world, pos);
		if(claim != null) {
			if(!claim.canUse(player) || ClaimConfig.forceNoPVPInClaim) { 
				e.setAction(new ActionResult<ItemStack>(EnumActionResult.FAIL, e.getBow()));
			}
		}
	}
	
	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public static void onProjectileImpact(ProjectileImpactEvent e) {
		Entity entity = e.getRayTraceResult().entityHit;
		if(entity instanceof EntityLiving) {
			EntityLiving el = (EntityLiving) entity;
			ClaimArea claim = ClaimManager.getManager().getClaimAtLocation(el.world, el.getPosition());
			if(claim != null) {
				if(!claim.isPermissionToggled(ClaimPermissions.ALLOW_PROJECTILES)) {
					e.setCanceled(true);
				}
			}
		}
	}
	
	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public static void onEntitySpawn(LivingSpawnEvent.CheckSpawn e) {
		ClaimArea claim = ClaimManager.getManager().getClaimAtLocation(e.getWorld(), new BlockPos(e.getX(), e.getY(), e.getZ()));
		if(claim != null) {
			if(!claim.isPermissionToggled(ClaimPermissions.ENTITY_SPAWN)) {
				e.setResult(Result.DENY);
			}
		}
	}
	
}
