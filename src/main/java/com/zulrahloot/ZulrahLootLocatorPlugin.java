package com.zulrahloot;

import javax.inject.Inject;

import com.google.inject.Provides;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.CollisionData;
import net.runelite.api.CollisionDataFlag;
import net.runelite.api.NPC;
import net.runelite.api.WorldView;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.AnimationChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.NpcSpawned;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

@Slf4j
@PluginDescriptor(
	name = "Zulrah Loot Locator",
	description = "Highlight where Zulrah loot will spawn",
	tags = {"highlight", "overlay", "zulrah", "loot"}
)
public class ZulrahLootLocatorPlugin extends Plugin
{
	@Inject
	private Client client;

	@Getter
	private NPC zulrah = null;
	private int[][] collisionMap = null;
	private WorldPoint playerLocation = null;
	@Getter
	private int lastZulrahHealthRatio = -1;
	@Getter
	private int lastZulrahHealthScale = -1;
	public static final int ZULRAH_DEATH_ANIM = 5804;
	public static final int ZULRAH_DEATH_TICKS = 7;
	public static final int ZULRAH_MAX_HP = 500;
	public static final String ZULRAH_NAME = "Zulrah";

	@Inject
	private ZulrahLootLocatorOverlay lootTileOverlay;
	@Inject
	private net.runelite.client.ui.overlay.OverlayManager overlayManager;
	@Getter
	private WorldPoint currentLootTile;

	private int tickCount = 0;
	@Getter
	private int fadeStartClientTick = -1;
	@Getter
	private boolean isZulrahDying = false;
	
	@Override
	protected void startUp() throws Exception
	{
		overlayManager.add(lootTileOverlay);
		log.info("ZulrahLootLocator Started");
	}
	
	@Override
	protected void shutDown() throws Exception
	{
		overlayManager.remove(lootTileOverlay);
		log.info("ZulrahLootLocator Stopped");
	}

	@Subscribe
	public void onGameTick(GameTick event) {
		playerLocation = client.getLocalPlayer().getWorldLocation();
		if (zulrah == null || playerLocation == null || collisionMap == null) {
			handleFade();
			return;
		}
		
		WorldView wv = client.getTopLevelWorldView();
		zulrah = wv.npcs().byIndex(zulrah.getIndex());
		if (zulrah == null) {
			// Zulrah finished dying
			return;
		}
		
		// Zulrah is alive
		int baseX = client.getTopLevelWorldView().getBaseX();
		int baseY = client.getTopLevelWorldView().getBaseY();
		WorldPoint zulrahSW = zulrah.getWorldLocation();
		currentLootTile = LootPlacement.getLootDestinationTile(zulrahSW, playerLocation, collisionMap, baseX, baseY);

		if (zulrah.getHealthRatio() > -1) {
			lastZulrahHealthRatio = zulrah.getHealthRatio();
		}
		if (zulrah.getHealthScale() > -1) {
			lastZulrahHealthScale = zulrah.getHealthScale();
		}

		// Cancel fade if it was active
		isZulrahDying = false;
		fadeStartClientTick = -1;
	}

	@Subscribe
	public void onNpcSpawned(NpcSpawned npcSpawned)
	{
		final NPC npc = npcSpawned.getNpc();
		final String npcName = npc.getName();

		if (npcName == null)
		{
			return;
		}

		if (npcName.equals(ZULRAH_NAME)) {
			zulrah = npc;
			collisionMap = getCollisionMap();
		}
	}

	private void handleFade() {
		if (isZulrahDying)
		{
			tickCount++;

			if (tickCount >= ZULRAH_DEATH_TICKS)
			{
				currentLootTile = null;
				isZulrahDying = false;
				tickCount = 0;
			}
		}
	}

	public int[][] getCollisionMap()
	{
		CollisionData collisions = client.getTopLevelWorldView().getCollisionMaps()[0];
		int[][] originalFlags = collisions.getFlags();
	
		int[][] flags = new int[originalFlags.length][];
		for (int x = 0; x < originalFlags.length; x++)
		{
			flags[x] = originalFlags[x].clone();
		}
	
		/* From Mod Ash:
		 *
		 * When Zulrah dies, it wipes any poison cloud sites in case there's a cloud there. It does this by spawning invisible scenery pieces on their SW corners. The invisible scenery is only meant to be spawned for 1 tick, but the game engine's behaviour has changed in the last year or so regarding scenery spawned temporarily in instances, and what happens when the spawn expires. That'd probably account for it.
		 */
		int[][] invisibleWallCoords = {
			{52, 52},
			{56, 55},
			{56, 58},
			{56, 61},
			{46, 56},
			{46, 59}
		};
	
		for (int[] coord : invisibleWallCoords)
		{
			int x = coord[0];
			int y = coord[1];
			if (x >= 0 && x < flags.length && y >= 0 && y < flags[x].length)
			{
				flags[x][y] |= CollisionDataFlag.BLOCK_MOVEMENT_FULL;
			}
		}
	
		return flags;
	}


	@Subscribe
	public void onAnimationChanged(AnimationChanged event) {
		if (event.getActor().getAnimation() == ZULRAH_DEATH_ANIM) {
			isZulrahDying = true;
			fadeStartClientTick = client.getGameCycle();
			zulrah = null;
			lastZulrahHealthRatio = -1;
			lastZulrahHealthScale = -1;
		}
	}

	@Provides
	ZulrahLootLocatorConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(ZulrahLootLocatorConfig.class);
	}
}
