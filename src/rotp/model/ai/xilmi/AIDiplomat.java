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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import rotp.model.ai.interfaces.Diplomat;
import rotp.model.combat.CombatStack;
import rotp.model.combat.ShipCombatResults;
import rotp.model.empires.DiplomaticEmbassy;
import rotp.model.empires.Empire;
import rotp.model.empires.EmpireView;
import rotp.model.empires.GalacticCouncil;
import rotp.model.empires.Leader;
import rotp.model.empires.SpyNetwork.Mission;
import rotp.model.empires.SpyReport;
import rotp.model.empires.TreatyWar;
import rotp.model.galaxy.Galaxy;
import rotp.model.galaxy.ShipFleet;
import rotp.model.galaxy.StarSystem;
import rotp.model.galaxy.Transport;
import rotp.model.incidents.AlliedWithEnemyIncident;
import rotp.model.incidents.AtWarWithAllyIncident;
import rotp.model.incidents.ColonyAttackedIncident;
import rotp.model.incidents.ColonyCapturedIncident;
import rotp.model.incidents.ColonyDestroyedIncident;
import rotp.model.incidents.ColonyInvadedIncident;
import rotp.model.incidents.DiplomaticIncident;
import rotp.model.incidents.EncroachmentIncident;
import rotp.model.incidents.EspionageTechIncident;
import rotp.model.incidents.EvictedSpiesIncident;
import rotp.model.incidents.ExpansionIncident;
import rotp.model.incidents.FinancialAidIncident;
import rotp.model.incidents.MilitaryBuildupIncident;
import rotp.model.incidents.OathBreakerIncident;
import rotp.model.incidents.SabotageBasesIncident;
import rotp.model.incidents.SabotageFactoriesIncident;
import rotp.model.incidents.SkirmishIncident;
import rotp.model.incidents.SpyConfessionIncident;
import rotp.model.incidents.TechnologyAidIncident;
import rotp.model.incidents.TrespassingIncident;
import rotp.model.ships.ShipDesign;
import rotp.model.tech.Tech;
import rotp.ui.diplomacy.DialogueManager;
import rotp.ui.diplomacy.DiplomacyTechOfferMenu;
import rotp.ui.diplomacy.DiplomaticCounterReply;
import rotp.ui.diplomacy.DiplomaticMessage;
import rotp.ui.diplomacy.DiplomaticReply;
import rotp.ui.notifications.DiplomaticNotification;
import rotp.util.Base;

public class AIDiplomat implements Base, Diplomat {
    private static final float ERRATIC_WAR_PCT = .02f;
    private final Empire empire;
    private float cumulativeSeverity = 0;

    public AIDiplomat (Empire c) {
        empire = c;
    }
    @Override
    public String toString()   { return concat("Diplomat: ", empire.raceName()); }

    private boolean diplomats(int empId) {
        return empire.viewForEmpire(empId).diplomats();
    }

    //-----------------------------------
    //  OFFER AID
    //-----------------------------------
    @Override
    public boolean canOfferAid(Empire e) { 
        if (!diplomats(id(e)) || empire.atWarWith(id(e)) || !empire.inEconomicRange(id(e)))
            return false;
                
        // do we have money to give?
        if (!offerAidAmounts().isEmpty())
            return true;
        
        // if not, do we have techs to give?
        return !offerableTechnologies(e).isEmpty();
    }
    public boolean canOfferMoney(Empire e) { 
        if (!diplomats(id(e)) || empire.atWarWith(id(e)) || !empire.inEconomicRange(id(e)))
            return false;
        return !offerAidAmounts().isEmpty();
    }
    public boolean canOfferTechnology(Empire e)  { 
        if (!diplomats(id(e)) || empire.atWarWith(id(e)) || !empire.inEconomicRange(id(e)))
            return false;
                
        return !offerableTechnologies(e).isEmpty();
    }
    @Override
    public List<Tech> offerableTechnologies(Empire e) {
        List<String> allMyTechIds = empire.tech().allKnownTechs();
        List<String> hisTechIds = e.tech().allKnownTechs();
        List<String> hisTradedTechIds = e.tech().tradedTechs();
        allMyTechIds.removeAll(hisTechIds);
        allMyTechIds.removeAll(hisTradedTechIds);
         
        List<Tech> allTechs = new ArrayList<>();
        for (String id: allMyTechIds)
            allTechs.add(tech(id));
        allTechs.removeAll(e.tech().tradedTechs());
        
        int maxTechs = 5;
        // sort unknown techs by our research value 
        Tech.comparatorCiv = empire;
        Collections.sort(allTechs, Tech.RESEARCH_VALUE);
        if (allTechs.size() <= maxTechs)
            return allTechs;
        List<Tech> techs = new ArrayList<>(maxTechs);
        for (int i=0; i<maxTechs;i++)
            techs.add(allTechs.get(i));
        return techs;
    }
    @Override
    public List<Integer> offerAidAmounts() {
        float reserve = empire.totalReserve();
        List<Integer> amts = new ArrayList<>();
        if (reserve > 25000) {
            amts.add(10000);amts.add(5000); amts.add(1000); amts.add(500);
        }
        else if (reserve > 10000) {
            amts.add(5000); amts.add(1000); amts.add(500); amts.add(100);
        }
        else if (reserve > 2500) {
            amts.add(1000);amts.add(500); amts.add(100); amts.add(50);
        }
        else if (reserve > 1000) {
            amts.add(500); amts.add(100); amts.add(50);
        }
        else if (reserve > 250) {
            amts.add(100); amts.add(50);
        }
        else if (reserve > 100) {
            amts.add(50);
        }
        return amts;
    }
    @Override
    public DiplomaticReply receiveFinancialAid(Empire donor, int amt) {
        if (amt > 0) {
            empire.addToTreasury(amt);
            donor.addToTreasury(0-amt);
        }
        EmpireView view = donor.viewForEmpire(empire);
        DiplomaticIncident inc = FinancialAidIncident.create(empire, donor, amt);
        return view.accept(DialogueManager.ACCEPT_FINANCIAL_AID, inc);
    }
    @Override
    public DiplomaticReply receiveTechnologyAid(Empire donor, String techId) {
        empire.tech().acquireTechThroughTrade(techId, donor.id);

        EmpireView view = donor.viewForEmpire(empire);
        DiplomaticIncident inc = TechnologyAidIncident.create(empire, donor, techId);
        return view.accept(DialogueManager.ACCEPT_TECHNOLOGY_AID, inc);
    }
    //-----------------------------------
    //  EXCHANGE TECHNOLOGY
    //-----------------------------------
    @Override
    public boolean canExchangeTechnology(Empire e) { 
        // to trade technology with another empire, all of the following must be true:
        // 1 - the game setup options allow it 
        // 2 - we have diplomats active
        // 3 - we are not at war
        // 4 - we are in economic range
        // 5 - our spies tell us they have technologies that are unknown to us
        return options().canTradeTechs(empire, e) 
                && diplomats(id(e)) 
                && !empire.atWarWith(id(e)) 
                && empire.inEconomicRange(id(e)) 
                && !empire.viewForEmpire(id(e)).spies().unknownTechs().isEmpty(); 
    }

