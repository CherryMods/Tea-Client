/*
 * Copyright (c) 2014-2025 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package net.wurstclient.other_features;

import java.util.ArrayList;

import net.wurstclient.DontBlock;
import net.wurstclient.SearchTags;
import net.wurstclient.events.ChatOutputListener;
import net.wurstclient.other_feature.OtherFeature;
import net.wurstclient.settings.CheckboxSetting;

@DontBlock
@SearchTags({"emoji", "emote", "unicode", "discord", "twitch"})
public final class EmojisOtf extends OtherFeature implements ChatOutputListener
{
	private final CheckboxSetting active =
		new CheckboxSetting("Active (replace sent emoji codes)", true);
	
	public EmojisOtf()
	{
		super("Emojis", "Turns stuff like :emoji: into the actual emoji.\n"
			+ "Check `.emojis` for a list of all emojis.");
		
		addSetting(active);
		
		EVENTS.add(ChatOutputListener.class, this);
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
	
	@Override
	public void onSentMessage(ChatOutputEvent event)
	{
		if(!isEnabled())
			return;
		
		event.setMessage(replaceEmotes(event.getMessage()));
	}
	
	private String replaceEmotes(String input)
	{
		String res = input;
		for(EmojiPair<String> pair : EMOJI_TABLE)
		{
			res = res.replaceAll(pair.getF(), pair.getS());
		}
		
		return res;
	}
	
	public static class EmojiPair<T>
	{
		private final T first;
		private final T second;
		
		public EmojiPair(T f, T s)
		{
			first = f;
			second = s;
		}
		
		public T getF()
		{
			return first;
		}
		
		public T getS()
		{
			return second;
		}
		
		public static <T> ArrayList<EmojiPair<T>> intoAL(T[] pairs)
		{
			ArrayList<EmojiPair<T>> res = new ArrayList<EmojiPair<T>>();
			
			for(int i = 1; i < pairs.length; i += 2)
			{
				res.add(new EmojiPair<T>(pairs[i - 1], pairs[i]));
			}
			
			return res;
		}
	}
	
	public static ArrayList<EmojiPair<String>> getEmojiTable()
	{
		return EMOJI_TABLE;
	}
	
	private static final ArrayList<EmojiPair<String>> EMOJI_TABLE =
		EmojiPair.intoAL(new String[]{":tea:", "☕", ":fight:", "⚔", ":sword:",
			"🗡", ":pickaxe:", "⛏", ":sus:", "ඞ", ":rocket:", "🚀", ":skull:",
			"☠", ":warn:", "⚠", ":fishing:", "🎣", ":smile:", "☺", ":frown:",
			"☹", ":heart:", "❤", ":heartdot:", "❣", ":heartbullet:", "❥",
			":m_4:", "♩", ":m_8:", "♪", ":m_8x2:", "♫", ":m_16:", "♬",
			":m_natural:", "♮", ":m_sharp:", "♯", ":m_flat:", "♭", ":X:", "✖",
			":!!:", "‼", ":checkbox:", "☑", ":check:", "✔", ":play:", "▶",
			":reverse:", "◀", ":wave:", "〰", ":peace:", "✌", ":point_u:", "☝",
			":write:", "✍", ":heat:", "♨", ":plane:", "✈", ":hourglass:", "⌛",
			":clock:", "⌚", ":sun:", "☀", ":cloud:", "☁", ":umbrella:", "☂",
			":snowflake:", "❄", ":snowman:", "☃", ":comet:", "☄", ":phone:",
			"☎", ":kb:", "⌨", ":mail:", "✉", ":pencil:", "✏", ":pen:", "✒",
			":shears:", "✂", ":rd:", "☢", ":bio:", "☣", ":U:", "⬆", ":D:", "⬇",
			":R:", "➡", ":L:", "⬅", ":UR:", "↗", ":DR:", "↘", ":DL:", "↙",
			":UL:", "↖", ":UD:", "↕", ":LR:", "↔", ":RcL:", "↩", ":LcR:", "↪",
			":female:", "♀", ":male:", "♂", ":glint:", "✳", ":star:", "✴",
			":compass:", "❇", ":(c):", "©", ":(C):", "©", ":(R):", "®", ":tm:",
			"™", ":(M):", "Ⓜ", ":congrats:", "㊗", ":secret:", "㊙", ":box_d:",
			"▪", ":box_de:", "▫", ":desc:", "☋", ":conj:", "☌", ":saltire:",
			"☓", ":point_bl", "☚", ":point_br:", "☛", ":communism:", "☭",
			":pencil_UR:", "✐", ":thunderstorm:", "☈", ":box_x:", "☒", ":star:",
			"★", ":pencil_DR:", "✎", ":caution:", "☡", ":sun_rays:", "☼",
			":uranus:", "♅", ":box:", "☐", ":point_d:", "☟", ":floral:", "❦",
			":asc:", "☊", ":opp:", "☍", ":adi:", "☬", ":farsi:", "☫",
			":crescent_l:", "☾", ":crescent_r:", "☽", ":medical:", "☤",
			":beet:", "❧", ":saturn:", "♄", ":earth:", "♁", ":ankh:", "☥",
			":smile_f:", "☻", ":neptune:", "♆", ":jupiter:", "♃", ":lightning:",
			"☇", ":point_r:", "☞", ":phone_e:", "☏", ":chi:", "☧",
			":circledot:", "☉", ":pluto:", "♇", ":point_l:", "☜", ":t_earth:",
			"☷", ":t_water:", "☵", ":t_mountain:", "☶", ":t_wind:", "☴",
			":t_heaven:", "☰", ":t_lake:", "☱", ":t_fire:", "☲", ":t_thunder:",
			"☳", ":r_david:", "✡", ":r_dharma:", "☸", ":r_yinyang:", "☯",
			":r_cross:", "✝", ":r_crosso:", "☦", ":r_crescent:", "☪",
			":r_peace:", "☮", ":r_jerusalem:", "☩", ":r_crossl:", "☨",
			":rook_b:", "♜", ":rook_w:", "♖", ":bishop_b:", "♝", ":bishop_w:",
			"♗", ":knight_b:", "♞", ":knight_w:", "♘", ":pawn_b:", "♟",
			":pawn_w:", "♙", ":king_b:", "♚", ":king_w:", "♔", ":queen_b:", "♛",
			":queen_w:", "♕", ":c_spade:", "♠", ":c_heart:", "♥", ":c_diamond:",
			"♦", ":c_club:", "♣", ":c_club_e:", "♧", ":c_spade_e:", "♤",
			":c_diamond_e:", "♢", ":c_heart_e:", "♡", ":a_aries:", "♈",
			":a_taurus:", "♉", ":a_gemini:", "♊", ":a_cancer:", "♋", ":a_leo:",
			"♌", ":a_virgo:", "♍", ":a_libra:", "♎", ":a_scorpio:", "♏",
			":a_sagittarius:", "♐", ":a_capricorn:", "♑", ":a_aquarius:", "♒",
			":a_pisces:", "♓", ":die1:", "⚀", ":die2:", "⚁", ":die3:", "⚂",
			":die4:", "⚃", ":die5:", "⚄", ":die6:", "⚅",});
}
