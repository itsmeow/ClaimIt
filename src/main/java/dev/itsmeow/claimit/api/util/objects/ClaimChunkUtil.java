package dev.itsmeow.claimit.api.util.objects;

import java.util.Objects;

import net.minecraft.util.math.BlockPos;

public class ClaimChunkUtil {
    
    public static class ClaimChunk {
        
        public final int x;
        public final int z;
        
        public ClaimChunk(int x, int z) {
            this.x = x;
            this.z = z;
        }
        
        @Override
        public boolean equals(Object o) {
            if(!(o instanceof ClaimChunk)) {
                return false;
            }
            ClaimChunk c = (ClaimChunk) o;
            return o == this || this.x == c.x && this.z == c.z;
        }
        
        @Override
        public int hashCode() {
            return Objects.hash(x, z);
        }
        
    }
    
    public static ClaimChunk getChunk(BlockPos pos) {
        return new ClaimChunk(pos.getX() >> 4, pos.getZ() >> 4);
    }
    
}
