package util;

import java.util.LinkedList;
import java.util.function.Predicate;

/**
 * @author Sharky
 */
public class StringMatch {

    private Predicate<Match>[] aPredicates;
    private String[] aWords; //note:: this includes words that are wrapped by quotation marks
    private boolean bWrapAround, bInterleaveQuotes, bIgnoreComments;
    private LinkedList<Match> lMatches;
    private int nCommentIndex;
    public String sContext, sContextMarked, sFindText, sReplaceText;

    /**
     * This is a search-and-replace tool that offers find and replace options similar to an IDE; with options for whole words only as well as interleaving/ignoring quoted text
     *
     * @param sContext          The full String/char[] set in which to look for some targeted text
     * @param sFindText         The targeted text to find within the given String/char[] set
     * @param sReplaceText      The replacement text to insert wherever an instance of some targeted text is found
     * @param bWrapAround       True for only matching instances of whole words
     * @param bInterleaveQuotes True for only matching instances not contained inside quoted sections of our String/char[] set
     * @param bIgnoreComments   True for only matching instances not inside of a comment-identifier
     * @param aPredicates       Optional predicate(s), found text will not be replaced if any of the given function arguments return false
     **/
    public StringMatch(String sContext, String sFindText, String sReplaceText, boolean bWrapAround, boolean bInterleaveQuotes, boolean bIgnoreComments, Predicate<Match>... aPredicates) {
        this.sContext = sContext;
        this.sReplaceText = sReplaceText;
        this.bWrapAround = bWrapAround;
        this.bInterleaveQuotes = bInterleaveQuotes;
        this.aPredicates = aPredicates;
        this.bIgnoreComments = bIgnoreComments; //applies for '//' identifiers (JS, Java) and '#" identifiers (Python)
        nCommentIndex = -1;
        SetFindText(sFindText);
    }

    /**
     * Will check for all matches including inside quoted sections and non-wrapped words, functions exactly like String.Match()
     *
     * @param sContext  The containing string to search in
     * @param aFindText The text(s) to search for in the containing string
     * @return True if there were any matches found for any 'aFindText' objects inside 'sContext'
     */
    public static boolean Match(String sContext, String... aFindText) {
        return Match(sContext, false, false, false, aFindText);
    }

