// src/main/java/net/john_just/edans/ClientCache.java
package net.john_just.edans;

import net.john_just.edans.network.ProfileSyncPacket;

/** Данные, которые нужны только на клиенте */
public final class ClientCache {
    private ClientCache() {}

    /** Последний полученный пакет профиля */
    public static ProfileSyncPacket profileLocal;   // ← публичное поле
}