    @Override
    public DiplomaticReply receiveRequestTech(Empire diplomat, Tech tech) {
        if (empire.isPlayerControlled()) {
            EmpireView v = diplomat.viewForEmpire(empire);
            // 1st, create the reply for the AI asking the player for the tech
            DiplomaticReply reply = v.otherView().accept(DialogueManager.OFFER_TECH_EXCHANGE);
            // decode the [tech] field in the reply text
            reply.decode("[tech]", tech.name());
            // 2nd, create the counter-offer menu that the player would present to the AI
            DiplomacyTechOfferMenu menu = DiplomacyTechOfferMenu.create(empire, diplomat, reply, tech);
            // if counter offers available, display the request in modal
            if (menu.hasCounterOffers())
                DiplomaticMessage.replyModal(menu);
            return null;
        }

        EmpireView v = empire.viewForEmpire(diplomat);
        v.embassy().noteRequest();
        if (v.embassy().alreadyOfferedTech())
            v.embassy().noteRequest();

        if (v.embassy().tooManyRequests())
            return v.refuse(DialogueManager.DECLINE_ANNOYED);
        
        // modnar: add in readyForTech check, limits one tech trade per turn per empire
        // this also prevents trading the same tech multiple times to the same empire
        if (!v.embassy().readyForTech())
            return v.refuse(DialogueManager.DECLINE_OFFER);
        
        v.embassy().resetTechTimer();

        List<Tech> counterTechs = empire.diplomatAI().techsRequestedForCounter(diplomat, tech);
        if (counterTechs.isEmpty())
            return v.refuse(DialogueManager.DECLINE_TECH_TRADE);

        // accept and present a menu of counter-offer techs
        return v.otherView().accept(DialogueManager.DIPLOMACY_TECH_CTR_MENU);
    }
    @Override
    public DiplomaticReply receiveCounterOfferTech(Empire diplomat, Tech offeredTech, Tech requestedTech) {
        EmpireView view = empire.viewForEmpire(diplomat);
        DiplomaticIncident inc = view.embassy().exchangeTechnology(offeredTech, requestedTech);
        return view.otherView().accept(DialogueManager.ACCEPT_TECH_EXCHANGE, inc);
    }
    @Override
    public List<Tech> techsAvailableForRequest(Empire diplomat) {
        EmpireView view = empire.viewForEmpire(diplomat);
        List<Tech> allUnknownTechs = view.spies().unknownTechs();

        List<Tech> allTechs = new ArrayList<>();
        for (int i=0; i<allUnknownTechs.size();i++) {
            Tech tech = allUnknownTechs.get(i);
            if (!diplomat.diplomatAI().techsRequestedForCounter(empire, tech).isEmpty())
                allTechs.add(allUnknownTechs.get(i));
        }        

        int maxTechs = 5;
        // sort unknown techs by our research value 
        Tech.comparatorCiv = empire;
        Collections.sort(allTechs, Tech.RESEARCH_VALUE);
        if (allTechs.size() <= maxTechs)
            return allTechs;
        List<Tech> techs = new ArrayList<>(maxTechs);
        for (int i=0; i<maxTechs;i++) 
            techs.add(allTechs.get(i));
        return techs;
    }
    @Override
    public List<Tech> techsRequestedForCounter(Empire requestor, Tech tech) {
        EmpireView view = empire.viewForEmpire(requestor);

        // what is this times the value of the request tech?dec
        float maxTechValue = techDealValue(view) * tech.tradeValue(empire);

        // what are all of the unknown techs that we could ask for
        List<Tech> allTechs = view.spies().unknownTechs();

        // include only those techs which have a research value >= the trade value
        // of the requestedTech we would be trading away
        List<Tech> worthyTechs = new ArrayList<>(allTechs.size());
        for (Tech t: allTechs) {
            if (t.quintile() == tech.quintile()) {
                if (t.baseValue(empire) < maxTechValue)
                    worthyTechs.add(t);
            }
        }

        // sort techs by the diplomat's research priority (hi to low)
        Tech.comparatorCiv = empire;
        Collections.sort(worthyTechs, Tech.RESEARCH_VALUE);

        // limit return to top 5 techs
        int maxTechs = 5;
        if (worthyTechs.size() <= maxTechs)
            return worthyTechs;
        List<Tech> topFiveTechs = new ArrayList<>(maxTechs);
        for (int i=0; i<maxTechs;i++)
            topFiveTechs.add(worthyTechs.get(i));
        return topFiveTechs;
    }
    private boolean decidedToExchangeTech(EmpireView v) {
        if (!willingToOfferTechExchange(v))
            return false;

        List<Tech> availableTechs = v.spies().unknownTechs();
        if (availableTechs.isEmpty())
            return false;

        // iterate over each of available techs, starting with the most desired
        // until one is found that we can make counter-offers for... use that one
        while (!availableTechs.isEmpty()) {
            Tech wantedTech = empire.ai().scientist().mostDesirableTech(availableTechs);
            availableTechs.remove(wantedTech);
            if (empire.ai().scientist().researchValue(wantedTech) > 1) {
                List<Tech> counterTechs = v.empire().diplomatAI().techsRequestedForCounter(empire, wantedTech);
                if (!counterTechs.isEmpty()) {
                    List<Tech> previouslyOffered = v.embassy().alreadyOfferedTechs(wantedTech);
                    // simplified logic so that if we have ever asked for wantedTech before, don't ask again
                    if (previouslyOffered == null) {
                         v.embassy().logTechExchangeRequest(wantedTech, counterTechs);
                        // there are counters available.. send request
                        DiplomaticReply reply = v.empire().diplomatAI().receiveRequestTech(empire, wantedTech);
                        if ((reply != null) && reply.accepted()) {
                            // techs the AI is willing to consider in exchange for wantedTech
                            // find the tech with the lowest trade value
                            counterTechs.add(wantedTech);
                            Collections.sort(counterTechs, Tech.TRADE_PRIORITY);
                            Tech cheapestCounter = counterTechs.get(0);
                            // if the lowest trade value tech is not the requested tech, then make the deal
                            if (cheapestCounter != wantedTech)
                                v.empire().diplomatAI().receiveCounterOfferTech(empire, cheapestCounter, wantedTech);
                        }
                        return true;
                    }
                }
            }
        }
        return false;
    }
    private boolean willingToOfferTechExchange(EmpireView v) {
        if (!canExchangeTechnology(v.empire()))
            return false;
        if(empire.enemies().contains(v.empire()))
        {
            return false;
        }
        return true;
    }
    public Tech mostDesirableTech(EmpireView v) {
        return empire.ai().scientist().mostDesirableTech(v.spies().unknownTechs());
    }
    private float techDealValue(EmpireView v) {
        if (v.embassy().alliance())
            return 1.0f;
        else if (v.embassy().pact())
            return 0.9f;
        else
            return 0.8f;
    }
    //-----------------------------------
    //  TRADE TREATIES
    //-----------------------------------
    @Override
    public boolean canOfferDiplomaticTreaties(Empire e) {
        if (!empire.inEconomicRange(id(e)))
            return false;
        EmpireView view = empire.viewForEmpire(id(e));
        if (view.embassy().finalWar() || view.embassy().unity())
            return false;
        return true;
    }
    @Override
    public boolean canOfferTradeTreaty(Empire e) {
        if (!empire.inEconomicRange(id(e)))
            return false;
        EmpireView view = empire.viewForEmpire(id(e));
        // Automatic exclusion for AI empires:
        //    1) Have trade established but it is not yet profitable
        // or 2) Not at full trade && and can't increase current trade level by +50%
        if (empire.isAIControlled()) {
            if ((view.trade().level() > 0)
            && (view.trade().profit() <= 0))
                return false;
            if (!view.trade().atFullLevel()
            || (view.trade().level() * 1.5) > view.trade().maxLevel())
                return false;
        }

        // no trade if no diplomats or at war
        if (!diplomats(id(e)) || empire.atWarWith(id(e)) )
            return false;
        // no trade offer if can't increase from current lvl
        if (view.nominalTradeLevels().isEmpty())
            return false;

        return true;
    }
    @Override
    public DiplomaticReply receiveOfferTrade(Empire requestor, int level) {
        // if the AI is asking the player, create an OfferTrade notification
        log(empire.name(), " receiving offer trade from: ", requestor.name(), "  for:", str(level), " BC");
        if (empire.isPlayerControlled()) {
            DiplomaticNotification.create(requestor.viewForEmpire(empire), DialogueManager.OFFER_TRADE);
            return null;
        }
        EmpireView v = empire.viewForEmpire(requestor);
        if (requestor.isPlayerControlled()) {
            if (random(100) < leaderDiplomacyAnnoyanceMod(v)) {
                v.embassy().withdrawAmbassador();
                return v.refuse(DialogueManager.DECLINE_ANNOYED);
            }
        }

        v.embassy().noteRequest();
        if (!v.embassy().readyForTrade(level))
            return v.refuse(DialogueManager.DECLINE_OFFER);

        v.embassy().resetTradeTimer(level);

        if(empire.enemies().contains(v.empire()))
        {
            return refuseOfferTrade(requestor, level);
        }

        v.otherView().embassy().tradeAccepted();
        DiplomaticIncident inc = v.otherView().embassy().establishTradeTreaty(level);
        return v.otherView().accept(DialogueManager.ACCEPT_TRADE, inc);
    }
    @Override
    public DiplomaticReply immediateRefusalToTrade(Empire requestor) {
        EmpireView v = empire.viewForEmpire(requestor);
        int bonus = requestor.diplomacyBonus();
        if ((baseChanceForTrade(v)+bonus) < 0) {
            return DiplomaticReply.answer(false, declineReasonText(v));
        }
        return null;
    }
    @Override
    public DiplomaticReply acceptOfferTrade(Empire e, int level) {
        EmpireView v = empire.viewForEmpire(e);
        DiplomaticIncident inc = v.embassy().establishTradeTreaty(level);
        return v.accept(DialogueManager.ANNOUNCE_TRADE, inc);
    }
    @Override
    public DiplomaticReply refuseOfferTrade(Empire requestor, int level) {
        EmpireView v = empire.viewForEmpire(requestor);
        v.embassy().resetTradeTimer(level);
        return DiplomaticReply.answer(false, declineReasonText(v));
    }
    private boolean willingToOfferTrade(EmpireView v, int level) {
        if (!canOfferTradeTreaty(v.empire()))
            return false;
        if (v.embassy().alliedWithEnemy()) 
            return false;
        
        // if asking player, check that we don't spam him
        if (v.empire().isPlayerControlled()) {
             if (!v.otherView().embassy().readyForTrade(level))
                return false;
        }

        float currentTrade = v.trade().level();
        float maxTrade = v.trade().maxLevel();
        if (maxTrade < (currentTrade * 1.1))
            return false;

        log(v.toString(), ": willing to offer trade. Max:", str(maxTrade), "    current:", str(currentTrade));
        if(empire.enemies().contains(v.empire()))
        {
            return false;
        }
        return true;
    }
    private float baseChanceForTrade(EmpireView v) {
        // -50 relations is minimum allowed to accept trade
        float adjustedRelations = v.embassy().relations()+50;
        float leaderMod = leaderAcceptTradeMod();
        float raceBonusMod = v.empire().tradePctBonus();
        float allianceMod = v.embassy().alliedWithEnemy() ? -50 : 0;
        return adjustedRelations+leaderMod+raceBonusMod+allianceMod;
    }
    private String declineReasonText(EmpireView v) {
        DialogueManager dlg = DialogueManager.current();
        DiplomaticIncident inc = worstWarnableIncident(v.embassy().allIncidents());

        // no reason or insignificant, so give generic error
        if ((inc == null) || (inc.currentSeverity() > -5))
            return v.decode(dlg.randomMessage(DialogueManager.DECLINE_OFFER, v.owner()));

        if (inc instanceof OathBreakerIncident)
            return v.decode(inc.decode(dlg.randomMessage(DialogueManager.DECLINE_OATHBREAKER, v.owner())));

        if (inc instanceof MilitaryBuildupIncident)
            return v.decode(inc.decode(dlg.randomMessage(DialogueManager.DECLINE_BUILDUP, v.owner())));

        if (inc instanceof EncroachmentIncident)
            return v.decode(inc.decode(dlg.randomMessage(DialogueManager.DECLINE_ENCROACH, v.owner())));

        if (inc instanceof SkirmishIncident)
            return v.decode(inc.decode(dlg.randomMessage(DialogueManager.DECLINE_SKIRMISH, v.owner())));

        if (inc instanceof ColonyAttackedIncident)
            return v.decode(inc.decode(dlg.randomMessage(DialogueManager.DECLINE_ATTACK, v.owner())));

        if ((inc instanceof ColonyCapturedIncident)
        || (inc instanceof ColonyDestroyedIncident)
        || (inc instanceof ColonyInvadedIncident))
            return v.decode(inc.decode(dlg.randomMessage(DialogueManager.DECLINE_INVASION, v.owner())));

        if (inc instanceof EspionageTechIncident)
            return v.decode(inc.decode(dlg.randomMessage(DialogueManager.DECLINE_ESPIONAGE, v.owner())));

        if ((inc instanceof SabotageBasesIncident)
        || (inc instanceof SabotageFactoriesIncident))
            return v.decode(inc.decode(dlg.randomMessage(DialogueManager.DECLINE_SABOTAGE, v.owner())));

        // unknown reason, return generic error
        return v.decode(dlg.randomMessage(DialogueManager.DECLINE_OFFER, v.owner()));
    }
    //-----------------------------------
    //  PEACE
    //-----------------------------------
    public boolean canOfferPeaceTreaty(Empire e)           { return diplomats(id(e)) && empire.atWarWith(id(e)); }
    @Override
    public DiplomaticReply receiveOfferPeace(Empire requestor) {
        log(empire.name(), " receiving offer of Peace from: ", requestor.name());
        if (empire.isPlayerControlled()) {
            DiplomaticNotification.create(requestor.viewForEmpire(empire), DialogueManager.OFFER_PEACE);
            return null;
        }

        int bonus = requestor.diplomacyBonus();
        EmpireView v = empire.viewForEmpire(requestor);
        if ((bonus+random(100)) < leaderDiplomacyAnnoyanceMod(v)) {
            v.embassy().withdrawAmbassador();
            return v.refuse(DialogueManager.DECLINE_ANNOYED);
        }

        v.embassy().noteRequest();

        if (!v.embassy().readyForPeace())
            return v.refuse(DialogueManager.DECLINE_OFFER);

        v.embassy().resetPeaceTimer();
        
        if (!warWeary(v))
            return refuseOfferPeace(requestor);

        DiplomaticIncident inc = v.embassy().signPeace();
        return v.otherView().accept(DialogueManager.ACCEPT_PEACE, inc);
    }
    @Override
    public DiplomaticReply acceptOfferPeace(Empire requestor) {
        EmpireView v = requestor.viewForEmpire(empire);
        DiplomaticIncident inc = v.embassy().signPeace();
        return v.otherView().accept(DialogueManager.ANNOUNCE_PEACE, inc);
    }
    @Override
    public DiplomaticReply refuseOfferPeace(Empire requestor) {
        EmpireView v = empire.viewForEmpire(requestor);
        v.embassy().resetPeaceTimer();
        return DiplomaticReply.answer(false, declineReasonText(v));
    }
    private boolean willingToOfferPeace(EmpireView v) {
        if (!v.embassy().war())
            return false;
        if (!v.embassy().onWarFooting() && !canOfferPeaceTreaty(v.empire()))
            return false;
        if (v.embassy().contactAge() < 1)
            return false;
        if (!v.otherView().embassy().readyForPeace())
            return false;

        return warWeary(v);
    }
    //-----------------------------------
    //  PACT
    //-----------------------------------
    public boolean canOfferPact(Empire e){ 
        if (!diplomats(id(e)))
            return false;
        if (!empire.inEconomicRange(id(e)))
            return false; 
        if (empire.atWarWith(id(e)))
            return false;
        if (!empire.hasTradeWith(e))
            return false;
        if (empire.pactWith(id(e)) || empire.alliedWith(id(e)))
            return false;
        return true;
   }

