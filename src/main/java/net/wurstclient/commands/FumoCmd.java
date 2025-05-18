/*
 * Copyright (c) 2014-2025 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.commands;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.Identifier;
import net.wurstclient.Category;
import net.wurstclient.command.CmdException;
import net.wurstclient.command.CmdSyntaxError;
import net.wurstclient.command.Command;
import net.wurstclient.events.GUIRenderListener;
import net.wurstclient.util.RenderUtils;

public final class FumoCmd extends Command implements GUIRenderListener
{
	private final Identifier fumo = Identifier.of("wurst", "mira-fumo.png");
	
	private boolean enabled;
	
	public FumoCmd()
	{
		super("fumo", "Spawns a Mira Fumo on your hotbar.\n"
			+ "\"Purple and black, push it to stack!\" - Agent Cascade, 2037\n"
			+ "Picrew made by @deathccino.");
		setCategory(Category.FUN);
	}
	
	@Override
	public void call(String[] args) throws CmdException
	{
		if(args.length != 0)
			throw new CmdSyntaxError();
		
		enabled = !enabled;
		
		if(enabled)
		{
			EVENTS.add(GUIRenderListener.class, this);
			
		}else
		{
			EVENTS.remove(GUIRenderListener.class, this);
		}
	}
	
	@Override
	public String getPrimaryAction()
	{
		return ":3 :3 :3";
	}
	
	@Override
	public void doPrimaryAction()
	{
		WURST.getCmdProcessor().process("fumo");
	}
	
	@Override
	public void onRenderGUI(DrawContext context, float partialTicks)
	{
		if(WURST.getHax().rainbowUiHack.isEnabled())
			RenderUtils.setShaderColor(WURST.getGui().getAcColor(), 1);
		else
			RenderSystem.setShaderColor(1, 1, 1, 1);
		
		int x = context.getScaledWindowWidth() / 2 - 32 + 16;
		int y = context.getScaledWindowHeight() - 32 - 19;
		int w = 64;
		int h = 32;
		context.drawTexture(RenderLayer::getGuiTextured, fumo, x, y, 0, 0, w, h,
			w, h);
	}
}
