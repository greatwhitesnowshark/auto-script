/*
 * To change this license opcode, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package packet.loopback;

import game.network.InPacket;
import java.util.LinkedList;
import packet.opcode.LoopbackCode;
import packet.PacketWriteRequest;
import script.Script;
import script.ScriptFieldObjMap;
import script.ScriptModifier;
import script.ScriptWriteRequest;

/**
 *
 * @author Sharky
 */
public class NpcSpecialAction extends PacketWriteRequest {
    
    private final boolean bLocal;
    private final int dwID, tDuration;
    private final String sMsg;
    
    public NpcSpecialAction(InPacket iPacket) {
        super(LoopbackCode.NpcSpecialAction.nCode);
        this.dwID = iPacket.DecodeInt();
        this.sMsg = iPacket.DecodeString();
        this.tDuration = iPacket.DecodeInt();
        this.bLocal = iPacket.DecodeBool();
    }

    @Override
    public ScriptModifier CreateScriptTemplateCopy() {
        ScriptModifier pScriptModifier = (Script pScript) -> {
            if (pScript.pTemplate != null) {
                dwField = pScript.dwField;
                pTemplate = pScript.pTemplate;
                pHistory = pScript.pHistory;
                nStrPaddingIndex = pScript.CurrentLinePadding();
            }
        };
        return pScriptModifier;
    }

    @Override
    public ScriptWriteRequest CreateScriptWriteRequest() {
        ScriptWriteRequest pWriteRequest = null;
        if (pTemplate != null) {
            int dwTemplateID = ScriptFieldObjMap.GetNpcTemplateID(this.dwID);
            String sOutput = "self.OnNpcSpecialAction(" + dwTemplateID + ", \"" + sMsg + "\", " + tDuration + ", " + (bLocal ? "true" : "false") + ");";
            pWriteRequest = new ScriptWriteRequest(dwField, sOutput, pTemplate, new LinkedList<>(), nStrPaddingIndex);
        }
        return pWriteRequest;
    }
}
