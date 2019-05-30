/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package script;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import packet.LoopbackCode;

/**
 *
 * @author Sharky
 */
public class ScriptFieldObjMap {
    
    private static final Map<Integer, LinkedHashMap<Integer, Integer>> mTemplate = Collections.synchronizedMap(new LinkedHashMap<>());;
    
    public static int GetMobTemplateID(int dwID) {
        return mTemplate.get(LoopbackCode.MobEnterField.nCode).get(dwID);
    }
    
    public static int GetNpcTemplateID(int dwID) {
        return mTemplate.get(LoopbackCode.NpcEnterField.nCode).get(dwID);
    }
    
    public static int GetReactorTemplateID(int dwID) {
        return mTemplate.get(LoopbackCode.ReactorEnterField.nCode).get(dwID);
    }
    
    public static void OnMobEnterField(int dwID, int dwTemplateID) {
        mTemplate.get(LoopbackCode.MobEnterField.nCode).put(dwID, dwTemplateID);
    }
    
    public static void OnNpcEnterField(int dwID, int dwTemplateID) {
        mTemplate.get(LoopbackCode.NpcEnterField.nCode).put(dwID, dwTemplateID);
    }
    
    public static void OnReactorEnterField(int dwID, int dwTemplateID) {
        mTemplate.get(LoopbackCode.ReactorEnterField.nCode).put(dwID, dwTemplateID);
    }
    
    public static void ResetMap() {
        mTemplate.get(LoopbackCode.MobEnterField.nCode).clear();
        mTemplate.get(LoopbackCode.NpcEnterField.nCode).clear();
        mTemplate.get(LoopbackCode.ReactorEnterField.nCode).clear();
    }
    
    static 
    {
        mTemplate.put(LoopbackCode.MobEnterField.nCode, new LinkedHashMap<>());
        mTemplate.put(LoopbackCode.NpcEnterField.nCode, new LinkedHashMap<>());
        mTemplate.put(LoopbackCode.ReactorEnterField.nCode, new LinkedHashMap<>());
    }
}
