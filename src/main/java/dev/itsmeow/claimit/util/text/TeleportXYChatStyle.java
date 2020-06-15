package dev.itsmeow.claimit.util.text;

public class TeleportXYChatStyle extends CommandChatStyle {

    public TeleportXYChatStyle(int dim, int x, int z) {
        super("/ci admin tpdimxz " + dim + " " + x + " " + z, true, "Click to teleport");
    }

}
