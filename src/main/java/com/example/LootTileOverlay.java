package com.example;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Polygon;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.Perspective;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayUtil;

public class LootTileOverlay extends Overlay
{
	private final Client client;
	private final ExamplePlugin plugin;

	@Inject
	public LootTileOverlay(Client client, ExamplePlugin plugin)
	{
		this.client = client;
		this.plugin = plugin;

		setPosition(OverlayPosition.DYNAMIC);
		setLayer(OverlayLayer.ABOVE_SCENE);
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		WorldPoint lootTile = plugin.getCurrentLootTile();

		if (lootTile == null || client.getPlane() != lootTile.getPlane())
		{
			return null;
		}

		LocalPoint localPoint = LocalPoint.fromWorld(client, lootTile);
		if (localPoint == null)
		{
			return null;
		}

		Polygon poly = Perspective.getCanvasTilePoly(client, localPoint);
		if (poly == null)
		{
			return null;
		}

		OverlayUtil.renderPolygon(
			graphics,
			poly,
			Color.RED,
			new Color(255, 0, 0, 50), // translucent fill
			new BasicStroke(2)
		);

		return null;
	}
}