/*
 * Copyright (c) 2014-2023 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.other_features;

import net.wurstclient.DontBlock;
import net.wurstclient.SearchTags;
import net.wurstclient.other_feature.OtherFeature;
import net.wurstclient.settings.CheckboxSetting;

@DontBlock
@SearchTags({"update", "auto update", "checker", "nag"})
public final class NoUpdateNagOtf extends OtherFeature
{
	private final CheckboxSetting active = new CheckboxSetting("Active", true);
	
	public NoUpdateNagOtf()
	{
		super("NoUpdateNag", "Disables the Wurst update nag message.");
		
		addSetting(active);
	}
	
	@Override
	public boolean isEnabled()
	{
		return active.isChecked();
	}
	
	@Override
	public String getPrimaryAction()
	{
		return isEnabled() ? "Disable" : "Enable";
	}
	
	@Override
	public void doPrimaryAction()
	{
		active.setChecked(!active.isChecked());
	}
}
