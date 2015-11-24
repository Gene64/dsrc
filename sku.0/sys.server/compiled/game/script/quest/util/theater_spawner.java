package script.quest.util;

import script.*;
import script.base_class.*;
import script.combat_engine.*;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Vector;
import script.base_script;

import script.library.quests;

public class theater_spawner extends script.base_script
{
    public theater_spawner()
    {
    }
    public static final String VAR_SPAWNER_CURRENT_POPULATION = "quest_spawner.current_pop";
    public static final String VAR_SPAWNED_BY = "quest_spawner.spawned_by";
    public int OnInitialize(obj_id self) throws InterruptedException
    {
        String spawner = getStringObjVar(self, "quest_spawner.type");
        if (spawner == null || spawner.length() < 1)
        {
            LOG("quest", "theater_spawner.OnInitialize -- spawner is null or empty");
            return SCRIPT_CONTINUE;
        }
        String datatable = getStringObjVar(self, "quest_spawner.datatable");
        if (datatable == null || datatable.length() < 1)
        {
            LOG("quest", "theater_spawner.OnInitialize -- datatable is null or empty");
            return SCRIPT_CONTINUE;
        }
        int index = dataTableSearchColumnForString(spawner, "type", datatable);
        if (index == -1)
        {
            LOG("quest", "theater_spawner.OnInitialize -- can't find spawner " + spawner + " within datatable " + datatable);
            return SCRIPT_CONTINUE;
        }
        dictionary row = dataTableGetRow(datatable, index);
        if (row == null)
        {
            LOG("quest", "theater_spawner.OnInitialize -- can't find data in row " + index + " for datatable " + datatable);
            return SCRIPT_CONTINUE;
        }
        int pulse = row.getInt("pulse");
        int max_spawn = row.getInt("max_spawn");
        int max_population = row.getInt("max_population");
        int expire = row.getInt("expire");
        setObjVar(self, "quest_spawner.pulse", pulse);
        setObjVar(self, "quest_spawner.max_spawn", max_spawn);
        setObjVar(self, "quest_spawner.max_pop", max_population);
        if (expire > 1)
        {
            setObjVar(self, "quest_spawner.time_expired", expire + getGameTime());
        }
        else 
        {
            setObjVar(self, "quest_spawner.time_expired", 0);
        }
        detachScript(self, "quest.util.theater_spawner");
        attachScript(self, "quest.util.quest_spawner");
        messageTo(self, "msgQuestSpawnerPulse", null, 5.0f, false);
        return SCRIPT_CONTINUE;
    }
}
