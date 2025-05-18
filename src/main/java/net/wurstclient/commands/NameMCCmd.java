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
import net.wurstclient.command.CmdException;
import net.wurstclient.command.CmdSyntaxError;
import net.wurstclient.command.Command;
import net.wurstclient.util.ChatUtils;

import java.io.InputStreamReader;
import java.net.URI;
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
		// must have 1 or more arguments
		if(args.length < 1)
			throw new CmdSyntaxError();
		
		// look up each target individually
		for(String target : args)
		{
			lookup(target);
		}
	}
	
	private void lookup(String target)
	{
		try
		{
			// try getting the uuid just to make sure the user exists
			fetchUUIDFromMojang(target);
			
			ChatUtils
				.message("Opening profile `" + target + "` in the browser...");
			Util.getOperatingSystem().open("https://namemc.com/" + target);
		}catch(Exception e)
		{
			ChatUtils.error("Failed to fetch profile `" + target + "`");
		}
	}
	
	private String fetchUUIDFromMojang(String name) throws Exception
	{
		URL url = URI.create(mojangAPI + name).toURL();
		
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
