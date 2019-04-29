/**
 *
 * @author Auto-Scripter
 * @quest [Root Abyss] Root Ruckus 1 (30003)
 * @npc  (0)
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
	}
	break; 

	case QuestRequest.CompleteScript: {
		self.Say("Did the portal work for you?", true, 1064001);
		self.Say("Maybe I'm just stuck here forever...", true, 1064001);
		self.QuestRecordSetState(30003, QuestRecord.Complete);
		self.OnUserQuestResult(QuestResultType.Success, 30004, 1064001, 0, true);
	}
	break; 

	case QuestRequest.LaterStep: {
	}
	break; 

}
