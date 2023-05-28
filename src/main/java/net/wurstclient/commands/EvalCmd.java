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
import net.wurstclient.command.CmdSyntaxError;
import net.wurstclient.command.Command;

@DontBlock
public final class EvalCmd extends Command {
	public EvalCmd() {
		super("eval", "Evaluates JavaScript and prints the result.", ".eval <code>");
	}

	@Override
	public void call(String[] args) throws CmdException {
		if(args.length < 1)
			throw new CmdSyntaxError();
		
		String message = String.join(" ", args);
		
		if(message.startsWith("/"))
			MC.getNetworkHandler().sendChatCommand(message.substring(1));
		else
			MC.getNetworkHandler().sendChatMessage(message);
	}
}
