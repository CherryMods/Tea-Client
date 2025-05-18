/*
 * Copyright (c) 2014-2025 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.hud;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.wurstclient.WurstClient;
import net.wurstclient.other_features.WurstLogoOtf;
import net.wurstclient.util.RenderUtils;

public final class WurstLogo
{
	private static final WurstClient WURST = WurstClient.INSTANCE;
	private static final Identifier LOGO_TEXTURE =
		Identifier.of("wurst", "wurst_128.png");
	
	public void render(DrawContext context)
	{
		MatrixStack matrixStack = context.getMatrices();
		WurstLogoOtf otf = WURST.getOtfs().wurstLogoOtf;
		WurstLogoOtf.Format fmt = otf.getFormat();
		
		if(!otf.isVisible())
			return;
		
		String version = getVersionString(fmt);
		TextRenderer tr = WurstClient.MC.textRenderer;
		
		// draw version background if enabled
		if(otf.isBgVisible())
		{
			// background
			int bgColor;
			if(WURST.getHax().rainbowUiHack.isEnabled())
				bgColor =
					RenderUtils.toIntColor(WURST.getGui().getAcColor(), 0.5F);
			else
				bgColor = otf.getBackgroundColor();
			context.fill(0, 6, tr.getWidth(version) + 76, 17, bgColor);
		}
		
		// version string
		context.drawText(tr, version, 74, 8, otf.getTextColor(), false);
		
		// Wurst logo
		context.drawTexture(RenderLayer::getGuiTextured, LOGO_TEXTURE, 0, 3, 0,
			0, 72, 18, 72, 18);
	}
	
	private String getVersionString(WurstLogoOtf.Format fmt)
	{
		if(fmt == WurstLogoOtf.Format.NoVersion)
			return "";
		String res = "";
		
		boolean showVer = (fmt == WurstLogoOtf.Format.FULL
			|| fmt == WurstLogoOtf.Format.WurstOnly);
		
		boolean showMCVer = (fmt == WurstLogoOtf.Format.FULL
			|| fmt == WurstLogoOtf.Format.MCOnly);
		
		if(showVer)
			res += "v" + WurstClient.VERSION + (showMCVer ? " " : "");
		if(showMCVer)
			res += "MC" + WurstClient.MC_VERSION;
		
		if(WURST.getUpdater().isOutdated())
			res += " (outdated)";
		
		return res;
	}
}
