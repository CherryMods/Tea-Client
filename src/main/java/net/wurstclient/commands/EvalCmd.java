/*
 * Copyright (c) 2014-2023 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.commands;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Value;

import net.wurstclient.DontBlock;
import net.wurstclient.command.CmdException;
import net.wurstclient.command.CmdSyntaxError;
import net.wurstclient.command.Command;
import net.wurstclient.util.ChatUtils;

@DontBlock
public final class EvalCmd extends Command {
	public static final String EXECUTE_ERROR_MSG = "<failed>";
	
	public EvalCmd() {
		super("eval", "Evaluates JavaScript and prints the result.", ".eval <code>");
	}

	@Override
	public void call(String[] args) throws CmdException {
		if(args.length < 1)
			throw new CmdSyntaxError();
		
		String payload = String.join(" ", args);
		
		String res = eval(payload);
		ChatUtils.message(res);
	}
	
	private String eval(String payload) {
		String out;

		try (Context context = Context.create()) {
			Value result = context.eval("js", payload);
			out = result.asString();
	
			// Close the GraalVM context
	        context.close();
		} catch (Exception e) {
			out = EXECUTE_ERROR_MSG;
		}
		
		return out;
	}
}
