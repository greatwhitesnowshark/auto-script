/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package packet.client;

import game.network.InPacket;
import java.awt.Point;
import packet.ClientCode;
import packet.PacketNullWrapper;
import script.Script;
import script.ScriptFieldObjMap;
import script.ScriptModifier;
import script.ScriptTemplateMap;
import script.ScriptWriteRequest;
import template.NpcTemplate;

/**
 *
 * @author Sharky
 */
public class UserSelectNpc extends PacketNullWrapper {
    
    public final int dwNpcObjectID;
    public final Point ptPos;
    public ScriptModifier pScriptMod = null;
    
    public UserSelectNpc(InPacket iPacket) {
        super(ClientCode.UserSelectNpc.nCode);
        this.dwNpcObjectID = iPacket.DecodeInt(); //not template ID, this relates to the map object ID
        this.ptPos = new Point(iPacket.DecodeShort(), iPacket.DecodeShort());
    }

    @Override
    public ScriptModifier CreateScriptModifierOnEnd() {
        return pScriptMod;
    }
    
    @Override
    public ScriptModifier CreateScriptModifier() {
        ScriptModifier pScriptModifier = (Script pScript) -> {
            NpcTemplate pNpcTemplate = ScriptTemplateMap.GetNpcTemplate(ScriptFieldObjMap.GetNpcTemplateID(dwNpcObjectID));
            if (pNpcTemplate != null) {
                pScript.CreateNewTemplate(new ScriptWriteRequest(pScript.dwField, pNpcTemplate), true);
            } else {
                pScriptMod = (Script pScriptReset) -> {
                    pScript.CreateNewTemplate(null);
                };
            }
        };
        return pScriptModifier;
    }
}
