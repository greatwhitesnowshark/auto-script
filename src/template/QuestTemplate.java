package template;

public abstract class QuestTemplate extends AbstractTemplate {

    public String sQuestName;

    public QuestTemplate(String sDirPath, String sScript, int dwTemplateID, String sQuestName) {
        super(sDirPath, sScript, dwTemplateID);
        this.sQuestName = sQuestName;
    }
}
