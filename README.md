Bugfixes:

Fixed an issue that allowed the player to accidentally send a fleet from a system to that same system.

Governor:

Fixed an issue that caused the governor to sometimes forget that it was supposed to continue building ships after doing something else.
Only systems with enabled governor will be considered for automatically sending transports to other colonies.
Now has the option "Develop colonies as quickly as possible" which uses the old growing-behavior that some people missed.

AI:

Transport-management no longer considers all systems within the reach of an enemy as systems willing to send all their pop to bolster new colonies. This also applies to governor when using the "Let AI handle transports"-option.
Now aware that reserve spent on artifact-planets is more effective.
No longer will keep an emergency fund in reserve for the rare events. Instead will only cut reserve-spending for other things when an event actually happens.
When deciding which planets to build ships or do research on, production obtained from reserve is no longer considered as otherwise Artifact planets might build ships instead of doing reserach after receiving financial aid.