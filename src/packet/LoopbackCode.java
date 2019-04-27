/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package packet;

import packet.inputstream.PacketInputStream;
import packet.loopback.UserTalk;
import java.io.IOException;
import java.util.Arrays;
import message.InGameDirectionEventType;
import message.MessageType;
import message.QuestResultType;
import message.UserEffectLocalType;
import packet.loopback.*;
import util.Logger;

/**
 *
 * @author Sharky
 */
public enum LoopbackCode {
    
    Message(108,
        (pStream) -> {
            int nType = pStream.readByte();
            if (nType == 1) {//QuestRecord
                int nQuestID = pStream.ReadInt();
                byte nQuestRecordStatus = pStream.readByte();
                String sRecord = "";
                boolean bAutoComplete = false;
                long tTimestamp = 0;
                switch (nQuestRecordStatus) {
                    case 0://None
                        bAutoComplete = pStream.readBoolean();
                        break;
                    case 1://Perform
                        sRecord = pStream.ReadString(true);
                        break;
                    case 2://Complete
                        tTimestamp = pStream.ReadLong();
                        break;
                }
                return new Message(nType, nQuestID, nQuestRecordStatus, sRecord, bAutoComplete, tTimestamp);
            }
            return null;
        }
    ),
    SetField(501,
        (pStream) -> {
            int dwField;
            byte nPortal = 0;
            pStream.ReadInt();
            pStream.readByte();
            pStream.ReadInt();
            pStream.readByte();
            pStream.ReadInt();
            pStream.readByte();
            pStream.ReadInt();
            pStream.ReadInt();
            boolean bEncode = pStream.readBoolean();
            if (!bEncode) {
                pStream.ReadShort();
                pStream.readByte();
                dwField = pStream.ReadInt();
                nPortal = pStream.readByte();
            } else {
                pStream.skip(112);
                byte nSP = pStream.readByte();
                for (int i = 0; i < nSP; i++) {
                    pStream.skip(5);
                }
                pStream.skip(20);
                dwField = pStream.ReadInt();
            }
            return new SetField(dwField, nPortal);
        }
    ),
    InGameCurNodeEventEnd(554,
        (pStream) -> {
            return pStream.readByte() == 1 ? new InGameCurNodeEventEnd() : null;
        }
    ),
    UserTalk(616, 
        (pStream) -> {
            pStream.readByte();
            pStream.ReadInt();
            String sMsg = pStream.ReadString(true);
            return new UserTalk(sMsg);
        }
    ),
    UserEffectLocal(807,
        (pStream) -> {
            byte nType = pStream.readByte();
            boolean bFlip;
            int nRange, nNameHeight;
            String sMsg;
            if (nType == UserEffectLocalType.Reserved) {
                bFlip = pStream.readBoolean();
                nRange = pStream.ReadInt();
                nNameHeight = pStream.ReadInt();
                sMsg = pStream.ReadString(true);
                return new UserEffectLocal(bFlip, nRange, nNameHeight, sMsg);
            } else if (nType == UserEffectLocalType.AvatarOriented) {
                sMsg = pStream.ReadString(true);
                return new UserEffectLocal(sMsg);
            }
            return null;
        }
    ),
    UserQuestResult(812,
        (pStream) -> {
            byte nResult = pStream.readByte();
            int nQuestID = 0, dwNpcTemplateID = 0, nNextQuestID = 0;
            byte bNavigation = 0, bQuestName = 0;
            int[] aTimerQuestID = null, aTimeRemaining = null;
            switch (nResult) {
                case QuestResultType.Success:
                    nQuestID = pStream.ReadInt();
                    dwNpcTemplateID = pStream.ReadInt();
                    nNextQuestID = pStream.ReadInt();
                    bNavigation = pStream.readByte();
                    break;
                case QuestResultType.FailedInventory:
                    nQuestID = pStream.ReadInt();
                    bQuestName = pStream.readByte();
                    break;
                case QuestResultType.StartQuestTimer:
                case QuestResultType.StartTimeKeepQuestTimer:
                    short nSize = pStream.ReadShort();
                    aTimeRemaining = new int[nSize];
                    aTimerQuestID = new int[nSize];
                    for (int i = 0; i < nSize; i++) {
                        aTimerQuestID[i] = pStream.ReadInt();
                        aTimeRemaining[i] = pStream.ReadInt();
                    }
                    break;
                case QuestResultType.EndQuestTimer:
                case QuestResultType.EndTimeKeepQuestTimer:
                    nSize = pStream.ReadShort();
                    aTimerQuestID = new int[nSize];
                    for (int i = 0; i < nSize; i++) {
                        aTimerQuestID[i] = pStream.ReadInt();
                    }
                    break;
                case QuestResultType.FailedTimeOver:
                case QuestResultType.ResetQuestTimer:
                    nQuestID = pStream.ReadInt();
                    break;
            }
            return new UserQuestResult(nResult, nQuestID, dwNpcTemplateID, nNextQuestID, bNavigation == 1, bQuestName == 1, aTimerQuestID, aTimeRemaining);
        }
    ),
    UserSetInGameDirectionMode(825,
        (pStream) -> {
            boolean bInGameDirectionMode = pStream.readBoolean();
            boolean bBlackFrame = pStream.readBoolean();
            boolean bForceMouseOver = pStream.available() > 0 ? pStream.readBoolean() : false;
            boolean bShowUI = pStream.available() > 0 ? pStream.readBoolean() : false;
            return new UserSetInGameDirectionMode(bInGameDirectionMode, bBlackFrame, bForceMouseOver, bShowUI);
        }
    ),
    UserSetStandaloneMode(826,
        (pStream) -> {
            boolean bEnable = pStream.readBoolean();
            return new UserSetStandaloneMode(bEnable);
        }
    ),
    UserInGameDirectionEvent(855,
        (pStream) -> {
            int nType = pStream.readByte();
            String sType = InGameDirectionEventType.GetName(nType);
            if (sType != null && !sType.equals("")) {
                String sText = "";
                switch (nType) {
                    case InGameDirectionEventType.ForcedAction:
                        int nAction = pStream.ReadInt();
                        int tDuration = pStream.ReadInt();
                        return new UserInGameDirectionEvent(nType, nAction, tDuration);
                        
                    case InGameDirectionEventType.Delay:
                        int tDelay = pStream.ReadInt();
                        return new UserInGameDirectionEvent(nType, tDelay);
                        
                    case InGameDirectionEventType.EffectPlay:
                        String sEffectUOL = pStream.ReadString(true);
                        tDuration = pStream.ReadInt();
                        int x = pStream.ReadInt();
                        int y = pStream.ReadInt();
                        byte bEncode = pStream.readByte();
                        int v17 = 0;
                        if (bEncode > 0) {
                            v17 = pStream.ReadInt();
                        }
                        bEncode = pStream.readByte();
                        int dwNpcID = 0;
                        if (bEncode > 0) {
                            dwNpcID = pStream.ReadInt();
                        }
                        boolean bNotOrigin = pStream.available() > 0 && pStream.readByte() == 1;
                        return new UserInGameDirectionEvent(nType, sEffectUOL, tDuration, x, y, v17, dwNpcID, bNotOrigin);
                        
                    case InGameDirectionEventType.ForcedInput:
                        int nForcedInput = pStream.ReadInt();
                        return new UserInGameDirectionEvent(nType, nForcedInput);
                        
                    case InGameDirectionEventType.PatternInputRequest:
                        String sPattern = pStream.ReadString(true);
                        int nAct = pStream.ReadInt();
                        int nRequestCount = pStream.ReadInt();
                        int nTime = pStream.ReadInt();
                        return new UserInGameDirectionEvent(nType, sPattern, nAct, nRequestCount, nTime);
                        
                    case InGameDirectionEventType.CameraMove:
                        boolean bBack = pStream.readByte() == 1;
                        int nPixelPerSec = pStream.ReadInt();
                        if (!bBack) {
                            x = pStream.ReadInt();
                            y = pStream.ReadInt();
                            return new UserInGameDirectionEvent(nType, bBack, nPixelPerSec, x, y);
                        } else {
                            return new UserInGameDirectionEvent(nType, bBack, nPixelPerSec);
                        }
                        
                    case InGameDirectionEventType.CameraOnCharacter:
                        dwNpcID = pStream.ReadInt();
                        return new UserInGameDirectionEvent(nType, dwNpcID);
                        
                    case InGameDirectionEventType.CameraZoom:
                        nTime = pStream.ReadInt();
                        int nScale = pStream.ReadInt();
                        int nTimePos = pStream.ReadInt();
                        x = pStream.ReadInt();
                        y = pStream.ReadInt();
                        return new UserInGameDirectionEvent(nType, nTime, nScale, nTimePos, x, y);
                        
                    case InGameDirectionEventType.CameraReleaseFromUserPoint:
                    case InGameDirectionEventType.RemoveAdditionalEffect:
                    case InGameDirectionEventType.CloseUI:
                        return new UserInGameDirectionEvent(nType);
                        
                    case InGameDirectionEventType.VansheeMode:
                        boolean bVanshee = pStream.readByte() == 1;
                        return new UserInGameDirectionEvent(nType, bVanshee);
                        
                    case InGameDirectionEventType.FaceOff:
                        int nFaceItemID = pStream.ReadInt();
                        return new UserInGameDirectionEvent(nType, nFaceItemID);
                        
                    case InGameDirectionEventType.Monologue:
                        String sMonologue = pStream.ReadString(true);
                        boolean bMonologueEnd = pStream.readByte() == 1;
                        return new UserInGameDirectionEvent(nType, sMonologue, bMonologueEnd);
                        
                    case InGameDirectionEventType.MonologueScroll:
                        sMonologue = pStream.ReadString(true);
                        boolean bStayModal = pStream.readByte() == 1;
                        short nAlign = pStream.ReadShort();
                        int nUpdateSpeedTime = pStream.ReadInt();
                        int nDecTic = pStream.ReadInt();
                        return new UserInGameDirectionEvent(nType, sMonologue, bStayModal, nAlign, nUpdateSpeedTime, nDecTic);
                        
                    case InGameDirectionEventType.AvatarLookSet:
                        byte nSize = pStream.readByte();
                        int[] aItem = new int[nSize];
                        for (int i = 0; i < nSize; i++) {
                            aItem[i] = pStream.ReadInt();
                        }
                        return new UserInGameDirectionEvent(nType, aItem);
                        
                    case InGameDirectionEventType.ForcedMove:
                        int nDir = pStream.ReadInt();
                        int nSpeed = pStream.ReadInt();
                        return new UserInGameDirectionEvent(nType, nDir, nSpeed);
                        
                    case InGameDirectionEventType.ForcedFlip:
                        int nForcedFlip = pStream.ReadInt();
                        return new UserInGameDirectionEvent(nType, nForcedFlip);
                        
                    case InGameDirectionEventType.InputUI:
                        byte nUIType = pStream.readByte();
                        return new UserInGameDirectionEvent(nType, nUIType);
                        
                }
            }
            return null;
        }
    ),
    ForceMoveByScript(1210,
        (pStream) -> {
            int nForcedMoveDir = pStream.ReadInt();
            int nForcedMoveFixel = pStream.ReadInt();
            int ptStartY = pStream.ReadInt();
            int ptStartX = pStream.ReadInt();
            return new ForceMoveByScript(nForcedMoveDir, nForcedMoveFixel, ptStartY, ptStartX);
        }
    ),
    NpcSpecialAction(1224,
        (pStream) -> {
            int dwID = pStream.ReadInt();
            String sMsg = pStream.ReadString(true);
            int tDuration = pStream.ReadInt();
            boolean bLocal = pStream.readBoolean();
            return new NpcSpecialAction(dwID, sMsg, tDuration, bLocal);
        }
    ),
    ScriptMessage(1658,
        (pStream) -> {
            int nSpeakerTypeID = 0, nSpeakerTemplateID = 0, nMsgType = 0;
            short bParam = 0;
            byte bSpecificSpeaker, eColor;
            try {
                nSpeakerTypeID = pStream.readByte();
                nSpeakerTemplateID = pStream.ReadInt();
                bSpecificSpeaker = pStream.readByte();
                if (bSpecificSpeaker == 1) {
                    nSpeakerTemplateID = pStream.ReadInt();
                }
                nMsgType = pStream.readByte();
                bParam = pStream.ReadShort();
                eColor = pStream.readByte();
                switch (nMsgType) {
                    case MessageType.Say:
                        String sText;
                        byte bPrev, bNext;
                        int tWait;
                        if ((bParam & MessageType.NpcReplayedByNpc) > 0) {
                            nSpeakerTemplateID = pStream.ReadInt();
                        }
                        sText = pStream.ReadString(true);
                        bPrev = pStream.readByte();
                        bNext = pStream.readByte();
                        tWait = pStream.ReadInt();
                        return new ScriptMessage(nSpeakerTypeID, nSpeakerTemplateID, nMsgType, bParam, bSpecificSpeaker, eColor, sText, bPrev, bNext, tWait);

                    case MessageType.SayImage:
                        byte nSize = pStream.readByte();
                        String[] aImage = new String[nSize];
                        for (byte i = 0; i < nSize; i++) {
                            aImage[i] = pStream.ReadString(true);
                        }
                        return new ScriptMessage(nSpeakerTypeID, nSpeakerTemplateID, nMsgType, bParam, bSpecificSpeaker, eColor, aImage);

                    case MessageType.AskYesNo:
                        if ((bParam & MessageType.NpcReplayedByNpc) > 0) {
                            nSpeakerTemplateID = pStream.ReadInt();
                        }
                        sText = pStream.ReadString(true);
                        return new ScriptMessage(nSpeakerTypeID, nSpeakerTemplateID, nMsgType, bParam, bSpecificSpeaker, eColor, sText);

                    case MessageType.AskMenu:
                        if ((bParam & MessageType.NpcReplayedByNpc) > 0) {
                            nSpeakerTemplateID = pStream.ReadInt();
                        }
                        sText = pStream.ReadString(true);
                        return new ScriptMessage(nSpeakerTypeID, nSpeakerTemplateID, nMsgType, bParam, bSpecificSpeaker, eColor, sText);

                    case MessageType.AskAccept:
                        if ((bParam & MessageType.NpcReplayedByNpc) > 0)
                        {
                            nSpeakerTemplateID = pStream.ReadInt();
                        }
                        sText = pStream.ReadString(true);
                        return new ScriptMessage(nSpeakerTypeID, nSpeakerTemplateID, nMsgType, bParam, bSpecificSpeaker, eColor, sText);

                    case MessageType.AskText:
                        String sDefaultText;
                        int nMin, nMax;
                        if ((bParam & MessageType.NpcReplayedByNpc) > 0) {
                            nSpeakerTemplateID = pStream.ReadInt();
                        }
                        sText = pStream.ReadString(true);
                        sDefaultText = pStream.ReadString(true);
                        nMin = pStream.ReadShort();
                        nMax = pStream.ReadShort();
                        return new ScriptMessage(nSpeakerTypeID, nSpeakerTemplateID, nMsgType, bParam, bSpecificSpeaker, eColor, sText, sDefaultText, (short) nMin, (short) nMax);

                    case MessageType.AskNumber:
                        int nDefault;
                        sText = pStream.ReadString(true);
                        nDefault = pStream.ReadInt();
                        nMin = pStream.ReadInt();
                        nMax = pStream.ReadInt();
                        return new ScriptMessage(nSpeakerTypeID, nSpeakerTemplateID, nMsgType, bParam, bSpecificSpeaker, eColor, sText, nDefault, nMin, nMax);

                    case MessageType.AskQuiz:
                        int nQuizResult, tRemain;
                        String sTitle, sProblemText, sHintText;
                        nQuizResult = pStream.readByte();
                        if (nQuizResult == 0) {//InitialQuizResult.Request
                            sTitle = pStream.ReadString(true);
                            sProblemText = pStream.ReadString(true);
                            sHintText = pStream.ReadString(true);
                            nMin = pStream.ReadInt();
                            nMax = pStream.ReadInt();
                            tRemain = pStream.ReadInt();
                            return new ScriptMessage(nSpeakerTypeID, nSpeakerTemplateID, nMsgType, bParam, bSpecificSpeaker, eColor, sTitle, sProblemText, sHintText, nMin, nMax, tRemain);
                        }
                        return null;

                    case MessageType.AskSpeedQuiz:
                        int nQuizType, dwQuizAnswer, nQuizCorrect, nQuizRemain;
                        nQuizResult = pStream.readByte();
                        if (nQuizResult == 0) {// InitialQuizResult.Request
                            nQuizType = pStream.ReadInt();
                            dwQuizAnswer = pStream.ReadInt();
                            nQuizCorrect = pStream.ReadInt();
                            nQuizRemain = pStream.ReadInt();
                            tRemain = pStream.ReadInt();
                            return new ScriptMessage(nSpeakerTypeID, nSpeakerTemplateID, nMsgType, bParam, bSpecificSpeaker, eColor, nQuizType, dwQuizAnswer, nQuizCorrect, nQuizRemain, tRemain);
                        }
                        return null;

                    case MessageType.AskIcQuiz:
                        nQuizResult = pStream.readByte();
                        if (nQuizResult == 0) {// InitialQuizResult.Request
                            sProblemText = pStream.ReadString(true);
                            sHintText = pStream.ReadString(true);
                            tRemain = pStream.ReadInt();
                            return new ScriptMessage(nSpeakerTypeID, nSpeakerTemplateID, nMsgType, bParam, bSpecificSpeaker, eColor, sProblemText, sHintText, tRemain);
                        }
                        return null;

                    case MessageType.AskAvatar:
                    case MessageType.Unknown2:
                        byte bAngelicBuster, bZeroBeta;
                        int[] aCode;
                        bAngelicBuster = pStream.readByte();
                        bZeroBeta = pStream.readByte();
                        sText = pStream.ReadString(true);
                        nSize = pStream.readByte();
                        aCode = new int[nSize];
                        for (byte i = 0; i < nSize; i++) {
                            aCode[i] = pStream.ReadInt();
                        }
                        return new ScriptMessage(nSpeakerTypeID, nSpeakerTemplateID, nMsgType, bParam, bSpecificSpeaker, eColor, sText, bAngelicBuster, bZeroBeta, aCode);

                    case MessageType.AskAndroid:
                    case MessageType.Unknown:
                        sText = pStream.ReadString(true);
                        nSize = pStream.readByte();
                        aCode = new int[nSize];
                        for (byte i = 0; i < nSize; i++)
                        {
                            aCode[i] = pStream.ReadInt();
                        }
                        return new ScriptMessage(nSpeakerTypeID, nSpeakerTemplateID, nMsgType, bParam, bSpecificSpeaker, eColor, sText, aCode);

                    case MessageType.AskPet: //Todo
                    case MessageType.AskPetAll: //Todo
                        sText = pStream.ReadString(true);
                        return new ScriptMessage(nSpeakerTypeID, nSpeakerTemplateID, nMsgType, bParam, bSpecificSpeaker, eColor, sText);

                    case MessageType.AskBoxText:
                        short nCol, nLine;
                        if ((bParam & MessageType.NpcReplayedByNpc) > 0) {
                            nSpeakerTemplateID = pStream.ReadInt();
                        }
                        sText = pStream.ReadString(true);
                        sDefaultText = pStream.ReadString(true);
                        nCol = pStream.ReadShort();
                        nLine = pStream.ReadShort();
                        return new ScriptMessage(nSpeakerTypeID, nSpeakerTemplateID, nMsgType, bParam, bSpecificSpeaker, eColor, sText, sDefaultText, nCol, nLine);

                    case MessageType.AskSlideMenu:
                        int nDlgType, nDefaultSelect;
                        nDlgType = pStream.ReadInt();
                        if (nDlgType == 1)
                        {
                            pStream.ReadInt();
                            pStream.ReadString(true);
                            //EncodeInt / EncodeString
                        }
                        nDefaultSelect = pStream.ReadInt();
                        sText = pStream.ReadString(true);
                        return new ScriptMessage(nSpeakerTypeID, nSpeakerTemplateID, nMsgType, bParam, bSpecificSpeaker, eColor, sText, nDlgType, nDefaultSelect);

                    case MessageType.AskSelectMenu:
                        String[] aPath;
                        nDlgType = pStream.ReadInt();
                        int nIntSize = pStream.ReadInt();
                        aPath = new String[nIntSize];
                        for (int i = 0; i < nIntSize; i++) {
                            aPath[i] = pStream.ReadString(true);
                        }
                        return new ScriptMessage(nSpeakerTypeID, nSpeakerTemplateID, nMsgType, bParam, bSpecificSpeaker, eColor, nDlgType, aPath);

                    case MessageType.SayIllustration:
                    case MessageType.SayDualIllustration:
                    case MessageType.AskYesNoIllustration:
                    case MessageType.AskAcceptIllustration:
                    case MessageType.AskYesNoDualIllustration:
                    case MessageType.AskAcceptDualIllustration:
                    case MessageType.AskMenuIllustration:
                    case MessageType.AskMenuDualIllustration:
                        int nNpcID, nFaceIndex, bIsLeft, nFaceIndex2 = 0;
                        if ((bParam & MessageType.NpcReplayedByNpc) > 0) {
                            nSpeakerTemplateID = pStream.ReadInt();
                        }
                        sText = pStream.ReadString(true);
                        bPrev = pStream.readByte();
                        bNext = pStream.readByte();
                        nNpcID = pStream.ReadInt();
                        nFaceIndex = pStream.ReadInt();
                        bIsLeft = pStream.ReadInt();
                        if (pStream.available() >= 4) {
                            nFaceIndex2 = pStream.ReadInt();
                        }
                        return new ScriptMessage(nSpeakerTypeID, nSpeakerTemplateID, nMsgType, bParam, bSpecificSpeaker, eColor, sText, bPrev, bNext, nNpcID, nFaceIndex, bIsLeft, nFaceIndex2);

                    case MessageType.AskBoxTextBgImg:
                        short nBgIdx, nFontSize;
                        nBgIdx = pStream.ReadShort();
                        sText = pStream.ReadString(true);
                        sDefaultText = pStream.ReadString(true);
                        nCol = pStream.ReadShort();
                        nLine = pStream.ReadShort();
                        nFontSize = pStream.ReadShort();
                        return new ScriptMessage(nSpeakerTypeID, nSpeakerTemplateID, nMsgType, bParam, bSpecificSpeaker, eColor, sText, nBgIdx, sDefaultText, nCol, nLine, nFontSize);

                    case MessageType.AskMixHair:
                        bAngelicBuster = pStream.readByte();
                        bZeroBeta = pStream.readByte();
                        pStream.readByte();
                        sText = pStream.ReadString(true);
                        nSize = pStream.readByte();
                        aCode = new int[nSize];
                        for (int i = 0; i < nSize; i++) {
                            aCode[i] = pStream.ReadInt();
                        }
                        return new ScriptMessage(nSpeakerTypeID, nSpeakerTemplateID, nMsgType, bParam, bSpecificSpeaker, eColor, sText, bAngelicBuster, bZeroBeta, aCode);

                    case MessageType.AskMixHairZero:
                        pStream.readByte();
                        sText = pStream.ReadString(true);
                        nSize = pStream.readByte();
                        aCode = new int[nSize];
                        for (int i = 0; i < nSize; i++) {
                            aCode[i] = pStream.ReadInt();
                        }
                        return new ScriptMessage(nSpeakerTypeID, nSpeakerTemplateID, nMsgType, bParam, bSpecificSpeaker, eColor, sText, aCode);

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
                    default:
                        return null;
                }
            } catch (Exception eee) {
                Logger.LogError("Exception thrown at ScriptMessage. nSpeakerTypeID[" + nSpeakerTypeID + "] nSpeakerTemplateID[" + nSpeakerTemplateID + "] nMsgType[" + nMsgType + "] bParam[" + bParam + "]");
                eee.printStackTrace();
                return null;
            }
        }
    );
    public int nCode;
    public OnPacket pDecodePacket;
    public String sScriptWriteOutput = "";
    
    LoopbackCode(int nCode, OnPacket pOnPacket) {
        this.nCode = nCode;
        this.pDecodePacket = pOnPacket;
    }
    
    public static LoopbackCode GetLoopback(int nCode) {
        for (LoopbackCode pCode : values()) {
            if (nCode == pCode.nCode) {
                return pCode;
            }
        }
        return null;
    }
    
    public static boolean IsLoopback(int nCode) {
        return Arrays.asList(values()).stream().anyMatch((pCode) -> pCode.nCode == nCode);
    }
    
    
    public static interface OnPacket {    
        public Packet ReadPacket(PacketInputStream pStream) throws IOException;    
    }
}
