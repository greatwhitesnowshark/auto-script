/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package script;

import java.util.LinkedList;
import java.util.List;
import template.AbstractTemplate;

public class ScriptWriteRequest<T extends AbstractTemplate> {
    
    private final int dwField, nStrPaddingIndex;
    private final T pTemplate;
    private final List<String> lConditionalText;
    private String sOutput;
    
    public ScriptWriteRequest(int dwField, String sOutput, T pTemplate, List<String> lConditionalText, int nStrPaddingIndex) {
        String sPadding = "";
        for (int i = 0; i < nStrPaddingIndex; i++) {
            sPadding += "\t";
        }
        this.sOutput = sPadding + sOutput;
        this.lConditionalText = lConditionalText;
        for (int i = 0; i < lConditionalText.size(); i++) {
            lConditionalText.set(i, (sPadding + lConditionalText.get(i)));
        }
        this.dwField = dwField;
        this.pTemplate = pTemplate;
        this.nStrPaddingIndex = nStrPaddingIndex;
    }
    
    public ScriptWriteRequest(int dwField, T pTemplate) {
        this.dwField = dwField;
        this.sOutput = "";
        this.pTemplate = pTemplate;
        this.lConditionalText = new LinkedList<>();
        this.nStrPaddingIndex = 0;
    }
    
    public int GetField() {
        return dwField;
    }
    
    public String GetFileName() {
        if (pTemplate != null) {
            String sName = pTemplate.sDirPath + pTemplate.sScript;
            return sName.contains(".js") ? sName : sName + ".js";
        }
        return null;
    }
    
    public String GetOutput() {
        return sOutput;
    }
    
    public T GetTemplate() {
        return pTemplate;
    }
    
    public List<String> GetNestedBlockOutput() {
        return lConditionalText;
    }
    
    public void SetOutput(String sOutput) {
        this.sOutput = sOutput;
    }
}