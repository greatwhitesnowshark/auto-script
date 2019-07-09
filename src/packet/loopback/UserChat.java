/*
 * To change this license opcode, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package packet.loopback;

import game.network.InPacket;
import packet.opcode.LoopbackCode;
import packet.PacketWrapperNull;
import script.ScriptWriteRequest;
import scriptmaker.ScriptMakerConfig;
import util.Logger;

/**
 *
 * @author Sharky
 */
public class UserChat extends PacketWrapperNull {
    
    public String sMsg = "";

    public UserChat(InPacket iPacket) {
        super(LoopbackCode.UserChat.nCode);
        iPacket.DecodeByte();
        iPacket.DecodeInt();
        this.sMsg = iPacket.DecodeString();
    }
    
    @Override
    public ScriptWriteRequest CreateScriptWriteRequest() {
        if (ScriptMakerConfig.OnPacketUserTalkDebug) {
            Logger.LogAdmin("UserChat: " + sMsg + "");
        }
        return null;
    }
}
