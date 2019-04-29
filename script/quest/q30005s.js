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
		self.SayUser("How in the world did you end up here, anyway? It's not exactly the greatest place for a little girl.", true, 1064001);
		self.SayUser("Whaaaat, you CREATED this place? ", true, 1064001);
		self.SayUser("You pretty much sound like a crazy person. Who ARE you?", true, 1064001);
		self.SayUser("World Tree? YOU are the World Tree?!", true, 1064001);
		self.SayUser("I still can't believe you're a tree.", true, 1064001);
	}
	break; 

	case QuestRequest.CompleteScript: {
	}
	break; 

	case QuestRequest.LaterStep: {
	}
	break; 

}
