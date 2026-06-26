package com.clanwars;

import com.google.inject.Provides;
import java.util.Locale;
import javax.inject.Inject;
import lombok.Getter;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.MessageNode;
import net.runelite.api.SoundEffectID;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.Text;

@PluginDescriptor(
        name = "ClanWars Countdown",
        description = "Shows a countdown until Clan Wars walls drop",
        tags = {"clan", "wars", "timer", "pvp"}
)
public class ClanWarsTimerPlugin extends Plugin
{
    private static final double TICK_LENGTH_SECONDS = 0.6d;

    @Inject
    private Client client;

    @Inject
    private OverlayManager overlayManager;

    @Inject
    private ClanWarsTimerOverlay overlay;

    @Inject
    private ClanWarsTimerConfig config;

    @Getter
    private int ticksRemaining = 0;

    @Getter
    private boolean active = false;

    private boolean alertPlayed = false;
    private int lastBattleMessageId = -1;
    private int lastBattleMessageTimestamp = -1;

    @Provides
    ClanWarsTimerConfig provideConfig(ConfigManager configManager)
    {
        return configManager.getConfig(ClanWarsTimerConfig.class);
    }

    @Override
    protected void startUp()
    {
        reset();
        overlayManager.add(overlay);
    }

    @Override
    protected void shutDown()
    {
        overlayManager.remove(overlay);
        reset();
    }

    private void reset()
    {
        active = false;
        ticksRemaining = 0;
        alertPlayed = false;
    }

    @Subscribe
    public void onGameStateChanged(GameStateChanged gameStateChanged)
    {
        if (gameStateChanged.getGameState() == GameState.LOADING
                || gameStateChanged.getGameState() == GameState.HOPPING
                || gameStateChanged.getGameState() == GameState.LOGIN_SCREEN)
        {
            reset();
        }
    }

    @Subscribe
    public void onGameTick(GameTick tick)
    {
        checkForBattleStartMessage();

        if (!active)
        {
            return;
        }

        ticksRemaining--;

        int secondsRemaining = getSecondsRemaining();

        if (config.soundEnabled()
                && !alertPlayed
                && secondsRemaining == config.alertSeconds())
        {
            client.playSoundEffect(SoundEffectID.UI_BOOP);
            alertPlayed = true;
        }

        if (ticksRemaining <= 0)
        {
            active = false;
            ticksRemaining = 0;
            alertPlayed = false;
        }
    }

    int getSecondsRemaining()
    {
        return (int) Math.ceil(ticksRemaining * TICK_LENGTH_SECONDS);
    }

    String getFormattedTimeRemaining()
    {
        int secondsRemaining = getSecondsRemaining();
        return String.format("%d:%02d", secondsRemaining / 60, secondsRemaining % 60);
    }

    private void checkForBattleStartMessage()
    {
        if (client.getGameState() != GameState.LOGGED_IN)
        {
            return;
        }

        MessageNode newestBattleMessage = null;
        for (MessageNode messageNode : client.getMessages())
        {
            if (messageNode == null || !isBattleStartMessage(messageNode))
            {
                continue;
            }

            if (newestBattleMessage == null || isNewer(messageNode, newestBattleMessage))
            {
                newestBattleMessage = messageNode;
            }
        }

        if (newestBattleMessage == null || !isNewerThanLastProcessed(newestBattleMessage))
        {
            return;
        }

        lastBattleMessageId = newestBattleMessage.getId();
        lastBattleMessageTimestamp = newestBattleMessage.getTimestamp();
        startTimer();
    }

    private boolean isBattleStartMessage(MessageNode messageNode)
    {
        String message = messageNode.getValue();
        if (message == null)
        {
            return false;
        }

        String normalized = Text.removeTags(message)
                .replace('\u00A0', ' ')
                .toLowerCase(Locale.ENGLISH)
                .trim();

        return normalized.contains("has initiated a battle")
                && normalized.contains("clan wars challenge area");
    }

    private boolean isNewer(MessageNode candidate, MessageNode current)
    {
        if (candidate.getTimestamp() != current.getTimestamp())
        {
            return candidate.getTimestamp() > current.getTimestamp();
        }

        return candidate.getId() > current.getId();
    }

    private boolean isNewerThanLastProcessed(MessageNode messageNode)
    {
        if (messageNode.getTimestamp() != lastBattleMessageTimestamp)
        {
            return messageNode.getTimestamp() > lastBattleMessageTimestamp;
        }

        return messageNode.getId() > lastBattleMessageId;
    }

    private void startTimer()
    {
        ticksRemaining = (int) Math.ceil(config.wallTime() / TICK_LENGTH_SECONDS);
        active = true;
        alertPlayed = false;
    }
}
