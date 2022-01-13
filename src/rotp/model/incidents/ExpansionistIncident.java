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
package rotp.model.incidents;

import rotp.model.empires.Empire;
import rotp.model.empires.EmpireView;

public class ExpansionistIncident extends DiplomaticIncident {
    private static final long serialVersionUID = 1L;
    final int empYou;
    final int empMe;
    public static ExpansionistIncident create(EmpireView ev) {
        return new ExpansionistIncident(ev);
    }
    @Override
    public boolean triggeredByAction()   { return false; }
    private ExpansionistIncident(EmpireView ev) {
        empYou = ev.empire().id;
        empMe = ev.ownerId();
        dateOccurred = galaxy().currentYear();
        duration = 1;
        severity = 0;
        
        if (ev.embassy().unity() || !ev.empire().inEconomicRange(ev.ownerId())) {
            return;
        }
        if(ev.owner().generalAI().absolution() < 1 || !ev.owner().leader().isExpansionist())
        {
            severity = 0;
            return;
        }
       
        float avgScore = 0;
        float currentScore = 0;
        float empiresChecked = 0;
        float max = 0;
        float min = Float.MAX_VALUE;
        
        for(Empire emp : ev.owner().contactedEmpires())
        {
            if(!ev.owner().inEconomicRange(emp.id))
                continue;
            float score = 1f / emp.numColonizedSystems();

            if(emp == ev.empire())
                currentScore = score;
            if(score > max)
                max = score;
            if(score < min)
                min = score;
            avgScore += score;
            empiresChecked++;
        }  
        
        avgScore /= empiresChecked;
        if(currentScore < avgScore)
            severity = -50 * (currentScore - avgScore) / (min - avgScore);
        if(currentScore > avgScore)
            severity = 50 * (currentScore - avgScore) / (max - avgScore);
        System.out.println(galaxy().currentTurn()+" "+ev.owner().name()+" evaluates "+ev.empire().name()+" expansion-score: "+currentScore+" min: "+min+" max: "+max+" avg: "+avgScore+" severity: "+severity);
    }
    @Override
    public String title()            { return text("INC_EXPANSIONIST_TITLE"); }
    @Override
    public String description()      { 
        if (severity == -50)
            return decode(text("INC_EXPANSIONIST_DESC1"));
        else if(severity < 0)
            return decode(text("INC_EXPANSIONIST_DESC2"));
        else if(severity < 50)
            return decode(text("INC_EXPANSIONIST_DESC3"));
        else
            return decode(text("INC_EXPANSIONIST_DESC4"));
    }
    @Override
    public String key() {
        return concat("Expansionist", str(dateOccurred));
    }
    @Override
    public String decode(String s) {
        String s1 = super.decode(s);
        s1 = galaxy().empire(empMe).replaceTokens(s1, "my");
        s1 = galaxy().empire(empYou).replaceTokens(s1, "your");
        return s1;
    }
}
