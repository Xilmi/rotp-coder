/*
 * Copyright 2015-2020 Ray Fowler
 * 
 * Licensed under the GNU General Public License, Version 3 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     https://www.gnu.org/licenses/gpl-3.0.html
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package rotp.model.ai.xilmi;

import java.util.Arrays;
import java.util.List;
import rotp.model.ai.interfaces.ShipDesigner;
import rotp.model.empires.Empire;
import rotp.model.empires.EmpireView;
import rotp.model.galaxy.ShipFleet;
import rotp.model.galaxy.Ships;
import rotp.model.galaxy.StarSystem;
import static rotp.model.game.IGameOptions.RESEARCH_SLOW;
import static rotp.model.game.IGameOptions.RESEARCH_SLOWER;
import static rotp.model.game.IGameOptions.RESEARCH_SLOWEST;
import rotp.model.planet.PlanetType;
import rotp.model.ships.ShipDesign;
import static rotp.model.ships.ShipDesign.maxSpecials;
import static rotp.model.ships.ShipDesign.maxWeapons;
import rotp.model.ships.ShipDesignLab;
import rotp.model.ships.ShipSpecial;
import rotp.model.ships.ShipSpecialColony;
import rotp.model.ships.ShipWeapon;
import rotp.model.tech.Tech;
import rotp.util.Base;

public class AIShipDesigner implements Base, ShipDesigner {
    private static final int OBS_DESTROYER_TURNS = 20;
    //ail: I have more slots available for bombers and fighters, so I can increase the time until they are obsolete
    private static final int OBS_FIGHTER_TURNS = 32;
    private static final int OBS_BOMBER_TURNS = 24;
    private static final int OBS_COLONY_TURNS = 8;
    private static final int OBS_SCOUT_TURNS = 1;

    private final Empire empire;
    private int[] shipCounts;

    public AIShipDesigner (Empire c) {
        empire = c;
    }
    @Override
    public String toString()   { return concat("ShipDesigner: ", empire.raceName()); }
    @Override
    public Empire empire()     { return empire; }
    @Override
    public ShipDesignLab lab() { return empire.shipLab(); }
    @Override
    public void nextTurn() {
        if (empire.isAIControlled()) {
            log(this+": nextTurn");
            shipCounts = galaxy().ships.shipDesignCounts(empire.id);
            // designs are updated in a specific order in order to prioritize
            // use of empty design slots
            updateFighterDesign();
            updateDestroyerDesign();
            updateBomberDesign();
            updateColonyDesign();
            updateScoutDesign();
            countdownObsoleteDesigns();
        }
    }
    @Override
    public ShipDesign bestDesignToColonize(ShipFleet fl, StarSystem sys) {
        // in case there is more than one colony ship in this fleet,
        // find the best one to use at this planet. And by "best" we
        // mean the ship that we want to most get rid of by using 
        // it to colonize the planet... pick the slowest one first.
        // if that ties, pick the one with the worst colony module.
        ShipDesignLab lab = lab();
        ShipDesign bestDesign = null;
        for (int i=0;i<fl.num.length;i++) {
            if (fl.num[i] > 0) {
                ShipDesign design = lab.design(i);
                ShipSpecialColony special = design.colonySpecial();
                if (special != null) {
                    PlanetType pt = sys.planet().type();
                    if (empire.ignoresPlanetEnvironment()
                    || (empire.canColonize(pt) && special.tech().canColonize(pt)) ) {
                        if ((bestDesign == null)
                        || (design.engine().warp() < bestDesign.engine().warp()))
                            bestDesign = design;
                        else if (design.engine().warp() == bestDesign.engine().warp()) {
                            if (special.tech().environment() < bestDesign.colonySpecial().tech().environment())
                                bestDesign = design;
                        }
                    }
                }
            }
        }
        return bestDesign;
    }
    private void countdownObsoleteDesigns() {
        Ships ships = galaxy().ships;
        int pid = empire.id;
        if(((empire.shipMaintCostPerBC() > (empire.fleetCommanderAI().maxShipMaintainance() * 1.5) && empire.enemies().isEmpty() && !empire.fleetCommanderAI().inExpansionMode()))
                || empire.netIncome() <= 0)
        {
            //System.out.print("\n"+empire.name()+" scrapWorstDesign max-maint: "+empire.fleetCommanderAI().maxShipMaintainance());
            scrapWorstDesign(empire.netIncome() <= 0);
        }
    }
    private void scrapWorstDesign(boolean forceCosting) {
        ShipDesignLab lab = lab();
        ShipDesign designToScrap = null;
        float lowestKeepScore = Float.MAX_VALUE;
        boolean shouldScrap = true;
        for (int slot=0;slot<ShipDesignLab.MAX_DESIGNS;slot++) {
            ShipDesign d = lab.design(slot);
            if (d.obsolete() || forceCosting) {
                if(forceCosting && shipCounts[d.id()] == 0)
                {
                    continue;
                }
                float keepScore = 1;
                //ail: for colony-ship we don't care about available space but engine- and colony-base-discrepancy
                if(d.isColonyShip())
                {
                    keepScore = ((float)d.engine().warp() / (float)lab().fastestEngine().warp()) * ((float)(d.special(0).tech()).level / (float)bestColonySpecial().tech().level);
                }
                else
                {
                    keepScore = 1 - d.availableSpace()/d.totalSpace();
                }
                if(d.isFighter())
                {
                    float currentDamageNoShields = lab.fighterDesign().firepowerAntiShip(0);
                    float currentDamageShields = lab.fighterDesign().firepowerAntiShip(empire.bestEnemyShieldLevel());
                    float obsoleteDamageNoShields = d.firepowerAntiShip(0);
                    float obsoleteDamageShields = d.firepowerAntiShip(empire.bestEnemyShieldLevel());
                    if(obsoleteDamageNoShields == 0)
                    {
                        keepScore = 0;
                    }
                    else
                    {
                        float currentPenetrate = currentDamageShields / currentDamageNoShields;
                        float obsoletePenetrate = obsoleteDamageShields / obsoleteDamageNoShields;
                        if(currentPenetrate > 0)
                        {
                            keepScore *= obsoletePenetrate / currentPenetrate;
                        }
                        else
                        {
                            keepScore = 0;
                        }
                    }
                }
                if(d.isBomber())
                {
                    float currentDamageNoShields = lab.bomberDesign().firepower(0);
                    float currentDamageShields = lab.bomberDesign().firepower(empire.bestEnemyPlanetaryShieldLevel());
                    float obsoleteDamageNoShields = d.firepower(0);
                    float obsoleteDamageShields = d.firepower(empire.bestEnemyPlanetaryShieldLevel());
                    if(obsoleteDamageNoShields == 0)
                    {
                        keepScore = 0;
                    }
                    else
                    {
                        float currentPenetrate = currentDamageShields / currentDamageNoShields;
                        float obsoletePenetrate = obsoleteDamageShields / obsoleteDamageNoShields;
                        if(currentPenetrate > 0)
                        {
                            keepScore *= obsoletePenetrate / currentPenetrate;
                        }
                        else
                        {
                            keepScore = 0;
                        }
                    }
                }
                keepScore *= shipCounts[d.id()] * d.cost();
                //we can scrap all that we don't need at all
                if(keepScore == 0)
                {
                    lab.scrapDesign(d);
                    //if we do this, we don't have to scrap the design with the lowest keepscore anymore because a slot is now free
                    shouldScrap = false;
                    break;
                }
                else if(keepScore < lowestKeepScore)
                {
                    //System.out.print("\n"+empire.name()+" "+d.name()+" keepScore: "+keepScore);
                    lowestKeepScore = keepScore;
                    designToScrap = d;
                }
            }
        }
        if(designToScrap != null && shouldScrap)
        {
            //System.out.print("\n"+empire.name()+" "+designToScrap.name()+" is scrapped.");
            lab.scrapDesign(designToScrap);
        }
    }

    public void updateScoutDesign() {
        ShipDesignLab lab = lab();
        // if we are not using scouts anymore, quit
        if (!lab.needScouts)
        {
            //ail: free up the slot for other designs
            ShipDesign currDesign = lab.scoutDesign();
            if(currDesign.active() && currDesign.isScout())
            {
                lab.scrapDesign(currDesign);
            }
            return;
        }
        
        ShipDesign currDesign = lab.scoutDesign();
        int currSlot = currDesign.id();
        if (currDesign.engine() == lab.fastestEngine() && currDesign.active())
            return;

        ShipDesign newScout = newScoutDesign();
        if (newScout.matchesDesign(currDesign, false) && currDesign.active())
            return;

        // NEW DESIGN IS BETTER THAN CURRENT
        // immediately replace scout design, don't bother with obsoleting them
        lab.scrapDesign(currDesign);
        lab.setScoutDesign(newScout, currSlot);
    }
    public void updateColonyDesign() {
        ShipDesignLab lab = lab();
        ShipDesign currDesign = lab.colonyDesign();
        int currSlot = currDesign.id();
        
        // weapons needed on colony ships if we've made AI contact
        boolean weaponsNeeded = !empire.contactedEmpires().isEmpty();
        // current design is "properlyArmed" if it is armed &weapons needed or unarmed & weapons unneeded
        //always try to make extended, if possible, additional-cost is worth it and once it fits on large, the costs won't matter much anyways
        boolean extendedFuelNeeded = !empire.tech().topFuelRangeTech().unlimited;

        ShipDesign newDesign = newColonyDesign(weaponsNeeded, extendedFuelNeeded);
        
        if (currDesign.matchesDesign(newDesign, true) && currDesign.active())
        {
            //System.out.print("\n"+empire.name()+" "+newDesign.name()+" size: "+newDesign.size()+" matches with "+currDesign.name()+" size: "+currDesign.size()); 
            return;
        }
        
        int slot = lab.availableDesignSlot();
        
        // if there is a slot available, use it for the new design
        if (slot >= 0) {
            lab.setColonyDesign(newDesign, slot);
            currDesign.becomeObsolete(OBS_COLONY_TURNS);
            //System.out.print("\n"+empire.name()+" "+newDesign.name()+" put in slot "+slot);
            return;
        }
        else
        {
            //if there is no slot available I push the old one to obsolete so a new slot will be freed up soon
            currDesign.becomeObsolete(OBS_COLONY_TURNS);
            scrapWorstDesign(false);
            slot = lab.availableDesignSlot();
            //System.out.print("\n"+empire.name()+" "+newDesign.name()+" put in slot "+slot+" after scrapping something.");
            lab.setColonyDesign(newDesign, slot);
        }
    }
    public void updateBomberDesign() {
        ShipDesignLab lab = lab();
        
        // recalculate current design's damage vs. current targets
        ShipDesign currDesign = lab.bomberDesign();
        
        // check for an available slot for the new design
        int slot = lab.availableDesignSlot();
        
        // if we don't have any faster engines
        if (currDesign.engine() == lab.fastestEngine() && currDesign.active()) {
            // and if the design has less than 10% free space or has less than 25/50/75/100 free space, we assume the redesign has no sense
            // 25/50/75/100 may be a too straight-line set of values
            if ((currDesign.availableSpace()/(currDesign.totalSpace()) < 0.1f) && slot < 0) {
                // we're fairly certain AI packed the ship before and if the current modules haven't shrinked enough,
                // that means we don't have enough new tech to make the new design markedly better            
                currDesign.remainingLife++;
                return;
            }
        }
        //System.out.print("\n"+empire.name()+" should design new bomber since old one has "+currDesign.availableSpace()/currDesign.totalSpace()+"free space");

        int currSlot = currDesign.id();
     
        // find best hypothetical design vs current targets
        ShipDesign newDesign = newBomberDesign(currDesign.size());

        // WE HAVE BOMBERS IN USE... VERIFY THIS UPGRADE JUSTIFIES SWITCHING OVER
        boolean betterEngine = (newDesign.engine().warp() > currDesign.engine().warp());
        boolean betterArmor = (newDesign.armor().sequence() > currDesign.armor().sequence());
        boolean betterSpecial = false;
        boolean oldHasCloaking = false;
        boolean newHasCloaking = false;
        boolean oldHasStasis = false;
        boolean newHasStasis = false;
        boolean oldHasBHG = false;
        boolean newHasBHG = false;
        
        for (int i=0;i<maxSpecials();i++) {
            if(currDesign.special(i).allowsCloaking() == true)
                oldHasCloaking = true; 
            if(newDesign.special(i).allowsCloaking() == true)
                newHasCloaking = true;
            if(!currDesign.special(i).isNone() && currDesign.special(i).tech().isType(Tech.STASIS_FIELD) == true)
                oldHasStasis = true;
            if(!newDesign.special(i).isNone() && newDesign.special(i).tech().isType(Tech.STASIS_FIELD) == true)
                newHasStasis = true;
            if(currDesign.special(i).createsBlackHole())
                oldHasBHG = true;
            if(newDesign.special(i).createsBlackHole())
                newHasBHG = true;
        }
        
        if(!oldHasCloaking && newHasCloaking)
            betterSpecial = true;
        if(!oldHasStasis && newHasStasis)
            betterSpecial = true;
        if(!oldHasBHG && newHasBHG)
            betterSpecial = true;

        // switch to new design when damage is floatd
        // more willing to upgrade when not at war
        float upgradeThreshold = empire.atWar() ? 1.5f : 1.25f;
        
        float upgradeChance = 1 + currDesign.availableSpace() / currDesign.totalSpace();
        
        //assume that the space not used for weapons is well used for other stuff and thus only look at firepower from space for weapons
        float currentWpnSpc = 0.0f;
        float newWpnSpc = 0.0f;
        for (int i=0; i<maxWeapons(); i++)
        {
            if(!currDesign.weapon(i).groundAttacksOnly())
                continue;
            currentWpnSpc += (currDesign.wpnCount(i) * currDesign.weapon(i).space(currDesign));
            newWpnSpc += (newDesign.wpnCount(i) * newDesign.weapon(i).space(newDesign));
        }
        
        float currentDPBC = currDesign.firepower(empire.bestEnemyPlanetaryShieldLevel()) / currentWpnSpc;
        float newDPBC = newDesign.firepower(empire.bestEnemyPlanetaryShieldLevel()) / newWpnSpc;
        if(currentDPBC > 0)
        {
            upgradeChance *= newDPBC / currentDPBC;
        }
        else if(newDPBC > 0)
        {
            upgradeChance *= 2;
        }
        
        //System.out.print("\n"+galaxy().currentYear()+" "+empire.name()+" Bomber upgrade "+currDesign.name()+" val: "+upgradeChance+" DPBC: "+newDPBC / currentDPBC+" better-Engine: "+betterEngine+" betterArmor: "+betterArmor);
        
        if (slot < 0 && !betterSpecial && !betterEngine && !betterArmor && (upgradeChance < upgradeThreshold) && currDesign.active() )
            return;
        
        //System.out.print("\n"+empire.name()+" designed new bomber which is "+upgradeChance+" better and should go to slot: "+slot);

        // if there is a slot available, use it for the new design
        if (slot >= 0) {
            log("Slot available: Bomber upgrade chance:"+upgradeChance);
            lab.setBomberDesign(newDesign, slot);
            currDesign.becomeObsolete(OBS_BOMBER_TURNS);
            return;
        }
        else
        {
            //with no free slots, we don't obsolete a design that is similar to an existing one
            if(!currDesign.matchesDesign(newDesign))
            {
                //if there is no slot available I push the old one to obsolete so a new slot will be freed up soon
                currDesign.becomeObsolete(OBS_BOMBER_TURNS);
                scrapWorstDesign(false);
                slot = lab.availableDesignSlot();
                lab.setBomberDesign(newDesign, slot);
            }
        }
    }
    public void updateFighterDesign() {
        ShipDesignLab lab = lab();
        
        boolean needRange = false;
        
        for(EmpireView ev : empire().contacts())
        {
            for(ShipDesign enemyDesign : ev.empire().shipLab().designs())
            {
                if(enemyDesign.scrapped())
                {
                    continue;
                }
                if(enemyDesign.repulsorRange() > 0)
                {
                    needRange = true;
                }
            }
        }
       
        ShipDesign currDesign = lab.fighterDesign();

        for (int j=0;j<maxSpecials();j++)
            if(currDesign.special(j).beamRangeBonus() > 0)
                needRange = false;
        for(int j=0;j<maxWeapons();j++)
            if(currDesign.weapon(j).range() > 1)
                needRange = false;
        
        // check for an available slot for the new design
        int slot = lab.availableDesignSlot();
        
        if (currDesign.engine() == lab.fastestEngine() && currDesign.active() && !needRange) {
            // and if the design has less than 10% free space or has less than 25/50/75/100 free space, we assume the redesign has no sense
            // 25/50/75/100 may be a too straight-line set of values
            if ((currDesign.availableSpace()/(currDesign.totalSpace()) < 0.1f) && slot < 0) {
                // we're fairly certain AI packed the ship before and if the current modules haven't shrinked enough,
                // that means we don't have enough new tech to make the new design markedly better
                currDesign.remainingLife++;
                return;
            }
        }

        int currSlot = currDesign.id();
        
        // find best hypothetical design vs current targets
        ShipDesign newDesign = newFighterDesign(currDesign.size());
      
        // WE HAVE FIGHTERS IN USE... VERIFY THIS UPGRADE JUSTIFIES SWITCHING OVER

        // factor in speed improvements when determining if new design is better
        // i.e. wrp 5, 100 dmg is better than wrp 3, 120 dmg
        boolean betterEngine = (newDesign.engine().warp() > currDesign.engine().warp());
        boolean betterArmor = (newDesign.armor().sequence() > currDesign.armor().sequence());
        boolean betterSpecial = false;
        boolean oldHasCloaking = false;
        boolean newHasCloaking = false;
        boolean oldHasStasis = false;
        boolean newHasStasis = false;
        boolean oldHasBHG = false;
        boolean newHasBHG = false;

        for (int i=0;i<maxSpecials();i++) {
            if(currDesign.special(i).allowsCloaking() == true)
                oldHasCloaking = true; 
            if(newDesign.special(i).allowsCloaking() == true)
                newHasCloaking = true;
            if(!currDesign.special(i).isNone() && currDesign.special(i).tech().isType(Tech.STASIS_FIELD) == true)
                oldHasStasis = true;
            if(!newDesign.special(i).isNone() && newDesign.special(i).tech().isType(Tech.STASIS_FIELD) == true)
                newHasStasis = true;
            if(currDesign.special(i).createsBlackHole())
                oldHasBHG = true;
            if(newDesign.special(i).createsBlackHole())
                newHasBHG = true;
        }
        
        if(!oldHasCloaking && newHasCloaking)
            betterSpecial = true;
        if(!oldHasStasis && newHasStasis)
            betterSpecial = true;
        if(!oldHasBHG && newHasBHG)
            betterSpecial = true;
        
        // switch to new design when damage is floatd
        // more willing to upgrade when not at war
        float upgradeThreshold = empire.atWar() ? 1.5f : 1.25f;
        
        float upgradeChance = 1 + currDesign.availableSpace() / currDesign.totalSpace();

        float currentWpnSpc = 0.0f;
        float newWpnSpc = 0.0f;
        for (int i=0; i<maxWeapons(); i++)
        {
            currentWpnSpc += (currDesign.wpnCount(i) * currDesign.weapon(i).space(currDesign));
            newWpnSpc += (newDesign.wpnCount(i) * newDesign.weapon(i).space(newDesign));
        }
        float currentDPBC = currDesign.firepowerAntiShip(empire.bestEnemyShieldLevel()) / currentWpnSpc;
        float newDPBC = newDesign.firepowerAntiShip(empire.bestEnemyShieldLevel()) / newWpnSpc;
        if(currentDPBC > 0)
        {
            upgradeChance *= newDPBC / currentDPBC;
        }
        else if(newDPBC > 0)
        {
            upgradeChance *= 2;
        }
        
        //System.out.print("\n"+galaxy().currentYear()+" "+empire.name()+" Fighter upgrade "+currDesign.name()+" val: "+upgradeChance+" DPBC: "+newDPBC / currentDPBC+" better-Engine: "+betterEngine+" betterArmor: "+betterArmor);
        
        //System.out.print("\n"+empire.name()+" designed new fighter which is "+upgradeChance+" better and should go to slot: "+slot);
        if (slot < 0 && !betterSpecial && !betterEngine && !betterArmor && (upgradeChance < upgradeThreshold) && !needRange && currDesign.active())
            return;

        // if there is a slot available, use it for the new design
        if (slot >= 0) {
            log("Slot available: Fighter upgrade chance:"+upgradeChance);
            lab.setFighterDesign(newDesign, slot);
            currDesign.becomeObsolete(OBS_FIGHTER_TURNS);
            return;
        }
        else
        {
            if(!currDesign.matchesDesign(newDesign))
            {
                currDesign.becomeObsolete(OBS_FIGHTER_TURNS);
                scrapWorstDesign(false);
                slot = lab.availableDesignSlot();
                lab.setFighterDesign(newDesign, slot);
            }
        }
    }
    public void updateDestroyerDesign() {
        //ail: don't use destroyer, just free up the design-slot here for more fighters and bombers
        ShipDesignLab lab = lab();
        
        ShipDesign currDesign = lab.destroyerDesign();
        if(currDesign.active() && currDesign.isDestroyer())
        {
            lab.scrapDesign(currDesign);
        }
    }
    @Override
    public ShipDesign newScoutDesign() {
        ShipDesignLab lab = lab();
        ShipDesign design = lab.newBlankDesign(ShipDesign.SMALL);
        design.engine(lab.fastestEngine());
        ShipSpecial special = lab.specialReserveFuel();
        design.special(0, special);
        design.setSmallestSize();
        design.mission(ShipDesign.SCOUT);
        design.maxUnusedTurns(OBS_SCOUT_TURNS);
        //lab.nameDesign(design);
        NewShipTemplate.nameShipDesign(this, design);
        lab.iconifyDesign(design);
        return design;
    }
    public ShipDesign newColonyDesign() {
        return newColonyDesign(!empire.contactedEmpires().isEmpty(), false);
    }
    public ShipDesign newColonyDesign(boolean weaponNeeded, boolean extendedRangeNeeded) {
        ShipDesignLab lab = lab();
        ShipDesign design = lab.newBlankDesign(ShipDesign.LARGE);
        NewShipTemplate.nameShipDesign(this, design);
        design.special(0, bestColonySpecial());
        design.engine(lab.fastestEngine());
        design.mission(ShipDesign.COLONY);
        design.maxUnusedTurns(OBS_COLONY_TURNS);

        boolean allowHuge = false;
        boolean unexploredInRange = false;
        
        for(StarSystem unexplored:empire.unexploredSystems())
        {
            if(empire.sv.inShipRange(unexplored.id))
            {
                unexploredInRange = true;
                break;
            }
        }
        
        float rangeTechLevelThreshold = 9;
        
        rangeTechLevelThreshold /= max(1.0f, session().researchMapSizeAdjustment());
        
        //System.out.print("\n"+empire.name()+" rangeTechLevelThreshold for galaxysize/empires: "+rangeTechLevelThreshold);
        
        if(session().options().selectedResearchRate().equals(RESEARCH_SLOW))
            rangeTechLevelThreshold /= sqrt(9/3.0f);
        else if(session().options().selectedResearchRate().equals(RESEARCH_SLOWER))
            rangeTechLevelThreshold /= sqrt(9);
        else if(session().options().selectedResearchRate().equals(RESEARCH_SLOWEST))
            rangeTechLevelThreshold /= sqrt(9*5);
            
        if(empire.uncolonizedPlanetsInRange(empire.shipRange()).isEmpty() 
                && empire.enemies().isEmpty()
                && !unexploredInRange
                && !empire.uncolonizedPlanetsInRange(empire.scoutRange()).isEmpty()
                && (empire.tech().propulsion().techLevel() >= rangeTechLevelThreshold && empire.tech().researchingShipRange() <= empire.shipRange() || empire.tech().propulsion().techLevel() >= 2 * rangeTechLevelThreshold))
            allowHuge = true;
            
        //System.out.print("\n"+empire.name()+" colonizable in normal range: "+empire.uncolonizedPlanetsInRange(empire.shipRange()).size()+" colonizable in extended-range: "+empire.uncolonizedPlanetsInRange(empire.scoutRange()).size()+" unexplored in range: "+unexploredInRange+" huge allowed: "+allowHuge+" rtlt: "+rangeTechLevelThreshold);
        // if we don't need regular-range colony ship
        if (extendedRangeNeeded) {
            ShipSpecial special = lab.specialReserveFuel();
            ShipSpecial prevSpecial = design.special(1);
            design.special(1, special);
            design.setSmallestSize();
            if (design.size() > ShipDesign.LARGE
                    && !allowHuge)
                design.special(1, prevSpecial);
        }
        design.setSmallestSize();
        //ail: only if we now still have room, we put a weapon on it. Extended range and smaller size is way more important than a weapon without computer
        if (weaponNeeded) {
            ShipWeapon bestWpn = lab.bestUnlimitedShotWeapon(design, 1);
            if (bestWpn != null && design.availableSpace() >= bestWpn.space(design))
                design.addWeapon(bestWpn, 1);
        }
        lab.iconifyDesign(design);
        return design;
    }
    @Override
    public int optimalShipFighterSize() {
        int preferredSize = empire.preferredShipSize();
        if (preferredSize == ShipDesign.SMALL)
            return preferredSize;

        List<StarSystem> systems = empire.allColonizedSystems();
        float maxProd = 0;
        for (StarSystem sys: systems)
            maxProd = max(sys.colony().production(), maxProd);

        int maxSize = ShipDesign.SMALL;
        if (maxProd >= 1500)
            maxSize = ShipDesign.HUGE;
        else if (maxProd >= 300)
            maxSize = ShipDesign.LARGE;
        else if (maxProd >= 60)
            maxSize = ShipDesign.MEDIUM;
        return min(preferredSize, maxSize);
    }
    @Override
    public int optimalShipBomberSize() {
        int preferredSize = empire.preferredShipSize()+1;
        if (preferredSize == ShipDesign.LARGE)
            return ShipDesign.LARGE;

        List<StarSystem> systems = empire.allColonizedSystems();
        float maxProd = 0;
        for (StarSystem sys: systems)
            maxProd = max(sys.colony().production(), maxProd);

        int maxSize = ShipDesign.MEDIUM;
        if (maxProd >= 1000)
            maxSize = ShipDesign.HUGE;
        else if (maxProd >= 200)
            maxSize = ShipDesign.LARGE;
        return min(preferredSize, maxSize);
    }
    @Override
    public int optimalShipDestroyerSize() {
        int preferredSize = empire.preferredShipSize()+1;
        if (preferredSize == ShipDesign.LARGE)
            return ShipDesign.LARGE;

        List<StarSystem> systems = empire.allColonizedSystems();
        float maxProd = 0;
        for (StarSystem sys: systems)
            maxProd = max(sys.colony().production(), maxProd);

        int maxSize = ShipDesign.MEDIUM;
        if (maxProd >= 1000)
            maxSize = ShipDesign.HUGE;
        else if (maxProd >= 200)
            maxSize = ShipDesign.LARGE;
        return min(preferredSize, maxSize);
    }
    @Override
    public ShipDesign newFighterDesign(int size) {
    //    ShipDesign design = ShipFighterTemplate.newDesign(this);
        ShipDesign design = NewShipTemplate.newFighterDesign(this);
        design.mission(ShipDesign.FIGHTER);
        design.maxUnusedTurns(OBS_FIGHTER_TURNS);
        return design;
    }
    @Override
    public ShipDesign newBomberDesign(int size) {
    //    ShipDesign design = ShipBomberTemplate.newDesign(this);
        ShipDesign design = NewShipTemplate.newBomberDesign(this);
        design.mission(ShipDesign.BOMBER);
        design.maxUnusedTurns(OBS_BOMBER_TURNS);
        return design;
    }
    @Override
    public ShipDesign newDestroyerDesign(int size) {
    //    ShipDesign design = ShipDestroyerTemplate.newDesign(this);
        ShipDesign design = NewShipTemplate.newDestroyerDesign(this);
        design.mission(ShipDesign.DESTROYER);
        design.maxUnusedTurns(OBS_DESTROYER_TURNS);
        return design;
    }
    @Override
    public ShipSpecialColony bestColonySpecial() {
        ShipSpecialColony bestSpecial = null;
        boolean ignoreEnv = empire.ignoresPlanetEnvironment();

        for (ShipSpecial spec : empire.shipLab().specials()) {
            if (spec.isColonySpecial()) {
                ShipSpecialColony colSpec = (ShipSpecialColony) spec;
                if (bestSpecial == null)
                    bestSpecial = colSpec;
                else if (ignoreEnv) {
                    if (bestSpecial.cost() >  colSpec.cost())  // silicoids measure by cost
                        bestSpecial = colSpec;
                }
                else {
                    if (bestSpecial.tech().hostilityAllowed() < colSpec.tech().hostilityAllowed())
                        bestSpecial = colSpec;
                }
            }
        }
        return bestSpecial;
    }
}
