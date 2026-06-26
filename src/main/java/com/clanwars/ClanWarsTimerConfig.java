package com.clanwars;


import java.awt.Color;
import net.runelite.client.config.Alpha;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.ui.overlay.OverlayPosition;

@ConfigGroup("Clan Wars Countdown Timer")
public interface ClanWarsTimerConfig extends Config
{
	@ConfigItem(
			keyName = "enabled",
			name = "Enable overlay",
			description = "Show the Clan Wars countdown overlay"
	)
	default boolean enabled()
	{
		return true;
	}

	@ConfigItem(
			keyName = "wallTime",
			name = "Wall drop time",
			description = "Time in seconds before the walls drop",
			position = 1
	)
	default int wallTime()
	{
		return 120;
	}

	@ConfigItem(
			keyName = "soundEnabled",
			name = "Enable sound alert",
			description = "Play a sound before the walls drop",
			position = 2
	)
	default boolean soundEnabled()
	{
		return true;
	}

	@ConfigItem(
			keyName = "alertSeconds",
			name = "Alert at",
			description = "Seconds remaining when the sound alert should play",
			position = 3
	)
	default int alertSeconds()
	{
		return 10;
	}

	@Alpha
	@ConfigItem(
			keyName = "textColor",
			name = "Text color",
			description = "Color of the countdown text",
			position = 4
	)
	default Color textColor()
	{
		return Color.RED;
	}

	@ConfigItem(
			keyName = "overlayPosition",
			name = "Overlay position",
			description = "Where the overlay is displayed",
			position = 5
	)
	default OverlayPosition overlayPosition()
	{
		return OverlayPosition.TOP_CENTER;
	}
}
