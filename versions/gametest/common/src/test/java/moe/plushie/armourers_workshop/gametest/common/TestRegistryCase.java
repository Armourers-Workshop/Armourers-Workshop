package moe.plushie.armourers_workshop.gametest.common;

import moe.plushie.armourers_workshop.gametest.utils.Asynchronous;
import org.junit.jupiter.api.Test;

import static moe.plushie.armourers_workshop.gametest.utils.AssertLog.assertPrintLog;

@Asynchronous
public class TestRegistryCase {

    @Test
    public void testSkinTypeRegisters() {
        assertPrintLog("Registering Skin 'armourers:unknown'");
        assertPrintLog("Registering Skin 'armourers:head'");
        assertPrintLog("Registering Skin 'armourers:chest'");
        assertPrintLog("Registering Skin 'armourers:legs'");
        assertPrintLog("Registering Skin 'armourers:feet'");
        assertPrintLog("Registering Skin 'armourers:wings'");
        assertPrintLog("Registering Skin 'armourers:outfit'");
        assertPrintLog("Registering Skin 'armourers:sword'");
        assertPrintLog("Registering Skin 'armourers:shield'");
        assertPrintLog("Registering Skin 'armourers:bow'");
        assertPrintLog("Registering Skin 'armourers:trident'");
        assertPrintLog("Registering Skin 'armourers:pickaxe'");
        assertPrintLog("Registering Skin 'armourers:axe'");
        assertPrintLog("Registering Skin 'armourers:shovel'");
        assertPrintLog("Registering Skin 'armourers:hoe'");
        assertPrintLog("Registering Skin 'armourers:item'");
        assertPrintLog("Registering Skin 'armourers:block'");
        assertPrintLog("Registering Skin 'armourers:horse'");
        assertPrintLog("Registering Skin 'armourers:boat'");
        assertPrintLog("Registering Skin 'armourers:minecart'");
        assertPrintLog("Registering Skin 'armourers:fishing'");
        assertPrintLog("Registering Skin 'armourers:backpack'");
        assertPrintLog("Registering Skin 'armourers:part'");
    }

