package dev.itsmeow.claimit.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableList;

import dev.itsmeow.claimit.ClaimIt;
import dev.itsmeow.claimit.api.claim.ClaimArea;
import dev.itsmeow.claimit.api.event.claim.ClaimAddedEvent;
import dev.itsmeow.claimit.api.event.claim.ClaimRemovedEvent;
import dev.itsmeow.claimit.api.event.claim.ClaimsClearedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber(modid = ClaimIt.MOD_ID)
public class ClaimPageTracker {
    
    private static ArrayList<ClaimPage> pages = new ArrayList<ClaimPage>();
    private static HashMap<UUID, ArrayList<ClaimPage>> playerPages = new HashMap<UUID, ArrayList<ClaimPage>>();
    
    @SubscribeEvent
    public static void claimAdded(ClaimAddedEvent e) {
        boolean addedAny = false;
        ClaimArea claim = e.getClaim();
        for(ClaimPage page : pages) {
            if(page.addToPageIfNotFull(claim)) { addedAny = true; break; }
        }
        if(!addedAny) {
            pages.add(new ClaimPage(claim, null, null));
        }
        
        UUID owner = claim.getOwner();
        ensurePages(owner);
        
        boolean addedAny2 = false;
        for(ClaimPage page : playerPages.get(owner)) {
            if(page.addToPageIfNotFull(claim)) { addedAny2 = true; break; }
        }
        if(!addedAny2) {
            playerPages.get(owner).add(new ClaimPage(claim, null, null));
        }
    }
    
    @SubscribeEvent
    public static void claimRemoved(ClaimRemovedEvent e) {
        ArrayList<ClaimPage> playerPageList = playerPages.get(e.getClaim().getOwner());
        if(playerPageList != null && !playerPageList.isEmpty()) {
            playerPages.put(e.getClaim().getOwner(), removeAndRebuild(playerPageList, e.getClaim()));
        }
        if(!pages.isEmpty()) {
            pages = removeAndRebuild(pages, e.getClaim());
        }
    }
        
    private static ArrayList<ClaimPage> removeAndRebuild(ArrayList<ClaimPage> list, ClaimArea toRemove) {
        ArrayList<ClaimArea> claimList = new ArrayList<ClaimArea>();
        ArrayList<ClaimPage> newList = new ArrayList<ClaimPage>();
        list.forEach(page -> page.getClaimsInPage().forEach(claim -> claimList.add(claim)));
        claimList.remove(toRemove);
        for(ClaimArea claim : claimList) {
            boolean addedAny = false;
            for(ClaimPage page : newList) {
                if(page.addToPageIfNotFull(claim)) { addedAny = true; break; }
            }
            if(!addedAny) {
                newList.add(new ClaimPage(claim, null, null));
            }
        }
        return newList;
    }
    
    @SubscribeEvent
    public static void claimsCleared(ClaimsClearedEvent.Pre e) {
        pages.clear();
        playerPages.clear();
    }
    
    @Nullable
    public static ClaimPage getPage(@Nullable UUID owner, int index) {
        try {
            if(owner != null) {
                ensurePages(owner);
                return playerPages.get(owner).get(index);
            } else {
                return pages.get(index);
            }
        } catch(IndexOutOfBoundsException e) {
            return null;
        }
    }
    
    public static ImmutableList<ClaimPage> getPages(@Nullable UUID filter) {
        ensurePages(filter);
        return ImmutableList.copyOf(filter == null ? pages : playerPages.get(filter));
    }
    
    public static int getMaxPage(@Nullable UUID filter) {
        ensurePages(filter);
        return filter == null ? pages.size() : playerPages.get(filter).size();
    }
    
    private static void ensurePages(@Nullable UUID owner) {
        if(owner != null && !playerPages.containsKey(owner)) {
            playerPages.put(owner, new ArrayList<ClaimPage>());
        }
    }
    
}
