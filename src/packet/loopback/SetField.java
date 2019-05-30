/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package packet.loopback;

import game.network.InPacket;
import java.util.LinkedList;
import packet.LoopbackCode;
import packet.PacketWriteRequest;
import script.Script;
import script.ScriptFieldObjMap;
import script.ScriptWriteRequest;
import script.ScriptModifier;
import script.ScriptTemplateMap;
import template.FieldTemplate;

/**
 *
 * @author Sharky
 */
public class SetField extends PacketWriteRequest {
    
    public byte nPortal;
    public boolean bOnTransferSameField;
    public String sFieldName;
    public FieldTemplate pFieldTemplate;
    
    public SetField(InPacket iPacket) {
        super(LoopbackCode.SetField.nCode);
        this.nPortal = 0;
        iPacket.DecodeInt();
        iPacket.DecodeByte();
        iPacket.DecodeInt();
        iPacket.DecodeByte();
        iPacket.DecodeInt();
        iPacket.DecodeByte();
        iPacket.DecodeInt();
        iPacket.DecodeInt();
        boolean bEncode = iPacket.DecodeBool();
        if (!bEncode) {
            iPacket.DecodeShort();
            iPacket.DecodeByte();
            this.dwField = iPacket.DecodeInt();
            this.nPortal = iPacket.DecodeByte();
        } else {
            iPacket.DecodePadding(112);
            byte nSP = iPacket.DecodeByte();
            for (int i = 0; i < nSP; i++) {
                iPacket.DecodePadding(5);
            }
            iPacket.DecodePadding(20);
            this.dwField = iPacket.DecodeInt();
        }
        this.sFieldName = ScriptTemplateMap.GetFieldName(dwField);
        FieldTemplate pTemplateEnter;
        if ((pTemplateEnter = ScriptTemplateMap.GetFirstUserEnterTemplate(dwField)) == null) {
            if ((pTemplateEnter = ScriptTemplateMap.GetUserEnterTemplate(dwField)) == null) {
                pTemplateEnter = ScriptTemplateMap.GetFieldScript(dwField);
            }
        }
        this.pFieldTemplate = pTemplateEnter;
        this.bOnTransferSameField = false;
    }

    @Override
    public ScriptModifier CreateScriptModifierOnEnd() {
        if (!bOnTransferSameField) {
            ScriptModifier pScriptModifier = (Script pScript) -> {
                pScript.CreateNewTemplate(new ScriptWriteRequest(dwField, pFieldTemplate), true);
                ScriptFieldObjMap.ResetMap();
            };
            return pScriptModifier;
        }
        return null;
    }

    @Override
    public ScriptModifier CreateScriptModifierOnInput() {
        if (!bOnTransferSameField) {
            ScriptModifier pScriptModifier = (Script pScriptMod) -> {
                if (pScriptMod.pTemplate != null) {
                    if (pScriptMod.GetNestedBlockHistory() != null) {
                        pScriptMod.GetNestedBlockHistory().SetNestedBlockResult(dwField);
                    }
                }
            };
            return pScriptModifier;
        }
        return null;
    }

    @Override
    public ScriptModifier CreateScriptModifierOnMerge() {
        if (!bOnTransferSameField) {
            ScriptModifier pScriptModifier = (Script pScriptCopy) -> {
                if (pScriptCopy != null) {
                    dwField = pScriptCopy.dwField;
                    pHistory = pScriptCopy.pHistory;
                    pTemplate = pScriptCopy.pTemplate;
                    nStrPaddingIndex = pScriptCopy.GetStrPaddingIndex();
                }
            };
            return pScriptModifier;
        }
        return null;
    }

    @Override
    public ScriptWriteRequest CreateScriptWriteRequest() {
        if (!bOnTransferSameField && pTemplate != null) {
            String sOutput = "pTarget.OnTransferField(" + dwField + ", " + nPortal + "); //to: \"" + sFieldName + "\"";
            return new ScriptWriteRequest(dwField, sOutput, pTemplate, new LinkedList<>(), nStrPaddingIndex);
        }
        return null;
    }
    
    @Override
    public ScriptModifier CreateScriptModifier() {
        ScriptModifier pScriptModifier = (Script pScriptMod) -> {
            if (pScriptMod.dwField == dwField) {
                bOnTransferSameField = true;
            }
            pScriptMod.dwField = dwField;
        };
        return !bOnTransferSameField ? pScriptModifier : null;
    }
}
