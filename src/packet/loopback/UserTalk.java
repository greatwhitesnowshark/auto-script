/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package packet.loopback;

import game.network.InPacket;
import packet.LoopbackCode;
import packet.PacketNullWrapper;
import script.ScriptWriteRequest;
import util.Config;
import util.Logger;

/**
 *
 * @author Sharky
 */
public class UserTalk extends PacketNullWrapper {
    
    public String sMsg = "";

    public UserTalk(InPacket iPacket) {
        super(LoopbackCode.UserTalk.nCode);
        iPacket.DecodeByte();
        iPacket.DecodeInt();
        this.sMsg = iPacket.DecodeString();
    }
    
    @Override
    public ScriptWriteRequest CreateScriptWriteRequest() {
        if (Config.UserTalkDebug) {
            Logger.LogAdmin("UserTalk: " + sMsg + "");
        }
        return null;
    }
}
