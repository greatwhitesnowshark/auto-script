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
public class DeveloperTemplate extends AbstractTemplate {
    
    public DeveloperTemplate(String sScript) {
        super("script\\developer\\", sScript, 0);
    }
    
    public DeveloperTemplate(String sScript, int nStrPaddingIndex) {
        super("script\\developer\\", sScript, 0, nStrPaddingIndex);
    }
}
