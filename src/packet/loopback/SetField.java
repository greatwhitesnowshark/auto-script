/*
 * To change this license opcode, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package packet.loopback;

import game.network.InPacket;
import java.util.LinkedList;
import packet.opcode.LoopbackCode;
import packet.PacketWriteRequest;
import script.*;
import template.FieldTemplate;

/**
 *
 * @author Sharky
 */
public class SetField extends PacketWriteRequest {
    
    public byte nPortal;
    public boolean bIsUserRespawn, bIsSkippedFieldTransfer, bIsFieldScriptTemplate;
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
        FieldTemplate pUserFieldTemplate;
        if ((pUserFieldTemplate = ScriptTemplateMap.GetFirstUserEnterTemplate(dwField)) == null) {
            if ((pUserFieldTemplate = ScriptTemplateMap.GetUserEnterTemplate(dwField)) == null) {
                pUserFieldTemplate = ScriptTemplateMap.GetFieldScript(dwField);
            }
        }
        this.pFieldTemplate = pUserFieldTemplate;
        this.bIsSkippedFieldTransfer = false;
        this.bIsFieldScriptTemplate = false;
    }

    @Override
    public ScriptModifier CreateScriptTemplateCopy() {
        ScriptModifier pScriptModifier = null;
        if (!bIsUserRespawn) {
            pScriptModifier = (Script pScriptCopy) -> {
                if (pScriptCopy != null && pScriptCopy.GetTemplate() != null) {
                    if (pScriptCopy.dwField == dwField) {
                        this.bIsUserRespawn = true;
                    }
                    pScriptCopy.dwField = dwField;
                    pScriptCopy.sFieldName = ScriptTemplateMap.GetFieldName(dwField);
                    pHistory = pScriptCopy.pHistory;
                    pTemplate = pScriptCopy.pTemplate;
                    nStrPaddingIndex = pScriptCopy.CurrentLinePadding();
                    if (pScriptCopy.GetTemplate().IsFieldTemplate()) {
                        this.bIsFieldScriptTemplate = true;
                        MessageHistory pMsgHistory = pScriptCopy.GetMessageHistory();
                        if (pMsgHistory != null && pMsgHistory.GetOutput() != null) {
                            if (pMsgHistory.GetOutput().contains("dwField = pField.dwField;")) {
                                this.bIsSkippedFieldTransfer = true;
                                return;
                            }
                        }
                    } else if (pScriptCopy.GetTemplate().IsQuestTemplate()) {
                        MessageHistory pMsgHistory = pScriptCopy.GetMessageHistory();
                        if (pMsgHistory != null && pMsgHistory.GetOutput() != null) {
                            if (pMsgHistory.GetOutput().contains("QuestRecordSetState")) {
                                this.bIsSkippedFieldTransfer = true;
                                return;
                            }
                        }
                    }
                }
            };
        }
        return !bIsSkippedFieldTransfer ? pScriptModifier : null;
    }

    @Override
    public ScriptModifier SetScriptUserInputResult() {
        if (!bIsUserRespawn) {
            ScriptModifier pScriptModifier = (Script pScriptMod) -> {
                if (pScriptMod.pTemplate != null) {
                    if (pScriptMod.GetNestedBlockHistory() != null) {
                        if (pScriptMod.aHistoryNestedBlock.size() == 1) {
                            pScriptMod.GetNestedBlockHistory().SetNestedBlockResult(dwField);
                        }
                    }
                }
            };
            return pScriptModifier;
        }
        return null;
    }

    @Override
    public ScriptWriteRequest CreateScriptWriteRequest() {
        if (pTemplate != null) {
            if (!bIsUserRespawn) {
                String sOutput = "pTarget.OnTransferField(" + dwField + ", " + nPortal + "); //to: \"" + ScriptTemplateMap.GetFieldName(dwField) + "\"";
                if (bIsSkippedFieldTransfer || bIsFieldScriptTemplate) {
                    sOutput = "//" + sOutput;
                }
                return new ScriptWriteRequest(dwField, sOutput, pTemplate, new LinkedList<>(), nStrPaddingIndex);
            }
        }
        return null;
    }

    @Override
    public ScriptModifier CreateNewScriptTemplateResetNotPersist() {
        ScriptModifier pScriptModifier = (Script pScript) -> {
            pScript.CreateNewTemplate(new ScriptWriteRequest(dwField, pFieldTemplate), true);
            ScriptFieldObjMap.ResetMap();
        };
        return pScriptModifier;
    }

    @Override
    public boolean IsScriptResetNotPersist() {
        return true;
    }
}
