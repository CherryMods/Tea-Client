/*
 * Copyright (c) 2014-2023 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.commands;

import java.util.ArrayList;

import net.wurstclient.DontBlock;
import net.wurstclient.command.CmdException;
import net.wurstclient.command.CmdSyntaxError;
import net.wurstclient.command.Command;
import net.wurstclient.other_features.EmojisOtf;
import net.wurstclient.other_features.EmojisOtf.EmojiPair;
import net.wurstclient.util.ChatUtils;
import net.wurstclient.util.MathUtils;

@DontBlock
public final class EmojisCmd extends Command {
	private static final int EMOJIS_PER_PAGE = 40;

	public EmojisCmd() {
		super("emojis", "Shows a list of all emojis available with the Emojis feature.", ".emojis [<page>]");
	}

	@Override
	public void call(String[] args) throws CmdException {
		if (args.length > 1) throw new CmdSyntaxError();

		String arg = args.length > 0 ? args[0] : "1";

		if (MathUtils.isInteger(arg)) ChatUtils.message("All emojis:");
		listEmojis(Integer.parseInt(arg));
	}

	private void listEmojis(int page) throws CmdException {
		ArrayList<EmojiPair<String>> emojis = EmojisOtf.getEmojiTable();
		final int pages = Math.max(
			(int) Math.ceil(emojis.size() / (double) EMOJIS_PER_PAGE),
			1
		);

		if (page > pages || page < 1) {
			throw new CmdSyntaxError("Invalid page: " + page);
		}

		String total = "Total: " + emojis.size() + " emoji";
		total += emojis.size() != 1 ? "s" : "";
		ChatUtils.message(total);

		int start = (page - 1) * EMOJIS_PER_PAGE;
		int end = Math.min(page * EMOJIS_PER_PAGE, emojis.size());

		ChatUtils.message("Command list (page " + page + "/" + pages + ")");
		for (int i = start; i < end; i++) {
			EmojiPair<String> pair = emojis.get(i);
			ChatUtils.message("- " + pair.getF() + " >> " + pair.getS());
		}
	}
}
