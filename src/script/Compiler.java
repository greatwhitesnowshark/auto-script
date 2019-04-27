/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package script;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import packet.Packet;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import packet.ClientCode;
import packet.LoopbackCode;
import template.AbstractTemplate;
import template.FieldTemplate;
import template.NpcTemplate;
import template.PortalTemplate;
import template.QuestEndTemplate;
import template.QuestStartTemplate;
import template.ReactorTemplate;

/**
 *
 * @author Sharky
 */
public class Compiler {

    private static Script pScript = new Script();
    private static final Map<Class<? extends AbstractTemplate>, LinkedList<String>> mScript = new LinkedHashMap<>();
    private static final Map<Integer, Integer> mPacket = new LinkedHashMap<>();
    private static final Lock pProcessLock = new ReentrantLock();
    
    static {
        mScript.put(FieldTemplate.class, new LinkedList<>());
        mScript.put(NpcTemplate.class, new LinkedList<>());
        mScript.put(PortalTemplate.class, new LinkedList<>());
        mScript.put(ReactorTemplate.class, new LinkedList<>());
        mScript.put(QuestStartTemplate.class, new LinkedList<>());
        mScript.put(QuestEndTemplate.class, new LinkedList<>());
        for (LoopbackCode pCode : LoopbackCode.values()) {
            mPacket.put(pCode.nCode, 0);
        }
        for (ClientCode pCode : ClientCode.values()) {
            mPacket.put(pCode.nCode, 0);
        }
    }
    
    public static String GetOutputLog() {
        String sOutputLog;
        sOutputLog = " - Packets processed: \r\n";
        for (int nHeader : mPacket.keySet()) {
            LoopbackCode pLoopback = LoopbackCode.GetLoopback(nHeader);
            if (pLoopback != null) {
                sOutputLog += "     - [LP] " + pLoopback.name() + " (" + nHeader + ").... " + mPacket.get(nHeader) + " packets processed \r\n";
            } else {
                ClientCode pClient = ClientCode.GetClient(nHeader);
                if (pClient != null) {
                    sOutputLog += "     - [CP] " + pClient.name() + " (" + nHeader + ").... " + mPacket.get(nHeader) + " packets processed \r\n";
                }
            }
        }
        sOutputLog += " - Scripts created: \r\n";
        for (Class<? extends AbstractTemplate> pTemplate : mScript.keySet()) {
            sOutputLog += "     - " + pTemplate.getSimpleName() + " scripts.... \r\n";
            for (String sScript : mScript.get(pTemplate)) {
                sOutputLog += "           - " + (sScript.contains(".js") ? sScript : (sScript + ".js")) + " \r\n";
            }
        }
        return sOutputLog;
    }
    
    public static void ProcessPacket(Packet pPacket) {
        pProcessLock.lock();
        try {
            
            ScriptModifier pScriptModifier = pPacket.CreateScriptModifier();
            if (pScriptModifier != null) {
                pScriptModifier.SetScript(pScript);
                if (pScript.pTemplate != null) {
                    //Logger.LogError(pScript.pTemplate.getClass().getSimpleName());
                    if (!mScript.get(pScript.pTemplate.getClass()).contains(pScript.sFileName)) {
                        mScript.get(pScript.pTemplate.getClass()).add(pScript.sFileName);
                    }
                }
            }
            
            pScriptModifier = pPacket.CreateScriptModifierOnMerge();
            if (pScriptModifier != null) {
                pScriptModifier.SetScript(pScript);
            }
            
            pScriptModifier = pPacket.CreateScriptModifierOnInput();
            if (pScriptModifier != null) {
                pScriptModifier.SetScript(pScript);
            }
            
            ScriptWriteRequest pScriptWriteRequest = pPacket.CreateScriptWriteRequest();
            if (pScriptWriteRequest != null && pScriptWriteRequest.GetTemplate() != null) {
                pScript.ProcessWriteRequest(pScriptWriteRequest);
            }
            
            pScriptModifier = pPacket.CreateScriptModifierOnEnd();
            if (pScriptModifier != null) {
                pScriptModifier.SetScript(pScript);
            }
            
        } finally {
            int nCount = mPacket.get(pPacket.GetHeader()) != null ? (mPacket.get(pPacket.GetHeader()) + 1) : 1;
            mPacket.put(pPacket.GetHeader(), nCount);
            pProcessLock.unlock();
        }
    }
}
