/**
 *
 * @author Auto-Scripter
 * @quest [Root Abyss] Guardians of the World Tree (30006)
 * @npc  (1064001)
 */
nQRStatus = self.GetQuestRequestState(30006); 

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
		nRet = self.AskYesNo("Would you like to skip the cutscenes?", 9010000);
		if (nRet == 0) {
			self.OnInGameCurNodeEventEnd();
			self.OnUserSetStandaloneMode(true);
			self.SayUser("We need to find those baddies if we want to get you out of here.", true, 1064001);
			self.SayUser("They had to have left some clues behind. What about those weird doors over there?", true, 1064001);
			self.SayUser("Then that sounds like a good place to start. Maybe I should-", true, 1064001);
			self.EffectAvatarOriented("Effect/Direction11.img/effect/Aura/0");
			self.EffectAvatarOriented("Effect/BasicEff.img/Teleport");
			self.Say("#b#h0##k!!!", true, 1064001);
			self.OnUserSetStandaloneMode(false);
			pTarget.OnTransferField(910700300, 0); //to: "Root Abyss - Abyssal Cave"
		} else if (nRet == 1) {
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
