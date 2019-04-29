/**
 *
 * @author Auto-Scripter
 * @quest [Root Abyss] Root Ruckus 1 (30003)
 * @npc  (1064001)
 */
nQRStatus = self.GetQuestRequestState(30003); 

switch(nQRStatus) 
{
	case QuestRequest.LostItem: {
	}
	break; 

	case QuestRequest.AcceptQuest: {
	}
	break; 

	case QuestRequest.CompleteQuest: {
	}
	break; 

	case QuestRequest.ResignQuest: {
	}
	break; 

	case QuestRequest.OpeningScript: {
		self.Say("Did you find a way out?", true, 1064001);
		self.Say("I've already tried that about a hundred times, but I can't get out.", true, 1064001);
		self.QuestRecordSetState(30003, QuestRecord.Perform);
		pTarget.OnTransferField(105010200, 0); //to: "Swamp - Secret Swamp"
	}
	break; 

	case QuestRequest.CompleteScript: {
	}
	break; 

	case QuestRequest.LaterStep: {
	}
	break; 

}
