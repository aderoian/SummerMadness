package dev.armenderoian.summad.network;

public class ClientboundPacket {

    public record ClientboundCombatToast(boolean inCombat) {}
}
