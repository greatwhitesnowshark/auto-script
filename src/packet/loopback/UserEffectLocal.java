/*
 * To change this license opcode, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package packet.loopback;

import game.network.InPacket;
import game.user.UserEffect;
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
public class UserEffectLocal extends PacketWriteRequest {
    
    public byte nType;
    public boolean bFlip;
    public int nRange, nNameHeight;
    public String sMsg;
    
    public UserEffectLocal(byte nType, InPacket iPacket) {
        super(LoopbackCode.UserEffectLocal.nCode);
        this.nType = nType;
        if (nType == UserEffect.ReservedEffect) {
            this.bFlip = iPacket.DecodeBool();
            this.nRange = iPacket.DecodeInt();
            this.nNameHeight = iPacket.DecodeInt();
            this.sMsg = iPacket.DecodeString();
        } else if (nType == UserEffect.AvatarOriented) {
            this.sMsg = iPacket.DecodeString();
        }
    }
    
    public UserEffectLocal(String sMsg) {
        super(LoopbackCode.UserEffectLocal.nCode);
        this.nType = UserEffect.AvatarOriented;
        this.bFlip = false;
        this.nRange = 0;
        this.nNameHeight = 0;
        this.sMsg = sMsg;
    }

    @Override
    public ScriptModifier CreateScriptTemplateCopy() {
        ScriptModifier pScriptModifier = (Script pScriptCopy) -> {
            if (pScriptCopy.pTemplate != null) {
                dwField = pScriptCopy.dwField;
                pTemplate = pScriptCopy.pTemplate;
                pHistory = pScriptCopy.pHistory;
                nStrPaddingIndex = pScriptCopy.CurrentLinePadding();
            }
        };
        return pScriptModifier;
    }
    
    @Override
    public ScriptWriteRequest CreateScriptWriteRequest() {
        if (pTemplate != null) {
            String sOutput = "";
            if (nType == UserEffect.AvatarOriented) {
                sOutput = "self.EffectAvatarOriented(\"" + sMsg + "\");";
            } else if (nType == UserEffect.ReservedEffect) {
                sOutput = "self.EffectReserved(\"" + sMsg + "\"";
            }
            return new ScriptWriteRequest(dwField, sOutput, pTemplate, new LinkedList<>(), nStrPaddingIndex);
        }
        return null;
    }
}
