/**
 *
 * @author Auto-Scripter
 * @quest [Root Abyss] World Tree Guardian (30008)
 * @npc  (1101002)
 */
nQRStatus = self.GetQuestRequestState(30008); 

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
		self.Say("The Maple Alliance will focus all resources toward rescuing the World Tree.", true, 1101002);
		self.Say("The World Tree's control over the powers of", true, 1101002);
		self.Say("I don't know who would seek to harm the World Tree, but I know that we must stop it. It must be protected at all costs.", true, 1101002);
		nRet = self.AskAccept("", 1101002);
		if (nRet == 0) {
		} else if (nRet == 1) {
			self.Say("", true, 1101002);
			self.Say("From now on you will be able to use the Dimensional Mirror or the Maple Guide to reach the Root Abyss.", true, 1101002);
			pTarget.OnTransferField(130000101, 5); //to: ""
		}
	}
	break; 

	case QuestRequest.CompleteScript: {
	}
	break; 

	case QuestRequest.LaterStep: {
	}
	break; 

}
