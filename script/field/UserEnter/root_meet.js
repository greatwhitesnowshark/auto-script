/**
 *
 * @author Auto-Scripter
 * @field Swamp - Secret Swamp (910700200)
 * @script-type UserEnter
 */
dwField = pField.dwField; 

switch(dwField) 
{
	case 910700200: { // Root Abyss - Colossal Root
		self.OnInGameCurNodeEventEnd();
		self.OnUserSetStandaloneMode(true);
		self.Wait();
		self.OnUserSetStandaloneMode(false);
		self.OnUserQuestResult(QuestResultType.Success, 30006, 1064001, 0, false);
	}
	break;
}
