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
public abstract class AbstractTemplate {
    
    public final String sScript, sDirPath;
    public final int dwTemplateID;
    public int nStrPaddingIndex;
    
    public AbstractTemplate(String sDirPath, String sScript, int dwTemplateID) {
        this.sDirPath = sDirPath;
        this.sScript = sScript;
        this.dwTemplateID = dwTemplateID;
        this.nStrPaddingIndex = 0;
    }
    
    public AbstractTemplate(String sDirPath, String sScript, int dwTemplateID, int nStrPaddingIndex) {
        this.sDirPath = sDirPath;
        this.sScript = sScript;
        this.dwTemplateID = dwTemplateID;
        this.nStrPaddingIndex = nStrPaddingIndex;
    }
    
    public boolean IsDeveloper() {
        return this instanceof DeveloperTemplate;
    }
    
    public boolean IsFieldTemplate() {
        return this instanceof FieldTemplate;
    }
    
    public boolean IsNpcTemplate() {
        return this instanceof NpcTemplate;
    }
    
    public boolean IsPortalTemplate() {
        return this instanceof PortalTemplate;
    }
    
    public boolean IsReactorTemplate() {
        return this instanceof ReactorTemplate;
    }
    
    public boolean IsQuestStartTemplate() {
        return this instanceof QuestStartTemplate;
    }
    
    public boolean IsQuestEndTemplate() {
        return this instanceof QuestEndTemplate;
    }
    
    public boolean IsQuestTemplate() {
        return this instanceof QuestStartTemplate || this instanceof QuestEndTemplate;
    }
    
    public boolean IsUserEnterTemplate() {
        return this instanceof FieldTemplate && ((FieldTemplate) this).sType.equals("UserEnter");
    }
    
    public boolean IsFirstUserEnterTemplate() {
        return this instanceof FieldTemplate && ((FieldTemplate) this).sType.equals("FirstUserEnter");
    }
    
    public boolean IsFieldScriptTemplate() {
        return this instanceof FieldTemplate && ((FieldTemplate) this).sType.equals("FieldScript");
    }
}
