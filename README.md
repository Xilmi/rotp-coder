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

* Download my distribution of ROTP-1.5.jar (large file) and run that instead of 
original game.

or

* Download only the ROTP-1.5-governor.jar
* Place it in same directory that contains original Remnants.jar version Beta 1.4
* Run ROTP-1.5-governor.jar

To enable governor, use 'q' key on keyboard, or else click "Allocate Spending"
text in the planetary spending screen. Since version 1.5 Governor is on by default.

---

Additional features.

* This mod will transport population from planets that are full to planets that
are underpopulated. Population from planets with maximum population will be 
transported. Only population that will grow back in 1 turn will be transported 
(usually 1-2 pop). When choosing destination, target population and distance will
be taken into account. If you want to turn this off, add "-Dautotransport=false" 
to Java command line like this:

java -Dautotransport=false -Xmx2560m -jar ROTP-1.5.jar arg1
or
java -Dautotransport=false -Xmx2560m -jar ROTP-1.5-governor.jar

* This mod will build stargates on all planets when technology is available. If you
want to turn this off, add "-Dautogate=false" to Java command line.

java -Dautogate=false -Xmx2560m -jar ROTP-1.5.jar arg1
or
java -Dautogate=false -Xmx2560m -jar ROTP-1.5-governor.jar

* Since version 1.5 governor will be on by default on new colonies. If you want to 
turn this off, add "-Ddefaultgovernor=false" to Java command line.

java -Ddefaultgovernor=false -Xmx2560m -jar ROTP-1.5.jar arg1
or
java -Ddefaultgovernor=false -Xmx2560m -jar ROTP-1.5-governor.jar

* You can add multiple "-Dxxx=yyy" options on command line. I will implement GUI at
some point to toggle these options...

---

# Building from source

It's a maven build. Git clone the sources, then do "mvn package" and you have entire
project built and packaged in "target" directory.
