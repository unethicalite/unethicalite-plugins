package net.deceivedfx.plugins.thiever;

import net.deceivedfx.plugins.thiever.data.Stall;
import net.runelite.client.config.*;

@ConfigGroup("sneaky-thiever")
public interface SneakyThieverConfig extends Config {

    @ConfigTitle(
            keyName = "config",
            name = "Configuration",
            description = "Configure your settings",
            position = 0
    )
    String config = "Config";

    @ConfigItem(
            keyName = "stall",
            name = "Stall type",
            description = "What type of stall to thieve",
            position = 1,
            section = "config"

    )
    default Stall stall()
    {
        return Stall.CAKE;
    }

}
