package python.handle;

import util.StringUtil;

import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Sharky
 */
public class KeywordIgnore extends AbstractHandler {

    public static KeywordIgnore pInstance = new KeywordIgnore();
    public static List<String> aForceCommentLine = new LinkedList<>(); //ignores a read line if it contains specific text

    @Override
    public String Convert(String sScriptLine) {
        for (String sKeyword : aForceCommentLine) {
            if (sScriptLine.contains(sKeyword)) {
                int nPad = StringUtil.CountStringPaddingChar(sScriptLine);
                return StringUtil.AddStringPaddingChar(("//" + sScriptLine.trim()), nPad);
            }
        }
        return sScriptLine;
    }


    static {

        aForceCommentLine.add(".setInstanceInfo");
        aForceCommentLine.add(".setInstanceTime");
        aForceCommentLine.add("field.setProperty");
        aForceCommentLine.add("waitForMobDeath");
        aForceCommentLine.add("field.hasProperty");
        aForceCommentLine.add("field.getProperty");
        aForceCommentLine.add(".getAvatarData().getCharacterStat().setSubJob");

    }
}