    @Test
    public void testSkinPartTypeRegisters() {
        assertPrintLog("Registering Skin Part 'armourers:unknown'");
        assertPrintLog("Registering Skin Part 'armourers:hat.base'");
        assertPrintLog("Registering Skin Part 'armourers:head.base'");
        assertPrintLog("Registering Skin Part 'armourers:chest.base'");
        assertPrintLog("Registering Skin Part 'armourers:chest.leftArm'");
        assertPrintLog("Registering Skin Part 'armourers:chest.rightArm'");
        assertPrintLog("Registering Skin Part 'armourers:legs.skirt'");
        assertPrintLog("Registering Skin Part 'armourers:legs.leftLeg'");
        assertPrintLog("Registering Skin Part 'armourers:legs.rightLeg'");
        assertPrintLog("Registering Skin Part 'armourers:feet.leftFoot'");
        assertPrintLog("Registering Skin Part 'armourers:feet.rightFoot'");
        assertPrintLog("Registering Skin Part 'armourers:wings.leftWing'");
        assertPrintLog("Registering Skin Part 'armourers:wings.rightWing'");
        assertPrintLog("Registering Skin Part 'armourers:wings.leftWing2'");
        assertPrintLog("Registering Skin Part 'armourers:wings.rightWing2'");
        assertPrintLog("Registering Skin Part 'armourers:chest.base2'");
        assertPrintLog("Registering Skin Part 'armourers:chest.leftArm2'");
        assertPrintLog("Registering Skin Part 'armourers:chest.rightArm2'");
        assertPrintLog("Registering Skin Part 'armourers:legs.leftLeg2'");
        assertPrintLog("Registering Skin Part 'armourers:legs.rightLeg2'");
        assertPrintLog("Registering Skin Part 'armourers:pickaxe.base'");
        assertPrintLog("Registering Skin Part 'armourers:axe.base'");
        assertPrintLog("Registering Skin Part 'armourers:shovel.base'");
        assertPrintLog("Registering Skin Part 'armourers:hoe.base'");
        assertPrintLog("Registering Skin Part 'armourers:bow.frame0'");
        assertPrintLog("Registering Skin Part 'armourers:bow.frame1'");
        assertPrintLog("Registering Skin Part 'armourers:bow.frame2'");
        assertPrintLog("Registering Skin Part 'armourers:bow.frame3'");
        assertPrintLog("Registering Skin Part 'armourers:bow.arrow'");
        assertPrintLog("Registering Skin Part 'armourers:sword.base'");
        assertPrintLog("Registering Skin Part 'armourers:shield.base'");
        assertPrintLog("Registering Skin Part 'armourers:trident.base'");
        assertPrintLog("Registering Skin Part 'armourers:fishing.rod'");
        assertPrintLog("Registering Skin Part 'armourers:fishing.hook'");
        assertPrintLog("Registering Skin Part 'armourers:backpack.base'");
        assertPrintLog("Registering Skin Part 'armourers:shield.blocking'");
        assertPrintLog("Registering Skin Part 'armourers:trident.throwing'");
        assertPrintLog("Registering Skin Part 'armourers:fishing.rod1'");
        assertPrintLog("Registering Skin Part 'armourers:item.base'");
        assertPrintLog("Registering Skin Part 'armourers:block.base'");
        assertPrintLog("Registering Skin Part 'armourers:block.multiblock'");
        assertPrintLog("Registering Skin Part 'armourers:part.advanced_part'");
        assertPrintLog("Registering Skin Part 'armourers:part.locator'");
        assertPrintLog("Registering Skin Part 'armourers:part.static'");
        assertPrintLog("Registering Skin Part 'armourers:part.float'");
        assertPrintLog("Registering Skin Part 'armourers:boat.base'");
        assertPrintLog("Registering Skin Part 'armourers:boat.leftPaddle'");
        assertPrintLog("Registering Skin Part 'armourers:boat.rightPaddle'");
        assertPrintLog("Registering Skin Part 'armourers:minecart.base'");
        assertPrintLog("Registering Skin Part 'armourers:horse.head'");
        assertPrintLog("Registering Skin Part 'armourers:horse.neck'");
        assertPrintLog("Registering Skin Part 'armourers:horse.chest'");
        assertPrintLog("Registering Skin Part 'armourers:horse.left_front_leg'");
        assertPrintLog("Registering Skin Part 'armourers:horse.right_front_leg'");
        assertPrintLog("Registering Skin Part 'armourers:horse.left_hind_leg'");
        assertPrintLog("Registering Skin Part 'armourers:horse.right_hind_leg'");
        assertPrintLog("Registering Skin Part 'armourers:horse.left_front_leg2'");
        assertPrintLog("Registering Skin Part 'armourers:horse.right_front_leg2'");
        assertPrintLog("Registering Skin Part 'armourers:horse.left_hind_leg2'");
        assertPrintLog("Registering Skin Part 'armourers:horse.right_hind_leg2'");
        assertPrintLog("Registering Skin Part 'armourers:horse.tail'");
    }

