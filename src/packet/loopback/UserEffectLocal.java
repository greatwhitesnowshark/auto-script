/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package packet.loopback;

import java.util.LinkedList;
import message.UserEffectLocalType;
import packet.LoopbackCode;
import packet.PacketWriteRequest;
import script.Script;
import script.ScriptModifier;
import script.ScriptWriteRequest;

/**
 *
 * @author Five
 */
public class UserEffectLocal extends PacketWriteRequest {
    
    private final byte nType;
    private final boolean bFlip;
    private final int nRange, nNameHeight;
    private final String sMsg;
    
    public UserEffectLocal(boolean bFlip, int nRange, int nNameHeight, String sMsg) {
        super(LoopbackCode.UserEffectLocal.nCode);
        this.nType = UserEffectLocalType.Reserved;
        this.bFlip = bFlip;
        this.nRange = nRange;
        this.nNameHeight = nNameHeight;
        this.sMsg = sMsg;
    }
    
    public UserEffectLocal(String sMsg) {
        super(LoopbackCode.UserEffectLocal.nCode);
        this.nType = UserEffectLocalType.AvatarOriented;
        this.bFlip = false;
        this.nRange = 0;
        this.nNameHeight = 0;
        this.sMsg = sMsg;
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
            String sOutput = "";
            if (nType == UserEffectLocalType.AvatarOriented) {
                sOutput = "self.EffectAvatarOriented(\"" + sMsg + "\");";
            } else if (nType == UserEffectLocalType.Reserved) {
                sOutput = "self.EffectReserved(\"" + sMsg + "\"";
            }
            return new ScriptWriteRequest(dwField, sOutput, pTemplate, new LinkedList<>(), nStrPaddingIndex);
        }
        return null;
    }
}
