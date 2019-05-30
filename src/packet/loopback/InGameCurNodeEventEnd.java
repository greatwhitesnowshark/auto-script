/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package packet.loopback;

import java.util.LinkedList;
import packet.LoopbackCode;
import packet.PacketWriteRequest;
import script.Script;
import script.ScriptModifier;
import script.ScriptWriteRequest;

/**
 *
 * @author Sharky
 */
public class InGameCurNodeEventEnd extends PacketWriteRequest {
    
    public InGameCurNodeEventEnd() {
        super(LoopbackCode.InGameCurNodeEventEnd.nCode);
    }

    @Override
    public ScriptModifier CreateScriptModifierOnMerge() {
        ScriptModifier pScriptModifier = (Script pScriptCopy) -> {
            dwField = pScriptCopy.dwField;
            pTemplate = pScriptCopy.pTemplate;
            nStrPaddingIndex = pScriptCopy.GetStrPaddingIndex();
        };
        return pScriptModifier;
    }

    @Override
    public ScriptWriteRequest CreateScriptWriteRequest() {
        ScriptWriteRequest pWriteRequest = null;
        if (pTemplate != null) {
            String sOutput = "self.OnInGameCurNodeEventEnd();";
            pWriteRequest = new ScriptWriteRequest(dwField, sOutput, pTemplate, new LinkedList<>(), nStrPaddingIndex);
        }
        return pWriteRequest;
    }
}
