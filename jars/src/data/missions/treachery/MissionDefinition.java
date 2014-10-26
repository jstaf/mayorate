package data.missions.treachery;

import com.fs.starfarer.api.campaign.CargoAPI.CrewXPLevel;
import com.fs.starfarer.api.combat.BaseEveryFrameCombatPlugin;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.fleet.FleetGoal;
import com.fs.starfarer.api.fleet.FleetMemberType;
import com.fs.starfarer.api.mission.FleetSide;
import com.fs.starfarer.api.mission.MissionDefinitionAPI;
import com.fs.starfarer.api.mission.MissionDefinitionPlugin;
import java.util.List;

public class MissionDefinition implements MissionDefinitionPlugin {

    @Override
    public void defineMission(MissionDefinitionAPI api) {
        // Set up the fleets so we can add ships and fighter wings to them.
        // In this scenario, the fleets are attacking each other, but
        // in other scenarios, a fleet may be defending or trying to escape
        api.initFleet(FleetSide.PLAYER, "MNS", FleetGoal.ATTACK, false, 7);
        api.initFleet(FleetSide.ENEMY, "HSS", FleetGoal.ATTACK, true, 7);

        // Set a small blurb for each fleet that shows up on the mission detail and
        // mission results screens to identify each side.
        api.setFleetTagline(FleetSide.PLAYER, "Mayorate 3rd Battlegroup");
        api.setFleetTagline(FleetSide.ENEMY, "Hegemony Invasion Fleet");

        // These show up as items in the bulleted list under 
        // "Tactical Objectives" on the mission detail screen
        api.addBriefingItem("Force a Hegemony retreat by whatever means necessary");
        api.addBriefingItem("Your destroyer's thermal lances will quickly tear through exposed enemy armor.");
        api.addBriefingItem("Deploy your cruiser wisely. It is the only ship you have with strong defensive abilities.");

        // Set up the player's fleet.  Variant names come from the
        // files in data/variants and data/variants/fighters
        api.addToFleet(FleetSide.PLAYER, "ilk_del_azarchel_artillery", FleetMemberType.SHIP, "MNS Nazarin", true, CrewXPLevel.REGULAR);
        api.addToFleet(FleetSide.PLAYER, "ilk_jamaran_support", FleetMemberType.SHIP, false, CrewXPLevel.GREEN);
        api.addToFleet(FleetSide.PLAYER, "ilk_foraker_escort", FleetMemberType.SHIP, false, CrewXPLevel.REGULAR);
        api.addToFleet(FleetSide.PLAYER, "ilk_cimeterre_strike", FleetMemberType.SHIP, false, CrewXPLevel.REGULAR);
        api.addToFleet(FleetSide.PLAYER, "ilk_cimeterre_strike", FleetMemberType.SHIP, false, CrewXPLevel.REGULAR);
        api.addToFleet(FleetSide.PLAYER, "ilk_safir_standard", FleetMemberType.SHIP, false, CrewXPLevel.GREEN);
        api.addToFleet(FleetSide.PLAYER, "ilk_safir_converted_support", FleetMemberType.SHIP, false, CrewXPLevel.GREEN);
        api.addToFleet(FleetSide.PLAYER, "ilk_tiamat_assault", FleetMemberType.SHIP, false, CrewXPLevel.GREEN);
        api.addToFleet(FleetSide.PLAYER, "ilk_tiamat_assault", FleetMemberType.SHIP, false, CrewXPLevel.GREEN);
        api.addToFleet(FleetSide.PLAYER, "ilk_lilith_assault", FleetMemberType.SHIP, false, CrewXPLevel.GREEN);

        api.addToFleet(FleetSide.PLAYER, "ilk_angha_wing", FleetMemberType.FIGHTER_WING, false, CrewXPLevel.REGULAR);
        api.addToFleet(FleetSide.PLAYER, "ilk_angha_wing", FleetMemberType.FIGHTER_WING, false, CrewXPLevel.REGULAR);
        //api.addToFleet(FleetSide.PLAYER, "ilk_inanna_wing", FleetMemberType.FIGHTER_WING, false, CrewXPLevel.REGULAR);
        api.addToFleet(FleetSide.PLAYER, "ilk_raad_wing", FleetMemberType.FIGHTER_WING, false, CrewXPLevel.REGULAR);
        api.addToFleet(FleetSide.PLAYER, "ilk_raad_wing", FleetMemberType.FIGHTER_WING, false, CrewXPLevel.REGULAR);
        api.addToFleet(FleetSide.PLAYER, "ilk_raad_wing", FleetMemberType.FIGHTER_WING, false, CrewXPLevel.REGULAR);
	//api.addToFleet(FleetSide.PLAYER, "broadsword_wing", FleetMemberType.FIGHTER_WING, false, CrewXPLevel.REGULAR);

        // Set up the enemy fleet.
        api.addToFleet(FleetSide.ENEMY, "onslaught_Standard", FleetMemberType.SHIP, false);

        api.addToFleet(FleetSide.ENEMY, "dominator_Assault", FleetMemberType.SHIP, false);
        api.addToFleet(FleetSide.ENEMY, "enforcer_Assault", FleetMemberType.SHIP, false);
        api.addToFleet(FleetSide.ENEMY, "lasher_CS", FleetMemberType.SHIP, false);
        api.addToFleet(FleetSide.ENEMY, "lasher_CS", FleetMemberType.SHIP, false);
        api.addToFleet(FleetSide.ENEMY, "condor_FS", FleetMemberType.SHIP, false);
        api.addToFleet(FleetSide.ENEMY, "buffalo2_FS", FleetMemberType.SHIP, false);
        api.addToFleet(FleetSide.ENEMY, "buffalo2_FS", FleetMemberType.SHIP, false);

        api.addToFleet(FleetSide.ENEMY, "valkyrie_Elite", FleetMemberType.SHIP, false);
        api.addToFleet(FleetSide.ENEMY, "valkyrie_Elite", FleetMemberType.SHIP, false);
        api.addToFleet(FleetSide.ENEMY, "valkyrie_Elite", FleetMemberType.SHIP, false);
        api.addToFleet(FleetSide.ENEMY, "valkyrie_Elite", FleetMemberType.SHIP, false);

        api.addToFleet(FleetSide.ENEMY, "broadsword_wing", FleetMemberType.FIGHTER_WING, false);
        api.addToFleet(FleetSide.ENEMY, "talon_wing", FleetMemberType.FIGHTER_WING, false);
        api.addToFleet(FleetSide.ENEMY, "talon_wing", FleetMemberType.FIGHTER_WING, false);

        // Set up the map.
        float width = 24000f;
        float height = 18000f;
        api.initMap((float) -width / 2f, (float) width / 2f, (float) -height / 2f, (float) height / 2f);

        float minX = -width / 2;
        float minY = -height / 2;

        api.addNebula(minX + width * 0.5f - 300, minY + height * 0.5f, 1000);
        api.addNebula(minX + width * 0.5f + 300, minY + height * 0.5f, 1000);

        for (int i = 0; i < 5; i++) {
            float x = (float) Math.random() * width - width / 2;
            float y = (float) Math.random() * height - height / 2;
            float radius = 100f + (float) Math.random() * 400f;
            api.addNebula(x, y, radius);
        }

        api.addPlanet(0, 0, 200f, "ilk_ilkhanna", 350f, true);

        // Add an asteroid field
        api.addAsteroidField(minX + width / 2f, minY + height / 2f, 0, 8000f, 20f, 70f, 100);

        api.addPlugin(new BaseEveryFrameCombatPlugin() {
                        @Override
			public void advance(float amount, List events) {
			}
                        @Override
			public void init(CombatEngineAPI engine) {
				engine.getContext().setStandoffRange(10000f);
			}
		});
    }
}
