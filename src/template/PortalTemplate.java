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
public class PortalTemplate extends AbstractTemplate {
    
    public int dwField;
    public String sPortalName;
    
    public PortalTemplate(String sScript, int dwField, String sPortalName) {
        super("script\\portal\\", sScript, 0);
        this.dwField = dwField;
        this.sPortalName = sPortalName;
    }
}
