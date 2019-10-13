/*
 * To change this license opcode, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package packet.loopback;

import game.network.InPacket;
import game.scripting.ScriptSysFunc.InGameDirectionEvent;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.LinkedList;
import packet.opcode.LoopbackCode;
import packet.PacketWriteRequest;
import script.Script;
import script.ScriptModifier;
import script.ScriptTemplateMap;
import script.ScriptWriteRequest;
import template.FieldTemplate;

/**
 *
 * @author Sharky
 */
public class UserInGameDirectionEvent extends PacketWriteRequest {
    
    private boolean bNotOrigin = false, bBack = false, bVanshee = false, bMonologueEnd = false, bStayModal = false;
    private byte nUIType = 0;
    private short nAlign = 0;
    private int nType;
    private int nAction, tDuration, tDelay, x, y, v17, dwNpcID, nForcedInput, nAct, nRequestCount, nTime, nPixelPerSec, nScale, nTimePos,
            nFaceItemID, nUpdateSpeedTime, nDecTic, nDir, nSpeed, nForcedFlip;
    private int[] aItem = new int[0];
    private String sType, sEffectUOL = "", sPattern = "", sMonologue = "";
    
    public UserInGameDirectionEvent(InPacket iPacket) {
        super(LoopbackCode.UserInGameDirectionEvent.nCode);
        this.nType = iPacket.DecodeByte();
        this.sType = GetDirectionEventName(nType);
        switch (nType) {
            case InGameDirectionEvent.ForcedAction:
                this.nAction = iPacket.DecodeInt();
                this.tDuration = iPacket.DecodeInt();
                break;
            case InGameDirectionEvent.Delay:
                this.tDelay = iPacket.DecodeInt();
                break;
            case InGameDirectionEvent.EffectPlay:
                this.sEffectUOL = iPacket.DecodeString();
                this.tDuration = iPacket.DecodeInt();
                this.x = iPacket.DecodeInt();
                this.y = iPacket.DecodeInt();
                byte bEncode = iPacket.DecodeByte();
                this.v17 = 0;
                if (bEncode > 0) {
                    this.v17 = iPacket.DecodeInt();
                }
                bEncode = iPacket.DecodeByte();
                this.dwNpcID = 0;
                if (bEncode > 0) {
                    this.dwNpcID = iPacket.DecodeInt();
                }
                this.bNotOrigin = iPacket.CountRemaining() > 0 && iPacket.DecodeByte() == 1;
                break;
            case InGameDirectionEvent.ForcedInput:
                this.nForcedInput = iPacket.DecodeInt();
                break;
            case InGameDirectionEvent.PatternInputRequest:
                this.sPattern = iPacket.DecodeString();
                this.nAct = iPacket.DecodeInt();
                this.nRequestCount = iPacket.DecodeInt();
                this.nTime = iPacket.DecodeInt();
                break;
            case InGameDirectionEvent.CameraMove:
                this.bBack = iPacket.DecodeByte() == 1;
                this.nPixelPerSec = iPacket.DecodeInt();
                if (!this.bBack) {
                    this.x = iPacket.DecodeInt();
                    this.y = iPacket.DecodeInt();
                }
                break;
            case InGameDirectionEvent.CameraOnCharacter:
                this.dwNpcID = iPacket.DecodeInt();
                break;
            case InGameDirectionEvent.CameraZoom:
                this.nTime = iPacket.DecodeInt();
                this.nScale = iPacket.DecodeInt();
                this.nTimePos = iPacket.DecodeInt();
                this.x = iPacket.DecodeInt();
                this.y = iPacket.DecodeInt();
                break;
            case InGameDirectionEvent.CameraReleaseFromUserPoint:
            case InGameDirectionEvent.RemoveAdditionalEffect:
            case InGameDirectionEvent.CloseUI:
                break;
            case InGameDirectionEvent.VansheeMode:
                this.bVanshee = iPacket.DecodeByte() == 1;
                break;
            case InGameDirectionEvent.FaceOff:
                this.nFaceItemID = iPacket.DecodeInt();
                break;
            case InGameDirectionEvent.Monologue:
                this.sMonologue = iPacket.DecodeString();
                this.bMonologueEnd = iPacket.DecodeByte() == 1;
                break;
            case InGameDirectionEvent.MonologueScroll:
                sMonologue = iPacket.DecodeString();
                this.bStayModal = iPacket.DecodeByte() == 1;
                this.nAlign = iPacket.DecodeShort();
                this.nUpdateSpeedTime = iPacket.DecodeInt();
                this.nDecTic = iPacket.DecodeInt();
                break;
            case InGameDirectionEvent.AvatarLookSet:
                byte nSize = iPacket.DecodeByte();
                this.aItem = new int[nSize];
                for (int i = 0; i < nSize; i++) {
                    this.aItem[i] = iPacket.DecodeInt();
                }
                break;
            case InGameDirectionEvent.ForcedMove:
                this.nDir = iPacket.DecodeInt();
                this.nSpeed = iPacket.DecodeInt();
                break;
            case InGameDirectionEvent.ForcedFlip:
                this.nForcedFlip = iPacket.DecodeInt();
                break;
            case InGameDirectionEvent.InputUI:
                this.nUIType = iPacket.DecodeByte();
                break;
        }
    }

