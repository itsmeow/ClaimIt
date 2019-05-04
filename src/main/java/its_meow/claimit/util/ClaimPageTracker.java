package its_meow.claimit.util;

import java.util.ArrayList;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableList;

import its_meow.claimit.ClaimIt;
import its_meow.claimit.api.event.ClaimAddedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber(modid = ClaimIt.MOD_ID)
public class ClaimPageTracker {
    
    private static ArrayList<ClaimPage> pages = new ArrayList<ClaimPage>();
    
    @SubscribeEvent
    public static void claimAdded(ClaimAddedEvent e) {
        boolean addedAny = false;
        for(ClaimPage page : pages) {
            if(page.addToPageIfNotFull(e.getClaim())) { addedAny = true; break; }
        }
        if(!addedAny) {
            pages.add(new ClaimPage(e.getClaim(), null, null));
        }
    }
    
    @Nullable
    public static ClaimPage getPage(int index) {
        try {
            return pages.get(index);
        } catch(IndexOutOfBoundsException e) {
            return null;
        }
    }
    
    public static ImmutableList<ClaimPage> getPages() {
        return ImmutableList.copyOf(pages);
    }
    
    public static int getMaxPage() {
        return pages.size();
    }
    
}
