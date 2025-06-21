package com.zulrahloot;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Polygon;

import javax.inject.Inject;

import net.runelite.api.Client;
import net.runelite.api.Perspective;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayUtil;

public class ZulrahLootLocatorOverlay extends Overlay
{
	private final Client client;
	private final ZulrahLootLocatorPlugin plugin;
	private final ZulrahLootLocatorConfig config;

	@Inject
	public ZulrahLootLocatorOverlay(Client client, ZulrahLootLocatorPlugin plugin, ZulrahLootLocatorConfig config)
	{
		this.client = client;
		this.plugin = plugin;
		this.config = config;

		setPosition(OverlayPosition.DYNAMIC);
		setLayer(OverlayLayer.ABOVE_SCENE);
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		if (!shouldRender())
		{
			return null;
		}

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

		Color baseColor = config.lootTileColor();
		int baseAlpha = baseColor.getAlpha();
		
		int finalAlpha = baseAlpha;
		
		if (plugin.isZulrahDying())
		{
			int clientTicksElapsed = client.getGameCycle() - plugin.getFadeStartClientTick();
			int fadeClientTicks = ZulrahLootLocatorPlugin.ZULRAH_DEATH_TICKS * 30;
			if (clientTicksElapsed < fadeClientTicks)
			{
				float fadeRatio = 1f - (clientTicksElapsed / (float) fadeClientTicks);
				finalAlpha = Math.round(baseAlpha * fadeRatio);
			}
			else
			{	
				return null; // Fully faded out
			}
		}
		
		Color borderColor = new Color(baseColor.getRed(), baseColor.getGreen(), baseColor.getBlue(), finalAlpha);
		Color fillColor = new Color(baseColor.getRed(), baseColor.getGreen(), baseColor.getBlue(), finalAlpha);
		

		OverlayUtil.renderPolygon(
			graphics,
			poly,
			borderColor,
			fillColor,
			new BasicStroke(2)
		);

		return null;
	}

	private boolean shouldRender()
	{
		boolean zulrahUnderThreshold = ((float) plugin.getLastZulrahHealthRatio() / (float) plugin.getLastZulrahHealthScale()) * ZulrahLootLocatorPlugin.ZULRAH_MAX_HP <= config.hpThreshold();

		return plugin.isZulrahDying() || zulrahUnderThreshold;
	}
}