    @Override
    public ScriptModifier CreateNewScriptTemplate() {
        ScriptModifier pScriptModifier = (Script pScript) -> {
            if (pScript.pTemplate == null) {
                FieldTemplate pFieldTemplate = ScriptTemplateMap.GetUserEnterTemplate(pScript.dwField);
                if (pFieldTemplate != null && !pFieldTemplate.sScript.isEmpty()) {
                    pScript.CreateNewTemplate(new ScriptWriteRequest(pFieldTemplate.dwTemplateID, pFieldTemplate), true);
                }
            }
        };
        return pScriptModifier;
    }

    @Override
    public ScriptModifier CreateScriptTemplateCopy() {
        ScriptModifier pScriptModifier = (Script pScriptCopy) -> {
            if (pScriptCopy.pTemplate != null) {
                dwField = pScriptCopy.dwField;
                pTemplate = pScriptCopy.pTemplate;
                pHistory = pScriptCopy.pHistory;
                nStrPaddingIndex = pScriptCopy.CurrentLinePadding();
            }
        };
        return pScriptModifier;
    }
    
    @Override
    public ScriptWriteRequest CreateScriptWriteRequest() {
        if (pTemplate != null) {
            String sOutput = "self.OnUserInGameDirectionEvent(InGameDirectionEvent." + sType + ", ";
            switch (nType) {
                case InGameDirectionEvent.ForcedAction:
                    sOutput += (nAction + ", " + tDuration + ");");
                    break;
                case InGameDirectionEvent.Delay:
                    sOutput += (tDelay + ");");
                    break;
                case InGameDirectionEvent.EffectPlay:
                    sOutput += ("\"" + sEffectUOL + "\", " + tDuration + ", " + x + ", " + y + ", " + v17 + ", " + dwNpcID + ", " + (bNotOrigin ? "true" : "false") + ");");
                    break;
                case InGameDirectionEvent.ForcedInput:
                    sOutput += (nForcedInput + ");");
                    break;
                case InGameDirectionEvent.PatternInputRequest:
                    sOutput += ("\"" + sPattern + "\", " + nAct + ", " + nRequestCount + ", " + nTime + ");");
                    break;
                case InGameDirectionEvent.CameraMove:
                    if (!bBack) {
                        sOutput += ("false, " + nPixelPerSec + ", " + x + ", " + y + ");");
                    } else {
                        sOutput += ("true, " + nPixelPerSec + ");");
                    }
                    break;
                case InGameDirectionEvent.CameraOnCharacter:
                    sOutput += (dwNpcID + ");");
                    break;
                case InGameDirectionEvent.CameraZoom:
                    sOutput += (nTime + ", " + nScale + ", " + nTimePos + ", " + x + ", " + y + ");");
                    break;
                case InGameDirectionEvent.CameraReleaseFromUserPoint:
                case InGameDirectionEvent.RemoveAdditionalEffect:
                case InGameDirectionEvent.CloseUI:
                    sOutput += (");");
                    sOutput = sOutput.replace(", );", ");");
                    break;
                case InGameDirectionEvent.VansheeMode:
                    sOutput += ((bVanshee ? "true);" : "false);"));
                    break;
                case InGameDirectionEvent.FaceOff:
                    sOutput += (nFaceItemID + ");");
                    break;
                case InGameDirectionEvent.Monologue:
                    sOutput += ("\"" + sMonologue + "\", " + (bMonologueEnd ? "true" : "false") + ");");
                    break;
                case InGameDirectionEvent.MonologueScroll:
                    sOutput += ("\"" + sMonologue + "\", " + (bStayModal ? "true" : "false") + ", " + nAlign + ", " + nUpdateSpeedTime + ", " + nDecTic + ");");
                    break;
                case InGameDirectionEvent.AvatarLookSet:
                    for (int i = 0; i < aItem.length; i++) {
                        sOutput += aItem[i];
                        sOutput += (i != (aItem.length - 1)) ? ", " : ");";
                    }
                    break;
                case InGameDirectionEvent.ForcedMove:
                    sOutput += (nDir + ", " + nSpeed + ");");
                    break;
                case InGameDirectionEvent.ForcedFlip:
                    sOutput += (nForcedFlip + ");");
                    break;
                case InGameDirectionEvent.InputUI:
                    sOutput += (nUIType + ");");
                    break;
            }
            return new ScriptWriteRequest(dwField, sOutput, pTemplate, new LinkedList<>(), nStrPaddingIndex);
        }
        return null;
    }
    
    public static String GetDirectionEventName(int nType) {
        try {
            for (Field pField : InGameDirectionEvent.class.getDeclaredFields()) {
                int nMod = pField.getModifiers();
                if (Modifier.isStatic(nMod) && Modifier.isFinal(nMod)) {
                    if (pField.getInt(null) == nType) {
                        return pField.getName();
                    }
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return String.format("Unknown-FieldScriptType-Exception-Thrown[%d]", nType);
    }
}
