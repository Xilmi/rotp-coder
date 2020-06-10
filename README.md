# Remnants of the Precursors

Remnants of the Precursors is a Java-based modernization of the original Master of Orion game from 1993.

# Links
Official website: https://www.remnantsoftheprecursors.com/<br/>
Community subreddit: https://www.reddit.com/r/rotp/<br/>
Download build: https://rayfowler.itch.io/remnants-of-the-precursors

# Planetary Governor

This is a fork with Planetary Governor mod 

https://remnantsoftheprecursors.com/

https://rayfowler.itch.io/remnants-of-the-precursors

https://github.com/rayfowler/rotp-public

This governor manages planet spending to:

* Set ecology to minimum "clean"
* Set max production until all factories are built.
* Set max ecology until max population is reached.
* Set max defence until required number of bases is built.
* Build a stargate if technology is available.
* If all above have been built, research.

It can be toggled on or off for each planet. You can basically enable it on any
planet not building ships and leave it untouched for most of the game. With new 
tech discoveries it will readjust the sliders automatically. This cuts down the
amount of micromanagement needed drastically.

To run the mod:

* Download my distribution of ROTP-1.13.4.jar (large file) and run that instead of 
original game. You are also welcome to try ROTP-mini-1.13.4.jar which is smaller
as it uses better compression for game assets. 

or

* Download only the ROTP-1.13.4-governor.jar
* Place it in same directory that contains original Remnants.jar version Beta 1.4
* Run ROTP-1.13.4-governor.jar

To enable governor, use 'q' key on keyboard, or else click "Allocate Spending"
text in the planetary spending screen. Since version 1.8 Governor is on by default.

---

Additional features / changelog

* 1.13.4. Bugfix release- fix the "continue" to work correctly. Start work on savegame
to JSON conversion (not yet ready).

* 1.13.3. Added shipbuilding option. If it's turned on, governor remembers that planet was building
ships (keeps 1 tick in shipbuilding), and will resume building ships once it finishes all
the IND/ECO/DEF. If planet was not spending on shipbuilding, it keeps it on research. Also fixed
the patch (-governor) version. And fixed a potential bug with game loading.

* Added default missile base number configuration. Added automated reserve spending.
It will spend only on planets that have <30% average production. It will only spend
the amount planet can use this turn. You can set a reserve it autospend will keep
unspent. Autospend will only spend on planets that have IND or ECO unfinished. It will
spend on planets with lowest production first.

* Save game compatibility fixed in 1.10.1. (it was broken in 1.10). /u/sarlok contributed 
improvements to Governor eco/industry assignments in and more.

* Population transport is now modernized to comply with the rule changes in 1.9.
Governor Options dialog is now better and has more options for population transport.
I'm now using merge to keep the project in sync with upstream not to break the changes
made by other authors forking from me.

* Governed colonies are now shown in green in colonies list. If autotransport is on,
maximum population reached message should not be shown for governed planets and 
governor won't spend production on population growth if only 1-3 population remains 
until limit. Also, my email is shown in case of error.

* ROTP-1.11-mini.jar is now provided. It uses WebP images and Ooo Vorbis sounds.
It should have all the same features as ROTP but take up less space (~193 MB). Since
WebP library uses native parts, this will only work on Windows (32 and 64 bit),
Mac OSX 64 bit, Linux 64 bit. If you have a different system, use full-size ROTP.
On Windows, please install Microsoft Visual C++ 2015 Redistributable 
(https://www.microsoft.com/en-us/download/details.aspx?id=52685).
Please report any bugs with this (especially sounds) as it needs wider testing.

* Some problems with ROTP-mini have been fixed. I also found out that to run on 
Windows, ROTP-mini needs "vcruntime140d.dll" & "ucrtbased.dll" to run. Please get
them and place them in the same directory. This issue has been raised with WebP
library authors already: https://github.com/sejda-pdf/webp-imageio/issues/1

* Since version 1.5 governor will be on by default on new colonies.

* Governor will transport population from planets that are full to planets that
are underpopulated. Population from planets with maximum population will be 
transported. Only population that will grow back in 1 turn will be transported 
(usually 1-2 pop). When choosing destination, target population and distance will
be taken into account. 

* Governor will build stargates on Rich and Ultra Rich planets when technology is 
available.

GUI has now been added to control behaviour of the governor. Please click "Options"
next to "Allocate Spending" to go to governor options. Old system properties based
options are supported for now but will be dropped in future releases.

Governor options should be saved in your save game file.

---

# Building from source

It's a maven build. Git clone the sources, then do "mvn package" and you have entire
project built and packaged in "target" directory.

Minimized build is provided on a separate branch. Minimized assets are not committed 
to git, use shell scripts provided to do the conversion yourself.
