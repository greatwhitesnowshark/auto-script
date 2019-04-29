/**
 *
 * @author Auto-Scripter
 * @field Root Abyss - Colossal Root (910700300)
 * @script-type UserEnter
 */
dwField = pField.dwField; 

switch(dwField) 
{
	case 910700300: { // Root Abyss - Abyssal Cave
		nRet = self.AskYesNo("Would you like to skip the cutscenes?", 9010000);
		if (nRet == 0) {
			self.OnNpcSpecialAction(19916011, "summon", 0, false);
			self.OnSetInGameDirectionMode(true, true, false, false);
			self.Wait();
			self.SayUser("W-who's there?!", true, 1064017);
			self.SayUser("Are you the one who put a seal on the World Tree?", true, 1064017);
			self.SayUser("You keep saying #rhim#k. Are you talking about that demon with the eyepatch?", true, 1064017);
			self.SayUser("I'm not looking for a fight. The Demon Slayer is our ally. Why can't you just join us as well?", true, 1064017);
			self.Say("YOU use force on ME? Ha!", true, 1064017);
			self.EffectReserved("Effect/Direction11.img/rootabyssQuest/Scene1"
			self.OnSetInGameDirectionMode(false, true, false, false);
		} else if (nRet == 1) {
		}
	}
	break;
}
