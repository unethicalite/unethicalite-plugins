package net.deceivedfx.plugins.runecraft;

import net.runelite.client.config.*;

@ConfigGroup("sneaky-runecrafter")
public interface SneakyRunecrafterConfig extends Config {

    @ConfigTitle(
            keyName = "config",
            name = "Configuration",
            description = "Configure your settings",
            position = 0
    )
    String config = "Config";

    @ConfigItem(
            keyName = "showOverlay",
            name = "Show Overlay",
            description = "Show Overlay?",
            position = 2
    )
    default boolean showOverlay() { return false; }

    @ConfigItem(
            keyName = "toggleKey",
            name = "Toggle Key",
            description = "Key to toggle on / off",
            position = 3
    )
    default Keybind toggleKey()
    {
        return Keybind.NOT_SET;
    }

    @ConfigItem(
            keyName = "logParams",
            name = "Log MenuEntry Parameters",
            description = "Logs MenuEntry options",
            position = 4
    )
    default boolean logParams() { return false; }
}