    @Test
    public void testSkinPaintTypeRegisters() {
        assertPrintLog("Registering Skin Paint 'armourers:normal'");
        assertPrintLog("Registering Skin Paint 'armourers:dye_1'");
        assertPrintLog("Registering Skin Paint 'armourers:dye_2'");
        assertPrintLog("Registering Skin Paint 'armourers:dye_3'");
        assertPrintLog("Registering Skin Paint 'armourers:dye_4'");
        assertPrintLog("Registering Skin Paint 'armourers:dye_5'");
        assertPrintLog("Registering Skin Paint 'armourers:dye_6'");
        assertPrintLog("Registering Skin Paint 'armourers:dye_7'");
        assertPrintLog("Registering Skin Paint 'armourers:dye_8'");
        assertPrintLog("Registering Skin Paint 'armourers:rainbow'");
        assertPrintLog("Registering Skin Paint 'armourers:pulse_1'");
        assertPrintLog("Registering Skin Paint 'armourers:pulse_2'");
        assertPrintLog("Registering Skin Paint 'armourers:texture'");
        assertPrintLog("Registering Skin Paint 'armourers:flicker_1'");
        assertPrintLog("Registering Skin Paint 'armourers:flicker_2'");
        assertPrintLog("Registering Skin Paint 'armourers:flash_1'");
        assertPrintLog("Registering Skin Paint 'armourers:flash_2'");
        assertPrintLog("Registering Skin Paint 'armourers:skin'");
        assertPrintLog("Registering Skin Paint 'armourers:hair'");
        assertPrintLog("Registering Skin Paint 'armourers:eye'");
        assertPrintLog("Registering Skin Paint 'armourers:misc_1'");
        assertPrintLog("Registering Skin Paint 'armourers:misc_2'");
        assertPrintLog("Registering Skin Paint 'armourers:misc_3'");
        assertPrintLog("Registering Skin Paint 'armourers:misc_4'");
        assertPrintLog("Registering Skin Paint 'armourers:none'");
    }

    @Test
    public void testSkinGeometryTypeRegisters() {

        assertPrintLog("Registering Skin Geometry 'armourers:solid'");
        assertPrintLog("Registering Skin Geometry 'armourers:glowing'");
        assertPrintLog("Registering Skin Geometry 'armourers:glass'");
        assertPrintLog("Registering Skin Geometry 'armourers:glass_glowing'");
        assertPrintLog("Registering Skin Geometry 'armourers:cube'");
        assertPrintLog("Registering Skin Geometry 'armourers:cube_cull'");
        assertPrintLog("Registering Skin Geometry 'armourers:mesh'");
        assertPrintLog("Registering Skin Geometry 'armourers:mesh_cull'");

    }


    @Test
    public void testHolidayRegisters() {
        assertPrintLog("Registering Holiday 'armourers_workshop:halloween'");
        assertPrintLog("Registering Holiday 'armourers_workshop:halloween-season'");
        assertPrintLog("Registering Holiday 'armourers_workshop:christmas'");
        assertPrintLog("Registering Holiday 'armourers_workshop:christmas-season'");
        assertPrintLog("Registering Holiday 'armourers_workshop:valentines'");
        assertPrintLog("Registering Holiday 'armourers_workshop:new-years'");
        assertPrintLog("Registering Holiday 'armourers_workshop:ponytail-day'");
        assertPrintLog("Registering Holiday 'armourers_workshop:april-fools'");
    }

