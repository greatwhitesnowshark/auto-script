/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package script;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import packet.PacketWrapper;
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

    private static final Map<Integer, Integer> mPacketParsedCount = new LinkedHashMap<>();
    private static final Map<Class<? extends AbstractTemplate>, LinkedList<String>> mScriptCompiledCount = new LinkedHashMap<>();
    private static Lock pProcessLock = new ReentrantLock();
    private static Script pScript = new Script();

    public static void Compile(PacketWrapper pPacket) {
        pProcessLock.lock();
        try {
            //Handles creating a new script template before processing the packet's write-request
            ScriptModifier pScriptModifier = pPacket.CreateScriptModifier();
            if (pScriptModifier != null) {
                pScriptModifier.SetScript(pScript);
                if (pScript.pTemplate != null) {
                    if (!mScriptCompiledCount.get(pScript.pTemplate.getClass()).contains(pScript.sFileName)) {
                        mScriptCompiledCount.get(pScript.pTemplate.getClass()).add(pScript.sFileName);
                    }
                }
            }
            //Handles merging the existing script template to the packet's write-request template
            pScriptModifier = pPacket.CreateScriptModifierOnMerge();
            if (pScriptModifier != null) {
                pScriptModifier.SetScript(pScript);
            }
            //Handles updating input/response values given from the user; processed via ClientCode handles
            pScriptModifier = pPacket.CreateScriptModifierOnInput();
            if (pScriptModifier != null) {
                pScriptModifier.SetScript(pScript);
            }
            //Handles the actual write-request after template values have been set
            ScriptWriteRequest pScriptWriteRequest = pPacket.CreateScriptWriteRequest();
            if (pScriptWriteRequest != null && pScriptWriteRequest.GetTemplate() != null) {
                pScript.ProcessWriteRequest(pScriptWriteRequest);
            }
            //Handles resetting the template after a write-request processes - for example, if we know this will be the last action in a script
            pScriptModifier = pPacket.CreateScriptModifierOnEnd();
            if (pScriptModifier != null) {
                pScriptModifier.SetScript(pScript);
            }
        } finally {
            int nCount = mPacketParsedCount.get(pPacket.GetHeader()) != null ? (mPacketParsedCount.get(pPacket.GetHeader()) + 1) : 1;
            mPacketParsedCount.put(pPacket.GetHeader(), nCount);
            pProcessLock.unlock();
        }
    }
    
    /**
     * Todo:: clean this crap output.
     * @return A super ugly looking representation of what was parsed and created.
     */
    public static String GetOutputLog() {
        String sOutputLog;
        sOutputLog = " - Packets processed: \r\n";
        for (int nHeader : mPacketParsedCount.keySet()) {
            LoopbackCode pLoopback = LoopbackCode.GetLoopback(nHeader);
            if (pLoopback != null) {
                sOutputLog += "     - [LP] " + pLoopback.name() + " (" + nHeader + ").... " + mPacketParsedCount.get(nHeader) + " packets processed \r\n";
            } else {
                ClientCode pClient = ClientCode.GetClient(nHeader);
                if (pClient != null) {
                    sOutputLog += "     - [CP] " + pClient.name() + " (" + nHeader + ").... " + mPacketParsedCount.get(nHeader) + " packets processed \r\n";
                }
            }
        }
        sOutputLog += " - Scripts created: \r\n";
        for (Class<? extends AbstractTemplate> pTemplate : mScriptCompiledCount.keySet()) {
            sOutputLog += "     - " + pTemplate.getSimpleName() + " scripts.... \r\n";
            for (String sScript : mScriptCompiledCount.get(pTemplate)) {
                sOutputLog += "           - " + (sScript.contains(".js") ? sScript : (sScript + ".js")) + " \r\n";
            }
        }
        return sOutputLog;
    }
    
    
    static 
    {
        mScriptCompiledCount.put(FieldTemplate.class, new LinkedList<>());
        mScriptCompiledCount.put(NpcTemplate.class, new LinkedList<>());
        mScriptCompiledCount.put(PortalTemplate.class, new LinkedList<>());
        mScriptCompiledCount.put(ReactorTemplate.class, new LinkedList<>());
        mScriptCompiledCount.put(QuestStartTemplate.class, new LinkedList<>());
        mScriptCompiledCount.put(QuestEndTemplate.class, new LinkedList<>());
        
        for (LoopbackCode pCode : LoopbackCode.values()) {
            mPacketParsedCount.put(pCode.nCode, 0);
        }
        
        for (ClientCode pCode : ClientCode.values()) {
            mPacketParsedCount.put(pCode.nCode, 0);
        }
    }
}
