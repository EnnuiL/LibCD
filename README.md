<img src="src/main/resources/assets/libcd_ennui_edition/icon.png" align="right" width="128px"/>

# LibConditionalData: Ennui Edition

# Port Information

This is an unofficial port of LibCD to Minecraft 1.19.3, originally maintained from 1.14 to 1.16 by the CottonMC team. This fork is meant to be exclusively used by Crooked Crooks, but you may use it on your mods.

The reason for maintaining this is because Fabric API's Resource Conditions API turned out to be inadequate for me, and while a QSL module is planned, this updated fork is a temporary solution until that is created. The intention was to merge this fork back to Cotton's repository, and while the 1.18.2 update has been proven to be stable enough, the 1.19.2 one had sacrificed the Tag Extensions functionality due to it needing a massive overhaul. It has been somewhat succeeded by Vanilla's optional tags, but they are still a feature that should have been kept, and therefore, I don't believe that this is good enough for upstreaming. If you wish to use this work as a base for a complete update, go ahead! But well? This is currently just a bit of duct tape to keep Crooked Crooks working on its best form.

# Original Description

[>> Downloads <<](https://github.com/CottonMC/LibCD/releases)

*Improved data processing*

**This mod is open source and under a permissive license.** As such, it can be included in any modpack on any platform
without prior permission. We appreciate hearing about people using our mods, but you do not need to ask to use them. See
 the [LICENSE file](LICENSE) for more details.

LibCapableData, or LibCD, is a suite of hooks to make data packs easier for both mod and pack developers. Conditions
allow for conditional loading of data pack elements, ~~and Tweakers allow programmatic adding and modifying of recipes,
 loot tables, and more.~~ SEE COMMENT BELOW

## Conditions
Conditions are a framework for data-driven conditional loading of data pack elements. They're added with a `.mcmeta`
system like resource pack metadata is. Tags are also given a `libcd` block for conditional entries outside of
all-or-nothing loading.





# What happened to LibCD?

Long story short, drama happened. Tweakers caused a lot of drama, and when someone who had been causing me grief for a
while said that libcd was obsolete due to their new mod, I agreed. LibCD is now primary geared towards developers, and
pared down massively in scale. It become actively damaging to my health to continue maintaining it in the state it was
in. There will be a successor to tweakers, but it will be pared back as well. I have plans for what I'd like to see out
of its new iteration.