    @Test
    public void testPermissionRegisters() {
        assertPrintLog("Registering Permission 'armourers_workshop:skin-cube.undo'");
        assertPrintLog("Registering Permission 'armourers_workshop:skin-cube.redo'");
        assertPrintLog("Registering Permission 'armourers_workshop:skinnable.sit'");
        assertPrintLog("Registering Permission 'armourers_workshop:skinnable.sleep'");
        assertPrintLog("Registering Permission 'armourers_workshop:armourer.save'");
        assertPrintLog("Registering Permission 'armourers_workshop:armourer.load'");
        assertPrintLog("Registering Permission 'armourers_workshop:armourer.setting'");
        assertPrintLog("Registering Permission 'armourers_workshop:armourer.clear'");
        assertPrintLog("Registering Permission 'armourers_workshop:armourer.copy'");
        assertPrintLog("Registering Permission 'armourers_workshop:armourer.replace'");
        assertPrintLog("Registering Permission 'armourers_workshop:outfit-maker.make'");
        assertPrintLog("Registering Permission 'armourers_workshop:skin-library.reload'");
        assertPrintLog("Registering Permission 'armourers_workshop:skin-library.mkdir'");
        assertPrintLog("Registering Permission 'armourers_workshop:skin-library.rename'");
        assertPrintLog("Registering Permission 'armourers_workshop:skin-library.delete'");
        assertPrintLog("Registering Permission 'armourers_workshop:skin-library.skin.upload'");
        assertPrintLog("Registering Permission 'armourers_workshop:skin-library.skin.download'");
        assertPrintLog("Registering Permission 'armourers_workshop:skin-library.skin.load'");
        assertPrintLog("Registering Permission 'armourers_workshop:skin-library.skin.save'");
        assertPrintLog("Registering Permission 'armourers_workshop:advanced-skin-builder.skin.export'");
        assertPrintLog("Registering Permission 'armourers_workshop:advanced-skin-builder.skin.import'");
        assertPrintLog("Registering Permission 'armourers_workshop:skin-library-global.skin.upload'");
        assertPrintLog("Registering Permission 'armourers_workshop:wardrobe.open-gui'");
        assertPrintLog("Registering Permission 'armourers_workshop:wardrobe-op.open-gui'");
        assertPrintLog("Registering Permission 'armourers_workshop:skinnable.open-gui'");
        assertPrintLog("Registering Permission 'armourers_workshop:dye-table.open-gui'");
        assertPrintLog("Registering Permission 'armourers_workshop:skinning-table.open-gui'");
        assertPrintLog("Registering Permission 'armourers_workshop:skin-library-creative.open-gui'");
        assertPrintLog("Registering Permission 'armourers_workshop:skin-library.open-gui'");
        assertPrintLog("Registering Permission 'armourers_workshop:skin-library-global.open-gui'");
        assertPrintLog("Registering Permission 'armourers_workshop:hologram-projector.open-gui'");
        assertPrintLog("Registering Permission 'armourers_workshop:colour-mixer.open-gui'");
        assertPrintLog("Registering Permission 'armourers_workshop:armourer.open-gui'");
        assertPrintLog("Registering Permission 'armourers_workshop:outfit-maker.open-gui'");
        assertPrintLog("Registering Permission 'armourers_workshop:advanced-skin-builder.open-gui'");
    }

    @Test
    public void testEntityProfileRegisters() {
        assertPrintLog("Registering Entity Profile 'armourers_workshop:builtin/boat'");
        assertPrintLog("Registering Entity Profile 'armourers_workshop:builtin/conductor'");
        assertPrintLog("Registering Entity Profile 'armourers_workshop:builtin/fallback'");
        assertPrintLog("Registering Entity Profile 'armourers_workshop:builtin/head'");
        assertPrintLog("Registering Entity Profile 'armourers_workshop:builtin/horse'");
        assertPrintLog("Registering Entity Profile 'armourers_workshop:builtin/mannequin'");
        assertPrintLog("Registering Entity Profile 'armourers_workshop:builtin/minecart'");
        assertPrintLog("Registering Entity Profile 'armourers_workshop:builtin/minecolonies'");
        assertPrintLog("Registering Entity Profile 'armourers_workshop:builtin/mob'");
        assertPrintLog("Registering Entity Profile 'armourers_workshop:builtin/npc'");
        assertPrintLog("Registering Entity Profile 'armourers_workshop:builtin/player'");
        assertPrintLog("Registering Entity Profile 'armourers_workshop:builtin/projecting'");
        assertPrintLog("Registering Entity Profile 'armourers_workshop:builtin/vampirism'");
        assertPrintLog("Registering Entity Profile 'armourers_workshop:builtin/villager'");
    }

