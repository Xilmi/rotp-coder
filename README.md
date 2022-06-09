Gameplay:

Default-starting-design for Destroyer changed to Computer, Shield and 4 Lasers.
Default-starting-design for Bomber changed to Computer, 3 Nuclear Bombs.

UI:

Brought back the retreat-button to the monster-battle confirmation-dialogue.

AI:

Fixed an issue of AI trading away weapon-techs when it shouldn't.
When there's a friendly fleet retreating from a planet that is outside of scanner-range fleets will now wait for the retreating fleet to arrive before attempting another attack.
Fixed an issue that caused bio-weapons not be down-evaluated as much as intended when there are a lot of missile-bases. It might still not be enough, though.
Removed behavior to rush military on underdeveloped worlds. While it had it's intentions it often caused more harm than good long-term.
No longer build missile-bases when under siege by a fleet that would destroy them in the next turn anyways.
Removed tech-restrictions for going to war.
No longer using dedicated bomber-designs, only dedicated figthers or hybrids. It turned out that having more design-slots to rotate is more important and pure bombers are too hard to coordinate and too easy to defend against.
Now always starting with a certain amount of pure fighters before going into hybrids.
Fixed an issue that could lead to the wrong design being scrapped.
Fixed an issue where under certain circumstances a new design was developed and switched to every turn which could lead to not actually producing any ship for a long time. This same fix also reduced the frequency with which the AI will build duplicates of the same design.
Fixed an issue that could lead to trying to build a design that had already been scrapped.
Early-game-research-decsion-making is now more of an opening-book to get faster into a position of being able to mount a devastating attack.
Fixed an issue where weapon- and propulsion-future-techs were not prioritized as intended.
Decison-making for when to make an army and when to tech has been completely revamped. Basically: Way more commitment to military when already ahead or cornered and adaptive approach to build only as much as needed when not ahead.