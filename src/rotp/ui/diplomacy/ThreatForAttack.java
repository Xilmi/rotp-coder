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
package rotp.ui.diplomacy;

import rotp.model.empires.Empire;

public class ThreatForAttack extends TurnNotificationMessage {
    public ThreatForAttack(String  s) {
        messageType = s;
    }
    @Override
    public void diplomat(Empire e)  {
        super.diplomat(e);
    }
    @Override
    public int numReplies()       		{ return 2; }
    @Override
    public boolean enabled(int i)       { return true; }
    @Override
    public String reply(int i)          { 
        switch (i) {
            case 0 : return text("DIPLOMACY_IGNORE_THREAT");
            case 1 : return text("DIPLOMACY_RETREAT_SHIPS", diplomat().raceName());
        }
        return ""; 
    }
    @Override
    public void select(int i) {
        log("ThreatForAttack - selected: ", str(i));
        switch(i) {
        case 1: 
            player().retreatShipsFrom(diplomat().id);
            escape();
            break;
        case 0: 
        default:
            escape(); break;
        }
    }
    @Override
    public void escape() {
        session().resumeNextTurnProcessing();
    }
    @Override
    public String decode(String encodedMessage) { 
        String s1 = super.decode(encodedMessage); 
        return s1;
    }
}