    @Test
    public void testItemRegisters() {
        assertPrintLog("Registering Item 'armourers_workshop:skin'");
        assertPrintLog("Registering Item 'armourers_workshop:mannequin'");
        assertPrintLog("Registering Item 'armourers_workshop:skin-library'");
        assertPrintLog("Registering Item 'armourers_workshop:skin-library-creative'");
        assertPrintLog("Registering Item 'armourers_workshop:skin-library-global'");
        assertPrintLog("Registering Item 'armourers_workshop:skinning-table'");
        assertPrintLog("Registering Item 'armourers_workshop:dye-table'");
        assertPrintLog("Registering Item 'armourers_workshop:outfit-maker'");
        assertPrintLog("Registering Item 'armourers_workshop:hologram-projector'");
        assertPrintLog("Registering Item 'armourers_workshop:dye-bottle'");
        assertPrintLog("Registering Item 'armourers_workshop:mannequin-tool'");
        assertPrintLog("Registering Item 'armourers_workshop:armourers-hammer'");
        assertPrintLog("Registering Item 'armourers_workshop:wand-of-style'");
        assertPrintLog("Registering Item 'armourers_workshop:skin-unlock-head'");
        assertPrintLog("Registering Item 'armourers_workshop:skin-unlock-chest'");
        assertPrintLog("Registering Item 'armourers_workshop:skin-unlock-feet'");
        assertPrintLog("Registering Item 'armourers_workshop:skin-unlock-legs'");
        assertPrintLog("Registering Item 'armourers_workshop:skin-unlock-wings'");
        assertPrintLog("Registering Item 'armourers_workshop:skin-unlock-outfit'");
        assertPrintLog("Registering Item 'armourers_workshop:linking-tool'");
        assertPrintLog("Registering Item 'armourers_workshop:skin-template'");
        assertPrintLog("Registering Item 'armourers_workshop:soap'");
        assertPrintLog("Registering Item 'armourers_workshop:gift-sack'");
        assertPrintLog("Registering Item 'armourers_workshop:armourer'");
        assertPrintLog("Registering Item 'armourers_workshop:colour-mixer'");
        assertPrintLog("Registering Item 'armourers_workshop:advanced-skin-builder'");
        assertPrintLog("Registering Item 'armourers_workshop:skin-cube'");
        assertPrintLog("Registering Item 'armourers_workshop:skin-cube-glowing'");
        assertPrintLog("Registering Item 'armourers_workshop:skin-cube-glass'");
        assertPrintLog("Registering Item 'armourers_workshop:skin-cube-glass-glowing'");
        assertPrintLog("Registering Item 'armourers_workshop:paintbrush'");
        assertPrintLog("Registering Item 'armourers_workshop:paint-roller'");
        assertPrintLog("Registering Item 'armourers_workshop:burn-tool'");
        assertPrintLog("Registering Item 'armourers_workshop:dodge-tool'");
        assertPrintLog("Registering Item 'armourers_workshop:shade-noise-tool'");
        assertPrintLog("Registering Item 'armourers_workshop:colour-noise-tool'");
        assertPrintLog("Registering Item 'armourers_workshop:blending-tool'");
        assertPrintLog("Registering Item 'armourers_workshop:hue-tool'");
        assertPrintLog("Registering Item 'armourers_workshop:colour-picker'");
        assertPrintLog("Registering Item 'armourers_workshop:block-marker'");
    }

    @Test
    public void testItemTagRegisters() {
        assertPrintLog("Registering Item Tag 'armourers_workshop:skinnable/swords'");
        assertPrintLog("Registering Item Tag 'armourers_workshop:skinnable/shields'");
        assertPrintLog("Registering Item Tag 'armourers_workshop:skinnable/bows'");
        assertPrintLog("Registering Item Tag 'armourers_workshop:skinnable/tridents'");
        assertPrintLog("Registering Item Tag 'armourers_workshop:skinnable/pickaxes'");
        assertPrintLog("Registering Item Tag 'armourers_workshop:skinnable/axes'");
        assertPrintLog("Registering Item Tag 'armourers_workshop:skinnable/shovels'");
        assertPrintLog("Registering Item Tag 'armourers_workshop:skinnable/hoes'");
        assertPrintLog("Registering Item Tag 'armourers_workshop:skinnable/boats'");
        assertPrintLog("Registering Item Tag 'armourers_workshop:skinnable/minecarts'");
        assertPrintLog("Registering Item Tag 'armourers_workshop:skinnable/fishing_rods'");
        assertPrintLog("Registering Item Tag 'armourers_workshop:skinnable/backpacks'");
        assertPrintLog("Registering Item Tag 'armourers_workshop:skinnable/horse_armors'");
    }

