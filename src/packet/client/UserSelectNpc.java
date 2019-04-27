/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package packet.client;

import java.awt.Point;
import packet.ClientCode;
import packet.Packet;
import script.Script;
import script.ScriptModifier;
import script.ScriptTemplateMap;
import script.ScriptWriteRequest;
import template.NpcTemplate;

/**
 *
 * @author Five
 */
public class UserSelectNpc extends Packet {
    
    private final int nNpcID;
    private final Point ptPos;
    private ScriptModifier pScriptMod = null;
    
    public UserSelectNpc(int nNpcID, Point ptPos) {
        super(ClientCode.UserSelectNpc.nCode);
        this.nNpcID = nNpcID;
        this.ptPos = ptPos;
    }

    @Override
    public ScriptModifier CreateScriptModifierOnEnd() {
        return pScriptMod;
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
    
    @Override
    public ScriptModifier CreateScriptModifier() {
        ScriptModifier pScriptModifier = (Script pScript) -> {
            NpcTemplate pNpcTemplate = ScriptTemplateMap.GetNpcTemplate(nNpcID);
            if (pNpcTemplate != null) {
                pScript.CreateNewTemplate(new ScriptWriteRequest(pScript.dwField, pNpcTemplate), true);
            }/* else {
                pScriptModifierOverride = (Script pScript) -> {
                    pScript.CreateNewTemplate(null);
                };
            }*/
        };
        return pScriptModifier;
    }
}
