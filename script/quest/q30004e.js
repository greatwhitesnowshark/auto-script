/**
 *
 * @author Auto-Scripter
 * @quest [Root Abyss] Root Ruckus 2 (30004)
 * @npc  (1064001)
 */
nQRStatus = self.GetQuestRequestState(30004); 

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
		self.QuestRecordSetState(30004, QuestRecord.Complete);
	}
	break; 

	case QuestRequest.LaterStep: {
	}
	break; 

}
