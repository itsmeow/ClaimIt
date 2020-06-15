package dev.itsmeow.claimit.util;

import java.util.ArrayList;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableList;

import dev.itsmeow.claimit.api.claim.ClaimArea;

public class ClaimPage {
    
    private ArrayList<ClaimArea> page = new ArrayList<ClaimArea>(3);
    
    public ClaimPage(ClaimArea area1, @Nullable ClaimArea area2, @Nullable ClaimArea area3) {
        page.add(area1);
        if(area2 != null) {
            page.add(area2);
        }
        if(area3 != null) {
            page.add(area3);
        }
    }
    
    public int getPageSize() {
        return page.size();
    }
    
    public boolean isFull() {
        return page.size() == 3;
    }
    
    public boolean addToPageIfNotFull(ClaimArea claim) {
        if(!this.isFull()) {
            page.add(claim);
            return true;
        }
        return false;
    }
    
    @Nullable
    public ClaimArea getInPage(int index) {
        int i = index % 3;
        return page.size() >= i ? page.get(i) : null;
    }
    
    public ImmutableList<ClaimArea> getClaimsInPage() {
        return ImmutableList.copyOf(page);
    }

}
