/*
 * Copyright (c) 2014-2024 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */

package net.wurstclient.mixin;

import java.util.Map;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Pattern;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftProfileTextures;

import net.minecraft.client.texture.PlayerSkinProvider;
import net.minecraft.client.util.SkinTextures;
import net.minecraft.util.Uuids;
import net.wurstclient.util.json.JsonUtils;
import net.wurstclient.util.json.WsonObject;

@Mixin(PlayerSkinProvider.class)
public abstract class PlayerSkinProviderMixin
{
	@Unique
	private static HashMap<String, String> capes;
	
	@Unique
	private MinecraftProfileTexture currentCape;
	
	private static final String[] CAPE_SOURCES =
		{"https://www.wurstclient.net/api/v1/capes.json",
			"https://sparklet.org/api/tea-capes"};
	
	@Inject(at = @At("HEAD"),
		method = "fetchSkinTextures(Ljava/util/UUID;Lcom/mojang/authlib/minecraft/MinecraftProfileTextures;)Ljava/util/concurrent/CompletableFuture;")
	private void onFetchSkinTextures(UUID uuid,
		MinecraftProfileTextures textures,
		CallbackInfoReturnable<CompletableFuture<SkinTextures>> cir)
	{
		String uuidString = uuid.toString();
		
		try
		{
			if(capes == null)
				setupWurstCapes();
			
			if(capes.containsKey(uuidString))
			{
				String capeURL = capes.get(uuidString);
				currentCape = new MinecraftProfileTexture(capeURL, null);
				
			}else
				currentCape = null;
			
		}catch(Exception e)
		{
			System.err
				.println("[Wurst] Failed to load cape for UUID " + uuidString);
			
			e.printStackTrace();
		}
	}
	
	@ModifyVariable(at = @At("STORE"),
		method = "fetchSkinTextures(Ljava/util/UUID;Lcom/mojang/authlib/minecraft/MinecraftProfileTextures;)Ljava/util/concurrent/CompletableFuture;",
		ordinal = 1,
		name = "minecraftProfileTexture2")
	private MinecraftProfileTexture modifyCapeTexture(
		MinecraftProfileTexture old)
	{
		if(currentCape == null)
			return old;
		
		MinecraftProfileTexture result = currentCape;
		currentCape = null;
		return result;
	}
	
	@Unique
	private void setupWurstCapes()
	{
		capes = new HashMap<>();
		
		for(String src : CAPE_SOURCES)
		{
			try
			{
				// assign map first to prevent endless retries if download fails
				Pattern uuidPattern = Pattern.compile(
					"[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}");
				
				// download cape list from wurstclient.net
				WsonObject rawCapes = JsonUtils.parseURLToObject(src);
				
				// convert names to offline UUIDs
				for(Entry<String, String> entry : rawCapes.getAllStrings()
					.entrySet())
				{
					String name = entry.getKey();
					String capeURL = entry.getValue();
					
					// check if name is already a UUID
					if(uuidPattern.matcher(name).matches())
					{
						capes.put(name, capeURL);
						continue;
					}
					
					// convert name to offline UUID
					String offlineUUID = "" + Uuids.getOfflinePlayerUuid(name);
					capes.put(offlineUUID, capeURL);
				}
				
			}catch(Exception e)
			{
				System.err
					.println("[Wurst] Failed to load capes from +" + src + "!");
				
				e.printStackTrace();
			}
		}
	}
	
	// reduced and inlined version of this post:
	// https://stackoverflow.com/a/34092374/
	private static JsonObject extendJsonObject(JsonObject leftObj,
		JsonObject rightObj)
	{
		for(Map.Entry<String, JsonElement> rightEntry : rightObj.entrySet())
		{
			String rightKey = rightEntry.getKey();
			JsonElement rightVal = rightEntry.getValue();
			if(leftObj.has(rightKey))
			{
				// conflict
				JsonElement leftVal = leftObj.get(rightKey);
				if(leftVal.isJsonArray() && rightVal.isJsonArray())
				{
					JsonArray leftArr = leftVal.getAsJsonArray();
					JsonArray rightArr = rightVal.getAsJsonArray();
					// concat the arrays -- there cannot be a conflict
					// in an array, it's just a collection of stuff
					for(int i = 0; i < rightArr.size(); i++)
					{
						leftArr.add(rightArr.get(i));
					}
				}else if(leftVal.isJsonObject() && rightVal.isJsonObject())
				{
					// recursive merging
					extendJsonObject(leftVal.getAsJsonObject(),
						rightVal.getAsJsonObject());
				}else
				{
					// not both arrays or objects, normal merge with conflict
					// resolution
					leftObj.add(rightKey, rightVal);
				}
			}else
			{
				// no conflict, add to the object
				leftObj.add(rightKey, rightVal);
			}
		}
		
		return leftObj;
	}
}
