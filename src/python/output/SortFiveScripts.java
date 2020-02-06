package python.output;

import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedList;

/**
 * @author Sharky
 */
public class SortFiveScripts {

    private static final LinkedList<String> lFiveScripts = new LinkedList<>();

    public static final boolean IsFiveAuthorScript(String sFileName) {
        String sName = sFileName;
        if (sName.contains(".")) {
            sName = sName.substring(0, sName.indexOf("."));
        }
        return lFiveScripts.contains(sName);
    }

    public static final void SortFiveAuthorScripts() {
        try {

            Files.walk(Paths.get("C:\\Users\\Chris\\Desktop\\Neckson\\Game\\script")).forEach((pFile) -> {
                if (!pFile.toFile().isDirectory()) {
                    try (BufferedReader br = new BufferedReader(new FileReader(pFile.toFile()))) {
                        while (br.ready()) {
                            String sLine = br.readLine();
                            if (sLine.contains("@author") && (sLine.contains("Five") || sLine.contains("Sharky"))) {
                                String sName = pFile.toFile().getName();
                                if (sName.contains(".")) {
                                    sName = sName.substring(0, sName.indexOf("."));
                                }
                                if (!lFiveScripts.contains(sName)) {
                                    lFiveScripts.add(sName);
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
