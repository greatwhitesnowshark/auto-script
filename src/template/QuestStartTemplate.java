/*
 * To change this license opcode, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package template;

/**
 *
 * @author Sharky
 */
public class QuestStartTemplate extends QuestTemplate {
    
    public int dwNpcTemplateID, nQuestState;
    
    public QuestStartTemplate(String sScript, int dwTemplateID, int dwNpcTemplateID, String sQuestName) {
        super("script\\quest\\", sScript, dwTemplateID, sQuestName);
        this.dwNpcTemplateID = dwNpcTemplateID;
    }
}
