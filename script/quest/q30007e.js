/**
 *
 * @author Auto-Scripter
 * @quest [Root Abyss] World Tree in Danger (30007)
 * @npc  (1101002)
 */
nQRStatus = self.GetQuestRequestState(30007); 

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
		self.Say("You're alive! I find that slightly amusing. Did you investigate the area we spoke of?", true, 1101002);
		self.SayUser("(You tell Neinheart what happened in Root Abyss.)", true, 1101002);
		self.SayUser("The World Tree is stuck in Root Abyss, and she's not going to last long with all the dark energy.", true, 1101002);
		self.QuestRecordSetState(30007, QuestRecord.Perform);
	}
	break; 

	case QuestRequest.LaterStep: {
	}
	break; 

}
