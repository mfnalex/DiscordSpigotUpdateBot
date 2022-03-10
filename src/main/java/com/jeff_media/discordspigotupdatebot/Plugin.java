package com.jeff_media.discordspigotupdatebot;

import lombok.Getter;

public enum Plugin {

    ANGELCHEST_PLUS(88214, "AngelChest Plus", "angelchest/64.png"),
    ANGELCHEST_FREE(60383, "AngelChest Free", "angelchest/64.png"),
    CHESTSORT(59773, "ChestSort", "chestsort/96.png"),
    LUMBERJACK(60306, "LumberJack", "lumberjack/256.png"),
    FILTERED_HOPPERS(96037,"Filtered Hoppers", null),
    INVUNLOAD(60095,"InvUnload", "invunload/64.png"),
    AUTOCOMPOSTER(95013,"AutoComposter", null),
    AUTOSHULKER(89807,"AutoShulker", "autoshulker/64.png"),
    BETTERTRIDENTS(92656,"BetterTridents", "bettertridents/256.png"),
    DROP2INVENTORY_PLUS(87784,"Drop2Inventory Plus", "drop2inventory/64.png"),
    BETTERLOGSTRIP(99456,"BetterLogStrip", "betterlogstrip/256.png"),
    REPLANT(92668,"RePlant", "replant/256.png"),
    JUKEBOX_PLUS(87750,"Jukebox Plus", "jukeboxplus/64.png"),
    DOORS_RELOADED(91722,"Doors Reloaded", "doorsreloaded/64.png"),
    BESTTOOLS(81490,"BestTools", "besttools/256.png"),
    LIGHTPERMS(62447,"LightPerms", "lightperms/64.png"),
    DROP2INVENTORY_FREE(62214,"Drop2Inventory Free", "drop2inventory/64.png");


    @Getter private final int id;
    @Getter private final String name;
    private final String logo;

    public String getLogo() {
        if(logo == null) return null;
        return "https://static.jeff-media.com/img/" + logo;
    }

    Plugin(int id, String name, String logo) {
        this.id = id;
        this.name = name;
        this.logo = logo;
    }

    public static Plugin fromId(int id) {
        for(Plugin plugin : values()) {
            if(plugin.getId() == id) return plugin;
        }
        throw new IllegalArgumentException("No plugin found with ID " + id);
    }
}
