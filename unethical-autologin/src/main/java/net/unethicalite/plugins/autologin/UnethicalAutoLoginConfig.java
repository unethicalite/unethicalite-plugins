package net.unethicalite.plugins.autologin;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("hootautologin")
public interface UnethicalAutoLoginConfig extends Config
{
	@ConfigItem(
			keyName = "username",
			name = "Username",
			description = "Username",
			position = 0
	)
	default String username()
	{
		return "Username";
	}

	@ConfigItem(
			keyName = "password",
			name = "Password",
			description = "Password",
			secret = true,
			position = 1
	)
	default String password()
	{
		return "Password";
	}

	@ConfigItem(
			keyName = "auth",
			name = "Authenticator",
			description = "Authenticator",
			secret = true,
			position = 2
	)
	default String auth()
	{
		return "Authenticator";
	}

	@ConfigItem(
			keyName = "useWorld",
			name = "Select world",
			description = "Select world to login to",
			position = 3
	)
	default boolean useWorld()
	{
		return false;
	}

	@ConfigItem(
			keyName = "world",
			name = "World",
			description = "World Selector",
			position = 4,
			hidden = true,
			unhide = "useWorld"
	)
	default int world()
	{
		return 301;
	}

	@ConfigItem(
			keyName = "lastWorld",
			name = "Save last world",
			description = "Save last world",
			position = 5,
			hidden = true,
			unhide = "useWorld"
	)
	default boolean lastWorld()
	{
		return false;
	}

	@ConfigItem(
			keyName = "welcomeScreen",
			name = "Complete Welcome screen",
			description = "Automatically presses the 'Click here to Play' button after login",
			position = 6
	)
	default boolean welcomeScreen()
	{
		return false;
	}

	@ConfigItem(
			keyName = "loginOnDC",
			name = "Login On Disconnect",
			description = "Automatically logins when the game disconnects.",
			position = 6
	)
	default boolean loginOnDC()
	{
		return false;
	}
}