    @Override
    public DiplomaticReply receiveOfferPact(Empire requestor) {
        log(empire.name(), " receiving offer of Pact from: ", requestor.name());
        EmpireView v = empire.viewForEmpire(requestor);
        if (empire.isPlayerControlled()) {
            DiplomaticNotification.create(requestor.viewForEmpire(empire), DialogueManager.OFFER_PACT);
            return null;
        }

        if (requestor.isPlayerControlled()) {
            if (random(100) < leaderDiplomacyAnnoyanceMod(v)) {
                v.embassy().withdrawAmbassador();
                return v.refuse(DialogueManager.DECLINE_ANNOYED);
            }
        }

        v.embassy().noteRequest();

        if (!v.embassy().readyForPact())
            return v.refuse(DialogueManager.DECLINE_OFFER);

        v.embassy().resetPactTimer();
        
        //ail: just use the same logic we'd use for offering
        if(willingToOfferPact(empire.viewForEmpire(requestor)))
        {
            DiplomaticIncident inc = v.embassy().signPact();
            return v.otherView().accept(DialogueManager.ACCEPT_PACT, inc);
        }
        else
            return v.refuse(DialogueManager.DECLINE_OFFER);
    }
    @Override
    public DiplomaticReply acceptOfferPact(Empire requestor) {
        EmpireView v = empire.viewForEmpire(requestor);
        DiplomaticIncident inc = v.embassy().signPact();
        return v.accept(DialogueManager.ANNOUNCE_PACT, inc);
    }
    @Override
    public DiplomaticReply refuseOfferPact(Empire requestor) {
        EmpireView v = empire.viewForEmpire(requestor);
        v.embassy().resetPactTimer();
        return DiplomaticReply.answer(false, declineReasonText(v));
    }
    //ail: pacts just restrict us unnecessarily
    private boolean willingToOfferPact(EmpireView v) {
        return false;
    }
    //-----------------------------------
    //  ALLIANCE
    //-----------------------------------
    public boolean canOfferAlliance(Empire e) { 
        if (!diplomats(id(e)))
            return false;
        if (!empire.inEconomicRange(id(e)))
            return false; 
        if (empire.atWarWith(id(e)))
            return false;
        if (!empire.hasTradeWith(e))
            return false;
        if (empire.alliedWith(id(e)))
            return false;
        return true;
    }
    @Override
    public DiplomaticReply receiveOfferAlliance(Empire requestor) {
        log(empire.name(), " receiving offer of Alliance from: ", requestor.name());
        if (empire.isPlayerControlled()) {
            DiplomaticNotification.create(requestor.viewForEmpire(empire), DialogueManager.OFFER_ALLIANCE);
            return null;
        }

        EmpireView v = empire.viewForEmpire(requestor);
        if (requestor.isPlayerControlled()) {
            if (random(100) < leaderDiplomacyAnnoyanceMod(v)) {
                v.embassy().withdrawAmbassador();
                return v.refuse(DialogueManager.DECLINE_ANNOYED);
            }
        }
        v.embassy().noteRequest();

        List<Empire> myEnemies = v.owner().warEnemies();
        List<Empire> hisAllies = v.empire().allies();
        for (Empire enemy: myEnemies) {
            if (hisAllies.contains(enemy))
                return v.refuse(DialogueManager.DECLINE_ENEMY_ALLY, enemy);
        }
        if(willingToOfferAlliance(requestor))
            return signAlliance(requestor);
        else
            return refuseOfferAlliance(requestor);
    }
    public DiplomaticReply signAlliance(Empire requestor) {
        EmpireView v = empire.viewForEmpire(requestor);
        DiplomaticIncident inc = v.embassy().signAlliance();
        return v.otherView().accept(DialogueManager.ACCEPT_ALLIANCE, inc);
    }
    @Override
    public DiplomaticReply acceptOfferAlliance(Empire requestor) {
        EmpireView v = empire.viewForEmpire(requestor);
        DiplomaticIncident inc = v.embassy().signAlliance();
        return v.accept(DialogueManager.ANNOUNCE_ALLIANCE, inc);
    }
    @Override
    public DiplomaticReply refuseOfferAlliance(Empire requestor) {
        EmpireView v = empire.viewForEmpire(requestor);
        v.embassy().resetAllianceTimer();
        return DiplomaticReply.answer(false, declineReasonText(v));
    }
    @Override
    public boolean willingToOfferAlliance(Empire e) {
        //ail: Alliances are just a broken game mechanic. It defies the categorical imperative: If everyone would use it, it would make the whole game pointless
        return false;
    }
//-----------------------------------
//  JOINT WARS
//-----------------------------------
    public boolean willingToRequestAllyToJoinWar(Empire friend, Empire target) {
        // this method is called only for targets that we are at explicit war with
        // and the friend is our ALLY
        
        // if he's already at war, don't bother
        if (friend.atWarWith(target.id))
            return false;
        // if he's not in economic range, don't bother
        if (!friend.inShipRange(target.id))
            return false;
        return true;
    }
    public boolean willingToOfferJointWar(Empire friend, Empire target) {
        // this method is called only for targets that we are at war with
        // or targets we are preparing for war with
        // only ask people who we are in real contact with
        if (!empire.inEconomicRange(friend.id))
            return false;
        if (friend.isPlayerControlled()) {
            EmpireView v = empire.viewForEmpire(friend);
            if (!v.otherView().embassy().readyForJointWar())
                return false;
        }    
        // if he's already at war, don't bother
        if (friend.atWarWith(target.id))
            return false;
        // if he's allied with the target, don't bother
        if (friend.alliedWith(target.id))
            return false;
        // if he's not in ship range, don't bother
        if (!friend.inShipRange(target.id))
            return false;
        return true;
    }
    @Override
    public DiplomaticReply receiveOfferJointWar(Empire requestor, Empire target) {
        log(empire.name(), " receiving offer of Joint War from: ", requestor.name());
        if (empire.isPlayerControlled()) {
            DiplomaticNotification.create(requestor.viewForEmpire(empire), DialogueManager.OFFER_JOINT_WAR, target);
            return null;
        }
        
        if (empire.atWarWith(target.id))
            return DiplomaticReply.answer(false, "Already at war with that empire");

        EmpireView v = empire.viewForEmpire(requestor);
        
        // not helping someone whom I don't have real contact with
        if (!empire.inEconomicRange(requestor.id))
            return v.refuse(DialogueManager.DECLINE_OFFER, target);

        // never willing to declare war on an ally
        if (empire.alliedWith(target.id))
            return v.refuse(DialogueManager.DECLINE_NO_WAR_ON_ALLY, target);
        
        // never willing to declare war on an NAP partner
        if (empire.pactWith(target.id))
            return v.refuse(DialogueManager.DECLINE_OFFER, target);
        
        // if a peacy treaty is in effect with the target, then refuse
        if (empire.viewForEmpire(target.id).embassy().atPeace()) {
            return v.refuse(DialogueManager.DECLINE_PEACE_TREATY, target);
        }
        
        //ail: if we are preparing a war against them anyways, we can also make it official here
        if(empire.enemies().contains(target) && !empire.warEnemies().contains(target))
            return agreeToJointWar(requestor, target);
        
        if(!empire.enemies().isEmpty())
            return v.refuse(DialogueManager.DECLINE_OFFER, target);

         // will always declare war if allied with the requestor and he is already at war with the target
        if (requestor.alliedWith(id(empire)) && requestor.atWarWith(target.id))
            return agreeToJointWar(requestor, target);
        
        //ail: if the target is our common enemy and together we are stronger than them, we'll accept without a bribe
        //System.out.println(empire.galaxy().currentTurn()+" "+ requestor.name()+" asked "+empire.name()+" to declare joint war on "+target.name());
        if(target == empire.generalAI().bestVictim())
        {
            float combinedPower = requestor.powerLevel(requestor) + empire.powerLevel(empire);
            for(Empire targetWars:target.warEnemies())
            {
                if(targetWars == requestor || targetWars == empire)
                    continue;
                combinedPower += targetWars.powerLevel(targetWars);
            }
            //System.out.println(empire.galaxy().currentTurn()+" "+target.name()+" is best victim for "+empire.name()+" combined power: "+combinedPower+" their power: "+target.powerLevel(target));
            if(combinedPower > target.powerLevel(target))
            {
                //System.out.println(empire.galaxy().currentTurn()+" "+empire.name()+" agreed to support "+requestor.name()+" in their war against "+target.name());
                return agreeToJointWar(requestor, target);
            }
        }

        int maxBribe = galaxy().numberTurns()*50;
        float bribeValue = bribeAmountToJointWar(target);
        
        if (empire.alliedWith(target.id))
            bribeValue *= 2;
        else if (empire.pactWith(target.id))
            bribeValue *= 1.5;
        if (empire.leader().isPacifist())
            bribeValue *= 2;
        
        List<Tech> allTechs = v.spies().unknownTechs();   
        if (allTechs.isEmpty())
            return v.refuse(DialogueManager.DECLINE_OFFER, target);

        Tech.comparatorCiv = empire;
        Collections.sort(allTechs, Tech.WAR_TRADE_VALUE);
        
        List<String> requestedTechs = new ArrayList<>();
        for (Tech tech : allTechs) {
            if ((bribeValue > 0) && (requestedTechs.size() < 3)) {
                requestedTechs.add(tech.id());
                bribeValue -= tech.researchCost();
            }
        }
        if (requestedTechs.isEmpty())
            requestedTechs.add(allTechs.get(0).id());
        
        if (bribeValue > maxBribe)
            return v.refuse(DialogueManager.DECLINE_OFFER, target);
        return v.counter(DialogueManager.COUNTER_JOINT_WAR, target, requestedTechs, bribeValue);
    }
    @Override
    public DiplomaticReply receiveCounterJointWar(Empire requestor, DiplomaticCounterReply counter) {
        for (String techId: counter.techs()) 
            empire.tech().acquireTechThroughTrade(techId, requestor.id);
        
        if (counter.bribeAmt() > 0) {
            empire.addToTreasury(counter.bribeAmt());
            requestor.addToTreasury(0-counter.bribeAmt());
        }
        return agreeToJointWar(requestor, counter.target());
    }
    private DiplomaticReply agreeToJointWar(Empire requestor, Empire target) {
        int targetId = target.id;
        if (!requestor.atWarWith(targetId))
            requestor.viewForEmpire(targetId).embassy().declareWar();
 
        DiplomaticIncident inc =  empire.viewForEmpire(targetId).embassy().declareJointWar(requestor);
        return empire.viewForEmpire(requestor).accept(DialogueManager.ACCEPT_JOINT_WAR, inc);   
    }
    private float bribeAmountToJointWar(Empire target) {
        EmpireView v = empire.viewForEmpire(target);
        float myFleets = empire.totalArmedFleetSize();
        float tgtFleets = empire.totalFleetSize(target);
        float myTech = empire.tech().avgTechLevel();
        float tgtTech = v.spies().tech().avgTechLevel();
        float fleetShortcoming = (tgtFleets*tgtTech)-(myFleets*myTech);
        return max(0, fleetShortcoming);
    }
    @Override
    public DiplomaticReply acceptOfferJointWar(Empire requestor, Empire target) {
        int targetId = target.id;
        if (!requestor.atWarWith(targetId))
            requestor.viewForEmpire(targetId).embassy().declareWar();
 
        DiplomaticIncident inc = empire.viewForEmpire(targetId).embassy().declareJointWar(requestor);
        return empire.viewForEmpire(requestor).accept(DialogueManager.ACCEPT_JOINT_WAR, inc);   
    }
    @Override
    public DiplomaticReply refuseOfferJointWar(Empire requestor, Empire target) {
        EmpireView v = empire.viewForEmpire(requestor);
        v.embassy().resetJointWarTimer();
        
        if (empire.alliedWith(requestor.id) && requestor.atWarWith(target.id)) 
            return requestor.diplomatAI().receiveBreakAlliance(empire);        
        return null;
    }
    //-----------------------------------
    //  BREAK TREATIES
    //-----------------------------------
    public boolean canCloseEmbassy(Empire e)               { return empire.aggressiveWith(id(e)); }
    public boolean canDemandTribute(Empire e)              { return true; }
    public boolean canBreakTrade(Empire e)                 { return empire.tradingWith(e); }
    public boolean canBreakPact(Empire e)                  { return empire.pactWith(id(e)); }
    public boolean canBreakAlliance(Empire e)              { return empire.alliedWith(id(e)); }
    @Override
    public boolean canDeclareWar(Empire e)                 { return empire.inShipRange(id(e)) && !empire.atWarWith(id(e)) && !empire.alliedWith(id(e)); }
    @Override
    public boolean canThreaten(Empire e) { 
        if (!diplomats(id(e)))
            return false;
        return canEvictSpies(e) || canThreatenSpying(e) || canThreatenAttacking(e); 
    }
    @Override
    public boolean canThreatenSpying(Empire e) { 
        return false;
    }
    @Override
    public boolean canEvictSpies(Empire e) { 
        return false;
    }
    @Override
    public boolean canThreatenAttacking(Empire e) { 
        if (!empire.inEconomicRange(id(e)))
            return false;
        if (empire.atWarWith(id(e)))
            return false;
        
        EmpireView v = e.viewForEmpire(empire);
        if (v.embassy().hasCurrentAttackIncident())
            return true;
        return false; 
    }

