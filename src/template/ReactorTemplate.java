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
public class ReactorTemplate extends AbstractTemplate {
    
    public String sReactorName;
    
    public ReactorTemplate(String sScript, int dwTemplateID) {
        super("script\\reactor\\", sScript, dwTemplateID);
    }
    
    public ReactorTemplate(String sScript, int dwTemplateID, int nStrPaddingIndex) {
        super("script\\reactor\\", sScript, dwTemplateID, nStrPaddingIndex);
    }
}
