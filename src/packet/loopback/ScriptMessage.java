/*
 * To change this license opcode, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package packet.loopback;

import game.network.InPacket;
import game.scripting.ScriptMan;
import game.scripting.ScriptMan.MessageType;
import java.util.LinkedList;
import java.util.List;

import game.scripting.ScriptSysFunc;
import packet.opcode.LoopbackCode;
import packet.PacketWriteRequest;
import script.Script;
import script.ScriptModifier;
import script.ScriptWriteRequest;
import util.Logger;

/**
 *
 * @author Sharky
 */
public class ScriptMessage extends PacketWriteRequest {

    private int nSpeakerTypeID, nSpeakerTemplateID, nMsgType, tWait, nMin, nMax, nDefault, nQuizResult, tRemain,
            nQuizType, dwQuizAnswer, nQuizCorrect, nQuizRemain, nDlgType, nDefaultSelect, nNpcID, nFaceIndex, nFaceIndex2, bIsLeft;
    private short bParam, nCol, nLine, nBgIdx, nFontSize = 0;
    private byte bSpecificSpeaker, eColor, bPrev, bNext, bAngelicBuster, bZeroData;
    private String sText = "", sDefaultText = "", sTitle = "", sProblemText = "", sHintText = "";
    private int[] aCode = new int[0];
    private String[] aImage = new String[0], aPath = new String[0];

