-------------------------------------------
HBM Enhanced - Minecraft Forge Mod
-------------------------------------------

A mod that enhances the HBM's NTM mod by creating a research system. The main way of researching is by using Research blocks. These have different tiers and are connected via a main controller.

-------------------------------------------
Source installation information for modders
-------------------------------------------
This code follows the Minecraft Forge installation methodology. It will apply
some small patches to the vanilla MCP source code, giving you and it access 
to some of the data and functions you need to build a successful mod.

Note also that the patches are built against "unrenamed" MCP source code (aka
srgnames) - this means that you will not be able to read them directly against
normal code.

Source pack installation information:

Standalone source installation
==============================

To install this source code for development purposes, extract this zip file.
It ships with a demonstration mod. Run 'gradlew setupDevWorkspace' to create
a gradle environment primed with FML. Run 'gradlew eclipse' or 'gradlew idea' to
create an IDE workspace of your choice.
Refer to ForgeGradle for more information about the gradle environment
Note: On macs or linux you run the './gradlew.sh' instead of 'gradlew'

Forge source installation
=========================
MinecraftForge ships with this code and installs it as part of the forge
installation process, no further action is required on your part.

For reference this is version @MAJOR@.@MINOR@.@REV@.@BUILD@ of FML
for Minecraft version @MCVERSION@.

LexManos' Install Video
=======================
https://www.youtube.com/watch?v=8VEdtQLuLO0&feature=youtu.be

For more details update more often refer to the Forge Forums:
http://www.minecraftforge.net/forum/index.php/topic,14048.0.html


-------------------------------------------
CREDITS AND LICENSES
-------------------------------------------

This mod includes and depends on the following libraries and projects:

Minecraft Forge
  - License: LGPL v2.1
  - URL: https://minecraftforge.net/
  - Used for mod development framework

GSON (Google)
  - License: Apache License 2.0
  - Version: 2.3.1
  - URL: https://github.com/google/gson
  - Used for JSON parsing and serialization

WorldEdit
  - License: GPL v3
  - CurseForge: https://www.curseforge.com/minecraft/mods/worldedit
  - Used for world editing utilities

Lunatrius Core
  - License: MIT
  - CurseForge: https://www.curseforge.com/minecraft/mods/lunatriuscore
  - Dependency for Schematica

Schematica
  - License: MIT
  - CurseForge: https://www.curseforge.com/minecraft/mods/schematica
  - Used for schematic building features

OpenComputers (Li Cil)
  - License: MIT
  - Version: MC1.7.10-1.7.5.1356
  - URL: https://github.com/MightyPirates/OpenComputers
  - Used for computer integration

CodeChickenLib (CodeChicken)
  - License: LGPL v2.1
  - Version: 1.7.10-1.1.3.141
  - URL: https://github.com/Chicken-Bones/CodeChickenLib
  - Utility library for CodeChicken mods

CodeChickenCore (CodeChicken)
  - License: LGPL v2.1
  - Version: 1.7.10-1.0.7.48
  - URL: https://github.com/Chicken-Bones/CodeChickenCore
  - Core utilities and ASM transformations

NotEnoughItems (CodeChicken)
  - License: MIT
  - Version: 1.7.10-1.0.5.120
  - URL: https://github.com/Chicken-Bones/NotEnoughItems
  - Used for crafting recipe integration

HBM's Nuclear Tech GIT (JameH2)
  - License: Check source repository
  - URL: https://github.com/JameH2/Hbm-s-Nuclear-Tech-GIT
  - The base mod being enhanced by this project

-------------------------------------------
Building
-------------------------------------------

Ensure Java 8+ is installed. Run:
  ./gradlew setupDevWorkspace
  ./gradlew eclipse  (or './gradlew idea' for IntelliJ)

For build troubleshooting, refer to the ForgeGradle documentation.
