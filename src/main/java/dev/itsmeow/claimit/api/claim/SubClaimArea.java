package dev.itsmeow.claimit.api.claim;

import java.util.UUID;

import javax.annotation.Nonnull;

import org.apache.logging.log4j.Level;

import dev.itsmeow.claimit.api.ClaimItAPI;
import dev.itsmeow.claimit.api.claim.ClaimManager.ClaimAddResult;
import dev.itsmeow.claimit.api.permission.ClaimPermissionMember;
import dev.itsmeow.claimit.api.permission.ClaimPermissions;
import dev.itsmeow.claimit.api.util.nbt.ClaimNBTUtil;
import dev.itsmeow.claimit.api.util.objects.MemberContainer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;

public class SubClaimArea extends ClaimArea {

    protected ClaimArea parent;

    public SubClaimArea(ClaimArea parent, int posX, int posZ, int sideLengthX, int sideLengthZ) {
        super(parent.getDimensionID(), posX, posZ, sideLengthX, sideLengthZ, parent.getOwner());
        this.parent = parent;
        this.viewName = parent.hashCode() + "_" + Math.abs(posX) + Math.abs(posZ) + "-" + Math.round(Math.random() * 10);
    }

    public SubClaimArea(ClaimArea parent, int posX, int posZ, int sideLengthX, int sideLengthZ, String trueViewName) {
        this(parent, posX, posZ, sideLengthX, sideLengthZ);
        this.viewName = trueViewName;
    }

    @Override
    public boolean hasPermission(UUID uuid, ClaimPermissionMember permission) {
        return super.hasPermission(uuid, permission) || this.parent.hasPermission(uuid, ClaimPermissions.MANAGE_PERMS) || this.parent.isOwner(uuid);
    }

    @Override
    protected boolean hasPermissionFromGroup(ClaimPermissionMember permission, UUID uuid) {
        return false;
    }

    @Override
    public boolean setViewName(String nameIn) {
        boolean pass = true;
        for(SubClaimArea subclaim : this.parent.getSubClaims()) {
            if(subclaim.getTrueViewName().equals(this.parent.hashCode() + "_" + nameIn) && subclaim != this) { // Claim has the same name, is not this claim, and is owned by the same player
                pass = false;
            }
        }
        if(pass) {
            this.viewName = this.parent.hashCode() + "_" + nameIn;
        }
        return pass;
    }
    
    @Override
    public ClaimAddResult addSubClaim(SubClaimArea subclaim) {
        return ClaimAddResult.OVERLAP;
    }

    @Override
    public boolean removeSubClaim(SubClaimArea subclaim) {
        return false;
    }

    @Override
    public ClaimArea getMostSpecificClaim(BlockPos pos) {
        return parent.getMostSpecificClaim(pos);
    }

    @Override
    protected NBTTagCompound serializeSubClaims(NBTTagCompound data) {
        return data;
    }

    @Override
    public SubClaimArea getSubClaimAtLocation(BlockPos pos) {
        return parent.getSubClaimAtLocation(pos);
    }
    
    @Nonnull
    public ClaimArea getParent() {
        return parent;
    }

    public static SubClaimArea deserialize(ClaimArea parent, NBTTagCompound tag) {
        int[] claimVals = tag.getIntArray("CLAIMINFO");
        String trueViewName = tag.getString("TRUEVIEWNAME");
        if(trueViewName == null || trueViewName.equals("")) {
            trueViewName = parent.hashCode() + "_" + "errdeserializing" + "-" + Math.round(Math.random() * 10);
        }
        if(claimVals.length > 0 && claimVals[0] == 0) {
            ClaimItAPI.logger.debug("Valid version.");
            SubClaimArea claim = new SubClaimArea(parent, claimVals[2], claimVals[3], claimVals[4], claimVals[5], trueViewName);
            claim.addMembers(MemberContainer.readMembers(tag));
            claim.setToggles(ClaimNBTUtil.readToggles(tag));
            return claim;
        } else {
            ClaimItAPI.logger.log(Level.FATAL, "Detected version that doesn't exist yet! Mod was downgraded? Claim cannot be loaded.");
            throw new RuntimeException("Canceled loading to prevent loss of claim data. If you recently downgraded versions, please upgrade or contact author.");
        }
    }

}
