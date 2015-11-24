package script.event;

import script.*;
import script.base_class.*;
import script.combat_engine.*;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Vector;
import script.base_script;

import script.library.utils;
import script.library.healing;
import java.util.StringTokenizer;

public class boss_buff extends script.base_script
{
    public boss_buff()
    {
    }
    public static final String[] HELP_TEXT = 
    {
        "=========================================",
        "USAGE:",
        "\"Buff <VALUE>\": Sets your primary stats to <VALUE> and secondary stats to 1/2 of <VALUE>",
        "or \"Buff <VALUE> <SECONDARY STAT OPTIONS>\": See Secondary stats options below for details.",
        "or \"Buff <VALUE> <SECONDARY STAT OPTIONS> <DURATION IN SECONDS>: See Duration in Seconds below for details",
        "Secondary stats options: These can either be a specific number or the words \"default\" or \"low\".",
        "Secondary stats options: If you specify a number your secondary stats will be buffed to that value.",
        "Secondary stats options: Specify \"low\" to make your secondary stats automatically lower. This is useful when buffing to very high HAM to prevent crazy regen.",
        "Secondary stats options: Specify \"default\" to use the standard derived secondary stats.",
        "Duration in Seconds: Specify a number in seconds if you do not want the default 10000 seconds, which is roughly 2 hours and 45 minutes.",
        "Saying \"Detach\" will automatically remove this script.",
        "========================================="
    };
    public int OnAttach(obj_id self) throws InterruptedException
    {
        if (!isPlayer(self))
        {
            detachScript(self, "event.boss_buff");
        }
        if (!isGod(self))
        {
            detachScript(self, "event.boss_buff");
            sendSystemMessage(self, "You must be in God Mode for this script to take hold.", null);
            return SCRIPT_CONTINUE;
        }
        if (isGod(self))
        {
            int godLevel = getGodLevel(self);
            if (godLevel < 15)
            {
                detachScript(self, "event.boss_buff");
                sendSystemMessage(self, "You do not have the appropriate access level to use this script.", null);
                return SCRIPT_CONTINUE;
            }
        }
        sendSystemMessage(self, "Say \"Help\" for usage and options.", null);
        return SCRIPT_CONTINUE;
    }
    public int OnLogin(obj_id self) throws InterruptedException
    {
        if (!isGod(self))
        {
            detachScript(self, "event.boss_buff");
            sendSystemMessage(self, "You must be in God Mode to use this script.", null);
        }
        if (isGod(self))
        {
            int godLevel = getGodLevel(self);
            if (godLevel < 15)
            {
                detachScript(self, "event.boss_buff");
                sendSystemMessage(self, "You do not have the appropriate access level to use this script.", null);
                return SCRIPT_CONTINUE;
            }
        }
        return SCRIPT_CONTINUE;
    }
    public int OnHearSpeech(obj_id self, obj_id objSpeaker, String strText) throws InterruptedException
    {
        if (objSpeaker != self)
        {
            return SCRIPT_CONTINUE;
        }
        if (isGod(self))
        {
            int godLevel = getGodLevel(self);
            if (godLevel < 15)
            {
                detachScript(self, "event.boss_buff");
                sendSystemMessage(self, "You do not have the appropriate access level to use this script.", null);
                return SCRIPT_CONTINUE;
            }
        }
        if (!isGod(self))
        {
            detachScript(self, "event.boss_buff");
            sendSystemMessage(self, "You must be in God Mode to use this script.", null);
            return SCRIPT_CONTINUE;
        }
        if ((toLower(strText)).startsWith("buff") || (toLower(strText)).startsWith("target"))
        {
            obj_id target = self;
            if ((toLower(strText)).startsWith("buff"))
            {
                target = self;
            }
            if ((toLower(strText)).startsWith("target"))
            {
                target = getLookAtTarget(self);
                if (!isIdValid(target) || target == null)
                {
                    target = self;
                }
            }
            int buffLevel = 1;
            int buffTime = 10;
            int secondBuffLevel = 1;
            StringTokenizer st = new StringTokenizer(strText);
            if (st.countTokens() < 2 || st.countTokens() > 4)
            {
                return SCRIPT_CONTINUE;
            }
            if (st.countTokens() == 2)
            {
                String command = st.nextToken();
                String buffLevelStr = st.nextToken();
                buffLevel = utils.stringToInt(buffLevelStr);
                secondBuffLevel = buffLevel / 2;
                buffTime = 10000;
            }
            if (st.countTokens() == 3)
            {
                String command = st.nextToken();
                String buffLevelStr = st.nextToken();
                String secondBuffLevelStr = st.nextToken();
                buffLevel = utils.stringToInt(buffLevelStr);
                buffTime = 10000;
                if ((toLower(secondBuffLevelStr)).equals("low"))
                {
                    secondBuffLevel = buffLevel / 10;
                    if (secondBuffLevel > 10000)
                    {
                        secondBuffLevel = 10000;
                    }
                }
                if ((toLower(secondBuffLevelStr)).equals("default"))
                {
                    secondBuffLevel = buffLevel / 2;
                }
                if (!(toLower(secondBuffLevelStr)).equals("low") && !(toLower(secondBuffLevelStr)).equals("default"))
                {
                    secondBuffLevel = utils.stringToInt(secondBuffLevelStr);
                }
            }
            if (st.countTokens() == 4)
            {
                String command = st.nextToken();
                String buffLevelStr = st.nextToken();
                String secondBuffLevelStr = st.nextToken();
                String buffTimeStr = st.nextToken();
                buffLevel = utils.stringToInt(buffLevelStr);
                buffTime = utils.stringToInt(buffTimeStr);
                if ((toLower(secondBuffLevelStr)).equals("low"))
                {
                    secondBuffLevel = buffLevel / 10;
                    if (secondBuffLevel > 10000)
                    {
                        secondBuffLevel = 10000;
                    }
                }
                if ((toLower(secondBuffLevelStr)).equals("default"))
                {
                    secondBuffLevel = buffLevel / 2;
                }
                if (!(toLower(secondBuffLevelStr)).equals("low") && !(toLower(secondBuffLevelStr)).equals("default"))
                {
                    secondBuffLevel = utils.stringToInt(secondBuffLevelStr);
                }
            }
            addAttribModifier(target, "medical_enhance_health", HEALTH, buffLevel, buffTime, 0.0f, 10.0f, true, false, true);
            addAttribModifier(target, "medical_enhance_action", ACTION, buffLevel, buffTime, 0.0f, 10.0f, true, false, true);
            addAttribModifier(target, "medical_enhance_constitution", CONSTITUTION, secondBuffLevel, buffTime, 0.0f, 10.0f, true, false, true);
            addAttribModifier(target, "medical_enhance_stamina", STAMINA, secondBuffLevel, buffTime, 0.0f, 10.0f, true, false, true);
            sendSystemMessage(self, "Primary stats: " + buffLevel + " -- Secondary stats: " + secondBuffLevel + " -- Duration: " + buffTime, null);
        }
        if ((toLower(strText)).equals("detach"))
        {
            detachScript(self, "event.boss_buff");
            return SCRIPT_CONTINUE;
        }
        if ((toLower(strText)).equals("help"))
        {
            for (int i = 0; i < HELP_TEXT.length; i++)
            {
                sendSystemMessage(self, HELP_TEXT[i], null);
            }
            return SCRIPT_CONTINUE;
        }
        return SCRIPT_CONTINUE;
    }
}
