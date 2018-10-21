# Armourer's Workshop - Help

## Commands
Commands can run with the prefix /armourers.

#### Admin Panel
**Usage:** `/armourers adminPanel`
**Result:** Opens the admin panel.

#### Clear Model Cache
**Usage:** `/armourers clearModelCache <player>`
**Result:** Clears the players model cache.

####  Clear Skins
**Usage:** `/armourers clearSkins <player>`
**Result:** Clears all skins from the players wardrobe.

####  Give Skin
**Usage:** `/armourers giveSkin <player> "<skin name>" [<dyeIndex-R,G,B[-paintType]> | <dyeIndex-0xRRGGBB[-paintType>]]`
**Result:** Give a skin to the player.

**Example:** Gives the library skin "official/Fez".
`/armourers giveSkin @p "official/Fez"`

**Example:** Gives the library skin "official/Fez" with white dye in the first dye slot.
`/armourers giveSkin @p "official/Fez" 1-#FFFFFF` or `/armourers giveSkin @p "official/Fez" 1-255,255,255`

**Example:** Gives the library skin "official/Fez" with white dye in the first dye slot and black dye in the second slot.
`/armourers giveSkin @p "official/Fez" 1-#FFFFFF 2-#000000` or `/armourers giveSkin @p "official/Fez" 1-255,255,255 2-0,0,0`

**Example:** Gives the library skin "official/Fez" with white dye of the type hair paint in the first dye slot.
`/armourers giveSkin @p "official/Fez" 1-#FFFFFF-hair` or `/armourers giveSkin @p "official/Fez" 1-255,255,255-hair`

####  Set Skin
**Usage:** `/armourers setSkin <player> <slot id> "<skin name> [<dyeIndex-R,G,B[-paintType]> | <dyeIndex-0xRRGGBB[-paintType>]]"`
**Result:** Set a skin in the players wardrobe slot.

**Example:** Sets the library skin "official/Fez" in the first wardrobe slot.
`/armourers setSkin @p 1 "official/Fez"`

**Example:** Sets the library skin "official/Fez" in the first wardrobe slot, with white dye in the first dye slot.
`/armourers setSkin @p 1 "official/Fez 1-#FFFFFF"` or `/armourers setSkin @p 1 "official/Fez 1-255,255,255"`

**Example:** Sets the library skin "official/Fez" in the first wardrobe slot, with white dye in the first dye slot and black dye in the second slot.
`/armourers setSkin @p 1 "official/Fez 1-#FFFFFF 2-#000000"` or `/armourers setSkin @p 1 "official/Fez 1-255,255,255 2-0,0,0"`

**Example:** Sets the library skin "official/Fez" in the first wardrobe slot, with white dye of the type hair paint in the first dye slot.
`/armourers setSkin @p 1 "official/Fez" 1-#FFFFFF-hair` or `/armourers setSkin @p 1 "official/Fez" 1-255,255,255-hair`

####  Resync Wardrobe
**Usage:** `/armourers resyncWardrobe <player>`
**Result:** Resyncs the players wardrobe with the server.

####  Set Item Skinnable 
**Usage:** While holding the item to be marked as skinnable `/armourers setItemSkinnable <skintype> <add|remove>`
**Result:** Make an item skinnable/not skinnable.

**Example:** Makes the item in the player main hand able to accept sword skins.
`/armourers setItemSkinnable sword add`

#### Set Unlocked Wardrobe Slots
**Usage:** `/armourers setUnlockedWardrobeSlots <player> <skin type> <amount 1-8>`
**Result:**

#### Set Wardrobe Option
**Usage:** `/armourers setWardrobeOption <player> <option> <value>`
**Result:**

#### Other
**Skin Types**
```
sword
shield
bow
pickaxe
axe
shovel
hoe
```
**Paint Types**
```
normal
dye_1
dye_2
dye_3
dye_4
dye_5
dye_6
dye_7
dye_8
skin
hair
eye
misc
```

## Spawning
#### Skins
```
/give @p armourers_workshop:item.skin 1 0 {armourersWorkshop:{skinType:"armourers:wings", identifier:{libraryFile:"Angel Wings"}}}
/give @p armourers_workshop:item.skin 1 0 {armourersWorkshop:{skinType:"armourers:wings", identifier:{localId:-1559653466}}}
/give @p armourers_workshop:item.skin 1 0 {armourersWorkshop:{skinType:"armourers:wings", identifier:{globalId:293}}}
```
#### Gifts
Colours are ints, try using [https://www.shodor.org/stella2java/rgbint.html](https://www.shodor.org/stella2java/rgbint.html) to get colours.
```
/give @p armourers_workshop:item.gift-sack 1 0 {colour1:255,colour2:16711680,giftItem:{id:"minecraft:dirt",Count:1}}
/give @p armourers_workshop:item.gift-sack 1 0 {colour1:255,colour2:16711680,giftItem:{id:"minecraft:dirt",Count:1,Damage:1}}
```