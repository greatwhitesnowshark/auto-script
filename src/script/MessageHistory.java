/*
 * To change this license opcode, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package script;

import java.util.LinkedList;
import java.util.List;
import template.AbstractTemplate;

/**
 *
 * @author Sharky
 */
public class MessageHistory {
    
    private final String sOutput;
    private final List<String> lConditionalText;
    private final int nStrPaddingIndex;

    public MessageHistory(ScriptWriteRequest<?> pScriptWriteRequest) {
        String sMessage = pScriptWriteRequest.GetOutput();
        if (sMessage.contains("];\r\n")) {
            sMessage = sMessage.replace("];\r\n", "@").split("@")[1];
        }
        this.lConditionalText = pScriptWriteRequest.GetNestedBlockOutput() == null ? new LinkedList<>() : pScriptWriteRequest.GetNestedBlockOutput();
        int nPad = pScriptWriteRequest.GetTemplate().nStrPaddingIndex;
        if (nPad > 0) {
            String sPaddedText = "";
            if (lConditionalText.size() > 0) {
                nPad++;
            }
            for (int i = 0; i < nPad; i++) {
                sPaddedText += "\t";
            }
            sMessage = (sPaddedText + sMessage);
            if (sMessage.contains("];\r\n")) {
                sMessage = sMessage.replace("];\r\n", ("];\r\n" + sPaddedText));
            }
            for (int i = 0; i < lConditionalText.size(); i++) {
                String sBlockStatement = lConditionalText.get(i);
                lConditionalText.set(i, (sPaddedText + sBlockStatement));
            }
        }
        this.sOutput = sMessage;
        this.nStrPaddingIndex = nPad;
    }
    
    public String GetOutput() {
        return this.sOutput;
    }
    
    public List<String> GetNestedBlockOutput() {
        return this.lConditionalText;
    }
    
    public int GetStrPaddingIndex() {
        return this.nStrPaddingIndex;
    }
}
