/*
 * To change this license header), choose License Headers in Project Properties.
 * To change this template file), choose Tools | Templates
 * and open the template in the editor.
 */
package packet;

import packet.inputstream.PacketInputStream;
import java.awt.Point;
import java.io.IOException;
import java.util.Arrays;
import message.MessageType;
import message.QuestMessageType;
import packet.client.UserPortalScriptRequest;
import packet.client.UserQuestRequest;
import packet.client.UserScriptMessageAnswer;
import packet.client.UserSelectNpc;
import script.Script;
import util.Config;
import util.Logger;

/**
 *
 * @author Five
 */
public enum ClientCode {
    
    UserScriptMessageAnswer(243,
        (pStream) -> {
            int nMsgTypeInput, nRetInput = 1, nSelectionInput = -1; 
            nMsgTypeInput = pStream.readByte();
            if (nMsgTypeInput != MessageType.AskInGameDirection) {
                nRetInput = pStream.readByte();
                if (pStream.available() >= 2) {
                    nSelectionInput = -1;
                    if (pStream.available() >= 6) {
                        switch (nMsgTypeInput) {
                            case MessageType.AskMenu:
                                nSelectionInput = pStream.ReadInt();
                                break;
                        }
                    }
                }
            }
            return new UserScriptMessageAnswer(nMsgTypeInput, nRetInput, nSelectionInput);
        }
    ),
    UserSelectNpc(241,
        (pStream) -> {
            int dwNpcID = pStream.ReadInt(); //not template ID, this relates to the map object ID
            Point ptPos = new Point(pStream.ReadShort(), pStream.ReadShort());
            return new UserSelectNpc(dwNpcID, ptPos);
        }
    ),
    UserPortalScriptRequest(349,
        (pStream) -> {
            byte nType = pStream.readByte();
            String sPortalName = pStream.ReadString(true);
            return new UserPortalScriptRequest(nType, sPortalName);
        }
    ),
    UserQuestRequest(358,
        (pStream) -> {
            int nQuestState = pStream.readByte();
            int nQuestID = pStream.ReadInt();
            boolean bOpening = nQuestState == QuestMessageType.OpeningScript;
            boolean bComplete = nQuestState == QuestMessageType.CompleteScript;
            if (Config.QuestStateDebug) {
                Logger.LogAdmin(("\r\nUserQuestRequest hit... \r\nQuest ID: " + nQuestID + "  -  nQRStatus: " + QuestMessageType.sType[nQuestState] + "  -  bOpening: " + bOpening + "  -  bComplete: " + bComplete + "\r\n...end\r\n"));
            }
            return new UserQuestRequest(nQuestState, nQuestID, bOpening, bComplete);
        }
    );
    public int nCode;
    public OnPacket pDecodePacket;
    public Script pScript;
    
    ClientCode(int nCode, OnPacket pOnPacket) {
        this.nCode = nCode;
        this.pDecodePacket = pOnPacket;
    }
    
    public static ClientCode GetClient(int nCode) {
        for (ClientCode pCode : values()) {
            if (nCode == pCode.nCode) {
                return pCode;
            }
        }
        return null;
    }
    
    public static boolean IsClient(int nCode) {
        return Arrays.asList(values()).stream().anyMatch((pCode) -> pCode.nCode == nCode);
    }
    
    
    public static interface OnPacket {    
        public Packet ReadPacket(PacketInputStream pStream) throws IOException;    
    }
    
}
