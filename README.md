UI:

You can now max out a colony-slider while ensuring the ecology is kept at clean without locking it by clicking on the text to the right of the slider.

Governor:

Now uses more sophisticated algorithm to decide whether to build population or let it grow naturally. When it lets it grow naturally, it will still not build more factories than could be operated with the natural growh and instead spend the leftover into something else.
Now has a new option "Let AI handle population transportation", which uses the AI's approach to population-transport-management.
Now has a new option "Allow shields without bases", which allows the governor to build shields on planets where no bases where ordered.

Bugfixes:

Workaround to a glitch where the assumed cost of a missile base is marginally higher than the actual cost which caused the governor to put 1 tick to be spent into defense when nothing was to do. Note: I had previously implemented another workaround for the same issue when you don't use the governor. The new workaround works for both governor and AIs.

AI:

Ships that are both slower and have less range than opponent ships, will now consider the opponent ships as invincible and retreat instead of letting themselves be kited to death.