    /**
     * Will check for all word-wrapped matches of a sub-sequence inside of a containing string, includes the option to interleave(ignore) text that is wrapped inside of quotation marks
     *
     * @param sContext          The containing string to search in
     * @param bWrapAround       If true, will only search for instances of 'sFindText' that are whole words
     * @param bInterleaveQuotes If true, will ignore any text that lives inside a pair of quotation marks (example: read..."ignored")
     * @param bIgnoreComments   True for only matching instances not inside of a comment-identifier
     * @param aFindText         The text(s) to search for in the containing string
     * @return True if there were any matches found of 'sFindText' inside the containing string, 'sContext'
     */
    public static boolean Match(String sContext, boolean bWrapAround, boolean bInterleaveQuotes, boolean bIgnoreComments, String... aFindText) {
        if (aFindText != null && aFindText.length > 0) {
            StringMatch pMatch = new StringMatch(sContext, "", "", bWrapAround, bInterleaveQuotes, bIgnoreComments);
            for (String s : aFindText) {
                pMatch.SetFindText(s);
                if (pMatch.GetMatches() > 0) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * A static quick-replace function for ease of operation, includes the option to interleave(ignore) text that is wrapped inside of quotation marks
     *
     * @param sContext          The containing string to search in
     * @param sFindText         The text(s) to search for in the containing string
     * @param sReplaceText      The replacement text to insert wherever an instance of some targeted text is found
     * @param bWrapAround       If true, will only search for instances of 'sFindText' that are whole words
     * @param bInterleaveQuotes If true, will ignore any text that lives inside a pair of quotation marks (example: read..."ignored")
     * @param bIgnoreComments   True for only matching instances not inside of a comment-identifier
     * @param aPredicates       Optional predicate(s), found text will not be replaced if any of the given function arguments return false
     * @return True if there were any matches found of 'sFindText' inside the containing string, 'sContext'
     */
    public static String ReplaceAll(String sContext, String sFindText, String sReplaceText, boolean bWrapAround, boolean bInterleaveQuotes, boolean bIgnoreComments, Predicate<Match>... aPredicates) {
        StringMatch pMatch = new StringMatch(sContext, sFindText, sReplaceText, bWrapAround, bInterleaveQuotes, bIgnoreComments, aPredicates);
        return pMatch.GetMatches() > 0 ? pMatch.ReplaceAll() : sContext;
    }

    /**
     * Appends a string to the end of the container, BEFORE the commented portion of the container string
     *
     * @param sText The text to append
     * @param aPredicate Any conditional evaluations to make before appending to the non-commented part of the string
     * @return A string with the given text inserted at the end, before any comments
     */
    public String Append(String sText, Predicate<String>... aPredicate) {
        String sString, sComments = "";
        if (nCommentIndex >= 0) {
            sString = sContext.substring(0, nCommentIndex > 0 ? nCommentIndex - 1 : nCommentIndex).stripTrailing();
            sComments = sContext.substring(nCommentIndex);
        } else {
            sString = sContext;
        }
        if (aPredicate != null && aPredicate.length > 0) {
            for (Predicate<String> p : aPredicate) {
                if (!p.test(sString)) {
                    return sString + sComments;
                }
            }
        }
        return sString + sText + sComments;
    }

    /**
     * Checks to see whether or not the ending of the container string matches a given string, ignoring all whitespace padding
     *
     * @return True if the ending of the container string matches the string argument given
     */
    public boolean EndsWith(String sFindText) {
        if (sContext.length() > sFindText.length()) {
            char[] cFindText = sFindText.replaceAll("\t", "").trim().toCharArray();
            char[] cContext = sContext.replaceAll("\t", "").trim().toCharArray();
            for (int i = 1; i <= cFindText.length; i++) {
                if (cFindText[cFindText.length - i] != cContext[cContext.length - i]) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    /**
     * Reads the first whole word in the container string, useful for predicate arguments and specific operations
     *
     * @return The first word in the container string
     */
    public String GetFirstWholeWord() {
        return aWords.length > 0 ? aWords[0] : "";
    }

    /**
     * Reads the last, non-commented, whole word in the container string, useful for predicate arguments and specific operations
     *
     * @return The last, non-commented, word in the container string
     */
    public String GetLastWholeWord() {
        return aWords.length > 0 ? aWords[aWords.length - 1] : "";
    }

    /**
     * Gets the total number of matches found for during the search of 'sContext' for 'sFindText' using the current arguments
     *
     * @return The number of matches found
     */
    public int GetMatches() {
        return lMatches.size();
    }

    /**
     * Gets the total list of Match objects containing location-index information for each found match
     *
     * @return A list of Match objects, one for each string match that was found
     */
    public LinkedList<Match> GetMatchesList() {
        return lMatches;
    }

    /**
     * Determines whether or not a word is a whole word by analyzing the char values that are on opposite sides of the match in the container string
     *
     * @param cStart The char that sits 1 space before the string match text
     * @param cEnd   The char that sits 1 space after the string match text
     * @return True if both char values given can be valid values for a whole word to be in between
     */
    public boolean IsWholeWord(char cStart, char cEnd) {
        return (!Character.isLetter(cStart) && !Character.isLetter(cEnd));
                /*&&
                (cStart == ')' || cStart == '(' || cStart == '=' || cStart == '.' || cStart == ':' || cStart == '\"'
                        || (Character.isJavaIdentifierStart(cStart) || Character.isIdentifierIgnorable(cStart)
                        || Character.isJavaIdentifierPart(cStart) || Character.isSpaceChar(cStart) || Character.isWhitespace(cStart)))
                &&
                (cEnd == ')' || cEnd == '(' || cEnd == ';' || cEnd == ',' || cEnd == '.' || cEnd == ':' || cEnd == '\"'
                        || (Character.isJavaIdentifierStart(cEnd) || Character.isIdentifierIgnorable(cEnd)
                        || Character.isJavaIdentifierPart(cEnd) || Character.isSpaceChar(cEnd) || Character.isWhitespace(cEnd)))
                &&
                (!Character.isAlphabetic(cStart) && !Character.isAlphabetic(cEnd));*/
    }

    /**
     * A method to check for matches of a string other than the designated find-text using the same search arguments
     *
     * @param sFindText The text to search for
     * @return True if the containing string has any matches for the given text
     */
    public boolean Match(String sFindText) {
        return Match(sContext, bWrapAround, bInterleaveQuotes, bIgnoreComments, sFindText);
    }

    /**
     * Prints out the current string-matches found for user reference
     */
    public void PrintMatches() {
        System.out.println("StringMatch[locations-list]:");
        for (Match pMatch : GetMatchesList()) {
            System.out.println("\t[" + pMatch.GetStartIndex() + "-(nStartIndex), " + pMatch.GetEndIndex() + "-(nEndIndex)]  " + pMatch.GetString());
        }
    }

    /**
     * Prints out the current string-match/replace information for user reference
     */
    public void PrintSearchInfo() {
        System.out.println("StringMatch[to-string]:");
        System.out.println("\tOriginal string:      \"" + sContext.trim() + "\" [padding: " + StringUtil.GetLinePadding(sContext) + "]");
        System.out.println("\tMarked string:        \"" + sContextMarked.trim() + "\" [padding: " + StringUtil.GetLinePadding(sContextMarked) + "]");
        System.out.println("\tText to find:         \"" + sFindText + "\"");
        System.out.println("\tText to replace:      \"" + sReplaceText + "\"");
        System.out.println("\tTargets to replace:   [" + lMatches.size() + "]");
        System.out.println("\tMatch-Whole-Word:     [" + bWrapAround + "]");
        System.out.println("\tNew string:           \"" + ReplaceAll() + "\"");
    }

    /**
     * Replaces all matches found inside the containing string and returns a newly created String object
     *
     * @return A string with all matches replaced
     */
    public String ReplaceAll() {
        if (lMatches.size() > 0) {
            String sMatch = sContextMarked;
            for (int i = 1; i <= lMatches.size(); i++) {
                sMatch = sMatch.replace(("$->string-location-" + i + "$"), sReplaceText);
            }
            return sMatch;
        }
        return sContext;
    }

    /**
     * Replaces only the first string match found with the replacement text, reverts all other matches found to the original text
     *
     * @return A string with the first match only replaced
     */
    public String ReplaceFirst() {
        if (lMatches.size() > 0) {
            String sMatch = sContextMarked;
            for (int i = 1; i <= lMatches.size(); i++) {
                if (i == 1) {
                    sMatch = sMatch.replace("$->string-location-" + i + "$", sReplaceText);
                } else {
                    sMatch = sMatch.replace("$->string-location-" + i + "$", sFindText);
                }
            }
            return sMatch;
        }
        return sContext;
    }

    /**
     * Replaces only the last string match found with the replacement text, reverts all other matches found to the original text
     *
     * @return A string with the last match only replaced
     */
    public String ReplaceLast() {
        if (lMatches.size() > 0) {
            String sMatch = sContextMarked;
            for (int i = lMatches.size(); i > 0; i--) {
                if (i == lMatches.size()) {
                    sMatch = sMatch.replace("$->string-location-" + i + "$", sReplaceText);
                } else {
                    sMatch = sMatch.replace("$->string-location-" + i + "$", sFindText);
                }
            }
            return sMatch;
        }
        return sContext;
    }

    /**
     * Resets all of the search object's values and arguments before relocating matches in the container string
     *
     * @param sContext          The full String/char[] set in which to look for some targeted text
     * @param sFindText         The text(s) to search for in the containing string
     * @param sReplaceText      The replacement text to insert wherever an instance of some targeted text is found
     * @param bWrapAround       If true, will only search for instances of 'sFindText' that are whole words
     * @param bInterleaveQuotes If true, will ignore any text that lives inside a pair of quotation marks (example: read..."ignored")
     * @param bIgnoreComments   True for only matching instances not inside of a comment-identifier
     * @param aPredicates       Optional predicate(s), found text will not be replaced if any of the given function arguments return false
     */
    public final void Reset(String sContext, String sFindText, String sReplaceText, boolean bWrapAround, boolean bInterleaveQuotes, boolean bIgnoreComments, Predicate<Match>... aPredicates) {
        this.sContext = sContext;
        this.sReplaceText = sReplaceText;
        this.bWrapAround = bWrapAround;
        this.bInterleaveQuotes = bInterleaveQuotes;
        this.bIgnoreComments = bIgnoreComments;
        this.aPredicates = aPredicates;
        SetFindText(sFindText);
    }

    /**
     * Resets all of the search object's values, retains their arguments, and relocates new matches in the container string
     *
     * @param sContext          The full String/char[] set in which to look for some targeted text
     * @param sFindText         The text(s) to search for in the containing string
     * @param sReplaceText      The replacement text to insert wherever an instance of some targeted text is found
     * @param aPredicates       Optional predicate(s), found text will not be replaced if any of the given function arguments return false
     */
    public final void Reset(String sContext, String sFindText, String sReplaceText, Predicate<Match>... aPredicates) {
        Reset(sContext, sFindText, sReplaceText, bWrapAround, bInterleaveQuotes, bIgnoreComments, aPredicates);
    }

    /**
     * Sets the find text to a new string sub-sequence; resets any previous search values and locates matches with the current arguments
     *
     * @param sFindText The text to search for
     */
    public final void SetFindText(String sFindText) {
        sContextMarked = "";
        lMatches = new LinkedList<>();
        aWords = new String[]{};
        this.sFindText = sFindText;
        if (sContext != null && !sContext.isBlank()) {
            if (sFindText != null && !sFindText.isBlank()) {
                if (!sContext.trim().equals(sFindText.trim())) {
                    boolean bFoundComments = false;
                    int nLen = this.sFindText.length();
                    int nQuote = 0;
                    char[] aCharList = sContext.toCharArray();
                    for (int i = 0; i < aCharList.length; i++) {
                        if (bInterleaveQuotes && aCharList[i] == '"') {
                            nQuote++;
                        }
                        if (!bInterleaveQuotes || nQuote % 2 == 0) {
                            if (sContext.length() >= i + nLen) {
                                if (nQuote % 2 == 0 && sContext.length() > i + nLen) {
                                    if ((aCharList[i] == '/' && aCharList.length > (i + 1) && aCharList[i + 1] == '/') || aCharList[i] == '#') {
                                        bFoundComments = true;
                                        nCommentIndex = i;
                                    }
                                }
                                if (!bFoundComments || !bIgnoreComments) {
                                    String s = sContext.substring(i, (i + nLen));
                                    if (s.equals(sFindText)) {
                                        if (bWrapAround) {
                                            int nStart = i > 0 ? i - 1 : -1;
                                            int nEnd = sContext.length() > (i + nLen) ? (i + nLen) : -1;
                                            if (!IsWholeWord(nStart == -1 ? ' ' : aCharList[nStart], nEnd == -1 ? ' ' : aCharList[nEnd])) {
                                                sContextMarked += aCharList[i];
                                                continue;
                                            }
                                        }
                                        Match pMatch = new Match(sContext, s, i, (i + nLen));
                                        if (aPredicates != null && aPredicates.length > 0) {
                                            for (Predicate<Match> p : aPredicates) {
                                                if (!p.test(pMatch)) {
                                                    sContextMarked += aCharList[i];
                                                    continue;
                                                }
                                            }
                                        }
                                        lMatches.add(new Match(sContext, s, i, (i + nLen)));
                                        sContextMarked += "$->string-location-" + lMatches.size() + "$";
                                        i += nLen;
                                    }
                                }
                            }
                        }
                        if (sContext.length() > i) {
                            sContextMarked += aCharList[i];
                        }
                    }
                } else {
                    int nStart = StringUtil.GetLinePadding(sContext);
                    int nEnd = nStart + sFindText.length() - 1;
                    if (sContext.length() > nEnd) {
                        lMatches.add(new Match(sContext, sFindText, nStart, nEnd));
                        sContextMarked = StringUtil.AddStringPaddingChar(("$->string-location-" + lMatches.size() + "$"), nStart);
                    }
                }
                String s = nCommentIndex >= 0 && sContext.length() > nCommentIndex ? sContext.substring(0, nCommentIndex) : sContext;
                if (s.contains(" ")) {
                    aWords = s.split(" ");
                }
            } else {
                if (sContext.contains(" ")) {
                    aWords = sContext.split(" ");
                }
            }
            int nQuote = 0;
            char[] aCharList = sContext.toCharArray();
            for (int i = 0; i < aCharList.length; i++) {
                if (aCharList[i] == '"') {
                    nQuote++;
                }
                if (nQuote % 2 == 0) {
                    if ((aCharList[i] == '/' && aCharList.length > (i + 1) && aCharList[i + 1] == '/') || aCharList[i] == '#') {
                        nCommentIndex = i;
                    }
                }
            }
        }
    }

    /**
     * Sets the find text to a new string sub-sequence; resets any previous search values and locates matches with the current arguments
     *
     * @param sFindText        The text to search for
     * @param bClearPredicates True to clear current predicate function arguments
     */
    public final void SetFindText(String sFindText, boolean bClearPredicates) {
        if (bClearPredicates) {
            aPredicates = null;
        }
        SetFindText(sFindText);
    }

    /**
     * Sets the find text to a new string sub-sequence; adds predicate evaluation arguments; resets any previous search values and locates matches with the current arguments
     *
     * @param sFindText   The text to search for
     * @param aPredicates The predicate evaluation arguments to use before declaring a match
     */
    public final void SetFindText(String sFindText, Predicate<Match>... aPredicates) {
        this.aPredicates = aPredicates;
        SetFindText(sFindText);
    }

    /**
     * Sets the argument for ignoring text hidden by comment identifiers
     *
     * @param bIgnoreComments True to only find matches that are not inside of comments
     */
    public void SetIsIgnoreComments(boolean bIgnoreComments) {
        this.bIgnoreComments = bIgnoreComments;
    }

    /**
     * Sets the argument for ignoring text inside of quotation marks
     *
     * @param bInterleaveQuotes True to only find matches that are not inside of quotation marks
     */
    public void SetIsIgnoreQuotes(boolean bInterleaveQuotes) {
        this.bInterleaveQuotes = bInterleaveQuotes;
    }

    /**
     * Sets the argument for only matching whole word instances of the find-text
     *
     * @param bWrapAround True to only find whole-word matches
     */
    public void SetIsWrapAround(boolean bWrapAround) {
        this.bWrapAround = bWrapAround;
    }

    /**
     * Sets flexible predicate arguments that apply an arbitrary condition over every match and only discover the match if all predicate arguments return true
     *
     * @param aPredicates A number of flexible Match arguments to be used as conditional evaluations in the search
     */
    public void SetPredicate(Predicate<Match>... aPredicates) {
        this.aPredicates = aPredicates;
    }

    /**
     * Sets the replacement text, or the text that is to replace any or all instances of the find-text inside the container string
     *
     * @param sReplaceText The desired text to replace the find-text matches found
     */
    public void SetReplaceText(String sReplaceText) {
        this.sReplaceText = sReplaceText;
    }

    /**
     * Sets the container string to a new value
     *
     * @param sContext The container string to search for matches of 'sFindText' in
     */
    public void SetContext(String sContext) {
        this.sContext = sContext;
    }

    /**
     * Checks to see whether or not the beginning of the container string matches a given string, ignoring all whitespace padding
     *
     * @return True if the beginning of the container string matches the string argument given
     */
    public boolean StartsWith(String sFindText) {
        char[] cFindText = sFindText.replaceAll("\t", "").trim().toCharArray();
        char[] cContext = sContext.replaceAll("\t", "").trim().toCharArray();
        for (int i = 0; i < cFindText.length; i++) {
            if (cFindText[i] != cContext[i]) {
                return false;
            }
        }
        return true;
    }

    public static class Match {

        private int nStartIdx, nEndIdx;
        private String sContext, sFindText;

        /**
         * Represents a string sub-sequence match through start / end char position relative to its position in the container string
         *
         * @param sContext  The containing string to search in
         * @param sFindText The text to search for in the containing string
         * @param nStartIdx The starting character position for this match instance in the container string
         * @param nEndIdx   The ending character position for this match instance in the container string
         */
        public Match(String sContext, String sFindText, int nStartIdx, int nEndIdx) {
            this.sContext = sContext;
            this.sFindText = sFindText;
            this.nStartIdx = nStartIdx;
            this.nEndIdx = nEndIdx;
        }

        /**
         * Stores the character index of the last character in this match instance relative to the containing string
         *
         * @return The ending index of this string match in the container
         */
        public int GetEndIndex() {
            return nEndIdx;
        }

        /**
         * Stores the text searched for in the container string, use for referencing in the event of Precicate<Match> arguments
         *
         * @return The text searched for in the container
         */
        public String GetFindText() {
            return sFindText;
        }

        /**
         * Stores the character index of the first character in this match instance relative to the containing string
         *
         * @return The starting index of this string match in the container
         */
        public int GetStartIndex() {
            return nStartIdx;
        }

        /**
         * Stores the original container string, use for referencing in the event of Predicate<Match> arguments
         *
         * @return The original container string
         */
        public String GetString() {
            return sContext;
        }
    }
}
