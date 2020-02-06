/*
 * To change this license opcode, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package packet.client;

import base.network.InPacket;
import java.awt.Point;
import packet.opcode.ClientCode;
import packet.PacketWrapperNull;
import script.Script;
import script.ScriptFieldObjMap;
import script.ScriptModifier;
import script.ScriptTemplateMap;
import script.ScriptWriteRequest;
import template.NpcTemplate;
import scriptmaker.Config;
import util.Logger;

/**
 *
 * @author Sharky
 */
public class UserSelectNpc extends PacketWrapperNull {
    
    public final int dwNpcObjectID;
    public final Point ptPos;
    public boolean bResetNotPersist = false;
    
    public UserSelectNpc(InPacket iPacket) {
        super(ClientCode.UserSelectNpc.nCode);
        this.dwNpcObjectID = iPacket.DecodeInt(); //not template ID, this relates to the map object ID
        this.ptPos = new Point(iPacket.DecodeShort(), iPacket.DecodeShort());
    }

    @Override
    public boolean IsScriptResetNotPersist() {
        return bResetNotPersist;
    }
    
    @Override
    public ScriptModifier CreateNewScriptTemplate() {
        ScriptModifier pScriptModifier = (Script pScript) -> {
            int nNpcID = ScriptFieldObjMap.GetNpcTemplateID(dwNpcObjectID);
            if (nNpcID > 0) {
                NpcTemplate pNpcTemplate = ScriptTemplateMap.GetNpcTemplate(nNpcID);
                if (pNpcTemplate != null) {
                    pScript.CreateNewTemplate(new ScriptWriteRequest(pScript.dwField, pNpcTemplate), true);
                    if (Config.bUserSelectNpcLog) {
                        String sNpcName = ScriptTemplateMap.GetNpcName(nNpcID);
                        if (sNpcName == null || sNpcName.isEmpty()) {
                            sNpcName = "No-Name-Found";
                        }
                        Logger.LogReport("UserSelectNpc:  Script [%s],  Npc-Name [%s],  ID [%d]", pNpcTemplate.sScript, sNpcName, nNpcID);
                    }
                } else {
                    this.bResetNotPersist = true;
                    if (Config.bUserSelectNpcLog) {
                        Logger.LogError("UserSelectNpc:-  no script template could be found for Npc-ID [%d]", nNpcID);
                    }
                }
            }
        };
        return pScriptModifier;
    }
}
