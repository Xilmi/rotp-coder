Bugfix:

Fixed an issue that could lead to military-ships being considered colony-ships for calculating military-power of the human player.

Governor & AI:

AI no longer holds back on spying after it has been threatened. This behavior also affects the option "Use AI to handle espionage" in the governor.

AI:

Bio-Weapons will be considered less valueable the bigger the discrepancy in ground-combat-strength is.
Since quite some time ago the speed-difference of ships plays a role in their retreat-estimations ships will no longer automatically retreat when warp-dissipated below 3 maneuverability. This caused too many false-positives on retreating which was too exploitable.
Changed the order of operations in missile-tech-value, which made Hyper-V and Hyper-X-Missiles more valueable than they should be. Now neutron-pellet-gun will be preferred over Hyper-X-Missiles when available.
Making an exception to not building ships before adequate tech is available when there's incoming invasions.
For the calculations of whether the AI thinks it can hold position over an enemy system until invasion-forces arrive, it will no longer think it can kill missile-bases with bio-weapons.
Council-election-behavior changed. AI now calculates a score primarily based on trade-value. It will never vote for someone they don't have a profitable trade-treaty with. AI will no longer vote for other empires when it has more than 1/3rd of their population.
There are now 5 levels of aggression which depend on how well the race is suited for an aggressive playstyle.
Highest aggression: Ursinathi. They will always attack very soon as not doing so would quickly diminish their advantage.
High aggression: Altairi and Fiershan. They will always attack after getting some core-technologies.
Moderate aggression: Kholdan, Nazlok, Sslauran and Cryslonoid. They will attack when they see themselves at an advantage.
Low aggression: Mentaran and Meklonar. They will attack when they are at a significant advantage. (50% stronger than their desired victim)
Lowest aggression: Human. They will only attack when they are at a huge advantage. (100% stronger than their desired victim)
Note: This does not impact the AI's behavior of backstabbing someone who already is in trouble.