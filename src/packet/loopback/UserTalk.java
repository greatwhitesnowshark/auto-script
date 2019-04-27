/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package packet.loopback;

import packet.LoopbackCode;
import packet.Packet;
import script.ScriptModifier;
import script.ScriptWriteRequest;
import util.Config;
import util.Logger;

/**
 *
 * @author Sharky
 */
public class UserTalk extends Packet {
    
    private String sMsg = "";

    public UserTalk(String sMsg) {
        super(LoopbackCode.UserTalk.nCode);
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
        return null;
    }
    
    @Override
    public ScriptWriteRequest CreateScriptWriteRequest() {
        if (Config.UserTalkDebug) {
            Logger.LogAdmin("UserTalk: " + sMsg + "");
        }
        return null;
    }
}
