/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package template;

/**
 *
 * @author Five
 */
public class NpcTemplate extends AbstractTemplate {
    
    
    public NpcTemplate(String sScript, int dwTemplateID) {
        super("script\\npc\\", sScript, dwTemplateID);
    }
    
    public NpcTemplate(String sScript, int dwTemplateID, int nStrPaddingIndex) {
        super("script\\npc\\", sScript, dwTemplateID, nStrPaddingIndex);
    }
}