    @Test
    public void testLootItemFunctionTypeRegisters() {
        assertPrintLog("Registering Loot Item Function Type 'armourers_workshop:skin_randomly'");
    }

    @Test
    public void testCreativeModeTabRegisters() {
        assertPrintLog("Registering Creative Mode Tab 'armourers_workshop:main'");
        assertPrintLog("Registering Creative Mode Tab 'armourers_workshop:painting_tools'");
    }

    @Test
    public void testBlockRegisters() {
        assertPrintLog("Registering Block 'armourers_workshop:hologram-projector'");
        assertPrintLog("Registering Block 'armourers_workshop:skinnable'");
        assertPrintLog("Registering Block 'armourers_workshop:dye-table'");
        assertPrintLog("Registering Block 'armourers_workshop:skinning-table'");
        assertPrintLog("Registering Block 'armourers_workshop:skin-library-creative'");
        assertPrintLog("Registering Block 'armourers_workshop:skin-library'");
        assertPrintLog("Registering Block 'armourers_workshop:skin-library-global'");
        assertPrintLog("Registering Block 'armourers_workshop:outfit-maker'");
        assertPrintLog("Registering Block 'armourers_workshop:colour-mixer'");
        assertPrintLog("Registering Block 'armourers_workshop:armourer'");
        assertPrintLog("Registering Block 'armourers_workshop:advanced-skin-builder'");
        assertPrintLog("Registering Block 'armourers_workshop:skin-cube'");
        assertPrintLog("Registering Block 'armourers_workshop:skin-cube-glass'");
        assertPrintLog("Registering Block 'armourers_workshop:skin-cube-glowing'");
        assertPrintLog("Registering Block 'armourers_workshop:skin-cube-glass-glowing'");
        assertPrintLog("Registering Block 'armourers_workshop:bounding-box'");
    }

    @Test
    public void testBlockEntityTypeRegisters() {
        assertPrintLog("Registering Block Entity Type 'armourers_workshop:hologram-projector'");
        assertPrintLog("Registering Block Entity Type 'armourers_workshop:outfit-maker'");
        assertPrintLog("Registering Block Entity Type 'armourers_workshop:dye-table'");
        assertPrintLog("Registering Block Entity Type 'armourers_workshop:skinning-table'");
        assertPrintLog("Registering Block Entity Type 'armourers_workshop:colour-mixer'");
        assertPrintLog("Registering Block Entity Type 'armourers_workshop:armourer'");
        assertPrintLog("Registering Block Entity Type 'armourers_workshop:advanced-skin-builder'");
        assertPrintLog("Registering Block Entity Type 'armourers_workshop:skin-library'");
        assertPrintLog("Registering Block Entity Type 'armourers_workshop:skin-library-global'");
        assertPrintLog("Registering Block Entity Type 'armourers_workshop:skinnable'");
        assertPrintLog("Registering Block Entity Type 'armourers_workshop:bounding-box'");
        assertPrintLog("Registering Block Entity Type 'armourers_workshop:skin-cube'");
        assertPrintLog("Registering Block Entity Type 'armourers_workshop:skinnable-sr'");
        assertPrintLog("Registering Block Entity Type 'armourers_workshop:bounding-box-sr'");
        assertPrintLog("Registering Block Entity Type 'armourers_workshop:skin-cube-sr'");
    }

