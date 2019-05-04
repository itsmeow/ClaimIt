package its_meow.claimit.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableList;

import its_meow.claimit.ClaimIt;
import its_meow.claimit.api.claim.ClaimArea;
import its_meow.claimit.api.event.ClaimAddedEvent;
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