    public ScriptMessage(InPacket iPacket) {
        super(LoopbackCode.ScriptMessage.nCode);
        try {
            this.nSpeakerTypeID = iPacket.DecodeByte();
            this.nSpeakerTemplateID = iPacket.DecodeInt();
            this.bSpecificSpeaker = iPacket.DecodeByte();
            if (this.bSpecificSpeaker == 1) {
                this.nSpeakerTemplateID = iPacket.DecodeInt();
            }
            this.nMsgType = iPacket.DecodeByte();
            this.bParam = iPacket.DecodeShort();
            this.eColor = iPacket.DecodeByte();

            //todo:: double-check and update this if needed, for the cases below
            switch (nMsgType) {
                case MessageType.Say:
                    if ((this.bParam & ScriptSysFunc.SpeakerTypeID.NpcReplayedByNpc) > 0) {
                        this.nSpeakerTemplateID = iPacket.DecodeInt();
                    }
                    this.sText = iPacket.DecodeString();
                    this.bPrev = iPacket.DecodeByte();
                    this.bNext = iPacket.DecodeByte();
                    this.tWait = iPacket.DecodeInt();
                    break;

                case MessageType.SayImage:
                    byte nSize = iPacket.DecodeByte();
                    this.aImage = new String[nSize];
                    for (byte i = 0; i < nSize; i++) {
                        this.aImage[i] = iPacket.DecodeString();
                    }
                    break;

                case MessageType.AskYesNo:

                case MessageType.AskMenu:

                case MessageType.AskAccept:
                    if ((this.bParam & ScriptSysFunc.SpeakerTypeID.NpcReplayedByNpc) > 0) {
                        this.nSpeakerTemplateID = iPacket.DecodeInt();
                    }
                    this.sText = iPacket.DecodeString();
                    break;

                case MessageType.AskText:
                    if ((bParam & ScriptSysFunc.SpeakerTypeID.NpcReplayedByNpc) > 0) {
                        this.nSpeakerTemplateID = iPacket.DecodeInt();
                    }
                    this.sText = iPacket.DecodeString();
                    this.sDefaultText = iPacket.DecodeString();
                    this.nMin = iPacket.DecodeShort();
                    this.nMax = iPacket.DecodeShort();
                    break;

                case MessageType.AskNumber:
                    this.sText = iPacket.DecodeString();
                    this.nDefault = iPacket.DecodeInt();
                    this.nMin = iPacket.DecodeInt();
                    this.nMax = iPacket.DecodeInt();
                    break;

                case MessageType.AskQuiz:
                    this.nQuizResult = iPacket.DecodeByte();
                    if (this.nQuizResult == 0) {//InitialQuizResult.Request
                        this.sTitle = iPacket.DecodeString();
                        this.sProblemText = iPacket.DecodeString();
                        this.sHintText = iPacket.DecodeString();
                        this.nMin = iPacket.DecodeInt();
                        this.nMax = iPacket.DecodeInt();
                        this.tRemain = iPacket.DecodeInt();
                    }
                    break;

                case MessageType.AskSpeedQuiz:
                    this.nQuizResult = iPacket.DecodeByte();
                    if (this.nQuizResult == 0) {// InitialQuizResult.Request
                        this.nQuizType = iPacket.DecodeInt();
                        this.dwQuizAnswer = iPacket.DecodeInt();
                        this.nQuizCorrect = iPacket.DecodeInt();
                        this.nQuizRemain = iPacket.DecodeInt();
                        this.tRemain = iPacket.DecodeInt();
                    }
                    break;

                case MessageType.AskIcQuiz:
                    this.nQuizResult = iPacket.DecodeByte();
                    if (nQuizResult == 0) {// InitialQuizResult.Request
                        this.sProblemText = iPacket.DecodeString();
                        this.sHintText = iPacket.DecodeString();
                        this.tRemain = iPacket.DecodeInt();
                    }
                    break;

                case MessageType.AskAvatar:
                case MessageType.Unknown2:
                    this.bAngelicBuster = iPacket.DecodeByte();
                    this.bZeroData = iPacket.DecodeByte();
                    this.sText = iPacket.DecodeString();
                    nSize = iPacket.DecodeByte();
                    this.aCode = new int[nSize];
                    for (byte i = 0; i < nSize; i++) {
                        this.aCode[i] = iPacket.DecodeInt();
                    }
                    break;

                case MessageType.AskAndroid:
                case MessageType.Unknown:
                    this.sText = iPacket.DecodeString();
                    nSize = iPacket.DecodeByte();
                    this.aCode = new int[nSize];
                    for (byte i = 0; i < nSize; i++)
                    {
                        this.aCode[i] = iPacket.DecodeInt();
                    }
                    break;

                case MessageType.AskPet: //Todo
                case MessageType.AskPetAll: //Todo
                    this.sText = iPacket.DecodeString();
                    break;

                case MessageType.AskBoxText:
                    if ((this.bParam & ScriptSysFunc.SpeakerTypeID.NpcReplayedByNpc) > 0) {
                        this.nSpeakerTemplateID = iPacket.DecodeInt();
                    }
                    this.sText = iPacket.DecodeString();
                    this.sDefaultText = iPacket.DecodeString();
                    this.nCol = iPacket.DecodeShort();
                    this.nLine = iPacket.DecodeShort();
                    break;

                case MessageType.AskSlideMenu:
                    this.nDlgType = iPacket.DecodeInt();
                    if (this.nDlgType == 1)
                    {
                        iPacket.DecodeInt();
                        iPacket.DecodeString();
                        //EncodeInt / EncodeString
                    }
                    this.nDefaultSelect = iPacket.DecodeInt();
                    this.sText = iPacket.DecodeString();
                    break;

                case MessageType.AskSelectMenu:
                    this.nDlgType = iPacket.DecodeInt();
                    int nIntSize = iPacket.DecodeInt();
                    this.aPath = new String[nIntSize];
                    for (int i = 0; i < nIntSize; i++) {
                        this.aPath[i] = iPacket.DecodeString();
                    }
                    break;

                case MessageType.SayIllustration:
                case MessageType.SayDualIllustration:
                case MessageType.AskYesNoIllustration:
                case MessageType.AskAcceptIllustration:
                case MessageType.AskYesNoDualIllustration:
                case MessageType.AskAcceptDualIllustration:
                case MessageType.AskMenuIllustration:
                case MessageType.AskMenuDualIllustration:
                    if ((this.bParam & ScriptSysFunc.SpeakerTypeID.NpcReplayedByNpc) > 0) {
                        this.nSpeakerTemplateID = iPacket.DecodeInt();
                    }
                    this.sText = iPacket.DecodeString();
                    this.bPrev = iPacket.DecodeByte();
                    this.bNext = iPacket.DecodeByte();
                    this.nNpcID = iPacket.DecodeInt();
                    this.nFaceIndex = iPacket.DecodeInt();
                    this.bIsLeft = iPacket.DecodeInt();
                    if (iPacket.CountRemaining() >= 4) {
                        this.nFaceIndex2 = iPacket.DecodeInt();
                    }
                    break;

                case MessageType.AskBoxTextBgImg:
                    this.nBgIdx = iPacket.DecodeShort();
                    this.sText = iPacket.DecodeString();
                    this.sDefaultText = iPacket.DecodeString();
                    this.nCol = iPacket.DecodeShort();
                    this.nLine = iPacket.DecodeShort();
                    this.nFontSize = iPacket.DecodeShort();
                    break;

                case MessageType.AskMixHair:
                    this.bAngelicBuster = iPacket.DecodeByte();
                    this.bZeroData = iPacket.DecodeByte();
                    iPacket.DecodeByte();
                    this.sText = iPacket.DecodeString();
                    nSize = iPacket.DecodeByte();
                    this.aCode = new int[nSize];
                    for (int i = 0; i < nSize; i++) {
                        this.aCode[i] = iPacket.DecodeInt();
                    }
                    break;

                case MessageType.AskMixHairZero:
                    iPacket.DecodeByte();
                    this.sText = iPacket.DecodeString();
                    nSize = iPacket.DecodeByte();
                    this.aCode = new int[nSize];
                    for (int i = 0; i < nSize; i++) {
                        this.aCode[i] = iPacket.DecodeInt();
                    }
                    break;

                case MessageType.AskAngelicBuster:
                case MessageType.AskAvatarZero:
                case MessageType.Unknown3:
                case MessageType.Unknown4:
                case MessageType.AskWeaponBox:
                case MessageType.AskCustomMixHair:
                case MessageType.AskCustomMixHairAndProb:
                case MessageType.AskMixHairNew:
                case MessageType.AskMixHairNewZero:
                case MessageType.AskScreenShinningStarMsg:
                case MessageType.AskNumberKeypad:
                case MessageType.SpinoffGuitarRhythmGame:
                case MessageType.AskGhostParkEnterUI:
                case MessageType.CameraMsg:
                case MessageType.SlidePuzzle:
                case MessageType.Disguise:
                case MessageType.NeedClientResponse:
            }
        } catch (Exception eee) {
            Logger.LogError("Exception thrown at ScriptMessage. nSpeakerTypeID[" + nSpeakerTypeID + "] nSpeakerTemplateID[" + nSpeakerTemplateID + "] nMsgType[" + nMsgType + "] bParam[" + bParam + "]");
            eee.printStackTrace();
        }
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

    //todo:: finish the cases below, some are incomplete
    @Override
    public ScriptWriteRequest CreateScriptWriteRequest() {
        ScriptWriteRequest pWriteRequest = null;
        if (pTemplate != null && !sText.isEmpty()) {
            List<String> lConditionalText = new LinkedList<>();
            String sOutput = "";
            sText = sText.replace("\r\n", "\\r\\n");
            switch (nMsgType) {
                case MessageType.Say:
                    sOutput = bParam == 2 || bParam == 3 ? ("self.SayUser(\"" + sText + "\", true") : ("self.Say(\"" + sText + "\", true");
                    break;

                case MessageType.SayImage:
                    String sImage = "";
                    for (int i = 0; i < aImage.length; i++) {
                        sImage += aImage[i];
                        if (i < aImage.length - 1) {
                            sImage += ":.  ";
                        }
                        sOutput += ("self.SayImage(\"" + sImage + "\"");
                    }
                    break;

                case MessageType.AskYesNo:
                    sOutput += (bParam == 2 || bParam == 3) ? "nRet = self.AskYesNoUser(\"" + sText + "\"" : "nRet = self.AskYesNo(\"" + sText + "\"";
                    lConditionalText.add("if (nRet == 0) {");
                    lConditionalText.add("} else if (nRet == 1) {");
                    lConditionalText.add("}");
                    break;

                case MessageType.AskMenu:
                    sOutput += "nSel = self.AskMenu(\"" + sText + "\"";
                    String[] aSelection = sText.replace("\\r\\n", "@").split("@");
                    int i = 0;
                    for (String s : aSelection) {
                        String sSelection = "n/a";
                        if (s.contains("#L")) {
                            String[] asOption = s.replace("#L", "@").split("@");
                            String sOption = asOption[1];
                            for (int a = 2; a < asOption.length; a++) {
                                sOption = String.join("#L", sOption, asOption[a]);
                            }
                            sSelection = sOption.contains("#") ? sOption.substring(0, sOption.indexOf('#')) : "n/a";
                        }
                        if (!sSelection.equals("n/a")) {
                            if (i > 0) {
                                lConditionalText.set(lConditionalText.size() - 1, ("} else if (nSel == " + sSelection + ") {"));
                            } else {
                                lConditionalText.add(("if (nSel == " + sSelection + ") {"));
                            }
                            lConditionalText.add("}");
                            i++;
                        }
                    }
                    break;

                case MessageType.AskAccept:
                    sOutput += "nRet = self.AskAccept(\"" + sText + "\"";
                    lConditionalText.add("if (nRet == 0) {");
                    lConditionalText.add("} else if (nRet == 1) {");
                    lConditionalText.add("}");
                    break;

                case MessageType.AskText:
                    sOutput += "sInput = self.AskText(\"" + sText + "\", \"Type answer here\", 1, 99";
                    lConditionalText.add("if (sInput == \"\") {");
                    lConditionalText.add("} else {");
                    lConditionalText.add("}");
                    break;

                case MessageType.AskNumber:
                    sOutput += "nInput = self.AskNumber(\"" + sText + "\", 0, 0, 254";
                    lConditionalText.add("nResult = -1;");
                    lConditionalText.add("if (nInput == nResult) {");
                    lConditionalText.add("} else {");
                    lConditionalText.add("}");
                    break;

                case MessageType.AskQuiz:
                    sOutput += "nInput = self.AskQuiz(\"" + sText + "\", \"" + sTitle + "\", \"" + sProblemText + "\", \"" + sHintText + "\", " + nMin + ", " + nMax + ", " + tRemain + "";
                    lConditionalText.add("if (nInput ==  /*[INSERT ANSWER]*/-1) {");
                    lConditionalText.add("} else {");
                    lConditionalText.add("}");
                    break;

                case MessageType.AskSpeedQuiz:
                    sOutput += "nInput = self.AskSpeedQuiz(\"" + sText + "\", " + nQuizType + ", " + dwQuizAnswer + ", " + nQuizCorrect + ", " + nQuizRemain + ", " + tRemain + "";
                    lConditionalText.add("if (nInput == /*[INSERT ANSWER]*/-1) {");
                    lConditionalText.add("} else {");
                    lConditionalText.add("}");
                    break;

                case MessageType.AskAvatar:
                case MessageType.Unknown2:
                    String sCode = "aCode = [";
                    for (int nCode : aCode) {
                        sCode += nCode;
                        if (nCode != aCode[aCode.length - 1]) {
                            sCode += ", ";
                        }
                    }
                    sCode += "];\r\n";
                    sOutput += "nSel = self.AskAvatar(\"" + sText + "\", " + sCode;
                    break;

                case MessageType.AskSlideMenu:
                    sOutput += "nSel = script.AskSlideMenu(\"" + sText + "\", " + nDefaultSelect + ", " + nDlgType;
                    break;

                case MessageType.AskMixHair:
                case MessageType.AskMixHairZero:
                case MessageType.AskBoxTextBgImg:
                case MessageType.SayIllustration:
                case MessageType.SayDualIllustration:
                case MessageType.AskYesNoIllustration:
                case MessageType.AskAcceptIllustration:
                case MessageType.AskYesNoDualIllustration:
                case MessageType.AskAcceptDualIllustration:
                case MessageType.AskMenuIllustration:
                case MessageType.AskMenuDualIllustration:
                case MessageType.AskSelectMenu:
                case MessageType.AskPet:
                case MessageType.AskPetAll:
                case MessageType.AskBoxText:
                case MessageType.AskAndroid:
                case MessageType.Unknown:
                case MessageType.AskIcQuiz:
                default:
                    return null;
            }
            sOutput += (", " + nSpeakerTemplateID + ");");
            pWriteRequest = new ScriptWriteRequest(dwField, sOutput, pTemplate, lConditionalText, nStrPaddingIndex);
        }
        return pWriteRequest;
    }
}
