/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package packet;

import game.network.InPacket;
import game.user.UserEffect;
import packet.loopback.UserTalk;
import java.io.IOException;
import java.util.Arrays;
import packet.loopback.*;
import script.ScriptFieldObjMap;

/**
 *
 * @author Sharky
 */
public enum LoopbackCode {
    
    Message(108,
        (iPacket) -> {
            return new Message(iPacket);
        }
    ),
    SetField(501,
        (iPacket) -> {
            return new SetField(iPacket);
        }
    ),
    InGameCurNodeEventEnd(554,
        (iPacket) -> {
            return iPacket.DecodeByte() == 1 ? new InGameCurNodeEventEnd() : null;
        }
    ),
    UserTalk(616, 
        (iPacket) -> {
            return new UserTalk(iPacket);
        }
    ),
    UserEffectLocal(807,
        (iPacket) -> {
            byte nType = iPacket.DecodeByte();
            if (nType == UserEffect.ReservedEffect || nType == UserEffect.AvatarOriented) {
                return new UserEffectLocal(nType, iPacket);
            }
            return null;
        }
    ),
    UserQuestResult(812,
        (iPacket) -> {
            return new UserQuestResult(iPacket);
        }
    ),
    UserSetInGameDirectionMode(825,
        (iPacket) -> {
            return new UserSetInGameDirectionMode(iPacket);
        }
    ),
    UserSetStandaloneMode(826,
        (iPacket) -> {
            return new UserSetStandaloneMode(iPacket);
        }
    ),
    UserInGameDirectionEvent(855,
        (iPacket) -> {
            return new UserInGameDirectionEvent(iPacket);
        }
    ),
    MobEnterField(1102, 
        (iPacket) -> {
            iPacket.DecodeByte();
            int dwID = iPacket.DecodeInt();
            iPacket.DecodeByte();
            int dwTemplateID = iPacket.DecodeInt();
            ScriptFieldObjMap.OnMobEnterField(dwID, dwTemplateID);
            return null;
        }
    ),
    NpcEnterField(1199, 
        (iPacket) -> {
            int dwID = iPacket.DecodeInt();
            int dwTemplateID = iPacket.DecodeInt();
            ScriptFieldObjMap.OnNpcEnterField(dwID, dwTemplateID);
            return null;
        }
    ),
    ForceMoveByScript(1210,
        (iPacket) -> {
            return new ForceMoveByScript(iPacket);
        }
    ),
    NpcSpecialAction(1224,
        (iPacket) -> {
            return new NpcSpecialAction(iPacket);
        }
    ),
    ReactorEnterField(1254,
        (iPacket) -> {
            int dwID = iPacket.DecodeInt();
            int dwTemplateID = iPacket.DecodeInt();
            ScriptFieldObjMap.OnReactorEnterField(dwID, dwTemplateID);
            return null;
        }
    ),
    ScriptMessage(1658,
        (iPacket) -> {
            return new ScriptMessage(iPacket);
        }
    );
    public int nCode;
    public OnPacket pDecodePacket;
    public String sScriptWriteOutput = "";
    
    LoopbackCode(int nCode, OnPacket pOnPacket) {
        this.nCode = nCode;
        this.pDecodePacket = pOnPacket;
    }
    
    public static LoopbackCode GetLoopback(int nCode) {
        for (LoopbackCode pCode : values()) {
            if (nCode == pCode.nCode) {
                return pCode;
            }
        }
        return null;
    }
    
    public static boolean IsLoopback(int nCode) {
        return Arrays.asList(values()).stream().anyMatch((pCode) -> pCode.nCode == nCode);
    }
    
    
    public static interface OnPacket {    
        public PacketWrapper ReadPacket(InPacket iPacket) throws IOException;    
    }
}
