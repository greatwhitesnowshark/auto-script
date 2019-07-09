/*
 * To change this license opcode, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package packet.opcode;

import game.network.LoopbackPacket;
import game.user.UserEffect;
import packet.PacketWrapper;
import packet.loopback.UserChat;

import java.util.Arrays;
import packet.loopback.*;
import script.ScriptFieldObjMap;

/**
 *
 * @author Sharky
 */
public enum LoopbackCode {
    
    Message(LoopbackPacket.Message.Get(), //108
        (iPacket) -> {
            return new Message(iPacket);
        }
    ),
    SetField(LoopbackPacket.SetField.Get(), //501
        (iPacket) -> {
            return new SetField(iPacket);
        }
    ),
    InGameCurNodeEventEnd(LoopbackPacket.InGameCurNodeEventEnd.Get(), //554
        (iPacket) -> {
            return iPacket.CountRemaining() > 0 && iPacket.DecodeByte() == 1 ? new InGameCurNodeEventEnd() : null;
        }
    ),
    UserChat(LoopbackPacket.UserChat.Get(), //616
        (iPacket) -> {
            return new UserChat(iPacket);
        }
    ),
    UserEffectLocal(LoopbackPacket.UserEffectLocal.Get(), //807
        (iPacket) -> {
            byte nType = iPacket.DecodeByte();
            if (nType == UserEffect.ReservedEffect || nType == UserEffect.AvatarOriented) {
                return new UserEffectLocal(nType, iPacket);
            }
            return null;
        }
    ),
    UserQuestResult(LoopbackPacket.UserQuestResult.Get(), //812
        (iPacket) -> {
            return new UserQuestResult(iPacket);
        }
    ),
    UserSetInGameDirectionMode(LoopbackPacket.UserSetInGameDirectionMode.Get(), //825
        (iPacket) -> {
            return new UserSetInGameDirectionMode(iPacket);
        }
    ),
    UserSetStandaloneMode(LoopbackPacket.UserSetStandAloneMode.Get(), //826
        (iPacket) -> {
            return new UserSetStandaloneMode(iPacket);
        }
    ),
    UserInGameDirectionEvent(LoopbackPacket.UserInGameDirectionEvent.Get(), //855
        (iPacket) -> {
            return new UserInGameDirectionEvent(iPacket);
        }
    ),
    MobEnterField(LoopbackPacket.MobEnterField.Get(), //1102
        (iPacket) -> {
            iPacket.DecodeByte();
            int dwID = iPacket.DecodeInt();
            iPacket.DecodeByte();
            int dwTemplateID = iPacket.DecodeInt();
            ScriptFieldObjMap.OnMobEnterField(dwID, dwTemplateID);
            return null;
        }
    ),
    NpcEnterField(LoopbackPacket.NpcEnterField.Get(), //1199
        (iPacket) -> {
            int dwID = iPacket.DecodeInt();
            int dwTemplateID = iPacket.DecodeInt();
            ScriptFieldObjMap.OnNpcEnterField(dwID, dwTemplateID);
            return null;
        }
    ),
    ForceMoveByScript(LoopbackPacket.ForceMoveByScript.Get(), //1210
        (iPacket) -> {
            return new ForceMoveByScript(iPacket);
        }
    ),
    NpcSpecialAction(LoopbackPacket.NpcSpecialAction.Get(), //1224
        (iPacket) -> {
            return new NpcSpecialAction(iPacket);
        }
    ),
    ReactorEnterField(LoopbackPacket.ReactorEnterField.Get(), //1254
        (iPacket) -> {
            int dwID = iPacket.DecodeInt();
            int dwTemplateID = iPacket.DecodeInt();
            ScriptFieldObjMap.OnReactorEnterField(dwID, dwTemplateID);
            return null;
        }
    ),
    ScriptMessage(LoopbackPacket.ScriptMessage.Get(), //1658
        (iPacket) -> {
            return new ScriptMessage(iPacket);
        }
    );
    public int nCode;
    public PacketWrapper.OnPacket pDecodePacket;
    
    LoopbackCode(int nCode, PacketWrapper.OnPacket pOnPacket) {
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


}
