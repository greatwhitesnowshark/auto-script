/*
 * To change this license opcode, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package script;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import template.AbstractTemplate;
import template.NpcTemplate;
import template.PortalTemplate;
import template.QuestEndTemplate;
import template.QuestStartTemplate;
import template.ReactorTemplate;
import template.FieldTemplate;
import scriptmaker.Config;
import util.Logger;

/**
 *
 * @author Sharky
 */
public class ScriptTemplateMap {
    
    private static final ScriptTemplateMap pInstance = new ScriptTemplateMap();
    private final Map<Integer, LinkedHashMap<Object, AbstractTemplate>> mTemplate;
    private final Map<Integer, String> mFieldName;
    private final Map<Integer, String> mNpcName;
    
    public ScriptTemplateMap() {
        this.mTemplate = new HashMap<>();
        this.mTemplate.put(FieldTemplate.class.hashCode(), new LinkedHashMap<>());
        this.mTemplate.put(NpcTemplate.class.hashCode(), new LinkedHashMap<>());
        this.mTemplate.put(QuestStartTemplate.class.hashCode(), new LinkedHashMap<>());
        this.mTemplate.put(QuestEndTemplate.class.hashCode(), new LinkedHashMap<>());
        this.mTemplate.put(PortalTemplate.class.hashCode(), new LinkedHashMap<>());
        this.mTemplate.put(ReactorTemplate.class.hashCode(), new LinkedHashMap<>());
        this.mFieldName = new HashMap<>();
        this.mNpcName = new HashMap<>();
    }
    
    public static ScriptTemplateMap GetInstance() {
        return pInstance;
    }
    
    public static NpcTemplate GetNpcTemplate(int dwNpcTemplateID) {
        return (NpcTemplate) pInstance.mTemplate.get(NpcTemplate.class.hashCode()).get(dwNpcTemplateID);
    }
    
    public static QuestStartTemplate GetQuestStartTemplate(int dwQuestTemplateID) {
        return (QuestStartTemplate) pInstance.mTemplate.get(QuestStartTemplate.class.hashCode()).get(dwQuestTemplateID);
    }
    
    public static QuestEndTemplate GetQuestEndTemplate(int dwQuestTemplateID) {
        return (QuestEndTemplate) pInstance.mTemplate.get(QuestEndTemplate.class.hashCode()).get(dwQuestTemplateID);
    }
    
    public static PortalTemplate GetPortalTemplate(int dwField, String sPortalName) {
        return (PortalTemplate) pInstance.mTemplate.get(PortalTemplate.class.hashCode()).get(("" + dwField + sPortalName));
    }
    
    public static FieldTemplate GetUserEnterTemplate(int dwField) {
        return (FieldTemplate) pInstance.mTemplate.get(FieldTemplate.class.hashCode()).get(("" + dwField + "UserEnter"));
    }
    
    public static FieldTemplate GetFirstUserEnterTemplate(int dwField) {
        return (FieldTemplate) pInstance.mTemplate.get(FieldTemplate.class.hashCode()).get(("" + dwField + "FirstUserEnter"));
    }
    
    public static FieldTemplate GetFieldScript(int dwField) {
        return (FieldTemplate) pInstance.mTemplate.get(FieldTemplate.class.hashCode()).get(("" + dwField + "FieldScript"));
    }
    
    public static String GetFieldName(int dwField) {
        String sFieldName = pInstance.mFieldName.get(dwField);
        return sFieldName != null ? sFieldName : "";
    }
    
    public static String GetNpcName(int dwTemplateID) {
        String sNpcName = pInstance.mNpcName.get(dwTemplateID);
        return sNpcName != null ? sNpcName : "";
    }
    
