/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package packet.loopback;

import game.network.InPacket;
import java.util.LinkedList;
import packet.LoopbackCode;
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
            int dwTemplateID = ScriptFieldObjMap.GetNpcTemplateID(this.dwID);
            String sOutput = "self.OnNpcSpecialAction(" + dwTemplateID + ", \"" + sMsg + "\", " + tDuration + ", " + (bLocal ? "true" : "false") + ");";
            return new ScriptWriteRequest(dwField, sOutput, pTemplate, new LinkedList<>(), nStrPaddingIndex);
        }
        return null;
    }
}
