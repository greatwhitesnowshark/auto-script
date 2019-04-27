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
public class PortalTemplate extends AbstractTemplate {
    
    public int dwField;
    public String sPortalName;
    
    public PortalTemplate(String sScript, int dwField, String sPortalName) {
        super("script\\portal\\", sScript, 0);
        this.dwField = dwField;
        this.sPortalName = sPortalName;
    }
    
    public PortalTemplate(String sScript, int dwField, String sPortalName, int nStrPaddingIndex) {
        super("script\\portal\\", sScript, 0, nStrPaddingIndex);
        this.dwField = dwField;
        this.sPortalName = sPortalName;
    }
}
