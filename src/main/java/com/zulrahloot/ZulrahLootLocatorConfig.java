package com.zulrahloot;

import net.runelite.client.config.Alpha;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.Range;

import java.awt.Color;

@ConfigGroup("zulrahloot")
public interface ZulrahLootLocatorConfig extends Config
{
	@ConfigItem(
		keyName = "lootTileColor",
		name = "Loot Tile Color",
		description = "Color of the tile where loot is expected to appear"
	)
	@Alpha
	default Color lootTileColor()
	{
		return new Color(255, 255, 0, 100); // yellow
	}

	@Range(
		min = 0,
		max = 500
	)
	@ConfigItem(
		keyName = "hpThreshold",
		name = "Zulrah HP Threshold",
		description = "Show loot tile when Zulrah is at or below this HP (0-500)"
	)
	default int hpThreshold()
	{
		return 75;
	}
}