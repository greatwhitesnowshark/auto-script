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
import script.ScriptModifier;
import script.ScriptWriteRequest;

/**
 *
 * @author Sharky
 */
public class UserSetInGameDirectionMode extends PacketWriteRequest {
    
    private final boolean bInGameDirectionMode, bBlackFrame, bForceMouseOver, bShowUI;
    
    public UserSetInGameDirectionMode(InPacket iPacket) {
        super(LoopbackCode.UserSetInGameDirectionMode.nCode);
        this.bInGameDirectionMode = iPacket.DecodeBool();
        this.bBlackFrame = iPacket.DecodeBool();
        this.bForceMouseOver = iPacket.CountRemaining() > 0 ? iPacket.DecodeBool() : false;
        this.bShowUI = iPacket.CountRemaining() > 0 ? iPacket.DecodeBool() : false;
    }

    @Override
    public ScriptModifier CreateScriptModifierOnMerge() {
        ScriptModifier pScriptModifier = (Script pScriptCopy) -> {
            if (pScriptCopy.pTemplate != null) {
                dwField = pScriptCopy.dwField;
                pTemplate = pScriptCopy.pTemplate;
                nStrPaddingIndex = pScriptCopy.GetStrPaddingIndex();
            }
        };
        return pScriptModifier;
    }
    
    @Override
    public ScriptWriteRequest CreateScriptWriteRequest() {
        if (pTemplate != null) {
            String sOutput = ("self.OnSetInGameDirectionMode(" + bInGameDirectionMode + ", " + bBlackFrame + ", " + bForceMouseOver + ", " + bShowUI + ");");
            return new ScriptWriteRequest(dwField, sOutput, pTemplate, new LinkedList<>(), nStrPaddingIndex);
        }
        return null;
    }
}
