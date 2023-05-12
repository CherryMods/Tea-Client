/*
 * Copyright (c) 2014-2023 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.commands;

import net.wurstclient.DontBlock;
import net.wurstclient.command.CmdException;
import net.wurstclient.command.Command;
import net.wurstclient.other_features.EmojisOtf;
import net.wurstclient.other_features.EmojisOtf.EmojiPair;
import net.wurstclient.util.ChatUtils;

@DontBlock
public final class EmojisCmd extends Command {
	public EmojisCmd() {
		super("emojis", "Shows a list of all emojis available with the Emojis feature.", ".emojis");
	}

	@Override
	public void call(String[] args) throws CmdException {
		ChatUtils.message("All emojis:");
		
		for (EmojiPair<String> pair : EmojisOtf.getEmojiTable()) {
			ChatUtils.message(pair.getF() + " >> " + pair.getS());
		}
	}
}
