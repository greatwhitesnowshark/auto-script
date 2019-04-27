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
public class ForceMoveByScript extends PacketWriteRequest {
    
    private final int nForcedMoveDir, nForcedMoveFixel, ptStartY, ptStartX;
    
    public ForceMoveByScript(int nForcedMoveDir, int nForcedMoveFixel, int ptStartY, int ptStartX) {
        super(LoopbackCode.ForceMoveByScript.nCode);
        this.nForcedMoveDir = nForcedMoveDir;
        this.nForcedMoveFixel = nForcedMoveFixel;
        this.ptStartY = ptStartY;
        this.ptStartX = ptStartX;
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
            String sOutput = ("self.OnForceMoveByScript(" + nForcedMoveDir + ", " + nForcedMoveFixel + ", " + ptStartY + ", " + ptStartX + ");");
            pWriteRequest = new ScriptWriteRequest(dwField, sOutput, pTemplate, new LinkedList<>(), nStrPaddingIndex);
        }
        return pWriteRequest;
    }
    
}
