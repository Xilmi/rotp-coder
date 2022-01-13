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

public class DiplomatIncident extends DiplomaticIncident {
    private static final long serialVersionUID = 1L;
    final int empYou;
    final int empMe;
    public static DiplomatIncident create(EmpireView ev) {
        return new DiplomatIncident(ev);
    }
    @Override
    public boolean triggeredByAction()   { return false; }
    private DiplomatIncident(EmpireView ev) {
        empYou = ev.empire().id;
        empMe = ev.ownerId();
        dateOccurred = galaxy().currentYear();
        duration = 1;
        severity = 0;
        
        if (ev.embassy().unity() || !ev.empire().inEconomicRange(ev.ownerId())) {
            return;
        }
        if(ev.owner().generalAI().absolution() < 1 || !ev.owner().leader().isDiplomat())
        {
            severity = 0;
            return;
        }
       
        float avgDiploScore = 0;
        float currentDiploScore = 0;
        float empiresChecked = 0;
        float maxPopularity = -Float.MAX_VALUE;
        float minPopularity = Float.MAX_VALUE;
        
        for(Empire emp : ev.owner().contactedEmpires())
        {
            if(!ev.owner().inEconomicRange(emp.id))
                continue;
            float score = 0;
            for(Empire contacts : emp.contactedEmpires())
            {
                if(contacts.alliedWith(emp.id))
                    score += 3;
                else if(contacts.pactWith(emp.id))
                    score += 2;
                else if(contacts.atWarWith(emp.id))
                    score -= 2;
                else
                    score += 1;
            }
            if(emp == ev.empire())
                currentDiploScore = score;
            if(score > maxPopularity)
                maxPopularity = score;
            if(score < minPopularity)
                minPopularity = score;
            avgDiploScore += score;
            empiresChecked++;
        }  
        
        avgDiploScore /= empiresChecked;
        if(currentDiploScore < avgDiploScore)
            severity = -50 * (currentDiploScore - avgDiploScore) / (minPopularity - avgDiploScore);
        if(currentDiploScore > avgDiploScore)
            severity = 50 * (currentDiploScore - avgDiploScore) / (maxPopularity - avgDiploScore);
        //System.out.println(galaxy().currentTurn()+" "+ev.owner().name()+" evaluates "+ev.empire().name()+" score: "+currentDiploScore+" min: "+minPopularity+" max: "+maxPopularity+" avg: "+avgDiploScore+" severity: "+severity);
    }
    @Override
    public String title()            { return text("INC_DIPLOMAT_TITLE"); }
    @Override
    public String description()      { 
        if (severity == -50)
            return decode(text("INC_DIPLOMAT_DESC1"));
        else if(severity < 0)
            return decode(text("INC_DIPLOMAT_DESC2"));
        else if(severity < 50)
            return decode(text("INC_DIPLOMAT_DESC3"));
        else
            return decode(text("INC_DIPLOMAT_DESC4"));
    }
    @Override
    public String key() {
        return concat("Diplomat", str(dateOccurred));
    }
    @Override
    public String decode(String s) {
        String s1 = super.decode(s);
        s1 = galaxy().empire(empMe).replaceTokens(s1, "my");
        s1 = galaxy().empire(empYou).replaceTokens(s1, "your");
        return s1;
    }
}
