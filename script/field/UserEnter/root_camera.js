/**
 *
 * @author Auto-Scripter
 * @field Empress Road - Ereve (105010000)
 * @script-type UserEnter
 */
dwField = pField.dwField; 

switch(dwField) 
{
	case 105010000: { // Swamp - Silent Swamp
		self.OnUserQuestResult(QuestResultType.Success, 39306, 0, 0, false);
		pTarget.OnTransferField(105010200, 0); //to: "Swamp - Secret Swamp"
	}
	break;
}