    public DiplomaticReply receiveDemandTribute(Empire e) {
        EmpireView v = empire.viewForEmpire(id(e));
        v.embassy().noteRequest();
        if (random() > chanceToGiveTribute(v))
            return DiplomaticReply.answer(false, declineReasonText(v));

        DiplomaticIncident inc = v.otherView().embassy().demandTribute();
        return v.accept(DialogueManager.ACCEPT_JOINT_WAR, inc);
    }
    private float chanceToGiveTribute(EmpireView v) {
        return 0.50f;
    }
    @Override
    public DiplomaticReply receiveBreakPact(Empire e) {
        EmpireView v = empire.viewForEmpire(e);
        v.embassy().noteRequest();
        DiplomaticIncident inc = v.otherView().embassy().breakPact();
        v.embassy().withdrawAmbassador();
        return v.otherView().accept(DialogueManager.RESPOND_BREAK_PACT, inc);
    }
    @Override
    public DiplomaticReply receiveBreakAlliance(Empire e) {
        EmpireView v = empire.viewForEmpire(e);
        v.embassy().noteRequest();
        DiplomaticIncident inc = v.otherView().embassy().breakAlliance();
        v.embassy().withdrawAmbassador();
        return v.otherView().accept(DialogueManager.RESPOND_BREAK_ALLIANCE, inc);
    }
    @Override
    public DiplomaticReply receiveBreakTrade(Empire e) {
        EmpireView v = empire.viewForEmpire(e);
        v.embassy().noteRequest();
        DiplomaticIncident inc = v.otherView().embassy().breakTrade();
        v.embassy().withdrawAmbassador();
        return v.otherView().accept(DialogueManager.RESPOND_BREAK_TRADE, inc);
    }
    @Override
    public DiplomaticReply receiveThreatStopSpying(Empire e) {
        EmpireView v = empire.viewForEmpire(e);
        
        v.embassy().noteRequest();
        v.embassy().withdrawAmbassador();
        
        v.spies().ignoreThreat();
        return v.otherView().accept(DialogueManager.RESPOND_IGNORE_THREAT);
    }
    @Override
    public DiplomaticReply receiveThreatEvictSpies(Empire e) {
        EmpireView v = empire.viewForEmpire(e);
        
        v.embassy().noteRequest();
        v.embassy().withdrawAmbassador();

        EvictedSpiesIncident inc = EvictedSpiesIncident.create(v);
        v.embassy().addIncident(inc);
        
        v.spies().ignoreThreat();
        return v.otherView().accept(DialogueManager.RESPOND_IGNORE_THREAT);
    }
    @Override
    public DiplomaticReply receiveThreatStopAttacking(Empire e) {
        EmpireView v = empire.viewForEmpire(e);

        v.embassy().noteRequest();
        v.embassy().withdrawAmbassador();
        
        v.embassy().ignoreThreat();
        return v.otherView().accept(DialogueManager.RESPOND_IGNORE_THREAT);
    }
    @Override
    public DiplomaticReply receiveDeclareWar(Empire e) {
        EmpireView v = empire.viewForEmpire(e);

        v.embassy().noteRequest();
        DiplomaticIncident inc = v.otherView().embassy().declareWar();

        return v.otherView().accept(DialogueManager.DECLARE_HATE_WAR, inc);
    }
    private boolean decidedToBreakAlliance(EmpireView view) {
        if (!wantToBreakAlliance(view))
            return false;

        view.embassy().breakAlliance();
        if (view.empire().isPlayerControlled())
            DiplomaticNotification.create(view, DialogueManager.BREAK_ALLIANCE);
        return true;
    }
    //ail: no good reason to ever break an alliance
    private boolean wantToBreakAlliance(EmpireView v) {
        return false;
    }
    private boolean decidedToBreakPact(EmpireView view) {
        if (!wantToBreakPact(view))
            return false;

        view.embassy().breakPact();
        if (view.empire().isPlayerControlled())
            DiplomaticNotification.create(view, DialogueManager.BREAK_PACT);
        return true;
    }
    private boolean wantToBreakPact(EmpireView v) {
        if (!v.embassy().pact())
            return false;
        return true;
    }
    private boolean decidedToBreakTrade(EmpireView view) {
        if (!wantToBreakTrade(view))
            return false;

        view.embassy().breakTrade();
        if (view.empire().isPlayerControlled())
            DiplomaticNotification.create(view, DialogueManager.BREAK_TRADE);
        return true;
    }
    private boolean wantToBreakTrade(EmpireView v) {
        //ail: no need to break trade. War declaration will do it for us, otherwise it just warns our opponent
        return false;
    }
    //----------------
//
//----------------
    @Override
    public void makeDiplomaticOffers(EmpireView v) {
        if(empire.enemies().contains(v.empire()) && !empire.warEnemies().contains(v.empire()))
        {
            if(!empire.inShipRange(v.empId()))
                v.embassy().endWarPreparations();
        }
        if (v.embassy().contactAge() < 2)
            return;
        if (v.embassy().unity() || v.embassy().finalWar())
            return;

        if (v.embassy().diplomatGone() || v.otherView().embassy().diplomatGone())
            return;

        // check diplomat offers from worst to best
        if (decidedToDeclareWar(v))
            return;
        if (decidedToBreakAlliance(v))
            return;
        if (decidedToBreakPact(v))
            return;
        if (decidedToBreakTrade(v))
            return;
        if (decidedToIssueWarning(v))
            return;

        if (willingToOfferPeace(v)) {
            if (v.embassy().anyWar())
                v.empire().diplomatAI().receiveOfferPeace(empire);
            else
                v.embassy().endWarPreparations();
            return;
        }

        if (v.embassy().finalWar() || v.embassy().unity())
            return;
        
        // if this empire is at war with us or we are preparing
        // for war, then stop now. No more Mr. Nice Guy.
        List<Empire> enemies = empire.enemies();
        if (enemies.contains(v.empire()))
            return;
        
        // build a priority list for Joint War offers:
        for (Empire target: empire.enemies()) {
            if(target.powerLevel(target) > empire.powerLevel(empire))
            {
                if (willingToOfferJointWar(v.empire(), target)) {
                    v.empire().diplomatAI().receiveOfferJointWar(v.owner(), target); 
                    return;
                }
            }
        }
        
        if (willingToOfferTrade(v, v.trade().maxLevel())) {
            v.empire().diplomatAI().receiveOfferTrade(v.owner(), v.trade().maxLevel());
            return;
        }
        
        if (willingToOfferAlliance(v.empire())) {
            v.empire().diplomatAI().receiveOfferAlliance(v.owner());
            return;
        }

        if (decidedToExchangeTech(v))
            return;
        
        if (willingToOfferPact(v)) {
            v.empire().diplomatAI().receiveOfferPact(empire);
            return;
        }
        decidedToIssuePraise(v);
    }
    private boolean decidedToIssuePraise(EmpireView view) {
        if (!view.inEconomicRange())
            return false;

        log(view+": checkIssuePraise");
        DiplomaticIncident maxIncident = null;
        for (DiplomaticIncident ev: view.embassy().newIncidents()) {
            if (ev.triggersPraise() && ev.moreSevere(maxIncident))
                maxIncident = ev;
        }

        if (maxIncident == null)
            return false;

        log("cum.sev: ", str(cumulativeSeverity), "   maxInc:", maxIncident.praiseMessageId(), "  maxSev:", str(maxIncident.currentSeverity()));

        // don't issue praise unless new incidents are high enough
        if (maxIncident.currentSeverity() < view.embassy().minimumPraiseLevel())
            return false;

        maxIncident.notifyOfPraise();
        view.embassy().praiseSent();
        if (view.empire().isPlayerControlled())
            DiplomaticNotification.create(view, maxIncident, maxIncident.praiseMessageId());

        return true;
    }
    private int warningThreshold(EmpireView view) {
        DiplomaticEmbassy emb = view.embassy();
        int warnLevel = emb.minimumWarnLevel();
        if (emb.alliance())
            return warnLevel / 4;
        else if (emb.pact())
            return warnLevel /2;
        else
            return warnLevel;
    }
    private boolean decidedToIssueWarning(EmpireView view) {
        if (!view.inEconomicRange())
            return false;
        // no warnings if at war
        DiplomaticEmbassy emb = view.embassy();
        if (emb.anyWar() || emb.unity())
            return false;
        float threshold = 0 - warningThreshold(view);
        log(view+": checkIssueWarning. Threshold: "+ threshold);
        DiplomaticIncident maxIncident = null;
        cumulativeSeverity = 0;
        for (DiplomaticIncident ev: emb.newIncidents()) {
            log(view.toString(), "new incident:", ev.toString());
            float sev = ev.currentSeverity();
            cumulativeSeverity += sev;
            if (ev.triggersWarning() && ev.moreSevere(maxIncident))
                maxIncident = ev;
        }
        
        if (maxIncident == null)
            return false;
        
        if (maxIncident.currentSeverity() > threshold)
            return false;

        log("cumulative severity: "+cumulativeSeverity);
        view.embassy().logWarning(maxIncident);
        
        // if we are warning player, send a notification
        if (view.empire().isPlayerControlled()) {
            // we will only give one expansion warning
            if (maxIncident instanceof ExpansionIncident) {
                if (view.embassy().gaveExpansionWarning())
                    return true;
                view.embassy().giveExpansionWarning();
            }
            //ail: don't nag about spy-confession-incidents
            if(!(maxIncident instanceof SpyConfessionIncident
                    || maxIncident instanceof EspionageTechIncident))
                DiplomaticNotification.create(view, maxIncident, maxIncident.warningMessageId());
        }
        return true;
    }
    private boolean decidedToDeclareWar(EmpireView view) {
        if (empire.isPlayerControlled())
            return false;
        if (view.embassy().unity() || view.embassy().anyWar())
            return false;
        if (!view.inEconomicRange())
            return false;
        if(empire.enemies().contains(view.empire()))
            return false;
        
        // look at new incidents. If any trigger war, pick
        // the one with the greatest severity
        DiplomaticIncident warIncident = null;
        float worstNewSeverity = 0;
        
        // check for a war incident if we are not at peace, or the start
        // date of our peace treaty precedes the current time
        if (!view.embassy().atPeace()
        || (view.embassy().treatyDate() < galaxy().currentTime())) {
            for (DiplomaticIncident ev: view.embassy().newIncidents()) {
                if (!ev.declareWarId().isEmpty()) {
                    if (ev.triggersWar()) {
                        float sev = ev.currentSeverity();
                        if (ev.triggersWarning() && (sev < worstNewSeverity))
                            warIncident = ev;
                    }
                    else if (view.embassy().timerIsActive(ev.timerKey()))
                        warIncident = ev;
                }
            }
            if (warIncident != null) {
                if(!warIncident.isSpying())
                {
                    //System.out.println(empire.galaxy().currentTurn()+" "+ empire.name()+" starts Incident-War ("+warIncident.toString()+") vs. "+view.empire().name());
                    beginIncidentWar(view, warIncident);
                    return true;
                }
                else
                {
                    if(wantToDeclareWar(view, true))
                    {
                        //System.out.println(empire.galaxy().currentTurn()+" "+ empire.name()+" starts Incident-War ("+warIncident.toString()+") vs. "+view.empire().name());
                        beginIncidentWar(view, warIncident);
                            return true;
                    }   
                }
            }
        }
        
        if (wantToDeclareWarOfOpportunity(view)) {
            //ail: even if the real reason is because of geopolitics, we can still blame it on an incident, if there ever was one, so the player thinks it is their own fault
            //System.out.println(empire.galaxy().currentTurn()+" "+empire.name()+" starts Opportunity-War vs. "+view.empire().name());
            beginOpportunityWar(view);
            return true;          
        }
        
        if (wantToDeclareWarOfPrevention(view)) {
            //System.out.println(empire.galaxy().currentTurn()+" "+empire.name()+" starts Prevention-War vs. "+view.empire().name());            
            beginErraticWar(view);
            return true;
        }
        return false;
    }
    @Override
    public boolean wantToDeclareWarOfHate(EmpireView v) {
        if (v.embassy().atPeace())
            return false;
        
        // from -70 to -90
        float warThreshold = v.empire().diplomatAI().leaderHateWarThreshold();
        
        // modnar: change war threshold by number of our wars vs. number of their wars
        // try not to get into too many wars, and pile on if target is in many wars
        float enemyMod = (float) (10 * (v.empire().numEnemies() - empire.numEnemies()));
        warThreshold += enemyMod;
        
        // allied with an enemy? not good
        if (v.embassy().alliedWithEnemy())
            warThreshold += 30;
        
        // higher contempt = more likely to increase war
        // positive contempt raises the threshold = more likely for war
        // if relative power is 3, then contempt mod is 30 or -30
        float contemptMod = 10 * v.scaleOfContempt();
        warThreshold += contemptMod;
        
        return (v.embassy().relations() <= warThreshold);
    }
    public boolean wantToDeclareWarOfPrevention(EmpireView v) {
        if (v.embassy().atPeace())
        {
            return false;
        }
        Galaxy gal = galaxy();
        if(empire.alliedWith(v.empId()))
            return false;
        float totalPotentialBombard = 0.0f;
        for (int id=0;id<empire.sv.count();id++) 
        {
            StarSystem current = gal.system(id);
            if(current.colony() == null)
                continue;
            if(current.colony().empire() != empire)
                continue;
            for(ShipFleet orbiting : current.orbitingFleets())
            {
                if(orbiting.empId != v.empId())
                    continue;
                totalPotentialBombard += orbiting.expectedBombardDamage() / (200 * current.colony().maxSize());
            }
            for(ShipFleet incoming : current.incomingFleets())
            {
                if(incoming.empId != v.empId())
                    continue;
                if(!empire.visibleShips().contains(incoming))
                    continue;
                totalPotentialBombard += incoming.expectedBombardDamage(current) / (200 * current.colony().maxSize());
            }
        }
        return totalPotentialBombard >= 1.0f;
    }
    @Override
    public boolean wantToDeclareWarOfOpportunity(EmpireView v) {
        return wantToDeclareWar(v, false);
    }
    public boolean wantToDeclareWar(EmpireView v, boolean overrideBestVictim) {
        //System.out.println(empire.name()+" atpeace: "+v.embassy().atPeace()+" no enemies:  "+empire.enemies().isEmpty());
        if (v.embassy().atPeace())
        {
            return false;
        }
        Empire bestVictim = empire.generalAI().bestVictim();
        if(overrideBestVictim)
            bestVictim = v.empire();
        if(v.empire() == bestVictim)
        {
            boolean warAllowed = false;
            if (empire.enemies().isEmpty()) 
                warAllowed = true;
            if(empire.enemies().size() == 1 && empire.enemies().contains(bestVictim))
                warAllowed = true;
            float developmentPct = 0;
            float countedColonies = 0;
            for(StarSystem sys : empire.allColonizedSystems())
            {
                if(sys.colony() != null)
                {
                    if(sys.planet().isResourcePoor() || sys.planet().isResourceUltraPoor())
                        continue;
                    developmentPct += sys.colony().currentProductionCapacity();
                    countedColonies++;
                }
            }
            if(countedColonies > 0)
                developmentPct /= countedColonies;
            float helpingPower = 0.0f;
            float enemyAllyPower = 0.0f;
            float superiorityThreshold = 1.5f - (developmentPct - 0.75f) * 6;
            //we can still grow otherwise
            if(empire.generalAI().additionalColonizersToBuild(true) > 0
                    || developmentPct < 0.75f)
                warAllowed = false;
            if(empire.tech().avgTechLevel() < v.empire().tech().avgTechLevel() 
                    && empire.tech().avgTechLevel() < 99)
                warAllowed = false;
            if(empire.tech().topSpeed() < 2)
                warAllowed = false;
            if(empire.tech().topShipWeaponTech().damageHigh() < 5)
                warAllowed = false;
            for(Empire emp : bestVictim.warEnemies())
            {
                helpingPower += emp.powerLevel(emp);
            }
            for(Empire emp : bestVictim.allies())
            {
                enemyAllyPower += emp.powerLevel(emp);
            }
            //System.out.println(empire.name()+" col: "+empire.generalAI().additionalColonizersToBuild(true)+" devPct: "+developmentPct+" allied: "+empire.alliedWith(bestVictim.id)+" tech: "+(empire.tech().avgTechLevel() < v.empire().tech().avgTechLevel()));
            //System.out.println(empire.name()+" bestVictim: "+bestVictim+" power: "+empire.powerLevel(empire) + helpingPower+" vs. "+(bestVictim.powerLevel(bestVictim) + enemyAllyPower) + " Threshold: "+superiorityThreshold +" "+empire.leader().personality()+" "+empire.leader().objective()+" War Allowed: "+warAllowed);
            if(warAllowed &&
                    empire.powerLevel(empire) + helpingPower > (bestVictim.powerLevel(bestVictim) + enemyAllyPower) * superiorityThreshold)
            {
                return true;
            }
        }
        return false;
    }
    private DiplomaticIncident worstWarnableIncident(Collection<DiplomaticIncident> incidents) {
        DiplomaticIncident worstIncident = null;
        float worstNewSeverity = 0;
        for (DiplomaticIncident ev: incidents) {
            float sev = ev.currentSeverity();
            if (ev.triggersWarning() && (sev < worstNewSeverity))
                worstIncident = ev;
        }
        return worstIncident;
    }
    private void beginIncidentWar(EmpireView view, DiplomaticIncident inc) {
        log(view.toString(), " - Declaring war based on incident: ", inc.toString(), " id:", inc.declareWarId());
        view.embassy().beginWarPreparations(inc.declareWarId(), inc);
        //System.out.print("\n"+empire.name()+" starts war on "+view.empire().name()+ " because of "+inc.toString());
        if (inc.triggersImmediateWar())
            view.embassy().declareWar();
    }
    private void beginOpportunityWar(EmpireView view) {
        log(view+" - Declaring war based on opportunity");
        //System.out.print("\n"+empire.name()+" starts opportunity-war on "+view.empire().name());
        view.embassy().beginWarPreparations(DialogueManager.DECLARE_OPPORTUNITY_WAR, null);
    }
    //ail: I need a war that isn't checked for still being valid for our prevention-war
    private void beginErraticWar(EmpireView view) {
        log(view+" - Declaring war based on erratic");
        view.embassy().beginWarPreparations(DialogueManager.DECLARE_ERRATIC_WAR, null);
    }
    @Override
    public Empire councilVoteFor(Empire civ1, Empire civ2) {
        EmpireView cv1 = empire.viewForEmpire(civ1);
        EmpireView cv2 = empire.viewForEmpire(civ2);

        // always vote for yourself
        if (civ1 == empire)   return castVoteFor(civ1);
        if (civ2 == empire)   return castVoteFor(civ2);
        
        float pct;

        // if allied with one, vote for that ally
        if (cv1.embassy().alliance() && !cv2.embassy().alliance())
            return castVoteFor(civ1);
        if (cv2.embassy().alliance() && !cv1.embassy().alliance())
            return castVoteFor(civ2);

        // if at war with one, vote for other (if contacted)
        if (cv1.embassy().anyWar() && !cv2.embassy().anyWar()) {
            if (empire.inEconomicRange(cv2.empId()))
                return castVoteFor(civ2);
            else
                return castVoteFor(null);
        }
        if (cv2.embassy().anyWar() && !cv1.embassy().anyWar()) {
            if (empire.inEconomicRange(cv1.empId()))
                return castVoteFor(civ1);
            else
                return castVoteFor(null);
        }
        //ail: I want it to be deterministic and based on relations, not power
        if(cv1.embassy().relations()/100.0f > cv2.embassy().relations()/100.0f
                && empire.inEconomicRange(cv1.empId()))
            return castVoteFor(civ1);
        if(cv2.embassy().relations()/100.0f > cv1.embassy().relations()/100.0f
                && empire.inEconomicRange(cv2.empId()))
            return castVoteFor(civ2);
        // return undecided
        return castVoteFor(null);
    }
    @Override
    public void acceptCouncilRuling(GalacticCouncil c) {
        // player will be prompted by UI
        if (empire.isPlayerControlled())
            return;
        
        // if elected, always accept. Only players are sadomasochists about this
        if (c.leader() == empire)
            c.acceptRuling(empire);
        else if (giveLoyaltyTo(c.leader()))
            c.acceptRuling(empire);
        else
            c.defyRuling(empire);
    }
    private boolean giveLoyaltyTo(Empire c) {
        // modnar: add empire power bonus relative to own power for accepting winner
        // only what own empire can see (through spies)
        // more likely to accept more powerful empire, less likely to accept less powerful empire
        // powerBonus vary from -0.25 to 0.25
        float powerBonus = 0.5f * (c.powerLevel(c) / (c.powerLevel(c) + empire.powerLevel(empire)) - 0.5f);
        
        if (empire.lastCouncilVoteEmpId() == c.id)
            return true;

        if (c.orionCouncilBonus() > 0)
            return true;
        
        EmpireView cv1 = empire.viewForEmpire(c);
        if (cv1.embassy().alliance())
            return true;
        if (empire.leader().isPacifist())
            return true;
        if (cv1.embassy().pact() && empire.leader().isHonorable())
            return true;
        
        if (cv1.embassy().anyWar()) {
            if (empire.leader().isXenophobic())
                return random() < powerBonus; // modnar: add powerBonus
            else if (empire.leader().isAggressive())
                return random() < 0.50f + powerBonus; // modnar: add powerBonus
            else
                return random() < 0.75f + powerBonus; // modnar: add powerBonus
        }
        
        if (empire.leader().isXenophobic())
            return random() < 0.50f + powerBonus; // modnar: add powerBonus
        else if (empire.leader().isErratic())
            return random() < 0.75f + powerBonus; // modnar: add powerBonus

        return random() < 0.90f + powerBonus; // modnar: add powerBonus
    }
    // ----------------------------------------------------------
// PRIVATE METHODS
// ----------------------------------------------------------
    private float previousVoteBonus(Empire c) {
        return c.id == empire.lastCouncilVoteEmpId() ? 0.6f : 0;
    }
    private Empire conditionallyCastVoteFor(EmpireView ev) {
        if (ev.embassy().noTreaty() && galaxy().council().nextVoteWouldElect(ev.empire()))
            return castVoteFor(null);
        else
            return castVoteFor(ev.empire());
    }
    private Empire castVoteFor(Empire c) {
        if (c == null)
            empire.lastCouncilVoteEmpId(Empire.ABSTAIN_ID);
        else
            empire.lastCouncilVoteEmpId(c.id);
        return c;
    }
    //-----------------------------------
    // INCIDENTS
    //-----------------------------------
    @Override
    public void noticeIncident(DiplomaticIncident inc, Empire emp) {
        EmpireView view = empire.viewForEmpire(emp);
        // incidents don't matter once final war is declared
        if (view.embassy().finalWar())
            return;
        
        view.embassy().addIncident(inc);

        if (inc.triggersWar() && !view.embassy().anyWar())
            beginIncidentWar(view, inc);
    }
    @Override
    public DiplomaticIncident noticeSkirmishIncident(ShipCombatResults res) {
        DiplomaticIncident inc = null;
        for (Empire emp: res.empires()) {
            if  (!empire.alliedWith(emp.id)) {
                float winModifier = victoryModifier(res);
                float skirmishSeverity = skirmishSeverity(res);
                float severity = min(-1.0f, winModifier*skirmishSeverity);
                EmpireView view = empire.viewForEmpire(emp.id);
                inc = new SkirmishIncident(view, res, severity);
                view.embassy().addIncident(inc);
            }
        }
        return inc;
    }
    @Override
    public void noticeExpansionIncidents(EmpireView view, List<DiplomaticIncident> events) {
        int numberSystems = view.empire().numSystemsForCiv(view.empire());
        if (numberSystems < 6)
            return;

        Galaxy gal = Galaxy.current();
        int allSystems = gal.numColonizedSystems();
        int numCivs = gal.numActiveEmpires();

        // modnar: scale expansion penalty with ~1/[(numCivs)^(0.75)] rather than 1/numCivs
        // this allows empires to be somewhat bigger than average before the diplomatic size penalty kicks in
        // not linear with numCivs to account for expected fluctuation of empire sizes with larger number of empires
        // at the max number of empires (50), you can be ~2 times as large as average before being penalized
        // use a denominator coefficient factor of ~1.44225 (3^(1/3)) to maps the expression
        // back to the equal 1/3 "share" of planets when only three empires are remaining
        // (and when only two are remaining, they won't like you even if you have slightly less planets than they do)
        //
        // numCivs(X)   1/X     1/[(1.44225*X)^(0.75)]
        //      2       50.00%  45.18%
        //      3       33.33%  33.33%
        //      4       25.00%  26.86%
        //      5       20.00%  22.72%
        //      6       16.67%  19.82%
        //      8       12.50%  15.97%
        //      10      10.00%  13.51%
        //      15      6.67%   9.97%
        //      20      5.00%   8.03%
        //      30      3.33%   5.93%
        //      50      2.00%   4.04%
        //
        //int maxSystemsWithoutPenalty = max(5, (allSystems /numCivs)+1);
        int maxSystemsWithoutPenalty = max(5, (int) Math.ceil(allSystems / Math.pow(1.44225*numCivs, 0.75)));

        if (numberSystems > maxSystemsWithoutPenalty)
            events.add(ExpansionIncident.create(view,numberSystems, maxSystemsWithoutPenalty));
    }
    @Override
    public void noticeTrespassingIncidents(EmpireView view, List<DiplomaticIncident> events) {
        if (view.empire().alliedWith(empire.id))
            return;
        for (StarSystem sys: empire.allColonizedSystems()) {
            List<ShipFleet> fleets = sys.orbitingFleets();
            for (ShipFleet fl: fleets) {
                if (!fl.retreating() && (fl.empire() == view.empire()))
                    events.add(new TrespassingIncident(view,sys,fl));
            }
        }
    }
    @Override
    public void noticeNoRelationIncident(EmpireView view, List<DiplomaticIncident> events) {

    }
    @Override
    public void noticeAtWarWithAllyIncidents(EmpireView view, List<DiplomaticIncident> events) {
        if (!view.embassy().finalWar()) {
            for (Empire ally: empire.allies()) {
                if (ally.atWarWith(view.empId())) 
                    events.add(new AtWarWithAllyIncident(view, ally));
            }
        }
    }
    @Override
    public void noticeAlliedWithEnemyIncidents(EmpireView view, List<DiplomaticIncident> events) {
        if (!view.embassy().finalWar()) {
            for (Empire ally: view.empire().allies()) {
                if (empire.atWarWith(ally.id)) 
                    events.add(new AlliedWithEnemyIncident(view, ally));
            }
        }
    }
    @Override
    public void noticeBuildupIncidents(EmpireView view, List<DiplomaticIncident> events) {
        float shipRange = view.owner().shipRange();

        float multiplier = -0.05f;
        if (view.owner().atWarWith(view.empId()))
            multiplier *= 2;
        else if (view.owner().pactWith(view.empId()))
            multiplier /= 8;
        else if (view.owner().alliedWith(view.empId()))
            multiplier /= 64;

        if (view.owner().leader().isXenophobic())
            multiplier *= 2;

        for (StarSystem sys: view.owner().allColonizedSystems()) {
            float systemSeverity = 0;
            for (ShipFleet fl: view.owner().fleetsForEmpire(view.empire())) {
                if (fl.isActive() && (sys.distanceTo(fl) <= shipRange)) {
                    float fleetThreat = fl.visibleFirepower(view.owner().id, sys.colony().defense().missileShieldLevel());
                    systemSeverity += (multiplier*fleetThreat);
                }
            }
            if (systemSeverity > 0)
                events.add(new MilitaryBuildupIncident(view,sys, systemSeverity));
        }
    }
    //
    // PRIVATE
    //
    private float victoryModifier(ShipCombatResults res) {
        // how much do we magnify lost ships when we lose
        // how much do we minimize lost ships when we lose

        //  do we hate everyone else?
        float multiplier = 1.0f;
        if (empire.leader().isXenophobic())
            multiplier *= 2;

        // did we win? if aggressive stacks still active, then no
        boolean won = true;
        for (CombatStack st: res.activeStacks()) {
            if (st.empire.aggressiveWith(empire.id))
                won = false;
        }
        // if we won, then losses don't seem as bad
        if (won)
                    multiplier /= 2;

        // was this attack at our colonies?
        if (res.defender() == empire)
            multiplier *= 2;

        return multiplier;
    }
    private float skirmishSeverity(ShipCombatResults res) {
        float lostBC = 0;
        // how many ships & bases were lost, relative to empire production
        for (ShipDesign d: res.shipsDestroyed().keySet()) {
            if (d.empire() == empire) {
                int num = res.shipsDestroyed().get(d);
                lostBC += (num * d.cost());
            }
        }
        if (res.defender() == empire) {
            lostBC += (res.basesDestroyed() * empire.tech().newMissileBaseCost());
            lostBC += (res.factoriesDestroyed() * empire.tech().maxFactoryCost());
        }
        float totalIndustry = empire.totalPlanetaryProduction();

        // -1 severity for each 1% of total production lost
        return -1.0f*lostBC*100/totalIndustry;
    }
   private boolean warWeary(EmpireView v) {
        if (v.embassy().finalWar())
            return false;
        //ail: when we have incoming transports, we don't want them to perish
        for(Transport trans:empire.transports())
        {
            if(trans.destination().empire() == v.empire())
                return false;
        }
        if(!empire.inShipRange(v.empId()))
            return true;
        //ail: only colonies and factories relevant for war-weariness. Population and Military are merely tools to achieve our goals
        Empire emp = v.owner();
        TreatyWar treaty = (TreatyWar) v.embassy().treaty();
        if (treaty.colonyChange(emp) < 0.85f)
            return true;
        if (treaty.factoryChange(emp) < 0.85f)
            return true;
        return false;
    }
    /*
      Interfaces to allow overriding of default leader behavior
    */
    @Override
    public float leaderExploitWeakerEmpiresRatio(){ 
        return empire.leader().exploitWeakerEmpiresRatio();
    } 
    @Override
    public float leaderRetreatRatio(Empire c){ 
        return empire.leader().retreatRatio(c);
    } 
    @Override
    public float leaderContemptDeclareWarMod(Empire e){ 
        return empire.leader().contemptDeclareWarMod(e);
    } 
    @Override
    public float leaderContemptAcceptPeaceMod(Empire e){ 
        return empire.leader().contemptAcceptPeaceMod(e);
    } 
    @Override
    public int leaderGenocideDurationMod() {
        switch(empire.leader().personality) {
            case PACIFIST:   return 150;
            case HONORABLE:  return 50;
            case XENOPHOBIC: return 100;
            case RUTHLESS:   return 10;
            case AGGRESSIVE: return 0;
            case ERRATIC:    return 25;
            default:         return 25;
        }
    } 
    @Override
    public float leaderBioweaponMod()         { 
        return empire.leader().bioweaponMod();
    }
    @Override
    public int leaderOathBreakerDuration() { 
        int objMod = 1;
        switch(empire.leader().objective) {
            case DIPLOMAT:  objMod = 2;
        }
        switch(empire.leader().personality) {
            case PACIFIST:   return objMod*50;
            case HONORABLE:  return objMod*100;
            case XENOPHOBIC: return objMod*50;
            case RUTHLESS:   return 0;
            case AGGRESSIVE: return objMod*50;
            case ERRATIC:    return objMod*25;
            default:         return objMod*1;
        }
    } 
    @Override
    public float leaderDiplomacyAnnoyanceMod(EmpireView v) { 
        return empire.leader().diplomacyAnnoyanceMod(v);
    }     
    @Override
    public float leaderDeclareWarMod() { 
        return empire.leader().declareWarMod();
    } 
    @Override
    public float leaderAcceptPeaceTreatyMod() { 
        return empire.leader().acceptPeaceTreatyMod();
    } 
    @Override
    public float leaderAcceptPactMod(Empire other) { 
        return empire.leader().acceptPactMod(other);
    } 
    @Override
    public float leaderAcceptAllianceMod(Empire other) { 
        return empire.leader().acceptAllianceMod(other);
    } 
    @Override
    public float leaderAcceptTradeMod() { 
        return empire.leader().acceptTradeMod();
    } 
    @Override
    public float leaderHateWarThreshold() { 
        return empire.leader().hateWarThreshold();
    } 
    @Override
    public float leaderAcceptJointWarMod() { 
        return empire.leader().acceptJointWarMod();
    } 
    @Override
    public float leaderPreserveTreatyMod() { 
        return empire.leader().preserveTreatyMod();
    } 
    @Override
    public float leaderAffinityMod(Leader.Personality p1, Leader.Personality p2) {
        return empire.leader().affinityMod(p1,p2); 
    }
    @Override
    public  boolean leaderHatesAllSpies() { return false; }
}

