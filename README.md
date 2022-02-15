UI:

Add mouse-wheel-scrolling for flags on unexplored alien systems. (Ray)
Genocide notice now uses the same way of determining whether killer and victim where known rather an inconsistent one.

Translations:

Fixed some issues for Italian and French. (Ray)

Bugfixes:

Ray fixed some bug about ship-deployment but I don't really know what exactly. (Ray)

AI:

General:
AI now computes a probability of how likely they think they'll be able to colonize a system when there's also others who could compete for the same system. This is then used in the calculation how many colony-ships should be built.
AI now computes a confidence-value for the stability of their bridge-heads and considers this value in both how it should reinforce the position (all fleets individually or wait for bigger fleet) and whether it should risk an invasion of a system.

Ship-Combat:
Fixed an issue where ships would keep attacking a planet instead of going for a ships.
Fixed an issue where ships would stay out of range of their own weapons when they had repulsors but where slower than their target when the target had enough range to hit them anyways.
When a fleet that thinks it would lose but could destroy a colony before, is blocked by a repulsor-ship or 5 stacks surrounding the colony, they will fall back to their normal retreat-check.
Cloaking is now also ignored for superiority check of own ships.

Expert:
For uneven tech-trades you now get more or less reputation depending on the cost-difference. It'll still be 5 if it's even.
The decision on whether spies should remain hidden after being warned is now based on whether a war is considered acceptable.
Expansionist leaders ignore the bew probability-check for colonizations and will always try to compete for all systems.
The victim-selection for opportunity-wars was simplyfied and should now always prefer the easiest-to-beat opponent.
Opportunity wars are now only started when a leader-personality-specific threshold of superiority has been exceeded.
There's now a new type of war: Desperation-war. It will always target the empire with the most room for population amongst those in reach. Wars of this type are only started when an AI couldn't do anything else anymore. (No wars, nothing to colonize, all colonies fully developed, tech-rank appropriate to their size)
There's now different types of war-wearyness depending on the type of war and who declared it. Empires stubbornly continuing wars that are just to their disadvantage should be a thing of the past.
Allies don't have to wait for a cooldown to ask the human player to join a war against their foes since this decision will have direct consequences on the alliance.
Only Xenophobic leaders will continue to break up with their allies if both are up for election.
Any leader could break an alliance if their ally is the best target for a desperation war. So better keep your allies busy.