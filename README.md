Includes all changes of official 1.03

Additional changes:

Bugfixes:

When loading a game that included opponents with their AI set to "Random", those shall now get the same AI assigned as they had before instead of getting a new random one.

Governor:

Fixed an issue that prevented the governor from realizing that Meklonar get maximum robotics-control-level automatically without refitting.

AI:

Renamed "Expert" into "Advanced".
Renamed "Legacy" into "Cruel" and made "Cruel" the new default.
Removed "Intermediate" and merged it with "Rookie" instead as they were so similar in the first place.
The new "Rookie" uses primarily modules from the Modnar-AI with a fix to the "fake wars"-issue but also 3 base-modules, where that seemed more advanced or equal.
"Random+" is no longer available as auto-play-option.

General:
Now all missile-stacks are added up instead of looked at individually when it comes to the decision of whether to retreat from them or try to dodge them.
Fixed an issue where opponents that were further away were considered more scary than closer ones, which could lead to stuff like unnecessary preparations for war or positioning fleets on the wrong border in peacetime.
AI is now a lot smarter about what tech-trades to take:
It will not trade important techs against regular techs unless those are at least 5 level higher.
It will not trade important techs against unimportant techs at all.
It will not trade regular techs against unimportant techs unless those are at least 5 levels higher.

Cruel:
Now internally likes everyone a lot independed from their behavior for questionable reasons.
Being more sensible what opponents could be taken on.
Now will be okay with having several wars at once if none of the opponents poses a threat.
When not planning to go to war will take more risks teching up compared to maintainig a defensive fleet.
Take trade-pact-value into account for victim-choice. Better trade means less likely to declare war than worse or no trade.
Take espionage-incidents into account for victim-choice. Having lots of them means you are more likely to get declared war on.

Advanced:
No longer hiding spies in allied empires.
Fixed that they only allowed you to ask them to stop spying when you were caught instead of when they caught you.

Authors notes:

The general theme of this update is to establish "Cruel", formerly known as "Legacy", as the default to play against. One of the main-complaints was that eventually everyone ended up having all techs and there was barely any disparity in the tech-levels and available techs. By making them more picky what to trade for what and taking enemy spies more seriously, this should be a bit better now. I think this mode is simply more exciting to play as the AI cannot really be manipulated to do anything that is against their interest and just helps the player.