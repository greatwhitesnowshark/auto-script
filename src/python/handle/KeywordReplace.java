package python.handle;

import util.StringMatch;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 *
 * @author Sharky
 */
public class KeywordReplace extends AbstractHandler {

    public static KeywordReplace pInstance = new KeywordReplace();
    public static Map<String, String> mFunctionKeywordReplace = new LinkedHashMap<>(); //all text-to-text replacements

    @Override
    public String Convert(String sScriptLine) {
        for (String sKey : mFunctionKeywordReplace.keySet()) {
            StringMatch pMatch = new StringMatch(sScriptLine, sKey, mFunctionKeywordReplace.get(sKey), !sKey.startsWith(".") && !sKey.endsWith(".") && !sKey.endsWith("("), true, true);
            if (pMatch.GetMatches() > 0) {
                sScriptLine = pMatch.ReplaceAll();
            }
        }
        return sScriptLine;
    }


    static {

        mFunctionKeywordReplace.put("giveNewSecondary", "UserEquipNewItem");
        mFunctionKeywordReplace.put("parentID", "self.GetTemplateID()");
        mFunctionKeywordReplace.put("self.GetTemplateID", "self.GetTemplateID");
        mFunctionKeywordReplace.put("self.showEffect(","self.OnUserInGameDirectionEvent(InGameDirectionEvent.EffectPlay, ");
        mFunctionKeywordReplace.put("sm.showEffect(","self.OnUserInGameDirectionEvent(InGameDirectionEvent.EffectPlay, ");
        mFunctionKeywordReplace.put("addAP","UserIncAP");
        mFunctionKeywordReplace.put("addDamageSkin","UserSaveDamageSkin");
        mFunctionKeywordReplace.put("addLevel","UserIncLevel");
        mFunctionKeywordReplace.put("addMaxHP","UserIncMHP");
        mFunctionKeywordReplace.put("addMaxMP","UserIncMMP");
        mFunctionKeywordReplace.put("addSP","UserIncSP");
        mFunctionKeywordReplace.put("addSp","UserIncSP");
        mFunctionKeywordReplace.put("avatarLookSet(","OnUserInGameDirectionEvent(InGameDirectionEvent.AvatarLookSet, ");
        mFunctionKeywordReplace.put("avatarOriented","EffectAvatarOriented");
        mFunctionKeywordReplace.put("addPopupSay","UserAddPopupSay");
        mFunctionKeywordReplace.put("addPopUpSay","UserAddPopupSay");
        mFunctionKeywordReplace.put("addQRValue","QuestRecordSet");
        mFunctionKeywordReplace.put("updateQRValue","QuestRecordSet");
        mFunctionKeywordReplace.put("balloonMsg","UserBalloonMsg");
        mFunctionKeywordReplace.put("blind","FieldEffectBlind");
        mFunctionKeywordReplace.put("bgmVolume","FieldEffectBGMVolumeOnly");
        mFunctionKeywordReplace.put("canHold","InventoryIsSlotFreeItemID");
        mFunctionKeywordReplace.put("changeBGM","FieldEffectChangeBGM");
        mFunctionKeywordReplace.put("changeChannelAndWarp","RegisterTransferFieldChannel"); //review these to determine if these should be instanced locations
        mFunctionKeywordReplace.put("changeCharacterLook","TryChangeHairSkinOrFace");
        mFunctionKeywordReplace.put("changeFoothold","SendDynamicObjUrusSync");
        mFunctionKeywordReplace.put("chatBlue","UserScriptMessage");
        mFunctionKeywordReplace.put("chatRed","UserScriptMessage");
        mFunctionKeywordReplace.put("chat","UserScriptMessage");
        mFunctionKeywordReplace.put("chatScript","UserScriptProgressMessage");
        mFunctionKeywordReplace.put("checkAllianceName","IsAllianceNameFree");
        mFunctionKeywordReplace.put("checkParty","UserIsPartyReadyCheck");
        mFunctionKeywordReplace.put("closeUI","UserCloseUI");
        mFunctionKeywordReplace.put("completeQuest","QuestRecordSetComplete");
        mFunctionKeywordReplace.put("completeQuestNoCheck","QuestRecordSetComplete");
        mFunctionKeywordReplace.put("completeQuestNoRewards","QuestRecordSetComplete");
        mFunctionKeywordReplace.put("consumeItem","UserStatChangeItemUseRequest");
        mFunctionKeywordReplace.put("createClock","FieldClock");
        mFunctionKeywordReplace.put("createFallingCatcher","CreateFieldFallingCatcher");
        mFunctionKeywordReplace.put("createFieldTextEffect","OnUserTextEffect");
        mFunctionKeywordReplace.put("createStopWatch","FieldClockStopwatch");
        mFunctionKeywordReplace.put("createQuestWithQRValue","QuestRecordExSet");
        mFunctionKeywordReplace.put("curNodeEventEnd","OnInGameCurNodeEventEnd");
        mFunctionKeywordReplace.put("deductMesos","UserDeductMoney");
        mFunctionKeywordReplace.put("deleteQuest","QuestRecordRemove");
        mFunctionKeywordReplace.put("delta","dlta"); //sharky note:: for python keyword 'del'
        mFunctionKeywordReplace.put("dropItem","FieldDropItem");
        mFunctionKeywordReplace.put("faceOff(","OnUserInGameDirectionEvent(InGameDirectionEvent.FaceOff, ");
        mFunctionKeywordReplace.put("fadeInOut","OnUserFadeInOutEffect");
        mFunctionKeywordReplace.put("False","false");
        mFunctionKeywordReplace.put("forcedAction(","OnUserInGameDirectionEvent(InGameDirectionEvent.ForcedAction, ");
        mFunctionKeywordReplace.put("forcedFlip(","OnUserInGameDirectionEvent(InGameDirectionEvent.ForcedFlip, ");
        mFunctionKeywordReplace.put("forcedInput(","OnUserInGameDirectionEvent(InGameDirectionEvent.ForcedInput, ");
        mFunctionKeywordReplace.put("forcedMove(","OnUserInGameDirectionEvent(InGameDirectionEvent.ForcedMove, ");
        mFunctionKeywordReplace.put("gainItem","InventoryItemExchange");
        mFunctionKeywordReplace.put("getChr","GetUser");
        mFunctionKeywordReplace.put("getDay","GetDay");
        mFunctionKeywordReplace.put("getFieldID","UserGetFieldID");
        mFunctionKeywordReplace.put("getMesos","UserGetMoney");
        mFunctionKeywordReplace.put("partyIsQuestInProgress", "UserPartyIsQuestInProgress");
        mFunctionKeywordReplace.put("getPartyMembersInSameField","UserGetPartyMemberFieldCount");
        mFunctionKeywordReplace.put("getQRValue","QuestRecordExGet");
        mFunctionKeywordReplace.put("getQuantityOfItem","InventoryGetItemCount");
        mFunctionKeywordReplace.put("getRandomIntBelow","GetRand");
        mFunctionKeywordReplace.put("getReactorQuantity","FieldGetReactorCount");
        mFunctionKeywordReplace.put("getReactorState","FieldGetReactorState");
        mFunctionKeywordReplace.put("getReturnField","GetReturnFieldID");
        mFunctionKeywordReplace.put("giveExp","UserIncEXP");
        mFunctionKeywordReplace.put("giveItem","InventoryItemExchange");
        mFunctionKeywordReplace.put("giveMesos","UserIncMoney");
        mFunctionKeywordReplace.put("giveSkill","UserLearnSkill");
        mFunctionKeywordReplace.put("golluxPortalOpen","FieldGolluxPortalEnable");
        mFunctionKeywordReplace.put("hasHadQuest","IsQuestActive");
        mFunctionKeywordReplace.put("hasItem","InventoryIsHoldingItemCount");
        mFunctionKeywordReplace.put("field.getLifeByTemplateId", "FieldGetMobByID");
        mFunctionKeywordReplace.put("hasMobsInField","FieldMobExists");
        mFunctionKeywordReplace.put("hasQuestWithValue","QuestRecordExExists");
        mFunctionKeywordReplace.put("hasQuestCompleted","IsQuestComplete");
        mFunctionKeywordReplace.put("hasQuest","IsQuestInProgress");
        mFunctionKeywordReplace.put("hasSkill","UserSkillIsLearned");
        mFunctionKeywordReplace.put("hasTutor","UserHasHireTutor");
        mFunctionKeywordReplace.put("heal","UserHealMax");
        mFunctionKeywordReplace.put("hideNpcByTemplateId","FieldNpcViewOrHide");
        mFunctionKeywordReplace.put("hideUser(","OnUserInGameDirectionEvent(InGameDirectionEvent.VansheeMode, ");
        mFunctionKeywordReplace.put("hireTutor","UserHireTutor");
        mFunctionKeywordReplace.put("increaseReactorState","FieldSetReactorState");
        mFunctionKeywordReplace.put("isEquipped","InventoryIsEquippedItem");
        //mFunctionKeywordReplace.put("isFinishedEscort", "isFinishedEscort"); //todo:: remove usages of this, looks custom
        mFunctionKeywordReplace.put("isPartyLeader","UserIsPartyBoss");
        mFunctionKeywordReplace.put("killMobs","FieldRemoveAllMob");
        mFunctionKeywordReplace.put("killmobs","FieldRemoveAllMob");
        mFunctionKeywordReplace.put("levelUntil","UserIncLevelSet");
        mFunctionKeywordReplace.put("localEmotion","UserLocalEmotion");
        mFunctionKeywordReplace.put("lockInGameUI","OnSetInGameDirectionMode");
        mFunctionKeywordReplace.put("moveCamera(","OnUserInGameDirectionEvent(InGameDirectionEvent.CameraMove, ");
        mFunctionKeywordReplace.put("moveCameraBack(","OnUserInGameDirectionEvent(InGameDirectionEvent.CameraMove, true, ");
        mFunctionKeywordReplace.put("moveLayer","EffectOnOffLayer");
        mFunctionKeywordReplace.put("moveNpcByTemplateId","OnForceMoveByScript"); //todo:: verify that the arguments passed correspond
        mFunctionKeywordReplace.put("moveParticleEff","UserMoveParticleEff");
        mFunctionKeywordReplace.put("offLayer","EffectOffLayer");
        mFunctionKeywordReplace.put("spineScreen","FieldEffectSpineScreen");
        mFunctionKeywordReplace.put("offSpineScreen","FieldEffectOffSpineScreen");
        mFunctionKeywordReplace.put("onLayer","EffectOnLayer");
        mFunctionKeywordReplace.put("openDimensionalMirror","UserOpenUIDimensionalMirror");//todo:: TEST & see if/how this works with AskSlideMenu
        mFunctionKeywordReplace.put("openNodestone","ConsumeNodestone");
        mFunctionKeywordReplace.put("openNpc","UserEnforceNpcChat");
        mFunctionKeywordReplace.put("openUI","UserOpenUI");
        mFunctionKeywordReplace.put("patternInputRequest(","OnUserInGameDirectionEvent(InGameDirectionEvent.PatternInputRequest, ");
        mFunctionKeywordReplace.put("playExclSoundWithDownBGM","EffectPlayExclSoundWithDownBGM");
        mFunctionKeywordReplace.put("playSound","EffectSound");
        mFunctionKeywordReplace.put("progressMessageFont","SendProgressMessageFont");
        mFunctionKeywordReplace.put("randomInt","rndInt"); //sharky note:: so that the 'and' python keyword doesn't pick this shit up
        mFunctionKeywordReplace.put("removeCTS","UserRemoveBuff"); //todo:: probably need to change arguments if they used objects for CTS
        mFunctionKeywordReplace.put("removeAdditionalEffect(","OnUserInGameDirectionEvent(InGameDirectionEvent.RemoveAdditionalEffect");
        mFunctionKeywordReplace.put("removeMobByTemplateId","FieldRemoveMob");
        mFunctionKeywordReplace.put("removeNpc","FieldVanishNpc");
        mFunctionKeywordReplace.put("removeOverlapScreen","FieldEffectRemoveOverlapDetail");
        mFunctionKeywordReplace.put("removeReactor","FieldRemoveAllReactor");
        mFunctionKeywordReplace.put("chr.removeSkillAndSendPacket", "self.UserRemoveSkill");
        mFunctionKeywordReplace.put("removeSkill","UserRemoveSkill");
        mFunctionKeywordReplace.put("reservedEffect","EffectReserved");
        mFunctionKeywordReplace.put("reservedEffectRepeat","EffectReservedRepeat");
        mFunctionKeywordReplace.put("resetNpcSpecialActionByTemplateId","NpcResetSpecialAction");
        mFunctionKeywordReplace.put("resetParam","ResetModifyFlag");
        mFunctionKeywordReplace.put("sayMonologue(","OnUserInGameDirectionEvent(InGameDirectionEvent.Monologue, ");
        mFunctionKeywordReplace.put("sendAskAccept","AskAccept");
        mFunctionKeywordReplace.put("sendAskAvatar","AskAvatar");
        mFunctionKeywordReplace.put("sendAskMenuNext","AskMenuNoESC");
        mFunctionKeywordReplace.put("sendAskNumber","AskNumber");
        mFunctionKeywordReplace.put("sendAskSelectMenu","AskMenu");
        mFunctionKeywordReplace.put("sendAskText","AskText");
        mFunctionKeywordReplace.put("sendAskYesNo","AskYesNo");
        mFunctionKeywordReplace.put("sendPrev","Say");
        mFunctionKeywordReplace.put("sendSay","Say");
        mFunctionKeywordReplace.put("sendNext","Say");
        mFunctionKeywordReplace.put("sendDelay(","OnUserInGameDirectionEvent(InGameDirectionEvent.Delay, ");
        mFunctionKeywordReplace.put("setAP","UserSetAP");
        mFunctionKeywordReplace.put("setCameraOnNpc(","OnUserInGameDirectionEvent(InGameDirectionEvent.CameraZoom, ");
        mFunctionKeywordReplace.put("setDEX","UserSetDex");
        mFunctionKeywordReplace.put("setDeathCount","UserSetDeathCount");
        mFunctionKeywordReplace.put("setFieldColour","setFieldColour");
        mFunctionKeywordReplace.put("setHp","UserSetHP");
        mFunctionKeywordReplace.put("setINT","UserSetINT");
        mFunctionKeywordReplace.put("setLUK","UserSetLUK");
        mFunctionKeywordReplace.put("setLevel","UserIncLevelSet");
        mFunctionKeywordReplace.put("setMaxHp","UserSetMHP");
        mFunctionKeywordReplace.put("setMaxMp","UserSetMMP");
        mFunctionKeywordReplace.put("setMp","UserSetMP");
        mFunctionKeywordReplace.put("setQRValue","QuestRecordExSet");
        mFunctionKeywordReplace.put("setReturnField","UserSetReturnFieldID");
        mFunctionKeywordReplace.put("setSpeakerID","SetNPCTemplateID");
        mFunctionKeywordReplace.put("setInnerOverrideSpeakerTemplateID","SetNPCTemplateID");
        mFunctionKeywordReplace.put("setSpeakerType","SetNPCType");
        mFunctionKeywordReplace.put("setColor","SetNPCColor");
        mFunctionKeywordReplace.put("setSTR","UserSetSTR");
        mFunctionKeywordReplace.put("setUnionCoin","UserSetUnionCoin");
        mFunctionKeywordReplace.put("showBalloonMsgOnNpc","UserBalloonMsg/*OnNpc*/");
        mFunctionKeywordReplace.put("showBalloonMsg","UserBalloonMsg");
        mFunctionKeywordReplace.put("showEffect(","OnUserInGameDirectionEvent(InGameDirectionEvent.EffectPlay, ");
        mFunctionKeywordReplace.put("showNpcSpecialActionByTemplateId","NpcSetSpecialAction");
        mFunctionKeywordReplace.put("showFadeTransition","FieldEffectOverlapDetail");
        mFunctionKeywordReplace.put("spawnNpc","FieldSummonNpc");
        mFunctionKeywordReplace.put("startQuestNoCheck", "QuestRecordTryStartAct");
        mFunctionKeywordReplace.put("startQuest","QuestRecordTryStartAct");
        mFunctionKeywordReplace.put("int(", "parseInt(");
        mFunctionKeywordReplace.put("str(","(");
        mFunctionKeywordReplace.put("systemMessage","UserScriptMessage");
        mFunctionKeywordReplace.put("True","true");
        mFunctionKeywordReplace.put("warpField", "RegisterTransferField");
        mFunctionKeywordReplace.put("warpPartyIn","UserTransferPartyNoInstance");
        mFunctionKeywordReplace.put("warp","RegisterTransferField");
        mFunctionKeywordReplace.put("zoomCamera(","OnUserInGameDirectionEvent(InGameDirectionEvent.CameraZoom, ");
        mFunctionKeywordReplace.put("zoomCameraNoResponse(","OnUserInGameDirectionEvent(InGameDirectionEvent.CameraZoom, ");
        mFunctionKeywordReplace.put("oxChatPlayerAsSpeaker(","SetModifyFlag(SpeakerTypeID.ScenarioIlluChat, SpeakerTypeID.NpcReplacedByUser");
        mFunctionKeywordReplace.put("flipBoxChat(","SetModifyFlag(SpeakerTypeID.ScenarioIlluChat, SpeakerTypeID.FlipImage");
        mFunctionKeywordReplace.put("flipDialogue(","SetModifyFlag(SpeakerTypeID.NpcReplayedByNpc");
        mFunctionKeywordReplace.put("flipDialoguePlayerAsSpeaker(","SetModifyFlag(SpeakerTypeID.FlipImage, SpeakerTypeID.NpcReplacedByUser");
        mFunctionKeywordReplace.put("flipSpeaker(","SetModifyFlag(SpeakerTypeID.FlipImage");
        mFunctionKeywordReplace.put("setPlayerAsSpeaker(","SetModifyFlag(SpeakerTypeID.NpcReplacedByUser");
        mFunctionKeywordReplace.put("removeEscapeButton(","SetModifyFlag(SpeakerTypeID.NoESC");
        mFunctionKeywordReplace.put("setBoxChat(","SetModifyFlag(SpeakerTypeID.ScenarioIlluChat");
        mFunctionKeywordReplace.put("addEscapeButton(","ResetModifyFlag(SpeakerTypeID.NoESC");
        mFunctionKeywordReplace.put("flipBoxChatPlayerNoEscape(","SetModifyFlag(SpeakerTypeID.ScenarioIlluChat, SpeakerTypeID.FlipImage, SpeakerTypeID.NpcReplacedByUser, SpeakerTypeID.NoESC");
        mFunctionKeywordReplace.put("sendAskSlideMenu","AskSlideMenu");
        mFunctionKeywordReplace.put("scriptInfo.","self.");
        mFunctionKeywordReplace.put("removeParam","ResetModifyFlag");
        mFunctionKeywordReplace.put("addParam","SetModifyFlag");
        mFunctionKeywordReplace.put("setParam","SetModifyFlag");
        mFunctionKeywordReplace.put("showFieldEffect","EffectScreenAutoLetterBox");
        mFunctionKeywordReplace.put("flipNpcByTemplateId","SetForceFlip");
        mFunctionKeywordReplace.put("flipBSetModifyFlag(","SetModifyFlag(SpeakerTypeID.ScenarioIlluChat, SpeakerTypeID.FlipImage");
        mFunctionKeywordReplace.put("spawnMobWithAppearType","FieldSummonMob");
        mFunctionKeywordReplace.put("spawnMob","FieldSummonMob");
        mFunctionKeywordReplace.put("setJob","UserJob");
        mFunctionKeywordReplace.put("setMapTaggedObjectVisible","UserSetMapTaggedObjectVisible");
        mFunctionKeywordReplace.put("speechBalloon","UserSpeechBalloonEffect");
        mFunctionKeywordReplace.put("jobAdvance","UserJobAdvance");
        mFunctionKeywordReplace.put("getPartySize","UserGetPartyMemberCount");
        mFunctionKeywordReplace.put("getParty","UserGetPartyData");
        mFunctionKeywordReplace.put("giveAndEquip","UserEquipNewItem");
        mFunctionKeywordReplace.put("teleportToPortal","UserTeleport");
        mFunctionKeywordReplace.put("showWeatherNoticeToField","UserWeatherEffectNotice");
        mFunctionKeywordReplace.put("showWeatherNotice","UserWeatherEffectNotice");
        mFunctionKeywordReplace.put("getnOptionByCTS","UserGetNOption");
        mFunctionKeywordReplace.put("invokeForParty","UserExecFuncForParty"); //(Has TODO: Sharky Shit for the actual invoking of func)
        mFunctionKeywordReplace.put("rideVehicle","UserRideVehicle");
        mFunctionKeywordReplace.put("setFieldColour","EffectColorChange");
        mFunctionKeywordReplace.put("teleportInField","UserTeleportPosition");
        mFunctionKeywordReplace.put("tutorAutomatedMsg","UserTutorialMsg"); //(Must exec UserHireTutor first)
        mFunctionKeywordReplace.put("setFieldGrey","EffectGrayScale");
        mFunctionKeywordReplace.put("showObjectFieldEffect","EffectObject");
        mFunctionKeywordReplace.put("showWeatherNoticeToField","FieldWeatherEffectNotice");
        mFunctionKeywordReplace.put("giveCTS","UserGiveCTS");
        mFunctionKeywordReplace.put("setFuncKeyByScript","UserSetFuncKey");
        mFunctionKeywordReplace.put("showEffectToField","FieldEffectUOL");
        mFunctionKeywordReplace.put("showHP","MobShowHP");
        mFunctionKeywordReplace.put("useItem","UserGiveBuff");
        mFunctionKeywordReplace.put("getOffFieldEffectFromWz","EffectTopScreenDelayed");
        mFunctionKeywordReplace.put("showFieldBackgroundEffect","EffectScreenDelayed");
        mFunctionKeywordReplace.put("spawnMobOnChar","FieldSummonMobOnChar");
        mFunctionKeywordReplace.put("getDropInRect","FieldFindDropInRect");
        mFunctionKeywordReplace.put("getPartySize","UserGetPartyMemberCount");
        mFunctionKeywordReplace.put("hasCTS","UserHasCTS");
        mFunctionKeywordReplace.put("showOffFieldEffect","EffectTopScreenDelayed");
        mFunctionKeywordReplace.put("tutorCustomMsg","UserTutorialMsg"); //Must exec HireTutor first
        mFunctionKeywordReplace.put("getSkillByItem","UserGetSkillByItemID");
        mFunctionKeywordReplace.put("from net.swordie.ms.enums import WeatherEffNoticeType","WeatherEffectNoticeType = Java.type(\"game.field.WeatherEffectNoticeType\")");
        mFunctionKeywordReplace.put("WeatherEffNoticeType.","WeatherEffectNoticeType.");
        mFunctionKeywordReplace.put("SayOkay","Say");
        mFunctionKeywordReplace.put("warpInstanceIn", "RegisterTransferFieldInstance");
        mFunctionKeywordReplace.put("warpInstanceOut", "RegisterTransferField");
        mFunctionKeywordReplace.put("RegisterTransferFieldInstanceIn","RegisterTransferFieldInstance");
        mFunctionKeywordReplace.put("UserScriptMessageScript","UserScriptMessage");
        mFunctionKeywordReplace.put("RegisterTransferFieldInstanceOut","RegisterTransferField");
        mFunctionKeywordReplace.put("chr.getLevel","self.UserGetLevel");
        mFunctionKeywordReplace.put("QuestRecordTryStartActNoCheck","QuestRecordTryStartAct");
        mFunctionKeywordReplace.put("QuestRecordSetCompleteNoRewards","QuestRecordSetComplete");
        mFunctionKeywordReplace.put("QuestRecordSetCompleteNoCheck","QuestRecordSetComplete");
        mFunctionKeywordReplace.put("chr.getJob","self.UserGetJob");
        mFunctionKeywordReplace.put("IsQuestInProgressCompleted","IsQuestInProgressOrComplete");
        mFunctionKeywordReplace.put("field.getId","pField.dwField");
        mFunctionKeywordReplace.put("SpeakerTypeID","SpeakerTypeID"); //this is just so SpeakerTypeID doesn't get logged as an alien function
        mFunctionKeywordReplace.put("self.OnUserInGameDirectionEvent","self.OnUserInGameDirectionEvent"); //this is just so OnUserInGameDirectionEvent doesn't get logged as an alien function
        mFunctionKeywordReplace.put("Java.type","Java.type"); //this is just so Java.type doesn't get logged as an alien function
        mFunctionKeywordReplace.put("reactor.incHitCount","self.FieldIncReactorState");
        mFunctionKeywordReplace.put("reactor.getHitCount","self.FieldGetReactorState");
        mFunctionKeywordReplace.put(".getPosition(objectID).getX",".FieldReactorGetPosX");
        mFunctionKeywordReplace.put(".getPosition(objectID).getY",".FieldReactorGetPosY");
        mFunctionKeywordReplace.put("getNpcObjectIdByTemplateId","FieldGetNpcGameObjectID");
        mFunctionKeywordReplace.put(".getEquippedInventory().getItemBySlot",".GetEquippedItemIDBySlot");
        mFunctionKeywordReplace.put("setPreviousFieldID","UserSetReturnFieldID");
        mFunctionKeywordReplace.put("member.getLevel","self.UserGetPartyMemberLevel");
        mFunctionKeywordReplace.put("getCurrentDateAsString","GetCurrentDate");
        mFunctionKeywordReplace.put("chr.getPosition","GetUser().GetCurrentPosition");
        mFunctionKeywordReplace.put("chr.getField","UserGetField");
        mFunctionKeywordReplace.put("field.removeLife","self.FieldRemoveMob");
        mFunctionKeywordReplace.put("pos.getX","pos.getX");
        mFunctionKeywordReplace.put("pos.getY","pos.getY");
        mFunctionKeywordReplace.put("Map/Effect.img/giantBoss/enter/","Map/Effect.img/giantBoss/enter/");
        mFunctionKeywordReplace.put("showEffectOnPosition","OnUserEffectPlay");
        mFunctionKeywordReplace.put("showNpcEffectOnPosition","OnUserNpcEffectPlay");
        mFunctionKeywordReplace.put("chr.getPreviousFieldID","self.GetReturnFieldID");
        mFunctionKeywordReplace.put("JobConstants.getJobLevel","self.UserGetJobLevel");
        mFunctionKeywordReplace.put("chr.getUnion().getUnionRank","self.UserGetUnionRank");
        mFunctionKeywordReplace.put("chr.setSpToCurrentJob","self.UserIncSP");
        mFunctionKeywordReplace.put("SayImage","SayImage");
        mFunctionKeywordReplace.put("getUnionCoin", "UserGetAvailableUnionCoin");
        mFunctionKeywordReplace.put("getUnionCharacterCount","UserGetUnionCharacterCount");
        mFunctionKeywordReplace.put("getUnionLevel","UserGetUnionLevel");
        mFunctionKeywordReplace.put("rewards[Math.floor","rewards[Math.floor");
        mFunctionKeywordReplace.put("Math.rand","Math.rand");
        mFunctionKeywordReplace.put("JobConstants.", "Java.type(\"base.user.skill.accessor.JobAccessor\").");
        mFunctionKeywordReplace.put("isAdventurerBowman", "IsBowman");
        mFunctionKeywordReplace.put("isAdventurerMage", "IsMagician");
        mFunctionKeywordReplace.put("isAdventurerPirate", "IsPirate");
        mFunctionKeywordReplace.put("isAdventurerWarrior", "IsWarrior");
        mFunctionKeywordReplace.put("isAdventurerThief", "IsThief");
        mFunctionKeywordReplace.put("sendChat", "UserScriptMessage");
        mFunctionKeywordReplace.put("Math.floor", "Math.floor");
        mFunctionKeywordReplace.put("CharacterTemporaryStat.Morph", "CharacterTemporaryStat.Morph");
        mFunctionKeywordReplace.put("CharacterTemporaryStat.PAD", "CharacterTemporaryStat.PAD");
        mFunctionKeywordReplace.put("CharacterTemporaryStat.RideVehicle", "CharacterTemporaryStat.RideVehicle");
        mFunctionKeywordReplace.put("from net.swordie.ms.client.character.skills.temp import CharacterTemporaryStat", "");
        mFunctionKeywordReplace.put("GameConstants.ARKARIUM_QUEST", "99994");
        mFunctionKeywordReplace.put("GameConstants.DOJO_DUMMY_DURATION", "10");
        mFunctionKeywordReplace.put("GameConstants.DOJO_SPAWM_BOSS_DELAY", "3");
        mFunctionKeywordReplace.put("GameConstants.EASY_HILLA_QUEST", "99995");
        mFunctionKeywordReplace.put("GameConstants.EASY_HORNTAIL_QUEST", "99996");
        mFunctionKeywordReplace.put("GameConstants.EVAN_INTRO", "99992");
        mFunctionKeywordReplace.put("GameConstants.EVAN_INTRO2", "99993");
        mFunctionKeywordReplace.put("from net.swordie.ms.constants import GameConstants", "");
        mFunctionKeywordReplace.put("GreyFieldType.Background", "1");
        mFunctionKeywordReplace.put("GreyFieldType.FieldScript", "0");
        mFunctionKeywordReplace.put("GreyFieldType.NPC", "4");
        mFunctionKeywordReplace.put("from net.swordie.ms.world.field.fieldeffect import GreyFieldType", "");
        mFunctionKeywordReplace.put("ItemConstants.NODESTONE", "2436324");
        mFunctionKeywordReplace.put("from net.swordie.ms.constants import ItemConstants", "");
        mFunctionKeywordReplace.put("from net.swordie.ms.constants import JobConstants", "");
        mFunctionKeywordReplace.put("from net.swordie.ms.enums import ObtacleAtomEnum", "");
        mFunctionKeywordReplace.put("from net.swordie.ms.life.npc.NpcScriptInfo import Param", "");
        mFunctionKeywordReplace.put("QuestConstants.FIFTH_JOB_QUEST", "1465");
        mFunctionKeywordReplace.put("QuestConstants.SILENT_CRUSADE_WANTED_TAB_1", "1648");
        mFunctionKeywordReplace.put("QuestConstants.SILENT_CRUSADE_WANTED_TAB_2", "1649");
        mFunctionKeywordReplace.put("QuestConstants.SILENT_CRUSADE_WANTED_TAB_3", "1650");
        mFunctionKeywordReplace.put("QuestConstants.SILENT_CRUSADE_WANTED_TAB_4", "1651");
        mFunctionKeywordReplace.put("from net.swordie.ms.constants import QuestConstants", "");
        mFunctionKeywordReplace.put("from net.swordie.ms.constants import SkillConstants", "");
        mFunctionKeywordReplace.put("UIType.BLOCKBUSTER_SELECT_UI", "1103");
        mFunctionKeywordReplace.put("UIType.EVOLVING_SYSTEM", "100");
        mFunctionKeywordReplace.put("UIType.PARTYSEARCH", "21");
        mFunctionKeywordReplace.put("UIType.UI_DIMENSION_LIBRARY", "179");
        mFunctionKeywordReplace.put("UIType.UI_GROWTH_HELPER", "202");
        mFunctionKeywordReplace.put("UIType.UI_MAKING_SKILL", "42");
        mFunctionKeywordReplace.put("UIType.UI_PLATFORM_STAGE_SELECT", "1112");
        mFunctionKeywordReplace.put("UIType.UI_TOWERRANK", "208");
        mFunctionKeywordReplace.put("UIType.UNION_BOARD", "1148");
        mFunctionKeywordReplace.put("UIType.V_NODES", "1131");
        mFunctionKeywordReplace.put("from net.swordie.ms.enums import UIType", "");
        mFunctionKeywordReplace.put("WzConstants.EFFECT_CLEAR", "\"Map/EffectTW.img/arisan/clear\"");
        mFunctionKeywordReplace.put("WzConstants.EFFECT_DOJO_CLEAR", "\"Map/Effect.img/dojang/end/clear\"");
        mFunctionKeywordReplace.put("WzConstants.EFFECT_MONSTER_PARK_CLEAR", "\"Map/Effect.img/monsterPark/clear\"");
        mFunctionKeywordReplace.put("WzConstants.EFFECT_MONSTER_PARK_FINAL_STAGE", "\"Map/Effect.img/monsterPark/stageEff/final\"");
        mFunctionKeywordReplace.put("WzConstants.EFFECT_MONSTER_PARK_STAGE", "\"Map/Effect.img/monsterPark/stageEff/stage\"");
        mFunctionKeywordReplace.put("WzConstants.EFFECT_MONSTER_PARK_STAGE_NUMBER", "\"Map/Effect.img/monsterPark/stageEff/number/\"");
        mFunctionKeywordReplace.put("WzConstants.ICON_EXP", "\"#fUI/UIWindow2.img/QuestIcon/8/0#\"");
        mFunctionKeywordReplace.put("WzConstants.ICON_OBTAINED", "\"#fUI/UIWindow2.img/QuestIcon/4/0#\"");
        mFunctionKeywordReplace.put("from net.swordie.ms.constants import WzConstants", "");
        mFunctionKeywordReplace.put("from net.swordie.ms.scripts import ScriptType", "");
        mFunctionKeywordReplace.put("CharacterTemporaryStat.", "Java.type(\"base.user.stat.CharacterTemporaryStat\").");
        mFunctionKeywordReplace.put("chr.addNodeShards", "self.UserIncNodeShard");
        mFunctionKeywordReplace.put("chr.getNodeShards", "self.UserGetNodeShard");
        mFunctionKeywordReplace.put("chr.addSkill", "self.UserLearnSkill");
        mFunctionKeywordReplace.put("chr.getId", "self.UserGetCharacterID");
        mFunctionKeywordReplace.put("TextEffectType.BlackFadedBrush.getVal", "1");
        mFunctionKeywordReplace.put("sendSayOkay", "Say");
        mFunctionKeywordReplace.put("sendSayImage", "SayImage");
        mFunctionKeywordReplace.put("sendSay", "Say");
        mFunctionKeywordReplace.put("getPosition", "GetPosition");
        mFunctionKeywordReplace.put("chr.addSkill", "self.UserLearnSkill");
        mFunctionKeywordReplace.put("chr.getSkillLevel", "self.GetSkillLevel");
        mFunctionKeywordReplace.put("sm.","self."); //try to keep this one at the bottom so its switched out last

    }
}
