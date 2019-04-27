/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package packet.loopback;

import java.util.LinkedList;
import packet.LoopbackCode;
import packet.PacketWriteRequest;
import script.Script;
import script.ScriptWriteRequest;
import script.ScriptModifier;
import script.ScriptTemplateMap;
import template.FieldTemplate;

/**
 *
 * @author Sharky
 */
public class SetField extends PacketWriteRequest {
    
    private final byte nPortal;
    private final String sFieldName;
    private final FieldTemplate pFieldTemplate;
    
    public SetField(int dwField, byte nPortal) {
        super(LoopbackCode.SetField.nCode);
        this.dwField = dwField;
        this.nPortal = nPortal;
        this.sFieldName = ScriptTemplateMap.GetFieldName(dwField);
        FieldTemplate pTemplateEnter;
        if ((pTemplateEnter = ScriptTemplateMap.GetFirstUserEnterTemplate(dwField)) == null) {
            if ((pTemplateEnter = ScriptTemplateMap.GetUserEnterTemplate(dwField)) == null) {
                pTemplateEnter = ScriptTemplateMap.GetFieldScript(dwField);
            }
        }
        this.pFieldTemplate = pTemplateEnter;
    }

    @Override
    public ScriptModifier CreateScriptModifierOnEnd() {
        ScriptModifier pScriptModifier = (Script pScript) -> {
            pScript.CreateNewTemplate(new ScriptWriteRequest(dwField, pFieldTemplate), true);
        };
        return pScriptModifier;
    }

    @Override
    public ScriptModifier CreateScriptModifierOnInput() {
        ScriptModifier pScriptModifier = (Script pScriptMod) -> {
            if (pScriptMod.pTemplate != null) {
                if (pScriptMod.GetNestedBlockHistory() != null) {
                    pScriptMod.GetNestedBlockHistory().SetNestedBlockResult(dwField);
                }
            }
        };
        return pScriptModifier;
    }

    @Override
    public ScriptModifier CreateScriptModifierOnMerge() {
        ScriptModifier pScriptModifier = (Script pScriptCopy) -> {
            if (pScriptCopy != null) {
                dwField = pScriptCopy.dwField;
                pTemplate = pScriptCopy.pTemplate;
                nStrPaddingIndex = pScriptCopy.GetStrPaddingIndex();
            }
        };
        return pScriptModifier;
    }

    @Override
    public ScriptWriteRequest CreateScriptWriteRequest() {
        if (pTemplate != null) {
            String sOutput = "pTarget.OnTransferField(" + dwField + ", " + nPortal + "); //to: \"" + sFieldName + "\"";
            return new ScriptWriteRequest(dwField, sOutput, pTemplate, new LinkedList<>(), nStrPaddingIndex);
        }
        return null;
    }
    
    @Override
    public ScriptModifier CreateScriptModifier() {
        ScriptModifier pScriptModifier = (Script pScriptMod) -> {
            pScriptMod.dwField = dwField;
        };
        return pScriptModifier;
    }
}
