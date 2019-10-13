package python.output;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Sharky
 */
public class SortFieldScript {

    public static List<String> aUserEnterScript = new ArrayList<>();
    public static List<String> aUserFirstEnterScript = new ArrayList<>();
    public static List<String> aFieldScript = new ArrayList<>();

    public static void main(String[] args) {
        SortFieldScriptMap();
    }

    public static void SortFieldScriptMap() {
        try (BufferedReader br = new BufferedReader(new FileReader("C:\\Users\\Chris\\Desktop\\Swordie\\FieldID_to_Script.txt"))) {
            FieldScriptType t = null;
            String sLine;
            while (br.ready()) {
                sLine = br.readLine();
                if (sLine.contains("<UserEnter>")) {
                    t = FieldScriptType.UserEnter;
                } else if (sLine.contains("<FirstUserEnter>")) {
                    t = FieldScriptType.FirstUserEnter;
                } else if (sLine.contains("<FieldScript>")) {
                    t = FieldScriptType.FieldScript;
                }
                if (sLine.contains("`") && sLine.contains(".js")) {
                    String sScript = sLine.substring(sLine.indexOf("`") + 1, sLine.indexOf("."));
                    switch (t) {
                        case UserEnter:
                            if (!aUserEnterScript.contains(sScript)) {
                                aUserEnterScript.add(sScript);
                            }
                            break;
                        case FirstUserEnter:
                            if (!aUserFirstEnterScript.contains(sScript)) {
                                aUserFirstEnterScript.add(sScript);
                            }
                            break;
                        case FieldScript:
                            if (!aFieldScript.contains(sScript)) {
                                aFieldScript.add(sScript);
                            }
                            break;
                    }
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static FieldScriptType GetFieldScriptType(String sScriptName) {
        if (sScriptName.contains(".")) {
            sScriptName = sScriptName.substring(0, sScriptName.indexOf("."));
        }
        if (aUserEnterScript.contains(sScriptName)) {
            return FieldScriptType.UserEnter;
        }
        if (aUserFirstEnterScript.contains(sScriptName)) {
            return FieldScriptType.FirstUserEnter;
        }
        if (aFieldScript.contains(sScriptName)) {
            return FieldScriptType.FieldScript;
        }
        return FieldScriptType.NotSorted;
    }


    public enum FieldScriptType { UserEnter, FirstUserEnter, FieldScript, NotSorted }

}
