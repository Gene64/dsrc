package script.event.auto_invasion;

import script.*;
import script.base_class.*;
import script.combat_engine.*;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Vector;
import script.base_script;

import script.library.ai_lib;
import script.library.utils;
import script.ai.ai_combat;
import script.library.healing;

public class invader extends script.base_script
{
    public invader()
    {
    }
    public static final String DATATABLE = "datatables/event/invasion/ewok_bonus_loot.iff";
    public int OnAttach(obj_id self) throws InterruptedException
    {
        messageTo(self, "startAttack", null, 1, false);
        messageTo(self, "goDie", null, 3600, false);
        if (hasObjVar(self, "auto_invasion.teh_uber"))
        {
            setScale(self, 5.0f);
            string_id bossNameSID = new string_id("auto_invasion", "ewok_boss_name");
            String bossName = utils.packStringId(bossNameSID);
            setName(self, bossName);
            int bossBuffLevel = getIntObjVar(self, "auto_invasion.boss_buff_level");
            int buffAmount = bossBuffLevel * bossBuffLevel * 10000;
            addAttribModifier(self, "medical_enhance_health", HEALTH, buffAmount, 10000, 0.0f, 10.0f, true, false, true);
            addAttribModifier(self, "medical_enhance_action", ACTION, buffAmount, 10000, 0.0f, 10.0f, true, false, true);
        }
        return SCRIPT_CONTINUE;
    }
    public int startAttack(obj_id self, dictionary params) throws InterruptedException
    {
        obj_id target = getObjIdObjVar(self, "auto_invasion.target");
        float destinationOffset = getFloatObjVar(target, "auto_invasion.dest_offset");
        location locationDestination = getLocation(target);
        location adjustedLoc = utils.getRandomLocationInRing(locationDestination, destinationOffset, destinationOffset + 10);
        ai_lib.aiPathTo(self, adjustedLoc);
        setMovementRun(self);
        return SCRIPT_CONTINUE;
    }
    public int goDie(obj_id self, dictionary params) throws InterruptedException
    {
        if (!ai_lib.isAiDead(self))
        {
            destroyObject(self);
        }
        return SCRIPT_CONTINUE;
    }
    public int OnIncapacitated(obj_id self, obj_id killer) throws InterruptedException
    {
        int myNumber = getIntObjVar(self, "auto_invasion.my_number");
        dictionary params = new dictionary();
        params.put("myNumber", myNumber);
        params.put("deadGuy", self);
        obj_id target = getObjIdObjVar(self, "auto_invasion.target");
        removeObjVar(target, "auto_invasion.spawn" + myNumber);
        messageTo(target, "invaderDied", params, 1, false);
        return SCRIPT_CONTINUE;
    }
    public int aiCorpsePrepared(obj_id self, dictionary params) throws InterruptedException
    {
        int chance = rand(1, 200);
        if (hasObjVar(self, "auto_invasion.teh_uber"))
        {
            chance = 200;
        }
        if (chance > 195)
        {
            int roll = rand(1, 100);
            int tableLength = dataTableGetNumRows(DATATABLE);
            if (tableLength < 1)
            {
                return SCRIPT_CONTINUE;
            }
            for (int i = 0; i < tableLength; i++)
            {
                int minRoll = dataTableGetInt(DATATABLE, i, "MIN_ROLL");
                if (minRoll > roll)
                {
                    String loot = dataTableGetString(DATATABLE, i, "LOOT");
                    obj_id inventory = utils.getInventoryContainer(self);
                    obj_id reward = createObject(loot, inventory, "");
                    return SCRIPT_CONTINUE;
                }
            }
        }
        return SCRIPT_CONTINUE;
    }
}