    public void LoadTemplateMap() throws IOException {
        List<AbstractTemplate> aTemplate = new LinkedList<>(), aTemplate2 = new LinkedList<>();
        String[] aTemplateInfo;
        String sLine, sScript, sStartScript, sEndScript, sQuestName, sPortalName, sNpcName;
        int dwTemplateID, dwField, nQuestStartNpcID, nQuestEndNpcID;
        //Npc
        BufferedReader pStream = new BufferedReader(new FileReader("script\\NpcID_to_Script.txt"));
        while (pStream.ready()) {
            sLine = pStream.readLine();
            if (sLine.contains("#")) {
                aTemplateInfo = sLine.split("#");
                dwTemplateID = Integer.parseInt(aTemplateInfo[0]);
                if (aTemplateInfo.length > 1) {
                    sScript = aTemplateInfo[1];
                    aTemplate.add(new NpcTemplate(sScript, dwTemplateID));
                }
            }
        }
        pStream.close();
        aTemplate.stream().forEach((pNpcTemplate) -> {
            mTemplate.get(NpcTemplate.class.hashCode()).put(pNpcTemplate.dwTemplateID, pNpcTemplate);
            if (Config.bTemplateMapNpcScript) {
                Logger.LogReport("pNpcTemplate.dwTemplateID [" + pNpcTemplate.dwTemplateID + "] / pNpcTemplate.sScript [" + pNpcTemplate.sScript + "]");
            }
        });
        aTemplate.clear();
        pStream = new BufferedReader(new FileReader("script\\NpcID_to_Name.txt"));
        while (pStream.ready()) {
            sLine = pStream.readLine();
            if (sLine.contains(" - ")) {
                aTemplateInfo = sLine.split(" - ");
                dwTemplateID = Integer.parseInt(aTemplateInfo[0]);
                if (aTemplateInfo.length > 1) {
                    sNpcName = aTemplateInfo[1];
                    if (!mNpcName.containsKey(dwTemplateID)) {
                        mNpcName.put(dwTemplateID, sNpcName);
                        if (Config.bTemplateMapNpcName) {
                            Logger.LogReport("Npc-Name-to-ID:  dwTemplateID [%d], sNpcName [%s]", dwTemplateID, sNpcName);
                        }
                    }
                }
            }
        }
        pStream.close();
        //Quest
        pStream = new BufferedReader(new FileReader("script\\QuestID_to_Script.txt"));
        while (pStream.ready()) {
            sLine = pStream.readLine();
            if (sLine.contains("@")) {
                aTemplateInfo = sLine.split("@");
                if (aTemplateInfo.length > 5) {
                    dwTemplateID = Integer.parseInt(aTemplateInfo[0]);
                    sStartScript = aTemplateInfo[1];
                    sEndScript = aTemplateInfo[2];
                    sQuestName = aTemplateInfo[3];
                    nQuestStartNpcID = Integer.parseInt(aTemplateInfo[4]);
                    nQuestEndNpcID = Integer.parseInt(aTemplateInfo[5]);
                    if (!sStartScript.equals("null")) {
                        aTemplate.add(new QuestStartTemplate(sStartScript, dwTemplateID, nQuestStartNpcID, sQuestName));
                        if (Config.bTemplateMapQuestStartScript) {
                            Logger.LogReport("QuestStartTemplate:  sStartScript [%s], dwTemplateID [%d], nQuestStartNpcID [%d], sQuestName [%s]", sStartScript, dwTemplateID, nQuestStartNpcID, sQuestName);
                        }
                    }
                    if (!sEndScript.equals("null")) {
                        aTemplate2.add(new QuestEndTemplate("script\\questEnd\\", sEndScript, dwTemplateID, nQuestEndNpcID, sQuestName));
                        if (Config.bTemplateMapQuestEndScript) {
                            Logger.LogReport("QuestEndTemplate:  sEndScript [%s], dwTemplateID [%d], nQuestEndNpcID [%d], sQuestName [%s]", sEndScript, dwTemplateID, nQuestEndNpcID, sQuestName);
                        }
                    }
                }
            }
        }
        pStream.close();
        aTemplate.stream().forEach((pQuestStartTemplate) -> mTemplate.get(QuestStartTemplate.class.hashCode()).put(pQuestStartTemplate.dwTemplateID, pQuestStartTemplate));
        aTemplate.clear();
        aTemplate2.stream().forEach((pQuestEndTemplate) -> mTemplate.get(QuestEndTemplate.class.hashCode()).put(pQuestEndTemplate.dwTemplateID, pQuestEndTemplate));
        aTemplate2.clear();
        //Portal
        pStream = new BufferedReader(new FileReader("script\\PortalName_to_Script.txt"));
        while (pStream.ready()) {
            sLine = pStream.readLine().trim();
            if (!sLine.isEmpty()) {
                if (sLine.contains("<FieldScript>")) {
                    sLine = sLine.replace(" `", "@@").replace("` ", "@@");
                    aTemplateInfo = sLine.split("@@");
                    dwField = aTemplateInfo.length > 2 ? Integer.parseInt(aTemplateInfo[2].trim()) : 0;
                    if (pStream.ready() && (sLine = pStream.readLine().trim()).contains("<Portal>")) {
                        while (pStream.ready() && !sLine.contains("</Portal>")) {
                            sLine = pStream.readLine();
                            aTemplateInfo = sLine.split(" ");
                            sPortalName = aTemplateInfo[0].trim();
                            if (aTemplateInfo.length > 1) {
                                sScript = aTemplateInfo[1].replaceAll("`", "").trim();
                                aTemplate.add(new PortalTemplate(sScript, dwField, sPortalName));
                                if (Config.bTemplateMapPortalScript) {
                                    Logger.LogReport("PortalTemplate:  sScript [%s], dwField [%d], sPortalName [%s]", sScript, dwField, sPortalName);
                                }
                            }
                        }
                    }
                }
            }
        }
        pStream.close();
        aTemplate.stream().forEach((pPortalTemplate) -> mTemplate.get(PortalTemplate.class.hashCode()).put(("" + ((PortalTemplate) pPortalTemplate).dwField + ((PortalTemplate) pPortalTemplate).sPortalName), pPortalTemplate));
        aTemplate.clear();
        //UserEnter
        pStream = new BufferedReader(new FileReader("script\\FieldID_to_Script.txt"));
        while (pStream.ready()) {
            sLine = pStream.readLine().trim();
            if (!sLine.isEmpty()) {
                if (sLine.contains("<FieldScript>")) {
                    sLine = sLine.replace(" `", "@@").replace("` ", "@@");
                    aTemplateInfo = sLine.split("@@");
                    dwField = aTemplateInfo.length > 2 ? Integer.parseInt(aTemplateInfo[2].trim().replace("[", "").replace("]", "")) : 0;
                    String sType;
                    while (pStream.ready() && !(sLine = pStream.readLine().trim()).contains("</FieldScript>")) {
                        if ((sLine.contains("<UserEnter>") || sLine.contains("<FirstUserEnter>") || sLine.contains("<FieldScript>")) && pStream.ready()) {
                            sType = sLine.replace("<", "").replace(">", "");
                            sLine = pStream.readLine().trim();
                            sScript = sType + "\\" + sLine.replace("`", "");
                            aTemplate.add(new FieldTemplate(sScript, dwField, sType));
                            if (Config.bTemplateMapFieldScript) {
                                Logger.LogReport("FieldScript-Script:  sType [%s], sScript [%s], dwField [%d]", sType, sScript, dwField);
                            }
                        }
                    }
                }
            }
        }
        pStream.close();
        pStream = new BufferedReader(new FileReader("script\\FieldID_to_Name.txt"));
        while (pStream.ready()) {
            sLine = pStream.readLine().trim().replace("[", "@@");
            if (sLine.contains(" -  @@")) {
                aTemplateInfo = sLine.split(" -  @@");
                int dwFieldID = Integer.parseInt(aTemplateInfo[0]);
                if (aTemplateInfo.length > 1) {
                    String sFieldName = aTemplateInfo[1].replace("]", "");
                    if (!mFieldName.containsKey(dwFieldID)) {
                        mFieldName.put(dwFieldID, sFieldName);
                        if (Config.bTemplateMapFieldName) {
                            Logger.LogReport("FieldScript-Name-to-ID:  dwFieldID [%d], sFieldName [%s]", dwFieldID, sFieldName);
                        }
                    }
                }
            }
        }
        pStream.close();
        aTemplate.stream().forEach((pUserEnterTemplate) -> mTemplate.get(FieldTemplate.class.hashCode()).put(("" + ((FieldTemplate) pUserEnterTemplate).dwField + ((FieldTemplate) pUserEnterTemplate).sType), pUserEnterTemplate));
        aTemplate.clear();
    }
}
