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
 * @author Five
 */
public class NpcSpecialAction extends PacketWriteRequest {
    
    private final boolean bLocal;
    private final int dwID, tDuration;
    private final String sMsg;
    
    public NpcSpecialAction(int dwID, String sMsg, int tDuration, boolean bLocal) {
        super(LoopbackCode.NpcSpecialAction.nCode);
        this.dwID = dwID;
        this.sMsg = sMsg;
        this.tDuration = tDuration;
        this.bLocal = bLocal;
    }
    
    @Override
    public ScriptModifier CreateScriptModifier() {
        return null;
    }

    @Override
    public ScriptModifier CreateScriptModifierOnEnd() {
        return null;
    }

    @Override
    public ScriptModifier CreateScriptModifierOnInput() {
        return null;
    }

    @Override
    public ScriptModifier CreateScriptModifierOnMerge() {
        ScriptModifier pScriptModifier = (Script pScript) -> {
            if (pScript.pTemplate != null) {
                dwField = pScript.dwField;
                pTemplate = pScript.pTemplate;
                nStrPaddingIndex = pScript.GetStrPaddingIndex();
            }
        };
        return pScriptModifier;
    }

    @Override
    public ScriptWriteRequest CreateScriptWriteRequest() {
        if (pTemplate != null) {
            String sOutput = "self.OnNpcSpecialAction(" + dwID + ", \"" + sMsg + "\", " + tDuration + ", " + (bLocal ? "true" : "false") + ");";
            return new ScriptWriteRequest(dwField, sOutput, pTemplate, new LinkedList<>(), nStrPaddingIndex);
        }
        return null;
    }
}
