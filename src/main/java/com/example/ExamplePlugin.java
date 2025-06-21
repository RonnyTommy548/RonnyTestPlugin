package com.example;

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
	name = "Example"
)
public class ExamplePlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private ExampleConfig config;

	private NPC zulrah = null;
	private int[][] collisionMap = null;
	private WorldPoint playerLocation = null;
	private static final int ZULRAH_DEATH_ANIM = 5804;
	private static final String ZULRAH_NAME = "Zulrah";

	@Inject
	private LootTileOverlay lootTileOverlay;
	@Inject
	private net.runelite.client.ui.overlay.OverlayManager overlayManager;
	@Getter
	private WorldPoint currentLootTile;

	@Override
	protected void startUp() throws Exception
	{
		log.info("Example started!");
		overlayManager.add(lootTileOverlay);
	}
	
	@Override
	protected void shutDown() throws Exception
	{
		log.info("Example stopped!");
		overlayManager.remove(lootTileOverlay);
	}

	@Subscribe
	public void onGameTick(GameTick event) {
		WorldView wv = client.getTopLevelWorldView();
		playerLocation = client.getLocalPlayer().getWorldLocation();

		if (zulrah == null || playerLocation == null || collisionMap == null) {
			return;
		}

		zulrah = wv.npcs().byIndex(zulrah.getIndex());
		if (zulrah == null) {
			// zulrah finished dying
			return;
		}
		
		int baseX = client.getTopLevelWorldView().getBaseX();
		int baseY = client.getTopLevelWorldView().getBaseY();
		WorldPoint zulrahSW = zulrah.getWorldLocation();
		currentLootTile = LootPlacementAlgo.getLootDestinationTile(zulrahSW, playerLocation, collisionMap, baseX, baseY);

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
			System.out.println("Zulrah spawned");
		}
	}

	public int[][] getCollisionMap()
	{
		CollisionData collisions = client.getTopLevelWorldView().getCollisionMaps()[0];
		int[][] originalFlags = collisions.getFlags();
	
		// Deep copy of collision flags
		int[][] flags = new int[originalFlags.length][];
		for (int x = 0; x < originalFlags.length; x++)
		{
			flags[x] = originalFlags[x].clone();
		}
	
		// Add invisible walls to the *copied* map
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
	

	public void printCollisionMapWithLoot(int[][] flags)
	{
		WorldPoint playerLoc = client.getLocalPlayer().getWorldLocation();

		int baseX = client.getTopLevelWorldView().getBaseX();
		int baseY = client.getTopLevelWorldView().getBaseY();

		int playerLocalX = playerLoc.getX() - baseX;
		int playerLocalY = playerLoc.getY() - baseY;

		int zulrahLocalX = -1;
		int zulrahLocalY = -1;
		int lootLocalX = -1;
		int lootLocalY = -1;

		if (zulrah != null) {
			WorldPoint zulrahSW = zulrah.getWorldLocation();
			WorldPoint zulrahCenter = new WorldPoint(zulrahSW.getX() + 2, zulrahSW.getY() + 2, zulrahSW.getPlane());

			zulrahLocalX = zulrahCenter.getX() - baseX;
			zulrahLocalY = zulrahCenter.getY() - baseY;

			WorldPoint lootTile = LootPlacementAlgo.getLootDestinationTile(zulrahSW, playerLoc, flags, baseX, baseY);
			lootLocalX = lootTile.getX() - baseX;
			lootLocalY = lootTile.getY() - baseY;
		}

		for (int y = 0; y < 104; y++) {
			StringBuilder line = new StringBuilder();
			for (int x = 0; x < 104; x++) {
				if (x == lootLocalX && y == lootLocalY) {
					line.append('X'); // Loot
				}
				else if (x == playerLocalX && y == playerLocalY) {
					line.append('O'); // Player
				}
				else if (zulrah != null && x == zulrahLocalX && y == zulrahLocalY) {
					line.append('Z'); // Zulrah
				}
				else {
					int flag = flags[x][y];
					boolean blocked = (flag & CollisionDataFlag.BLOCK_MOVEMENT_FULL) != 0;
					line.append(blocked ? '#' : '.');
				}
				line.append(' ');
			}
			System.out.println(line.toString());
		}
	}


	@Subscribe
	public void onAnimationChanged(AnimationChanged event) {
		if (event.getActor().getAnimation() == ZULRAH_DEATH_ANIM) {
			System.out.println("Zulrah died at position = " + event.getActor().getWorldLocation().getX() + ", " + event.getActor().getWorldLocation().getY());
			zulrah = null;
		}
	}

	@Provides
	ExampleConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(ExampleConfig.class);
	}
}
