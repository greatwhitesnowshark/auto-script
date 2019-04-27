/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package packet.loopback;

import java.util.LinkedList;
import java.util.List;
import message.MessageType;
import packet.LoopbackCode;
import packet.PacketWriteRequest;
import script.Script;
import script.ScriptModifier;
import script.ScriptTemplateMap;
import script.ScriptWriteRequest;
import template.NpcTemplate;

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
    
    public ScriptMessage(int nSpeakerTypeID, int nSpeakerTemplateID, int nMsgType, short bParam, byte bSpecificSpeaker, byte eColor, String sText, byte bPrev, byte bNext, int tWait) {
        super(LoopbackCode.ScriptMessage.nCode);
        this.nSpeakerTypeID = nSpeakerTypeID;
        this.nSpeakerTemplateID = nSpeakerTemplateID;
        this.nMsgType = nMsgType;
        this.bParam = bParam;
        this.bSpecificSpeaker = bSpecificSpeaker;
        this.eColor = eColor;
        this.sText = sText;
        this.bPrev = bPrev;
        this.bNext = bNext;
        this.tWait = tWait;
    }
    
    public ScriptMessage(int nSpeakerTypeID, int nSpeakerTemplateID, int nMsgType, short bParam, byte bSpecificSpeaker, byte eColor, String[] aImage) {
        super(LoopbackCode.ScriptMessage.nCode);
        this.nSpeakerTypeID = nSpeakerTypeID;
        this.nSpeakerTemplateID = nSpeakerTemplateID;
        this.nMsgType = nMsgType;
        this.bParam = bParam;
        this.bSpecificSpeaker = bSpecificSpeaker;
        this.eColor = eColor;
        this.aImage = aImage;
    }
    
    public ScriptMessage(int nSpeakerTypeID, int nSpeakerTemplateID, int nMsgType, short bParam, byte bSpecificSpeaker, byte eColor, String sText) {
        super(LoopbackCode.ScriptMessage.nCode);
        this.nSpeakerTypeID = nSpeakerTypeID;
        this.nSpeakerTemplateID = nSpeakerTemplateID;
        this.nMsgType = nMsgType;
        this.bParam = bParam;
        this.bSpecificSpeaker = bSpecificSpeaker;
        this.eColor = eColor;
        this.sText = sText;
    }
    
    public ScriptMessage(int nSpeakerTypeID, int nSpeakerTemplateID, int nMsgType, short bParam, byte bSpecificSpeaker, byte eColor, String sText, String sDefaultText, int nMin, int nMax) {
        super(LoopbackCode.ScriptMessage.nCode);
        this.nSpeakerTypeID = nSpeakerTypeID;
        this.nSpeakerTemplateID = nSpeakerTemplateID;
        this.nMsgType = nMsgType;
        this.bParam = bParam;
        this.bSpecificSpeaker = bSpecificSpeaker;
        this.eColor = eColor;
        this.sText = sText;
        this.sDefaultText = sDefaultText;
        this.nMin = nMin;
        this.nMax = nMax;
    }
    
    public ScriptMessage(int nSpeakerTypeID, int nSpeakerTemplateID, int nMsgType, short bParam, byte bSpecificSpeaker, byte eColor, String sText, int nDefault, int nMin, int nMax) {
        super(LoopbackCode.ScriptMessage.nCode);
        this.nSpeakerTypeID = nSpeakerTypeID;
        this.nSpeakerTemplateID = nSpeakerTemplateID;
        this.nMsgType = nMsgType;
        this.bParam = bParam;
        this.bSpecificSpeaker = bSpecificSpeaker;
        this.eColor = eColor;
        this.sText = sText;
        this.nDefault = nDefault;
        this.nMin = nMin;
        this.nMax = nMax;
    }
    
    public ScriptMessage(int nSpeakerTypeID, int nSpeakerTemplateID, int nMsgType, short bParam, byte bSpecificSpeaker, byte eColor, String sTitle, String sProblemText, String sHintText, int nMin, int nMax, int tRemain) {
        super(LoopbackCode.ScriptMessage.nCode);
        this.nSpeakerTypeID = nSpeakerTypeID;
        this.nSpeakerTemplateID = nSpeakerTemplateID;
        this.nMsgType = nMsgType;
        this.bParam = bParam;
        this.bSpecificSpeaker = bSpecificSpeaker;
        this.eColor = eColor;
        this.sTitle = sTitle;
        this.sProblemText = sProblemText;
        this.sHintText = sHintText;
        this.nMin = nMin;
        this.nMax = nMax;
        this.tRemain = tRemain;
    }
    
    public ScriptMessage(int nSpeakerTypeID, int nSpeakerTemplateID, int nMsgType, short bParam, byte bSpecificSpeaker, byte eColor, int nQuizType, int dwQuizAnswer, int nQuizCorrect, int nQuizRemain, int tRemain) {
        super(LoopbackCode.ScriptMessage.nCode);
        this.nSpeakerTypeID = nSpeakerTypeID;
        this.nSpeakerTemplateID = nSpeakerTemplateID;
        this.nMsgType = nMsgType;
        this.bParam = bParam;
        this.bSpecificSpeaker = bSpecificSpeaker;
        this.eColor = eColor;
        this.nQuizType = nQuizType;
        this.dwQuizAnswer = dwQuizAnswer;
        this.nQuizCorrect = nQuizCorrect;
        this.nQuizRemain = nQuizRemain;
        this.tRemain = tRemain;
    }
    
    //AskIcQuiz
    public ScriptMessage(int nSpeakerTypeID, int nSpeakerTemplateID, int nMsgType, short bParam, byte bSpecificSpeaker, byte eColor, String sProblemText, String sHintText, int tRemain) {
        super(LoopbackCode.ScriptMessage.nCode);
        this.nSpeakerTypeID = nSpeakerTypeID;
        this.nSpeakerTemplateID = nSpeakerTemplateID;
        this.nMsgType = nMsgType;
        this.bParam = bParam;
        this.bSpecificSpeaker = bSpecificSpeaker;
        this.eColor = eColor;
        this.sProblemText = sProblemText;
        this.sHintText = sHintText;
        this.tRemain = tRemain;
    }
    
    //AskAvatar
    public ScriptMessage(int nSpeakerTypeID, int nSpeakerTemplateID, int nMsgType, short bParam, byte bSpecificSpeaker, byte eColor, String sText, byte bAngelicBuster, byte bZeroData, int[] aCode) {
        super(LoopbackCode.ScriptMessage.nCode);
        this.nSpeakerTypeID = nSpeakerTypeID;
        this.nSpeakerTemplateID = nSpeakerTemplateID;
        this.nMsgType = nMsgType;
        this.bParam = bParam;
        this.bSpecificSpeaker = bSpecificSpeaker;
        this.eColor = eColor;
        this.sText = sText;
        this.bAngelicBuster = bAngelicBuster;
        this.bZeroData = bZeroData;
        this.aCode = aCode;
    }
    
    //AskAndroid
    public ScriptMessage(int nSpeakerTypeID, int nSpeakerTemplateID, int nMsgType, short bParam, byte bSpecificSpeaker, byte eColor, String sText, int[] aCode) {
        super(LoopbackCode.ScriptMessage.nCode);
        this.nSpeakerTypeID = nSpeakerTypeID;
        this.nSpeakerTemplateID = nSpeakerTemplateID;
        this.nMsgType = nMsgType;
        this.bParam = bParam;
        this.bSpecificSpeaker = bSpecificSpeaker;
        this.eColor = eColor;
        this.sText = sText;
        this.aCode = aCode;
    }
    
    //AskBoxText
    public ScriptMessage(int nSpeakerTypeID, int nSpeakerTemplateID, int nMsgType, short bParam, byte bSpecificSpeaker, byte eColor, String sText, String sDefaultText, short nCol, short nLine) {
        super(LoopbackCode.ScriptMessage.nCode);
        this.nSpeakerTypeID = nSpeakerTypeID;
        this.nSpeakerTemplateID = nSpeakerTemplateID;
        this.nMsgType = nMsgType;
        this.bParam = bParam;
        this.bSpecificSpeaker = bSpecificSpeaker;
        this.eColor = eColor;
        this.sText = sText;
        this.sDefaultText = sDefaultText;
        this.nCol = nCol;
        this.nLine = nLine;
    }
    
    //AskSlideMenu
    public ScriptMessage(int nSpeakerTypeID, int nSpeakerTemplateID, int nMsgType, short bParam, byte bSpecificSpeaker, byte eColor, String sText, int nDlgType, int nDefaultSelect) {
        super(LoopbackCode.ScriptMessage.nCode);
        this.nSpeakerTypeID = nSpeakerTypeID;
        this.nSpeakerTemplateID = nSpeakerTemplateID;
        this.nMsgType = nMsgType;
        this.bParam = bParam;
        this.bSpecificSpeaker = bSpecificSpeaker;
        this.eColor = eColor;
        this.sText = sText;
        this.nDlgType = nDlgType;
        this.nDefaultSelect = nDefaultSelect;
    }
    
    //AskSelectMenu
    public ScriptMessage(int nSpeakerTypeID, int nSpeakerTemplateID, int nMsgType, short bParam, byte bSpecificSpeaker, byte eColor, int nDlgType, String[] aPath) {
        super(LoopbackCode.ScriptMessage.nCode);
        this.nSpeakerTypeID = nSpeakerTypeID;
        this.nSpeakerTemplateID = nSpeakerTemplateID;
        this.nMsgType = nMsgType;
        this.bParam = bParam;
        this.bSpecificSpeaker = bSpecificSpeaker;
        this.eColor = eColor;
        this.nDlgType = nDlgType;
        this.aPath = aPath;
    }
    
    //AskIllustration
    public ScriptMessage(int nSpeakerTypeID, int nSpeakerTemplateID, int nMsgType, short bParam, byte bSpecificSpeaker, byte eColor, String sText, byte bPrev, byte bNext, int nNpcID, int nFaceIndex, int bIsLeft, int nFaceIndex2) {
        super(LoopbackCode.ScriptMessage.nCode);
        this.nSpeakerTypeID = nSpeakerTypeID;
        this.nSpeakerTemplateID = nSpeakerTemplateID;
        this.nMsgType = nMsgType;
        this.bParam = bParam;
        this.bSpecificSpeaker = bSpecificSpeaker;
        this.eColor = eColor;
        this.sText = sText;
        this.bPrev = bPrev;
        this.bNext = bNext;
        this.nNpcID = nNpcID;
        this.nFaceIndex = nFaceIndex;
        this.bIsLeft = bIsLeft;
        this.nFaceIndex2 = nFaceIndex2;
    }
    
    //AskIllustration
    public ScriptMessage(int nSpeakerTypeID, int nSpeakerTemplateID, int nMsgType, short bParam, byte bSpecificSpeaker, byte eColor, String sText, short nBgIdx, String sDefaultText, short nCol, short nLine, short nFontSize) {
        super(LoopbackCode.ScriptMessage.nCode);
        this.nSpeakerTypeID = nSpeakerTypeID;
        this.nSpeakerTemplateID = nSpeakerTemplateID;
        this.nMsgType = nMsgType;
        this.bParam = bParam;
        this.bSpecificSpeaker = bSpecificSpeaker;
        this.eColor = eColor;
        this.sText = sText;
        this.nBgIdx = nBgIdx;
        this.sDefaultText = sDefaultText;
        this.nCol = nCol;
        this.nLine = nLine;
        this.nFontSize = nFontSize;
    }
    
    @Override
    public ScriptModifier CreateScriptModifier() {
        ScriptModifier pScriptModifier = (Script pScript) -> {
            if (pScript.pTemplate == null) {
                NpcTemplate pNpcTemplate = ScriptTemplateMap.GetNpcTemplate(nSpeakerTemplateID);
                if (pNpcTemplate != null) {
                    pScript.CreateNewTemplate(new ScriptWriteRequest(pNpcTemplate.dwTemplateID, pNpcTemplate), true);
                }
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
        ScriptModifier pScriptModifier = (Script pScriptCopy) -> {
            if (pScriptCopy.pTemplate != null) {
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
            String sOutput = "";
            List<String> lConditionalText = new LinkedList<>();
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
                    sText = sText.replace("\r\n", "\\r\\n");
                    sOutput += "nSel = self.AskMenu(\"" + sText + "\"";
                    String[] aSelection = sText.replace("\r\n", "@").split("@");
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
            return new ScriptWriteRequest(dwField, sOutput, pTemplate, lConditionalText, nStrPaddingIndex);
        }
        return null;
    }
}
