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
public class FieldTemplate extends AbstractTemplate {
    
    public int dwField;
    public String sType;
    
    public FieldTemplate(String sScript, int dwField, String sType) {
        super("script\\field\\", sScript, 0);
        this.dwField = dwField;
        this.sType = sType;
    }
    
}
