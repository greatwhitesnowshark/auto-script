/*
 * To change this license header), choose License Headers in Project Properties.
 * To change this template file), choose Tools | Templates
 * and open the template in the editor.
 */
package packet;

import game.network.InPacket;
import java.io.IOException;
import java.util.Arrays;
import packet.client.UserPortalScriptRequest;
import packet.client.UserQuestRequest;
import packet.client.UserScriptMessageAnswer;
import packet.client.UserSelectNpc;
import script.Script;

/**
 *
 * @author Sharky
 */
public enum ClientCode {
    
    UserScriptMessageAnswer(243,
        (iPacket) -> {
            return new UserScriptMessageAnswer(iPacket);
        }
    ),
    UserSelectNpc(241,
        (iPacket) -> {
            return new UserSelectNpc(iPacket);
        }
    ),
    UserPortalScriptRequest(349,
        (iPacket) -> {
            return new UserPortalScriptRequest(iPacket);
        }
    ),
    UserQuestRequest(358,
        (iPacket) -> {
            return new UserQuestRequest(iPacket);
        }
    );
    public int nCode;
    public OnPacket pDecodePacket;
    public Script pScript;
    
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
    
    
    public static interface OnPacket {    
        public PacketWrapper ReadPacket(InPacket iPacket) throws IOException;    
    }
    
}
