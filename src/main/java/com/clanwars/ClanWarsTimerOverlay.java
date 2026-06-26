package com.clanwars;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import javax.inject.Inject;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;

public class ClanWarsTimerOverlay extends OverlayPanel
{
	private final ClanWarsTimerPlugin plugin;
	private final ClanWarsTimerConfig config;

	@Inject
	public ClanWarsTimerOverlay(ClanWarsTimerPlugin plugin, ClanWarsTimerConfig config)
	{
		this.plugin = plugin;
		this.config = config;

		setPosition(config.overlayPosition());
		setPriority(PRIORITY_MED);
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		if (!config.enabled() || !plugin.isActive())
		{
			return null;
		}

		setPosition(config.overlayPosition());

		panelComponent.getChildren().add(TitleComponent.builder()
				.text("Clan Wars Timer")
				.color(Color.WHITE)
				.build());

		panelComponent.getChildren().add(LineComponent.builder()
				.left("Walls drop")
				.right(plugin.getFormattedTimeRemaining())
				.rightColor(config.textColor())
				.build());

		return super.render(graphics);
	}
}
