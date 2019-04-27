/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package template;

/**
 *
 * @author Sharky
 */
public class QuestEndTemplate extends AbstractTemplate {
    
    public int dwNpcTemplateID, nQuestState;
    public String sQuestName;
    
    public QuestEndTemplate(String sDirPath, String sScript, int dwTemplateID, int dwNpcTemplateID, String sQuestName) {
        super("script\\quest\\", sScript, dwTemplateID);
        this.dwNpcTemplateID = dwNpcTemplateID;
        this.sQuestName = sQuestName;
    }
    
    public QuestEndTemplate(String sDirPath, String sScript, int dwTemplateID, int dwNpcTemplateID, String sQuestName, int nStrPaddingIndex) {
        super("script\\quest\\", sScript, dwTemplateID);
        this.dwNpcTemplateID = dwNpcTemplateID;
        this.sQuestName = sQuestName;
    }
}
