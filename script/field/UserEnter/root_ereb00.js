/**
 *
 * @author Auto-Scripter
 * @field El Nath - El Nath (913080000)
 * @script-type UserEnter
 */
dwField = pField.dwField; 

switch(dwField) 
{
	case 913080000: { // Empress Road - Ereve
		nRet = self.AskYesNo("Would you like to skip the cutscenes?", 9010000);
		if (nRet == 0) {
			self.OnInGameCurNodeEventEnd();
			self.OnUserSetStandaloneMode(true);
			self.Wait();
			self.SayUser("What's going on? I was in the middle of very important loot-related business.", true, 1064000);
			self.SayUser("Appeared?", true, 1064000);
			self.Say("", true, 1064000);
			self.Say("Go look around. The loss of one explorer would be far more acceptable than all of the Cygnus Knights. ", true, 1064000);
			self.OnUserSetStandaloneMode(false);
		} else if (nRet == 1) {
		}
	}
	break;
}