    @Test
    public void testBlockEntityCapabilityRegisters() {
        assertPrintLog("Registering Block Entity Capability 'armourers_workshop:item'");
        assertPrintLog("Registering Block Entity Capability 'armourers_workshop:fluid'");
        assertPrintLog("Registering Block Entity Capability 'armourers_workshop:energy'");
    }

    @Test
    public void testEntityTypeRegisters() {
        assertPrintLog("Registering Entity Type 'armourers_workshop:mannequin'");
        assertPrintLog("Registering Entity Type 'armourers_workshop:seat'");
    }

    @Test
    public void testEntityCapabilityRegisters() {
        assertPrintLog("Registering Entity Capability 'armourers_workshop:entity-skin-provider'");
    }

    @Test
    public void testMenuTypeRegisters() {
        assertPrintLog("Registering Menu Type 'armourers_workshop:wardrobe'");
        assertPrintLog("Registering Menu Type 'armourers_workshop:wardrobe-op'");
        assertPrintLog("Registering Menu Type 'armourers_workshop:skinnable'");
        assertPrintLog("Registering Menu Type 'armourers_workshop:dye-table'");
        assertPrintLog("Registering Menu Type 'armourers_workshop:skinning-table'");
        assertPrintLog("Registering Menu Type 'armourers_workshop:skin-library-creative'");
        assertPrintLog("Registering Menu Type 'armourers_workshop:skin-library'");
        assertPrintLog("Registering Menu Type 'armourers_workshop:skin-library-global'");
        assertPrintLog("Registering Menu Type 'armourers_workshop:hologram-projector'");
        assertPrintLog("Registering Menu Type 'armourers_workshop:colour-mixer'");
        assertPrintLog("Registering Menu Type 'armourers_workshop:armourer'");
        assertPrintLog("Registering Menu Type 'armourers_workshop:outfit-maker'");
        assertPrintLog("Registering Menu Type 'armourers_workshop:advanced-skin-builder'");
    }

    @Test
    public void testSoundEventRegisters() {
        assertPrintLog("Registering Sound Event 'armourers_workshop:page-turn'");
        assertPrintLog("Registering Sound Event 'armourers_workshop:paint'");
        assertPrintLog("Registering Sound Event 'armourers_workshop:burn'");
        assertPrintLog("Registering Sound Event 'armourers_workshop:dodge'");
        assertPrintLog("Registering Sound Event 'armourers_workshop:picker'");
        assertPrintLog("Registering Sound Event 'armourers_workshop:noise'");
        assertPrintLog("Registering Sound Event 'armourers_workshop:boi'");
    }

    @Test
    public void testDataComponentTypeRegisters() {
        assertPrintLog("Registering Data Component Type 'armourers_workshop:skin'");
        assertPrintLog("Registering Data Component Type 'armourers_workshop:holiday'");
        assertPrintLog("Registering Data Component Type 'armourers_workshop:linked_pos'");
        assertPrintLog("Registering Data Component Type 'armourers_workshop:gift'");
        assertPrintLog("Registering Data Component Type 'armourers_workshop:color1'");
        assertPrintLog("Registering Data Component Type 'armourers_workshop:color2'");
        assertPrintLog("Registering Data Component Type 'armourers_workshop:color'");
        assertPrintLog("Registering Data Component Type 'armourers_workshop:tool_flags'");
        assertPrintLog("Registering Data Component Type 'armourers_workshop:tool_options'");
    }

    @Test
    public void testDataSerializerRegisters() {
        assertPrintLog("Registering Entity Data Serializer 'armourers_workshop:player_texture'");
        assertPrintLog("Registering Entity Data Serializer 'armourers_workshop:player_texture_model'");
    }

    @Test
    public void testCommandArgumentTypeRegisters() {
        assertPrintLog("Registering Argument Type 'armourers_workshop:items'");
        assertPrintLog("Registering Argument Type 'armourers_workshop:files'");
        assertPrintLog("Registering Argument Type 'armourers_workshop:dye'");
        assertPrintLog("Registering Argument Type 'armourers_workshop:color'");
    }
}
