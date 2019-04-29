/**
 *
 * @author Auto-Scripter
 * @field Swamp - Silent Swamp (105010200)
 * @script-type UserEnter
 */
dwField = pField.dwField; 

switch(dwField) 
{
	case 105010200: { // Swamp - Secret Swamp
		nRet = self.AskYesNo("Would you like to skip the cutscenes?", 9010000);
		if (nRet == 0) {
			self.OnInGameCurNodeEventEnd();
			self.OnUserSetStandaloneMode(true);
			self.Wait();
			self.OnSetInGameDirectionMode(false, true, false, false);
			pTarget.OnTransferField(910700200, 0); //to: "Root Abyss - Colossal Root"
		} else if (nRet == 1) {
		}
	}
	break;
}
