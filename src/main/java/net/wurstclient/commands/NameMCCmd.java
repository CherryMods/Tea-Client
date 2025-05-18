/*
 * Copyright (c) 2014-2025 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.commands;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import net.minecraft.util.Util;
import net.wurstclient.command.CmdError;
import net.wurstclient.command.CmdException;
import net.wurstclient.command.CmdSyntaxError;
import net.wurstclient.command.Command;
import net.wurstclient.util.ChatUtils;

import java.io.InputStreamReader;
import java.net.URL;

public final class NameMCCmd extends Command
{
	private static final String mojangAPI =
		"https://api.mojang.com/users/profiles/minecraft/";
	
	public NameMCCmd()
	{
		super("namemc", "Quickly opens a user's NameMC profile.",
			".namemc <username>");
	}
	
	@Override
	public void call(String[] args) throws CmdException
	{
		if(args.length < 1)
			throw new CmdSyntaxError();
		
		final String target = String.join("", args);
		
		try
		{
			final String uuid = fetchMojang(target);
			
			ChatUtils.message("Opening profile in the browser...");
			
			String link = "https://namemc.com/profile/" + uuid;
			Util.getOperatingSystem().open(link);
			
		}catch(Exception e)
		{
			throw new CmdError("Failed to fetch profile");
		}
	}
	
	private String fetchMojang(String name) throws Exception
	{
		URL url = new URL(mojangAPI + name);
		
		JsonObject obj =
			JsonParser.parseReader(new InputStreamReader(url.openStream()))
				.getAsJsonObject();
		
		if(obj.has("id"))
		{
			return obj.get("id").getAsString();
		}
		
		return null;
	}
}
