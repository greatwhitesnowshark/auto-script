/*
 * To change this license opcode, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package packet.loopback;

import base.network.InPacket;
import java.util.LinkedList;
import packet.opcode.LoopbackCode;
import packet.PacketWriteRequest;
import script.Script;
import script.ScriptModifier;
import script.ScriptWriteRequest;

/**
 *
 * @author Sharky
 */
public class ForceMoveByScript extends PacketWriteRequest {
    
    private final int nForcedMoveDir, nForcedMoveFixel, ptStartY, ptStartX;
    
    public ForceMoveByScript(InPacket iPacket) {
        super(LoopbackCode.ForceMoveByScript.nCode);
        this.nForcedMoveDir = iPacket.DecodeInt();
        this.nForcedMoveFixel = iPacket.DecodeInt();
        this.ptStartY = iPacket.DecodeInt();
        this.ptStartX = iPacket.DecodeInt();
    }

    @Override
    public ScriptModifier CreateScriptTemplateCopy() {
        ScriptModifier pScriptModifier = (Script pScriptCopy) -> {
            dwField = pScriptCopy.dwField;
            pTemplate = pScriptCopy.pTemplate;
            pHistory = pScriptCopy.pHistory;
            nStrPaddingIndex = pScriptCopy.CurrentLinePadding();
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
