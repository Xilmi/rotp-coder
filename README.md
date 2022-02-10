Bugfixes:

Fixed accuracy not being considered in the bombardment-preview leading to incorrect estimates in the early stages of the game.

Threatening to stop spying:

Fixed that AI wouldn't care about your threat when it should.
You can no longer decide whether you want the AI to hide or remove their spies. This decision is now made by the AI.
The mechanism of how the AI reacts to stop-espionage-threats now works exactly the same regardless of whether it is asked by the player or another AI.
The duration for espionage-incidents were increased.
The AI now can decide on their own whether and if to reinstall spies that they previously retreated but will definitely do it once the incident has run out.

Governor:

When both refitting and terraforming is possible, the governor will now prefer to terraform first.
Fixed an issue where the governor behaved incorrectly in balancing industry and ecology for colonies of a Meklonar-player because it didn't consider that they don't have to refit.

AI:

General:

Space combat:
A fleet that would lose a fight but could destroy an enemy colony before doing so, will now try to do this instead of immediately retreating.
Unarmed colonies are not automatically dismissed as potential target by ships that also have other unused weapons than bombs.

Expert:

Target selection:
Erratic and Ruthless don't make a difference between ally or not for their next victim of choice, wheras an alliance with a honrable or pacifist empire will likely keep intact.
Relationship now taken into consideration for target-selection in opportunity-wars too.

Alliance behavior:
AI will now break trade-treaties if you are at war with their allies.
AI will now break an alliances with an empire that had the option to vote for them but didn't.
AI won't offer an alliance to anyone who didn't vote for them on the last election despite having the option.
AI will now break an alliance if both they and their ally were up for the election.
AI won't offer an alliance to someone who was up for election in the last council-meeting if they also were up for election.
Allies no longer considers alliances as sacred that must never be broken.
The chance they will break with their ally in order to declare war on them depends on personality.
Allies no longer gift techs to their partners since that is too risky now that alliances have quite a bunch of conditions to be broken.
Allies will now break a nap to declare war on someone else if an ally asked them to.

War-wearyness:
AI will no longer become war-weary unless they are really in serious trouble.

Voting behavior:
AI will no loner vote for their ally if they are up for the election themselves.
AI no longer needs to know both parties to vote for an ally or against an enemy.
Fixed that AI would vote for someone who they are at war with when they were at war with both nominees.

Rookie/Disciple:

Reverted to using their own respective diplomatic models.