/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package packet.client;

import packet.ClientCode;
import packet.Packet;
import script.Script;
import script.ScriptModifier;
import script.ScriptTemplateMap;
import script.ScriptWriteRequest;
import template.PortalTemplate;

/**
 *
 * @author Five
 */
public class UserPortalScriptRequest extends Packet {
    
    private final byte nType;
    private final String sPortalName;
    
    public UserPortalScriptRequest(byte nType, String sPortalName) {
        super(ClientCode.UserPortalScriptRequest.nCode);
        this.nType = nType;
        this.sPortalName = sPortalName;
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
        return null;
    }
    
}
