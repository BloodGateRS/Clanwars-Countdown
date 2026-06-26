package com.example;

import com.clanwars.ClanWarsTimerPlugin;
import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class PluginLauncher
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(ClanWarsTimerPlugin.class);
		RuneLite.main(args);
	}
}