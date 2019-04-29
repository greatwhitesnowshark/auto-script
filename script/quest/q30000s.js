/**
 *
 * @author Auto-Scripter
 * @quest [Root Abyss] An Urgent Summons (30000)
 * @npc  (1101002)
 */
nQRStatus = self.GetQuestRequestState(30000); 

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
		nRet = self.AskAccept("#b#h0##k! Your presence is needed in Ereve right away. We haven't a second to lose.", 1101002);
		if (nRet == 0) {
		} else if (nRet == 1) {
			self.Say("I will transport you here.", true, 1101002);
			pTarget.OnTransferField(913080000, 0); //to: "Empress Road - Ereve"
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
