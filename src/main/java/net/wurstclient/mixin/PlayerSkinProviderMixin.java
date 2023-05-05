/*
 * Copyright (c) 2014-2023 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.mixin;

import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;

import javax.annotation.Nullable;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import java.util.Map;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.InsecurePublicKeyException;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.PlayerSkinProvider;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

@Mixin(PlayerSkinProvider.class)
public class PlayerSkinProviderMixin
{
	@Shadow
	@Final
	private MinecraftSessionService sessionService;
	
	private static JsonObject capes;
	
	@Inject(at = @At("HEAD"),
		method = "loadSkin(Lcom/mojang/authlib/GameProfile;Lnet/minecraft/client/texture/PlayerSkinProvider$SkinTextureAvailableCallback;Z)V",
		cancellable = true)
	private void onLoadSkin(GameProfile profile,
		PlayerSkinProvider.SkinTextureAvailableCallback callback,
		boolean requireSecure, CallbackInfo ci)
	{
		// Can't @Inject nicely because everything is wrapped in a lambda.
		// Had to replace the whole method.
		
		Runnable runnable = () -> {
			HashMap<MinecraftProfileTexture.Type, MinecraftProfileTexture> map =
				Maps.newHashMap();
			try
			{
				map.putAll(sessionService.getTextures(profile, requireSecure));
			}catch(InsecurePublicKeyException e)
			{
				// empty catch block
			}
			if(map.isEmpty())
			{
				profile.getProperties().clear();
				if(profile.getId().equals(MinecraftClient.getInstance()
					.getSession().getProfile().getId()))
				{
					profile.getProperties().putAll(
						MinecraftClient.getInstance().getSessionProperties());
					map.putAll(sessionService.getTextures(profile, false));
				}else
				{
					sessionService.fillProfileProperties(profile,
						requireSecure);
					try
					{
						map.putAll(
							sessionService.getTextures(profile, requireSecure));
					}catch(InsecurePublicKeyException e)
					{
						
					}
				}
			}
			
			addWurstCape(profile, map);
			
			MinecraftClient.getInstance().execute(() -> {
				RenderSystem.recordRenderCall(() -> {
					ImmutableList.of(Type.SKIN, Type.CAPE).forEach(type -> {
						if(map.containsKey(type))
							loadSkin(map.get(type), type, callback);
					});
				});
			});
		};
		Util.getMainWorkerExecutor().execute(runnable);
		
		ci.cancel();
	}
	
	private void addWurstCape(GameProfile profile,
		HashMap<MinecraftProfileTexture.Type, MinecraftProfileTexture> map)
	{
		String name = profile.getName();
		String uuid = profile.getId().toString();
		
		try
		{
			if(capes == null)
				setupWurstCapes();
			
			if(capes.has(name))
			{
				String capeURL = capes.get(name).getAsString();
				map.put(Type.CAPE, new MinecraftProfileTexture(capeURL, null));
				
			}else if(capes.has(uuid))
			{
				String capeURL = capes.get(uuid).getAsString();
				map.put(Type.CAPE, new MinecraftProfileTexture(capeURL, null));
			}
			
		}catch(Exception e)
		{
			System.err.println("[Wurst] Failed to load cape for '" + name
				+ "' (" + uuid + ")");
			
			e.printStackTrace();
		}
	}
	
	private void setupWurstCapes()
	{
		JsonObject wurstCapes = new JsonObject();

		try
		{
			// TODO: download capes to file
			URL url = new URL("https://www.wurstclient.net/api/v1/capes.json");
			
			wurstCapes =
				JsonParser.parseReader(new InputStreamReader(url.openStream()))
					.getAsJsonObject();
			
		}catch(Exception e)
		{
			System.err
				.println("[Wurst] Failed to load capes from wurstclient.net!");
			
			e.printStackTrace();
		}
		
		JsonObject teaCapes = new JsonObject();
		
		try {
			URL url = new URL("https://sparklet.org/api/tea-capes");
			
			teaCapes =
				JsonParser.parseReader(new InputStreamReader(url.openStream()))
					.getAsJsonObject();
		} catch(Exception e) {
			System.err.println("[Wurst] Failed to load capes from sparklet.org!");
		}
		
		extendJsonObject(wurstCapes, teaCapes);
		capes = wurstCapes;
	}
	
	@Shadow
	public Identifier loadSkin(MinecraftProfileTexture profileTexture,
		Type type,
		@Nullable PlayerSkinProvider.SkinTextureAvailableCallback callback)
	{
		return null;
	}
	
	
	
	
	// reduced and inlined version of this post:
	// https://stackoverflow.com/a/34092374/
	private static JsonObject extendJsonObject(JsonObject leftObj, JsonObject rightObj) {
		for (Map.Entry<String, JsonElement> rightEntry : rightObj.entrySet()) {
			String rightKey = rightEntry.getKey();
			JsonElement rightVal = rightEntry.getValue();
			if (leftObj.has(rightKey)) {
				// conflict
				JsonElement leftVal = leftObj.get(rightKey);
				if (leftVal.isJsonArray() && rightVal.isJsonArray()) {
					JsonArray leftArr = leftVal.getAsJsonArray();
					JsonArray rightArr = rightVal.getAsJsonArray();
					// concat the arrays -- there cannot be a conflict
					// in an array, it's just a collection of stuff
					for (int i = 0; i < rightArr.size(); i++) {
						leftArr.add(rightArr.get(i));
					}
				} else if (leftVal.isJsonObject() && rightVal.isJsonObject()) {
					// recursive merging
					extendJsonObject(leftVal.getAsJsonObject(), rightVal.getAsJsonObject());
				} else {
					// not both arrays or objects, normal merge with conflict resolution
					leftObj.add(rightKey, rightVal);
				}
			} else {
				// no conflict, add to the object
				leftObj.add(rightKey, rightVal);
			}
		}

		return leftObj;
	}
}

