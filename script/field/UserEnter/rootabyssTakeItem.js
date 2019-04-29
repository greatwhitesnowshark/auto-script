/**
 *
 * @author Auto-Scripter
 * @field Root Abyss - South Garden (105200300)
 * @script-type UserEnter
 */
dwField = pField.dwField; 

switch(dwField) 
{
	case 105200300: { // Root Abyss - South Garden
		nRet = self.AskYesNoUser("Are you ready to enter? #b(If you accept, all party members in the current map will travel to the boss map automatically.)#k", 0);
		if (nRet == 0) {
		} else if (nRet == 1) {
			pTarget.OnTransferField(105200310, 0); //to: "Root Abyss - Queen's Castle"
		}
	}
	break;
}
