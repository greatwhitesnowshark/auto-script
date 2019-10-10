package python;

import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Sharky
 */
public class ForceComment extends Modifier {

    public static ForceComment pInstance = new ForceComment();
    public static List<String> aForceCommentLine = new LinkedList<>(); //ignores a read line if it contains specific text

    @Override
    public String Convert(String sScriptLine) {
        for (String sKeyword : aForceCommentLine) {
            if (sScriptLine.contains(sKeyword)) {
                return ("//" + sScriptLine);
            }
        }
        return sScriptLine;
    }


    static {

        aForceCommentLine.add(".setInstanceTime");
        aForceCommentLine.add("field.setProperty");
        aForceCommentLine.add("waitForMobDeath");
        aForceCommentLine.add("field.hasProperty");
        aForceCommentLine.add("field.getProperty");
        aForceCommentLine.add(".getAvatarData().getCharacterStat().setSubJob");

    }
}
