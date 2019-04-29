/**
 *
 * @author Auto-Scripter
 * @quest [Root Abyss] The World Girl (30005)
 * @npc  (1064001)
 */
nQRStatus = self.GetQuestRequestState(30005); 

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
		nRet = self.AskYesNo("Would you like to skip the cutscenes?", 9010000);
		if (nRet == 0) {
			self.OnInGameCurNodeEventEnd();
			self.OnUserSetStandaloneMode(true);
			self.Say("No! Those bad people did this to me!", true, 1064001);
			self.Say("", true, 1064001);
			self.SayUser("A demon with an eyepatch tried to kidnap you? Do you realize how crazy that sounds?", true, 1064001);
			self.SayUser("Is that why you couldn't get through the gateway?", true, 1064001);
			self.QuestRecordSetState(30005, QuestRecord.Perform);
			self.OnUserSetStandaloneMode(false);
		} else if (nRet == 1) {
		}
	}
	break; 

	case QuestRequest.LaterStep: {
	}
	break; 

}
