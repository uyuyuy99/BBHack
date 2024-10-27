package me.uyuyuy99.bbhack.SE;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class BBScript {
	
	private static Map<String, String> regexReplacements = new HashMap<String, String>();
	private static String[] regexes = new String[] {
/* 00 */	"(?:end|stop|quit|exit)",
/* 01 */	"{jump}",
/* 02 */	"{jump} (?:from |at |in |)(?:object |obj ){name}",
/* 03 */	"(?:return|go back|back)",
/* 04 */	"(?:delay |wait |pause )(?:for |)([0-9x\\.]+) ?(?:seconds|secs|sec|s|)",
/* 05 */	"object disappears (?:when |if ){flagset}",
/* 06 */	"object appears (?:when |if ){flagset}",
/* 07 */	"unknown 1",
/* 08 */	"(?:show |display |)text {num}",
/* 09 */	"(?:ask |show |display |)(?:yes\\/no|question|yes or no|yes and no){,}{jump} unless " +
					"(?:(?:player |)(?:chooses|chose|selects|selected) yes|(?:yes (?:is |was |)(?:selected|chosen)))",
/* 0A */	"{jump} unless (?:player |)(?:is |)(?:talking|speaking) ?(?:to object|to npc|to sprite|)",
/* 0B */	"{jump} unless (?:player |)(?:is |)checking ?(?:object|npc|sprite|)",
/* 0C */	"{jump} unless (?:player |)(?:is |)(?:using (?:psi|spell) |casting (?:psi |spell |)){const}(?: on object| on npc| on sprite|)",
/* 0D */	"{jump} unless (?:player |)(?:is |)using {item}(?: on object| on npc| on sprite|)",
/* 0E */	"unknown 2",
/* 0F */	"(?:reset|reboot|reload)(?: nes| system| console| game|)",
/* 10 */	"(?:(?:set |)(?:flag |){name} ?\\= ?(?:true|set|yes)|set (?:flag |){name}(?: (?:to |equal to |)(?:true|set|yes)|))",
/* 11 */	"(?:(?:set |)(?:flag |){name} ?\\= ?(?:true|set|yes)|(?:(?:unset|clear) (?:flag |){name}|" +
					"set (?:flag |){name} (?:to |equal to |)(?:false|unset|no|cleared)))",
/* 12 */	"{jump} unless {flagset}",
/* 13 */	"(?:{counter} ?\\-\\-|(?:dec(?:rease|)|sub(?:tract|) from) {counter})",
/* 14 */	"(?:{counter} ?\\+\\+|(?:inc(?:rease|)|add to) {counter})",
/* 15 */	"(?:unset|reset|clear) {counter}",
/* 16 */	"{jump} if {counter}(?: ?\\< ?|(?: is|)(?: less| smaller) th(?:a|e)n ){#}",
/* 17 */	"(?:change|set) map var(?:iable|) {#} to {#}",
/* 18 */	"(?:show char(?:acter|) (?:select(?:ion|) |choosing |)(?:menu|screen)|select char(?:acter|)){,}{jump} if {cancelled}",
/* 19 */	"{load} {char}",
/* 1A */	"{jump} unless {char} {isloaded}",
/* 1B */	"{jump} unless (?:(?:money |cash )(?:was |)added to {bank}|(?:money in |){bank} (?:bal(?:ance|) |)(?:changed|is diff(?:erent|)))" +
					"(?: (?:since|after|since after) (last call|previous call|calling)|)",
/* 1C */	"(?:open|display|show) (?:num(?:ber|)|\\#) (?:input|choosing|choose|selecting|select)(?: menu| screen|){,}{jump} if {cancelled}",
/* 1D */	"{load} (?:the |){num}",
/* 1E */	"{jump} if {theloaded} (?:num(?:ber|)|\\#)(?: ?\\< ?|(?:is |)(?: less| smaller) th(?:a|e)n ){#}",
/* 1F */	"(?:display|show|open) (?:available |usable |)(?:money|cash)(?: window|)",
/* 20 */	"{load} (?:an |a |)item(?: from {playerinv}|){,}{jump} if {cancelled}",
/* 21 */	"{load} (?:an |a |)item from {closet}{,}{jump} if {cancelled}",
/* 22 */	"(?:(?:select|choose) (?:item |)(?:from|between|b\\/t)(?: items|)|" +
					"(?:show |display |open |)(?:item |)(?:menu|shop)(?: (?:with|w\\/)(?: items|)|)) " +
					"{const}{,}{const}{,}{const}{,}{const}{,}{jump} if {cancelled}",
/* 23 */	"{jump} unless (?:{theloaded} char(?:acter|) (?:has|is carrying|carries) {item}(?: in {his} inv(?:entory|)|)|" +
					"{item} (?:is |)in(?:side|) (?:(?:of |){theloaded} char(?:acter|){'}s inv(?:entory|)|" +
					"(?:the |)inv(?:entory|) of {theloaded} char(?:acter|)))",
/* 24 */	"{jump} unless {item} (?:(?:is |)(?:being |)stored(?: in (?:the |)closet|)|(?:is |)in (?:the |)closet)",
/* 25 */	"{load} {item}",
/* 26 */	"{jump} unless {theloaded} item is {item}",
/* 27 */	"{jump} unless (?:{item} (?:is |)in(?:side|) (?:someone{'}s inv(?:entory|)|(?:the |)inv(?:entory|) of (?:a|any) char(?:aracter|))|" +
					"(?:someone|anyone|(?:a|some) char(?:acter|)) (?:in the (?:party|group) |)has {item}(?: in(?:side|) {his} inv(?:entory|)|))",
/* 28 */	"give {theloaded} (?:num(?:ber|)|\\#) (?:in|as) (?:dollars|\\$|currency|money|cash)(?: to (?:the |)player|){,}{jump} if " +
					"(?:(?:the |)player |)(?:can{'}t|can ?not|won{'}t|will not) (?:hold|carry) (?:any ?|)more (?:money|cash))",
/* 29 */	"(?:take|sub(?:tract|)|remove) {theloaded} (?:num(?:ber|)|\\#) (?:in|as) " +
					"(?:dollars|\\$|currency|money|cash)(?: from (?:the |)player|){,}{jump} if " +
					"(?:(?:the |)player |)(?:(?:doesn{'}t|does not) have (?:any ?|)more|has no more |(?:ran|runs|is) out of )(?:money|cash))",
/* 2A */	"add {theloaded} (?:num(?:ber|)|\\#) to {bank}(?: bal(?:ance|)|)){,}{jump} if " +
					"(?:it|{bank})(?:(?:{'}| i)s full| (?:can{'}t|can ?not|won{'}t|will not) (?:hold|carry) (?:any ?|)more (?:money|cash))",
/* 2B */	"(?:take|sub(?:tract|)|remove) {theloaded} (?:num(?:ber|)|\\#) from {bank}(?: bal(?:ance|)|)){,}{jump} if " +
					"(?:it|{bank}) (?:(?:doesn{'}t|does not) have (?:any ?|)more|has no more |(?:ran|runs|is) out of )(?:money|cash))",
/* 2C */	"{jump} if {theloaded} item (?:is (?:not |non|un)sellable|(?:can{'}|can ?not) (?:be sold|sell))",
/* 2D */	"(?:give|add|put) {theloaded} item (?:in|)to {playerinv}{,}{jump} if (?:it(?:{'}| i)s |{playerinv}(?: is|) |)full",
/* 2E */	"(?:take|remove|delete) {theloaded} item from {playerinv}{,}{jump} if " +
					"(?:it(?:{'}| i)s not (?:there|present)|(?:the |)player (?:doesn{'}t|does not) (?:have|own) it)",
/* 2F */	"(?:give|add|put) {theloaded} item (?:in|)to {closet}{,}{jump} if " +
					"(?:it(?:{'}| i)s |{closet}(?: is|) |)full",
/* 30 */	"(?:take|remove|delete) {theloaded} item from {closet}{,}{jump} if " +
					"(?:it(?:{'}| i)s not (?:there|present)|{closet} (?:doesn{'}t|does not) (?:have|contain) it)",
/* 31 */	"{load} (?:{loadedchar's} {#th} (?:item|slot)|(?:item|slot) {#} (?:from|in) (?:{loadedchar's} inv(?:entory|)|{theloaded} char(?:acter|)))" +
					"{,}{jump} if (?:empty(?: slot|)|no item(?: is there|)|slot (?:is |was |)empty)",
/* 32 */	"(?:mult(?:iply|)|times) {theloaded} (?:num(?:ber|)|\\#) (?:by|with) {%}",
/* 33 */	"{jump} if {char} (?:is not|isn{'}t) (?:present|in (?:the |)party)",
/* 34 */	"",
/* 35 */	"{jump} unless (?:player(?:(?:{'}| i)s|) |)(?:touching|colliding|hitting) (?:w(?:\\/|ith) |)(?:the |)(?:object|sprite)",
/* 36 */	"",
/* 37 */	"(?:open|display|show) (?:2|two|)(?:\\-| |)option (?:menu|screen){,}{jump} if (?:second|2nd) " +
					"(?:option |)(?:selected|chosen){,}{jump} if {cancelled}",
/* 38 */	"{jump} if {playerinv} (?:is empty|has (?:no|0|zero) items)",
/* 39 */	"{jump} if {closet} (?:is empty|has (?:no|0|zero) items)",
/* 3A */	"{load} {#th} {char}{,}{jump} if {he's} not (?:there|present|in (?:the |your |)party)",
/* 3B */	"run(?: away|)",
/* 3C */	"",
/* 3D */	"",
/* 3E */	"",
/* 3F */	"",
/* 40 */	"",
/* 41 */	"",
/* 42 */	"",
/* 43 */	"",
/* 44 */	"",
/* 45 */	"",
/* 46 */	"",
/* 47 */	"",
/* 48 */	"",
/* 49 */	"",
/* 4A */	"",
/* 4B */	"",
/* 4C */	"",
/* 4D */	"",
/* 4E */	"",
/* 4F */	"",
/* 50 */	"",
/* 51 */	"",
/* 52 */	"",
/* 53 */	"",
/* 54 */	"",
/* 55 */	"",
/* 56 */	"",
/* 57 */	"",
/* 58 */	"",
/* 59 */	"",
/* 5A */	"",
/* 5B */	"",
/* 5C */	"",
/* 5D */	"",
/* 5E */	"",
/* 5F */	"",
/* 60 */	"",
/* 61 */	"",
/* 62 */	"",
/* 63 */	"",
/* 64 */	"",
/* 65 */	"",
/* 66 */	"",
/* 67 */	"",
/* 68 */	"",
/* 69 */	"",
/* 6A */	"",
	};
	
	static {
		regexReplacements.put("\\{\\,\\}", "(?: ?\\, ?| )(?:and | ?)");
		regexReplacements.put("\\{name\\}", "([a-zA-Z0-9_]+)");
		regexReplacements.put("\\{\\#\\}", "(?:num(?:ber|) |\\# ?)([0-9x]+)");
		regexReplacements.put("\\{num\\}", "([0-9x]+)");
		regexReplacements.put("\\{const\\}", "(.+)");
		regexReplacements.put("\\{jump\\}", "(?:jump|go) ?to (?:label |)([a-zA-Z0-9_]+)");
		regexReplacements.put("\\{flagset\\}", "(?:flag |)([a-zA-Z0-9_]+)(?: is | ?\\= ?| )(?:true|set|yes)");
		regexReplacements.put("\\{counter\\}", "(?:count(?:er|) |)([a-zA-Z0-9_]+)");
		regexReplacements.put("\\{char\\}", "(?:char(?:acter|) |)(.+)");
		regexReplacements.put("\\{item\\}", "(?:item |the |a |an |)(.+)");
		regexReplacements.put("\\{cancelled\\}", "(?:(?:the |)b(?:(?:\\-| )button | )(?:is |was |)(?:pressed|used)|"
				+ "(?:player (?:pressed|used) (?:the |)b(?:(?:\\-| )button|)|(?:player |)cancel(?:s|ed|led)))");
		regexReplacements.put("\\{load\\}", "(?:load|select|choose)");
		regexReplacements.put("\\{isloaded\\}", "(?:is |was |)(?:loaded|selected|chosen)");
		regexReplacements.put("\\{theloaded\\}", "(?:the |)(?:loaded|selected|chosen)");
		regexReplacements.put("\\{\\'\\}", "(?:\\'|)");
		regexReplacements.put("\\{bank\\}", "(?:the |)(?:player(?:\\'|)s |)(?:(?:bank |atm |)(?:acct|account)|bank|atm)");
		regexReplacements.put("\\{his\\}", "(?:it{'}s|his|her|their)");
		regexReplacements.put("\\{playerinv\\}", "(?:the |)(?:player(?:\\'|)s |)inv(?:entory|)");
		regexReplacements.put("\\{\\#th\\}", "(?:num(?:ber|) |\\# ?)([0-9x]+)(?:st|nd|rd|th|)");
		regexReplacements.put("\\{loadedchar\\'s\\}", "(?:(?:the |)(?:loaded|selected|chosen) |)char(?:acter|)(?:\\'|)s");
		regexReplacements.put("\\{\\%\\}", "([0-9x]+) ?(?:per(?:cent|)|\\%)");
		regexReplacements.put("\\{closet\\}", "(?:the |)(?:closet|storage)");
		regexReplacements.put("\\{he\\'s\\}", "(?:(?:he|she|it)(?:{'}| i)s|they(?:{'}| a)re)");
		
		for (int i=0; i<regexes.length; i++) {
			for (Entry<String, String> entry : regexReplacements.entrySet()) {
				regexes[i] = regexes[i].replaceAll(entry.getKey(), entry.getValue());
			}
		}
	}
	
	public static String prepareString(String str) {
		str = str.replaceAll("([^ \t])\\,", "$1 ,");
		return str;
	}
	
	public static void test() {
		System.out.println(regexes[0x22]);
	}
	
}
