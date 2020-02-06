/*
 * To change this license opcode, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package packet.client;

import base.network.InPacket;
import packet.opcode.ClientCode;
import packet.PacketWrapperNull;
import script.Script;
import script.ScriptModifier;
import script.ScriptTemplateMap;
import script.ScriptWriteRequest;
import template.PortalTemplate;

/**
 *
 * @author Sharky
 */
public class UserPortalScriptRequest extends PacketWrapperNull {
    
    public final byte nType;
    public final String sPortalName;
    
    public UserPortalScriptRequest(InPacket iPacket) {
        super(ClientCode.UserPortalScriptRequest.nCode);
        this.nType = iPacket.DecodeByte();
        this.sPortalName = iPacket.DecodeString();
    }
    
    @Override
    public ScriptModifier CreateNewScriptTemplate() {
        ScriptModifier pScriptModifier = (Script pScript) -> {
            PortalTemplate pPortalTemplate = ScriptTemplateMap.GetPortalTemplate(pScript.dwField, sPortalName);
            if (pPortalTemplate != null) {
                pScript.CreateNewTemplate(new ScriptWriteRequest(pScript.dwField, pPortalTemplate), true);
            }
        };
        return pScriptModifier;
    }
}
