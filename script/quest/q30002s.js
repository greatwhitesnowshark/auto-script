/**
 *
 * @author Auto-Scripter
 * @quest [Root Abyss] The Mysterious Girl (30002)
 * @npc  (1064001)
 */
nQRStatus = self.GetQuestRequestState(30002); 

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
		self.Say("I want to get out of here.", true, 1064001);
		self.Say("I said I want to get out of here.", true, 1064001);
		self.Say("This place? This is Root Abyss. And you don't want to be here. Let's leave together. Follow me.", true, 1064001);
		nRet = self.AskYesNoUser("(She looks lost... maybe I should help her out?)", 1064001);
		if (nRet == 0) {
				self.OnInGameCurNodeEventEnd();
				self.OnUserSetStandaloneMode(true);
				self.SayUser("Hmm... there should be a way out somewhere...", true, 1064001);
				self.Wait();
				self.OnSetInGameDirectionMode(false, true, false, false);
				self.QuestRecordSetState(30002, QuestRecord.Complete);
		} else if (nRet == 1) {
			self.SayUser("All right, fine. I'll show you how to get out.", true, 1064001);
			self.QuestRecordSetState(30002, QuestRecord.Perform);
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
