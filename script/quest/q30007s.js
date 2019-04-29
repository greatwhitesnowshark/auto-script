/**
 *
 * @author Auto-Scripter
 * @quest [Root Abyss] World Tree in Danger (30007)
 * @npc  (1064001)
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
		self.Say("What happened? You disappeared all of sudden... I was worried.", true, 1064001);
		self.Say("Who? The same people that trapped me here?", true, 1064001);
		self.Say("Are you scared? I thought you were going to help me!", true, 1064001);
		self.Say("What should we do? I can't help with all this dark energy sapping my powers.", true, 1064001);
		self.SayUser("", true, 1064001);
		self.SayUser("I'll be back before you know it. Just stay strong.", true, 1064001);
		pTarget.OnTransferField(130000000, 0); //to: "Empress' Road - Ereve"
	}
	break; 

	case QuestRequest.CompleteScript: {
	}
	break; 

	case QuestRequest.LaterStep: {
	}
	break; 

}
