package net.blay09.mods.littlejoys.api.client;

public class LittleJoysClientAPI {
    private static InternalClientMethods internalMethods;

    /**
     * Internal use only.
     */
    public static void __setupAPI(InternalClientMethods internalMethods) {
        LittleJoysClientAPI.internalMethods = internalMethods;
    }

}
