/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package message;

/**
 *
 * @author Sharky
 */
public class MessageType {
        
    public static final byte Say = 0,
                        SayImage = 2,
                        AskYesNo = 3,
                        AskText = 4,
                        AskNumber = 5,
                        AskMenu = 6,
                        AskQuiz = 7,
                        AskSpeedQuiz = 8,
                        AskIcQuiz = 9,
                        AskAvatar = 10,
                        AskAndroid = 11,
                        Unknown = 12, // AskAndroid2?
                        AskPet = 13,
                        AskPetAll = 14,
                        AskActionPetEvolution = 15,
                        Script = 16,
                        AskAccept = 17,
                        AskBoxText = 19,
                        AskSlideMenu = 20,
                        AskInGameDirection = 21,
                        PlayMovieClip = 22,
                        AskCenter = 23,
                        Unknown2 = 24,
                        AskSelectMenu = 26,
                        AskAngelicBuster = 27,
                        SayIllustration = 28,
                        SayDualIllustration = 29,
                        AskYesNoIllustration = 30,
                        AskAcceptIllustration = 31,
                        AskMenuIllustration = 32,
                        AskYesNoDualIllustration = 33,
                        AskAcceptDualIllustration = 34,
                        AskMenuDualIllustration = 35,
                        AskSSN2 = 36,
                        AskAvatarZero = 37,
                        Unknown3 = 38,
                        Unknown4 = 39,
                        Monologue = 40,
                        AskWeaponBox = 41,
                        AskBoxTextBgImg = 42,
                        AskUserSurvey = 43,
                        SuccessCamera = 44,
                        AskMixHair = 45,
                        AskMixHairZero = 46,
                        AskCustomMixHair = 47,
                        AskCustomMixHairAndProb = 48,
                        AskMixHairNew = 49,
                        AskMixHairNewZero = 50,
                        NpcAction = 51,
                        AskScreenShinningStarMsg = 52,
                        InputUI = 53,
                        AskNumberKeypad = 55,
                        SpinoffGuitarRhythmGame = 56,
                        AskGhostParkEnterUI = 57,
                        CameraMsg = 58,
                        SlidePuzzle = 59,
                        Disguise = 60,
                        NeedClientResponse = 61;
        
    public static final int NoESC = 0x1,
                        NpcReplacedByUser = 0x2,
                        NpcReplacedByUser2 = 0x3,
                        NpcReplayedByNpc = 0x4,
                        FlipImage = 0x8,
                        NpcReplayedByUserLeft = 0x10,
                        ScenarioIlluChat = 0x20,
                        NoEnter = 0x40,
                        ScenarioIlluChatXL = 0x80;

}
