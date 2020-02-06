/*
 * To change this license opcode), choose License Headers in Project Properties.
 * To change this template file), choose Tools | Templates
 * and open the template in the editor.
 */
package packet.opcode;

import base.network.opcode.ClientPacket;
import base.network.InPacket;
import java.io.IOException;
import java.util.Arrays;

import packet.PacketWrapper;
import packet.client.*;

/**
 *
 * @author Sharky
 */
public enum ClientCode {
    
    UserScriptMessageAnswer(ClientPacket.UserScriptMessageAnswer.Get(), //243
        (iPacket) -> {
            return new UserScriptMessageAnswer(iPacket);
        }
    ),
    UserSelectNpc(ClientPacket.UserSelectNpc.Get(), //241
        (iPacket) -> {
            return new UserSelectNpc(iPacket);
        }
    ),
    UserScriptItemUseRequest(ClientPacket.UserScriptItemUseRequest.Get(),
        (iPacket) -> {
            return new UserScriptItemUseRequest(iPacket);
        }
    ),
    UserPortalScriptRequest(ClientPacket.UserPortalScriptRequest.Get(), //349
        (iPacket) -> {
            return new UserPortalScriptRequest(iPacket);
        }
    ),
    UserQuestRequest(ClientPacket.UserQuestRequest.Get(), //358
        (iPacket) -> {
            return new UserQuestRequest(iPacket);
        }
    );
    public int nCode;
    public OnPacket pDecodePacket;
    
    ClientCode(int nCode, OnPacket pOnPacket) {
        this.nCode = nCode;
        this.pDecodePacket = pOnPacket;
    }
    
    public static ClientCode GetClient(int nCode) {
        for (ClientCode pCode : values()) {
            if (nCode == pCode.nCode) {
                return pCode;
            }
        }
        return null;
    }
    
    public static boolean IsClient(int nCode) {
        return Arrays.asList(values()).stream().anyMatch((pCode) -> pCode.nCode == nCode);
    }


    public interface OnPacket {
        PacketWrapper ReadPacket(InPacket iPacket) throws IOException;
    }
}
