/**
 *
 * @author Auto-Scripter
 * @field Root Abyss - Colossal Root (910700000)
 * @script-type UserEnter
 */
dwField = pField.dwField; 

switch(dwField) 
{
	case 910700000: { // Sleepywood - Sleepywood
		nRet = self.AskYesNo("Would you like to skip the cutscenes?", 9010000);
		if (nRet == 0) {
			self.OnInGameCurNodeEventEnd();
			self.OnUserSetStandaloneMode(true);
			self.SayUser("Welp, this thing is ancient, but seems to be working. Guess I should head back.", true, 1064001);
			self.OnUserSetStandaloneMode(false);
			pTarget.OnTransferField(910700200, 0); //to: "Root Abyss - Colossal Root"
		} else if (nRet == 1) {
		}
	}
	break;
}
