/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package packet.client;

import game.network.InPacket;
import packet.ClientCode;
import packet.PacketNullWrapper;
import script.Script;
import script.ScriptModifier;
import script.ScriptTemplateMap;
import script.ScriptWriteRequest;
import template.PortalTemplate;

/**
 *
 * @author Sharky
 */
public class UserPortalScriptRequest extends PacketNullWrapper {
    
    public final byte nType;
    public final String sPortalName;
    
    public UserPortalScriptRequest(InPacket iPacket) {
        super(ClientCode.UserPortalScriptRequest.nCode);
        this.nType = iPacket.DecodeByte();
        this.sPortalName = iPacket.DecodeString();
    }
    
    @Override
    public ScriptModifier CreateScriptModifier() {
        ScriptModifier pScriptModifier = (Script pScript) -> {
            PortalTemplate pPortalTemplate = ScriptTemplateMap.GetPortalTemplate(pScript.dwField, sPortalName);
            if (pPortalTemplate != null) {
                pScript.CreateNewTemplate(new ScriptWriteRequest(pScript.dwField, pPortalTemplate), true);
            }
        };
        return pScriptModifier;
    }
}
