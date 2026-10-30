#!/usr/bin/env python3
"""Exact parity checks for the original gems that are active at runtime."""

from __future__ import annotations

import json
import sys
import zipfile
from pathlib import Path


ROOT = Path(__file__).resolve().parents[1]
RUNTIME_ROOT = ROOT / "src/main/resources/data/apotheotic_additions/gems"
ACTIVE = {
    "aether/aether.json": "mythic",
    "aether/purity.json": "rare",
    "aether/zanite_alchemist.json": "rare",
    "bumblezone/bee_queen.json": "uncommon",
    "bumblezone/hive.json": "rare",
    "crafted/cornelia.json": "apotheotic_additions:artifact",
    "crafted/ender_dragon.json": "apotheotic_additions:artifact",
    "crafted/frostmaw.json": "apotheotic_additions:artifact",
    "crafted/harbringer.json": "apotheotic_additions:artifact",
    "crafted/hydra.json": "apotheotic_additions:artifact",
    "crafted/ignis.json": "apotheotic_additions:artifact",
    "crafted/leviathan.json": "apotheotic_additions:artifact",
    "crafted/lich.json": "apotheotic_additions:artifact",
    "crafted/maledictus.json": "apotheotic_additions:artifact",
    "crafted/monstrosity.json": "apotheotic_additions:artifact",
    "crafted/naga.json": "apotheotic_additions:artifact",
    "crafted/remnant.json": "apotheotic_additions:artifact",
    "crafted/slider.json": "apotheotic_additions:artifact",
    "crafted/snow_queen.json": "apotheotic_additions:artifact",
    "crafted/sunbird.json": "apotheotic_additions:artifact",
    "crafted/ur-ghast.json": "apotheotic_additions:artifact",
    "crafted/warden.json": "apotheotic_additions:artifact",
    "crafted/wither.json": "apotheotic_additions:artifact",
    "crafted/wroughtnaut.json": "apotheotic_additions:artifact",
    "modded/armor.json": "common",
    "modded/cataclysm.json": "rare",
    "modded/archmage.json": "rare",
    "modded/ars_mana.json": "common",
    "modded/heartland.json": "epic",
    "modded/felfire.json": "uncommon",
    "modded/engineer.json": "rare",
    "modded/dracula.json": "rare",
    "modded/weeping_well.json": "epic",
    "modded/soultwisted.json": "rare",
    "curios/reinforced.json": "uncommon",
    "curios/twisted.json": "uncommon",
    "curios/vitalic.json": "uncommon",
    "modded/hearty.json": "common",
    "modded/midas.json": "epic",
    "modded/bloodcaster.json": "rare",
    "modded/mystic.json": "rare",
    "undergarden/decay.json": "rare",
    "undergarden/undergarden.json": "rare",
    "twilight/umbral_matrimony.json": "rare",
}
TYPE_MAP = {
    "bow": "apotheosis:bow",
    "crossbow": "apotheosis:bow",
    "pickaxe": "apotheosis:breaker",
    "shovel": "apotheosis:breaker",
    "heavy_weapon": "apotheosis:melee_weapon",
    "sword": "apotheosis:melee_weapon",
    "helmet": "apotheosis:helmet",
    "chestplate": "apotheosis:chestplate",
    "leggings": "apotheosis:leggings",
    "boots": "apotheosis:boots",
    "shield": "apotheosis:shield",
    "trident": "apotheosis:trident",
    "wand": "apotheotic_additions:wand",
    "curios:body": "apotheotic_additions:curios_armor",
    "curios:head": "apotheotic_additions:curios_armor",
    "curios:back": "apotheotic_additions:curios_armor",
    "curios:feet": "apotheotic_additions:curios_armor",
    "curios:hands": "apotheotic_additions:curios_armor",
    "curios:ring": "apotheotic_additions:curios_jewelry",
    "curios:an_focus": "apotheotic_additions:curios_jewelry",
    "curios:necklace": "apotheotic_additions:curios_jewelry",
    "curios:charm": "apotheotic_additions:curios_jewelry",
    "curios:belt": "apotheotic_additions:curios_jewelry",
    "curios:aether_gloves": "apotheotic_additions:aether_accessory",
    "curios:aether_accessory": "apotheotic_additions:aether_accessory",
    "curios:aether_shield": "apotheotic_additions:aether_accessory",
    "curios:aether_cape": "apotheotic_additions:aether_accessory",
    "curios:aether_pendant": "apotheotic_additions:aether_accessory",
    "curios:aether_ring": "apotheotic_additions:aether_accessory",
    "curios:spellbook": "apotheotic_additions:spellbook",
}
OPERATION_MAP = {
    "ADDITION": "add_value",
    "MULTIPLY_BASE": "add_multiplied_base",
    "MULTIPLY_TOTAL": "add_multiplied_total",
}
COMMON_TAG_MAP = {
    "forge:stone": "c:stones",
    "forge:cobblestone": "c:cobblestones",
    "forge:ores/iron": "c:ores/iron",
    "forge:raw_materials/iron": "c:raw_materials/iron",
    "forge:ores/diamond": "c:ores/diamond",
    "forge:gems/diamond": "c:gems/diamond",
    "forge:ores/zanite": "aether:ores/zanite",
    "forge:ores/silver": "c:ores/silver",
    "forge:raw_materials/silver": "c:raw_materials/silver",
}


def normalize_attribute(attribute: str) -> str:
    if attribute == "attributeslib:creative_flight":
        return "neoforge:creative_flight"
    if attribute.startswith("attributeslib:"):
        return "apothic_attributes:" + attribute.split(":", 1)[1]
    if attribute == "forge:entity_reach":
        return "minecraft:player.entity_interaction_range"
    if attribute == "forge:block_reach":
        return "minecraft:player.block_interaction_range"
    if attribute.startswith("eidolon:"):
        return "eidolon_repraised:" + attribute.split(":", 1)[1]
    return attribute


def normalize_enchantment(enchantment: str) -> str:
    if enchantment == "minecraft:sweeping":
        return "minecraft:sweeping_edge"
    if enchantment in {"apotheosis:rebounding", "apotheosis:icy_thorns"}:
        return "apothic_enchanting:" + enchantment.split(":", 1)[1]
    return enchantment if ":" in enchantment else "minecraft:" + enchantment


def normalize_effect(effect: str) -> str:
    if effect.startswith("eidolon:"):
        return "eidolon_repraised:" + effect.split(":", 1)[1]
    return effect


def normalize_types(original_types: list[str]) -> set[str]:
    return {TYPE_MAP[value] for value in original_types}


def main() -> int:
    if len(sys.argv) != 2:
        raise SystemExit("usage: test_runtime_gems.py <ApotheoticAdditionsV2.2.2.jar>")

    runtime_files = {
        path.relative_to(RUNTIME_ROOT).as_posix()
        for path in RUNTIME_ROOT.rglob("*.json")
    }
    assert runtime_files == set(ACTIVE), (runtime_files, set(ACTIVE))

    with zipfile.ZipFile(sys.argv[1]) as original_jar:
        for relative_path, minimum_rank in ACTIVE.items():
            original_name = f"data/apotheotic_additions/gems/{relative_path}"
            original = json.loads(original_jar.read(original_name))
            runtime = json.loads((RUNTIME_ROOT / relative_path).read_text(encoding="utf-8"))

            assert runtime["type"] == "apotheosis:gem"
            assert runtime["weights"]["weight"] == original["weight"]
            assert runtime["weights"]["quality"] == original["quality"]
            assert runtime.get("unique", False) == original.get("unique", False)
            if minimum_rank != "common":
                purity_by_rank = {
                    "uncommon": "chipped",
                    "rare": "flawed",
                    "epic": "normal",
                    "mythic": "flawless",
                    "apotheotic_additions:artifact": "perfect",
                }
                assert original["min_rarity"] == minimum_rank
                assert runtime["min_purity"] == purity_by_rank[minimum_rank]
            else:
                assert "min_rarity" not in original
                assert "min_purity" not in runtime

            if relative_path == "crafted/ender_dragon.json":
                assert len(runtime["bonuses"]) == len(original["bonuses"]) == 1
                old_bonus = original["bonuses"][0]
                new_bonus = runtime["bonuses"][0]
                assert old_bonus["type"] == "apotheosis:enchantment"
                assert new_bonus["type"] == "apotheotic_additions:original_enchantment"
                assert new_bonus["enchantment"] == normalize_enchantment(old_bonus["enchantment"])
                assert new_bonus["global"] == old_bonus["global"]
                assert new_bonus["values"] == old_bonus["values"]
                assert new_bonus["original_socket_types"] == old_bonus["gem_class"]["types"]
                assert new_bonus["gem_class"]["key"] == old_bonus["gem_class"]["key"]
                assert set(new_bonus["gem_class"]["types"]) == normalize_types(
                    old_bonus["gem_class"]["types"]
                )
            elif relative_path in {"crafted/wither.json", "crafted/wroughtnaut.json"}:
                assert len(runtime["bonuses"]) == 1
                new_bonus = runtime["bonuses"][0]
                assert new_bonus["type"] == "apotheotic_additions:original_multi_attribute"
                assert len(new_bonus["profiles"]) == len(original["bonuses"])
                original_types = set()
                for profile, old_bonus in zip(new_bonus["profiles"], original["bonuses"]):
                    assert old_bonus["type"] == "apotheosis:multi_attribute"
                    assert profile["key"] == old_bonus["gem_class"]["key"]
                    assert profile["original_socket_types"] == old_bonus["gem_class"]["types"]
                    assert len(profile["modifiers"]) == len(old_bonus["modifiers"])
                    original_types.update(old_bonus["gem_class"]["types"])
                    for modifier, old_modifier in zip(profile["modifiers"], old_bonus["modifiers"]):
                        assert modifier["attribute"] == normalize_attribute(old_modifier["attribute"])
                        assert modifier["operation"] == OPERATION_MAP[old_modifier["operation"].upper()]
                        assert modifier["values"] == old_modifier["values"]
                assert set(new_bonus["gem_class"]["types"]) == normalize_types(list(original_types))
            elif relative_path == "crafted/ur-ghast.json":
                assert len(runtime["bonuses"]) == len(original["bonuses"]) == 1
                old_bonus = original["bonuses"][0]
                new_bonus = runtime["bonuses"][0]
                assert old_bonus["type"] == "apotheosis:all_stats"
                assert new_bonus["type"] == "apotheotic_additions:original_all_stats"
                assert new_bonus["operation"] == OPERATION_MAP[old_bonus["operation"].upper()]
                assert new_bonus["values"] == old_bonus["values"]
                assert new_bonus["original_socket_types"] == old_bonus["gem_class"]["types"]
                assert set(new_bonus["attributes"]) == {
                    normalize_attribute(attribute) for attribute in old_bonus["attributes"]
                }
                assert new_bonus["gem_class"]["key"] == old_bonus["gem_class"]["key"]
                assert set(new_bonus["gem_class"]["types"]) == normalize_types(
                    old_bonus["gem_class"]["types"]
                )
            elif relative_path == "modded/midas.json":
                assert len(original["bonuses"]) == 3
                assert len(runtime["bonuses"]) == 2
                old_mage, old_enchantment, old_transform = original["bonuses"]
                new_mage, new_weapon = runtime["bonuses"]
                assert old_mage["type"] == "apotheosis:mageslayer"
                assert new_mage["type"] == "apotheotic_additions:original_mageslayer"
                assert new_mage["values"] == old_mage["values"]
                assert new_mage["original_socket_types"] == ["helmet"]
                assert new_mage["gem_class"]["types"] == ["apotheosis:helmet"]

                assert old_enchantment["type"] == "apotheosis:enchantment"
                assert old_transform["type"] == "apotheosis:drop_transform"
                assert new_weapon["type"] == "apotheotic_additions:original_midas_weapon"
                assert new_weapon["enchantment"] == normalize_enchantment(
                    old_enchantment["enchantment"]
                )
                assert new_weapon["enchantment_values"] == old_enchantment["values"]
                assert new_weapon["transform_values"] == old_transform["values"]
                assert new_weapon["light_socket_types"] == old_enchantment["gem_class"]["types"]
                assert new_weapon["heavy_socket_types"] == old_transform["gem_class"]["types"]
                combined_types = old_enchantment["gem_class"]["types"] + old_transform["gem_class"]["types"]
                assert set(new_weapon["gem_class"]["types"]) == normalize_types(combined_types)
                assert new_weapon["inputs"] == old_transform["inputs"]
                assert new_weapon["output"] == {"count": 1, "id": old_transform["output"]["item"]}
                assert new_weapon["desc"] == old_transform["desc"]
                assert new_weapon["conditions"] == [{
                    "condition": "apotheosis:matches_block",
                    "valid_blocks": "#" + old_transform["blocks"],
                }]
            elif relative_path == "modded/heartland.json":
                assert len(original["bonuses"]) == 4
                assert len(runtime["bonuses"]) == 3
                old_axe, old_knife, old_comfort, old_shovel = original["bonuses"]
                new_weapon, new_comfort, new_shovel = runtime["bonuses"]

                assert old_axe["type"] == "apotheosis:drop_transform"
                assert old_knife["type"] == "apotheosis:enchantment"
                assert new_weapon["type"] == "apotheotic_additions:original_midas_weapon"
                assert new_weapon["enchantment"] == normalize_enchantment(old_knife["enchantment"])
                assert new_weapon["enchantment_values"] == old_knife["values"]
                assert new_weapon["transform_values"] == old_axe["values"]
                assert new_weapon["light_socket_types"] == old_knife["gem_class"]["types"]
                assert new_weapon["heavy_socket_types"] == old_axe["gem_class"]["types"]
                assert new_weapon["inputs"] == old_axe["inputs"]
                assert new_weapon["output"] == {"count": 1, "id": old_axe["output"]["item"]}
                assert new_weapon["desc"] == old_axe["desc"]
                assert new_weapon["conditions"] == [{
                    "condition": "apotheosis:matches_block",
                    "valid_blocks": "#" + old_axe["blocks"],
                }]

                assert old_comfort["type"] == "apotheosis:mob_effect"
                assert new_comfort["type"] == "apotheotic_additions:original_mob_effect"
                assert new_comfort["mob_effect"] == old_comfort["mob_effect"]
                assert new_comfort["target"] == old_comfort["target"].lower()
                assert new_comfort["values"] == old_comfort["values"]
                assert new_comfort["original_socket_types"] == old_comfort["gem_class"]["types"]
                assert set(new_comfort["gem_class"]["types"]) == normalize_types(
                    old_comfort["gem_class"]["types"]
                )

                assert old_shovel["type"] == "apotheosis:drop_transform"
                assert new_shovel["type"] == "apotheotic_additions:original_drop_transform"
                assert new_shovel["values"] == old_shovel["values"]
                assert new_shovel["inputs"] == old_shovel["inputs"]
                assert new_shovel["output"] == {"count": 1, "id": old_shovel["output"]["item"]}
                assert new_shovel["desc"] == old_shovel["desc"]
                assert new_shovel["original_socket_types"] == old_shovel["gem_class"]["types"]
                assert new_shovel["conditions"] == [{
                    "condition": "apotheosis:matches_block",
                    "valid_blocks": "#" + old_shovel["blocks"],
                }]
                assert runtime["constraints"]["dimensions"] == original["dimensions"]
                assert runtime["neoforge:conditions"] == [
                    {"type": "neoforge:mod_loaded", "modid": "farmersdelight"}
                ]
            elif relative_path == "modded/cataclysm.json":
                assert len(original["bonuses"]) == 3
                assert len(runtime["bonuses"]) == 2
                old_chest, old_heavy, old_light = original["bonuses"]
                new_chest, new_split = runtime["bonuses"]
                assert new_chest["type"] == "apotheotic_additions:original_mob_effect"
                assert new_chest["mob_effect"] == old_chest["mob_effect"]
                assert new_chest["target"] == old_chest["target"]
                assert new_chest["values"] == old_chest["values"]
                assert new_chest["original_socket_types"] == old_chest["gem_class"]["types"]
                assert new_split["type"] == "apotheotic_additions:original_split_attack_effect"
                for profile_name, old_bonus in (
                    ("heavy_profile", old_heavy), ("light_profile", old_light)
                ):
                    profile = new_split[profile_name]
                    assert profile["mob_effect"] == old_bonus["mob_effect"]
                    assert profile["values"] == old_bonus["values"]
                    assert profile["original_socket_types"] == old_bonus["gem_class"]["types"]
                assert new_split["light_profile"]["stack_on_reapply"] is True
                assert old_light["stack_on_reapply"] is True
                assert runtime["constraints"]["dimensions"] == original["dimensions"]
                assert runtime["neoforge:conditions"] == [
                    {"type": "neoforge:mod_loaded", "modid": "cataclysm"}
                ]
            elif relative_path == "crafted/lich.json":
                assert len(runtime["bonuses"]) == len(original["bonuses"]) == 1
                old_bonus = original["bonuses"][0]
                new_bonus = runtime["bonuses"][0]
                assert new_bonus["type"] == "apotheotic_additions:original_attribute"
                assert new_bonus["attribute"] == normalize_attribute(old_bonus["attribute"])
                assert new_bonus["operation"] == OPERATION_MAP[old_bonus["operation"]]
                assert new_bonus["values"] == old_bonus["values"]
                assert new_bonus["original_socket_types"] == old_bonus["gem_class"]["types"]
                assert set(new_bonus["gem_class"]["types"]) == normalize_types(
                    old_bonus["gem_class"]["types"]
                )
                assert runtime["neoforge:conditions"] == [
                    {"type": "neoforge:mod_loaded", "modid": "eidolon_repraised"},
                    {"type": "neoforge:mod_loaded", "modid": "twilightforest"},
                ]
            elif relative_path in {
                "curios/reinforced.json",
                "curios/twisted.json",
                "curios/vitalic.json",
            }:
                assert len(runtime["bonuses"]) == len(original["bonuses"])
                for old_bonus, new_bonus in zip(original["bonuses"], runtime["bonuses"]):
                    old_types = old_bonus["gem_class"]["types"]
                    assert set(new_bonus["gem_class"]["types"]) == normalize_types(old_types)
                    if old_bonus["type"] == "apotheosis:attribute":
                        assert new_bonus["type"] == "apotheotic_additions:original_attribute"
                        assert new_bonus["attribute"] == normalize_attribute(old_bonus["attribute"])
                        assert new_bonus["operation"] == OPERATION_MAP[old_bonus["operation"]]
                        assert new_bonus["values"] == old_bonus["values"]
                        assert new_bonus["original_socket_types"] == old_types
                    else:
                        assert old_bonus["type"] == "apotheosis:multi_attribute"
                        assert new_bonus["type"] == "apotheotic_additions:original_multi_attribute"
                        assert len(new_bonus["profiles"]) == 1
                        profile = new_bonus["profiles"][0]
                        assert profile["original_socket_types"] == old_types
                        assert len(profile["modifiers"]) == len(old_bonus["modifiers"])
                        for modifier, old_modifier in zip(profile["modifiers"], old_bonus["modifiers"]):
                            assert modifier["attribute"] == normalize_attribute(old_modifier["attribute"])
                            assert modifier["operation"] == OPERATION_MAP[old_modifier["operation"]]
                            assert modifier["values"] == old_modifier["values"]
                assert runtime["neoforge:conditions"] == [
                    {"type": "neoforge:mod_loaded", "modid": "curios"}
                ]
            elif relative_path == "twilight/umbral_matrimony.json":
                assert len(runtime["bonuses"]) == len(original["bonuses"]) == 4
                old_drop, *old_attributes = original["bonuses"]
                new_drop, *new_attributes = runtime["bonuses"]
                assert new_drop["type"] == "apotheotic_additions:original_drop_transform"
                assert new_drop["conditions"] == [{
                    "condition": "apotheosis:matches_block",
                    "valid_blocks": "#" + COMMON_TAG_MAP[old_drop["blocks"]],
                }]
                assert new_drop["inputs"] == old_drop["inputs"]
                assert new_drop["output"] == {"count": 1, "id": old_drop["output"]["item"]}
                assert new_drop["values"] == old_drop["values"]
                assert new_drop["desc"] == old_drop["desc"]
                assert new_drop["original_socket_types"] == old_drop["gem_class"]["types"]
                for old_bonus, new_bonus in zip(old_attributes, new_attributes):
                    assert new_bonus["type"] == "apotheotic_additions:original_attribute"
                    assert new_bonus["attribute"] == normalize_attribute(old_bonus["attribute"])
                    assert new_bonus["operation"] == OPERATION_MAP[old_bonus["operation"]]
                    assert new_bonus["values"] == old_bonus["values"]
                    assert new_bonus["original_socket_types"] == old_bonus["gem_class"]["types"]
                    assert set(new_bonus["gem_class"]["types"]) == normalize_types(
                        old_bonus["gem_class"]["types"]
                    )
                assert runtime["constraints"]["dimensions"] == original["dimensions"]
                assert runtime["neoforge:conditions"] == [
                    {"type": "neoforge:mod_loaded", "modid": "aether"},
                    {"type": "neoforge:mod_loaded", "modid": "curios"},
                    {"type": "neoforge:mod_loaded", "modid": "twilightforest"},
                ]
            elif relative_path == "bumblezone/hive.json":
                assert len(runtime["bonuses"]) == len(original["bonuses"]) == 4
                for old_bonus, new_bonus in zip(original["bonuses"], runtime["bonuses"]):
                    assert old_bonus["type"] == "apotheosis:attribute"
                    assert new_bonus["type"] == "apotheotic_additions:original_attribute"
                    assert new_bonus["attribute"] == normalize_attribute(old_bonus["attribute"])
                    assert new_bonus["operation"] == OPERATION_MAP[old_bonus["operation"].upper()]
                    assert new_bonus["values"] == old_bonus["values"]
                    assert new_bonus["original_socket_types"] == old_bonus["gem_class"]["types"]
                    assert set(new_bonus["gem_class"]["types"]) == normalize_types(
                        old_bonus["gem_class"]["types"]
                    )
                assert runtime["constraints"]["dimensions"] == original["dimensions"]
                assert runtime["neoforge:conditions"] == [
                    {"type": "neoforge:mod_loaded", "modid": "aether"},
                    {"type": "neoforge:mod_loaded", "modid": "irons_spellbooks"},
                    {"type": "neoforge:mod_loaded", "modid": "curios"},
                    {"type": "neoforge:mod_loaded", "modid": "the_bumblezone"},
                ]
            elif relative_path == "modded/bloodcaster.json":
                assert len(runtime["bonuses"]) == len(original["bonuses"]) == 3
                old_effect, old_attribute, old_multi = original["bonuses"]
                new_effect, new_attribute, new_multi = runtime["bonuses"]
                assert new_effect["type"] == "apotheotic_additions:original_mob_effect"
                assert new_effect["mob_effect"] == normalize_effect(old_effect["mob_effect"])
                assert new_effect["target"] == old_effect["target"].lower()
                assert new_effect["values"] == old_effect["values"]
                assert new_effect["original_socket_types"] == old_effect["gem_class"]["types"]
                assert set(new_effect["gem_class"]["types"]) == normalize_types(
                    old_effect["gem_class"]["types"]
                )
                assert new_attribute["type"] == "apotheotic_additions:original_attribute"
                assert new_attribute["attribute"] == normalize_attribute(old_attribute["attribute"])
                assert new_attribute["operation"] == OPERATION_MAP[old_attribute["operation"].upper()]
                assert new_attribute["values"] == old_attribute["values"]
                assert new_attribute["original_socket_types"] == old_attribute["gem_class"]["types"]
                assert set(new_attribute["gem_class"]["types"]) == normalize_types(
                    old_attribute["gem_class"]["types"]
                )
                assert new_multi["type"] == "apotheotic_additions:original_multi_attribute"
                assert len(new_multi["profiles"]) == 1
                profile = new_multi["profiles"][0]
                assert profile["original_socket_types"] == old_multi["gem_class"]["types"]
                assert len(profile["modifiers"]) == len(old_multi["modifiers"])
                for modifier, old_modifier in zip(profile["modifiers"], old_multi["modifiers"]):
                    assert modifier["attribute"] == normalize_attribute(old_modifier["attribute"])
                    assert modifier["operation"] == OPERATION_MAP[old_modifier["operation"].upper()]
                    assert modifier["values"] == old_modifier["values"]
                assert runtime["neoforge:conditions"] == [
                    {"type": "neoforge:mod_loaded", "modid": "eidolon_repraised"},
                    {"type": "neoforge:mod_loaded", "modid": "irons_spellbooks"},
                    {"type": "neoforge:mod_loaded", "modid": "curios"},
                ]
            elif relative_path == "modded/mystic.json":
                assert len(runtime["bonuses"]) == len(original["bonuses"]) == 3
                old_effect, old_attribute, old_drop = original["bonuses"]
                new_effect, new_attribute, new_drop = runtime["bonuses"]
                assert new_effect["type"] == "apotheotic_additions:original_mob_effect"
                assert new_effect["mob_effect"] == normalize_effect(old_effect["mob_effect"])
                assert new_effect["target"] == old_effect["target"].lower()
                assert new_effect["values"] == old_effect["values"]
                assert new_effect["original_socket_types"] == old_effect["gem_class"]["types"]
                assert set(new_effect["gem_class"]["types"]) == normalize_types(
                    old_effect["gem_class"]["types"]
                )
                assert new_attribute["type"] == "apotheotic_additions:original_attribute"
                assert new_attribute["attribute"] == normalize_attribute(old_attribute["attribute"])
                assert new_attribute["operation"] == OPERATION_MAP[old_attribute["operation"].upper()]
                assert new_attribute["values"] == old_attribute["values"]
                assert new_attribute["original_socket_types"] == old_attribute["gem_class"]["types"]
                assert set(new_attribute["gem_class"]["types"]) == normalize_types(
                    old_attribute["gem_class"]["types"]
                )
                assert new_drop["type"] == "apotheotic_additions:original_drop_transform"
                assert new_drop["conditions"] == [{
                    "condition": "apotheosis:matches_block",
                    "valid_blocks": "#" + COMMON_TAG_MAP[old_drop["blocks"]],
                }]
                assert new_drop["inputs"] == {
                    "tag": COMMON_TAG_MAP[old_drop["inputs"]["tag"]]
                }
                assert new_drop["output"] == {"count": 1, "id": old_drop["output"]["item"]}
                assert new_drop["values"] == old_drop["values"]
                assert new_drop["desc"] == old_drop["desc"]
                assert new_drop["original_socket_types"] == old_drop["gem_class"]["types"]
                assert set(new_drop["gem_class"]["types"]) == normalize_types(
                    old_drop["gem_class"]["types"]
                )
                assert runtime["neoforge:conditions"] == [
                    {"type": "neoforge:mod_loaded", "modid": "eidolon_repraised"},
                    {"type": "neoforge:mod_loaded", "modid": "irons_spellbooks"},
                    {"type": "neoforge:mod_loaded", "modid": "curios"},
                ]
            elif relative_path == "modded/felfire.json":
                assert len(original["bonuses"]) == 5
                assert len(runtime["bonuses"]) == 4
                old_heavy, old_drop, old_light, old_enchantment, old_attribute = original["bonuses"]
                combined, new_drop, new_enchantment, new_attribute = runtime["bonuses"]
                assert combined["type"] == "apotheotic_additions:original_felfire_weapon"
                assert set(combined["gem_class"]["types"]) == normalize_types(
                    old_heavy["gem_class"]["types"] + old_light["gem_class"]["types"]
                )
                assert len(combined["heavy_profiles"]) == 1
                profile = combined["heavy_profiles"][0]
                assert profile["original_socket_types"] == old_heavy["gem_class"]["types"]
                for modifier, old_modifier in zip(profile["modifiers"], old_heavy["modifiers"]):
                    assert modifier["attribute"] == normalize_attribute(old_modifier["attribute"])
                    assert modifier["operation"] == OPERATION_MAP[old_modifier["operation"]]
                    assert modifier["values"] == old_modifier["values"]
                assert combined["light_mob_effect"] == old_light["mob_effect"]
                assert combined["light_target"] == old_light["target"].lower()
                assert combined["light_values"] == old_light["values"]
                assert combined["light_stack_on_reapply"] == old_light["stack_on_reapply"]
                assert combined["light_original_socket_types"] == old_light["gem_class"]["types"]
                assert new_drop["type"] == "apotheotic_additions:original_drop_transform"
                assert new_drop["conditions"] == [{
                    "condition": "apotheosis:matches_block",
                    "valid_blocks": "#" + COMMON_TAG_MAP[old_drop["blocks"]],
                }]
                assert new_drop["inputs"] == {"tag": COMMON_TAG_MAP[old_drop["inputs"]["tag"]]}
                assert new_drop["output"] == {"count": 1, "id": old_drop["output"]["item"]}
                assert new_drop["values"] == old_drop["values"]
                assert new_enchantment["enchantment"] == normalize_enchantment(
                    old_enchantment["enchantment"]
                )
                assert new_enchantment["values"] == old_enchantment["values"]
                assert new_attribute["attribute"] == normalize_attribute(old_attribute["attribute"])
                assert new_attribute["operation"] == OPERATION_MAP[old_attribute["operation"]]
                assert new_attribute["values"] == old_attribute["values"]
                assert runtime["neoforge:conditions"] == [
                    {"type": "neoforge:mod_loaded", "modid": "born_in_chaos_v1"}
                ]
            elif relative_path == "modded/soultwisted.json":
                assert len(original["bonuses"]) == 7
                assert len(runtime["bonuses"]) == 6
                old_chest, old_boots, old_heavy, old_light, *old_attributes = original["bonuses"]
                new_chest, new_boots, split, *new_attributes = runtime["bonuses"]
                for old_effect, new_effect in ((old_chest, new_chest), (old_boots, new_boots)):
                    assert new_effect["type"] == "apotheotic_additions:original_mob_effect"
                    assert new_effect["mob_effect"] == normalize_effect(old_effect["mob_effect"])
                    assert new_effect["target"] == old_effect["target"].lower()
                    assert new_effect["values"] == old_effect["values"]
                    assert new_effect["original_socket_types"] == old_effect["gem_class"]["types"]
                assert split["type"] == "apotheotic_additions:original_split_attack_effect"
                assert set(split["gem_class"]["types"]) == normalize_types(
                    old_heavy["gem_class"]["types"] + old_light["gem_class"]["types"]
                )
                for name, old_effect in (("heavy_profile", old_heavy), ("light_profile", old_light)):
                    new_effect = split[name]
                    assert new_effect["mob_effect"] == normalize_effect(old_effect["mob_effect"])
                    assert new_effect["values"] == old_effect["values"]
                    assert new_effect["original_socket_types"] == old_effect["gem_class"]["types"]
                    assert new_effect.get("stack_on_reapply", False) == old_effect.get(
                        "stack_on_reapply", False
                    )
                for old_attribute, new_attribute in zip(old_attributes, new_attributes):
                    assert new_attribute["type"] == "apotheotic_additions:original_attribute"
                    assert new_attribute["attribute"] == normalize_attribute(old_attribute["attribute"])
                    assert new_attribute["operation"] == OPERATION_MAP[old_attribute["operation"]]
                    assert new_attribute["values"] == old_attribute["values"]
                    assert new_attribute["original_socket_types"] == old_attribute["gem_class"]["types"]
                    assert set(new_attribute["gem_class"]["types"]) == normalize_types(
                        old_attribute["gem_class"]["types"]
                    )
                assert runtime["neoforge:conditions"] == [
                    {"type": "neoforge:mod_loaded", "modid": "eidolon_repraised"}
                ]
            elif relative_path in {
                "modded/engineer.json",
                "modded/dracula.json",
                "modded/weeping_well.json",
            }:
                assert len(runtime["bonuses"]) == len(original["bonuses"])
                for old_bonus, new_bonus in zip(original["bonuses"], runtime["bonuses"]):
                    old_types = old_bonus["gem_class"]["types"]
                    assert new_bonus["gem_class"]["key"] == old_bonus["gem_class"]["key"]
                    assert set(new_bonus["gem_class"]["types"]) == normalize_types(old_types)

                    if old_bonus["type"] == "apotheosis:attribute":
                        assert new_bonus["type"] == "apotheotic_additions:original_attribute"
                        assert new_bonus["attribute"] == normalize_attribute(old_bonus["attribute"])
                        assert new_bonus["operation"] == OPERATION_MAP[old_bonus["operation"].upper()]
                        assert new_bonus["values"] == old_bonus["values"]
                        assert new_bonus["original_socket_types"] == old_types
                    elif old_bonus["type"] == "apotheosis:multi_attribute":
                        assert new_bonus["type"] == "apotheotic_additions:original_multi_attribute"
                        assert len(new_bonus["profiles"]) == 1
                        profile = new_bonus["profiles"][0]
                        assert profile["key"] == old_bonus["gem_class"]["key"]
                        assert profile["original_socket_types"] == old_types
                        assert len(profile["modifiers"]) == len(old_bonus["modifiers"])
                        for modifier, old_modifier in zip(profile["modifiers"], old_bonus["modifiers"]):
                            assert modifier["attribute"] == normalize_attribute(old_modifier["attribute"])
                            assert modifier["operation"] == OPERATION_MAP[old_modifier["operation"].upper()]
                            assert modifier["values"] == old_modifier["values"]
                    elif old_bonus["type"] == "apotheosis:enchantment":
                        assert new_bonus["type"] == "apotheotic_additions:original_enchantment"
                        assert new_bonus["enchantment"] == normalize_enchantment(old_bonus["enchantment"])
                        assert new_bonus["values"] == old_bonus["values"]
                        assert new_bonus["original_socket_types"] == old_types
                    elif old_bonus["type"] == "apotheosis:mob_effect":
                        assert new_bonus["type"] == "apotheotic_additions:original_mob_effect"
                        assert new_bonus["mob_effect"] == normalize_effect(old_bonus["mob_effect"])
                        assert new_bonus["target"] == old_bonus["target"].lower()
                        assert new_bonus["values"] == old_bonus["values"]
                        assert new_bonus.get("stack_on_reapply", False) == old_bonus.get(
                            "stack_on_reapply", False
                        )
                        assert new_bonus["original_socket_types"] == old_types
                    else:
                        assert old_bonus["type"] == "apotheosis:drop_transform"
                        assert new_bonus["type"] == "apotheotic_additions:original_drop_transform"
                        assert new_bonus["conditions"] == [{
                            "condition": "apotheosis:matches_block",
                            "valid_blocks": "#" + COMMON_TAG_MAP[old_bonus["blocks"]],
                        }]
                        assert new_bonus["inputs"] == {
                            "tag": COMMON_TAG_MAP[old_bonus["inputs"]["tag"]]
                        }
                        assert new_bonus["output"] == {
                            "count": 1,
                            "id": old_bonus["output"]["item"],
                        }
                        assert new_bonus["values"] == old_bonus["values"]
                        assert new_bonus["desc"] == old_bonus["desc"]
                        assert new_bonus["original_socket_types"] == old_types

                assert runtime.get("constraints", {}).get("dimensions", []) == original["dimensions"]
                original_mod = next(
                    condition["modid"]
                    for condition in original["conditions"]
                    if condition["type"] == "forge:mod_loaded"
                )
                runtime_mod = "eidolon_repraised" if original_mod == "eidolon" else original_mod
                assert runtime["neoforge:conditions"] == [
                    {"type": "neoforge:mod_loaded", "modid": runtime_mod}
                ]
            elif relative_path in {"modded/archmage.json", "modded/ars_mana.json"}:
                assert len(runtime["bonuses"]) == len(original["bonuses"])
                for old_bonus, new_bonus in zip(original["bonuses"], runtime["bonuses"]):
                    assert new_bonus["original_socket_types"] == old_bonus["gem_class"]["types"]
                    assert new_bonus["values"] == old_bonus["values"]
                    assert set(new_bonus["gem_class"]["types"]) == normalize_types(
                        old_bonus["gem_class"]["types"]
                    )
                    if old_bonus["type"] == "apotheosis:attribute":
                        assert new_bonus["type"] == "apotheotic_additions:original_attribute"
                        assert new_bonus["attribute"] == normalize_attribute(old_bonus["attribute"])
                        assert new_bonus["operation"] == OPERATION_MAP[old_bonus["operation"].upper()]
                    elif old_bonus["type"] == "apotheosis:enchantment":
                        assert new_bonus["type"] == "apotheotic_additions:original_enchantment"
                        assert new_bonus["enchantment"] == normalize_enchantment(old_bonus["enchantment"])
                    else:
                        assert old_bonus["type"] == "apotheosis:mob_effect"
                        assert new_bonus["type"] == "apotheotic_additions:original_mob_effect"
                        assert new_bonus["mob_effect"] == old_bonus["mob_effect"]
                        assert new_bonus["target"] == old_bonus["target"]
                assert runtime["neoforge:conditions"] == [
                    {"type": "neoforge:mod_loaded", "modid": "ars_nouveau"}
                ]
            elif relative_path == "bumblezone/bee_queen.json":
                assert len(runtime["bonuses"]) == len(original["bonuses"]) == 3
                old_effect, old_attribute, old_multi = original["bonuses"]
                new_effect, new_attribute, new_multi = runtime["bonuses"]
                assert new_effect["values"] == old_effect["values"]
                assert new_effect["mob_effect"] == old_effect["mob_effect"]
                assert new_effect["stack_on_reapply"] == old_effect["stack_on_reapply"]
                assert new_effect["original_socket_types"] == old_effect["gem_class"]["types"]
                assert new_attribute["attribute"] == normalize_attribute(old_attribute["attribute"])
                assert new_attribute["operation"] == OPERATION_MAP[old_attribute["operation"]]
                assert new_attribute["values"] == old_attribute["values"]
                profile = new_multi["profiles"][0]
                assert profile["original_socket_types"] == old_multi["gem_class"]["types"]
                assert len(profile["modifiers"]) == len(old_multi["modifiers"])
                for modifier, old_modifier in zip(profile["modifiers"], old_multi["modifiers"]):
                    assert modifier["attribute"] == normalize_attribute(old_modifier["attribute"])
                    assert modifier["operation"] == OPERATION_MAP[old_modifier["operation"]]
                    assert modifier["values"] == old_modifier["values"]
                assert runtime["constraints"]["dimensions"] == original["dimensions"]
                assert runtime["neoforge:conditions"] == [
                    {"type": "neoforge:mod_loaded", "modid": "the_bumblezone"}
                ]
            elif relative_path in {
                "crafted/harbringer.json",
                "crafted/leviathan.json",
                "crafted/remnant.json",
                "crafted/sunbird.json",
            }:
                assert len(runtime["bonuses"]) == len(original["bonuses"]) == 1
                old_bonus = original["bonuses"][0]
                new_bonus = runtime["bonuses"][0]
                assert old_bonus["type"] == "apotheosis:mob_effect"
                assert new_bonus["type"] == "apotheotic_additions:original_mob_effect"
                assert new_bonus["mob_effect"] == old_bonus["mob_effect"]
                assert new_bonus["target"] == old_bonus["target"].lower()
                assert new_bonus["values"] == old_bonus["values"]
                assert new_bonus["original_socket_types"] == old_bonus["gem_class"]["types"]
                assert new_bonus["gem_class"]["key"] == old_bonus["gem_class"]["key"]
                assert set(new_bonus["gem_class"]["types"]) == normalize_types(
                    old_bonus["gem_class"]["types"]
                )
                expected_conditions = [
                    {"type": "neoforge:mod_loaded", "modid": condition["modid"]}
                    for condition in original["conditions"]
                    if condition["type"] == "forge:mod_loaded"
                ]
                assert runtime["neoforge:conditions"] == expected_conditions
            elif relative_path == "aether/aether.json":
                assert len(runtime["bonuses"]) == len(original["bonuses"]) == 2
                for old_bonus, new_bonus in zip(original["bonuses"], runtime["bonuses"]):
                    assert old_bonus["type"] == "apotheosis:attribute"
                    assert new_bonus["type"] == "apotheotic_additions:original_attribute"
                    assert new_bonus["attribute"] == normalize_attribute(old_bonus["attribute"])
                    assert new_bonus["operation"] == OPERATION_MAP[old_bonus["operation"].upper()]
                    assert new_bonus["values"] == old_bonus["values"]
                    assert new_bonus["original_socket_types"] == old_bonus["gem_class"]["types"]
                    assert new_bonus["gem_class"]["key"] == old_bonus["gem_class"]["key"]
                    assert set(new_bonus["gem_class"]["types"]) == normalize_types(
                        old_bonus["gem_class"]["types"]
                    )
                assert runtime["constraints"]["dimensions"] == original["dimensions"]
                assert runtime["neoforge:conditions"] == [
                    {"type": "neoforge:mod_loaded", "modid": "aether"}
                ]
            elif relative_path in {
                "aether/purity.json",
                "aether/zanite_alchemist.json",
                "undergarden/decay.json",
                "undergarden/undergarden.json",
            }:
                assert len(runtime["bonuses"]) == len(original["bonuses"]) == 3
                for old_bonus, new_bonus in zip(original["bonuses"], runtime["bonuses"]):
                    assert new_bonus["original_socket_types"] == old_bonus["gem_class"]["types"]
                    assert new_bonus["gem_class"]["key"] == old_bonus["gem_class"]["key"]
                    assert set(new_bonus["gem_class"]["types"]) == normalize_types(
                        old_bonus["gem_class"]["types"]
                    )
                    assert new_bonus["values"] == old_bonus["values"]
                    if old_bonus["type"] == "apotheosis:drop_transform":
                        assert new_bonus["type"] == "apotheotic_additions:original_drop_transform"
                        assert new_bonus["conditions"] == [{
                            "condition": "apotheosis:matches_block",
                            "valid_blocks": "#" + COMMON_TAG_MAP[old_bonus["blocks"]],
                        }]
                        assert new_bonus["inputs"] == {
                            "tag": COMMON_TAG_MAP[old_bonus["inputs"]["tag"]]
                        }
                        assert new_bonus["output"] == {
                            "count": 1,
                            "id": old_bonus["output"]["item"],
                        }
                        assert new_bonus["desc"] == old_bonus["desc"]
                    elif old_bonus["type"] == "apotheosis:attribute":
                        assert new_bonus["type"] == "apotheotic_additions:original_attribute"
                        assert new_bonus["attribute"] == normalize_attribute(old_bonus["attribute"])
                        assert new_bonus["operation"] == OPERATION_MAP[old_bonus["operation"].upper()]
                    else:
                        assert old_bonus["type"] == "apotheosis:mob_effect"
                        assert new_bonus["type"] == "apotheotic_additions:original_mob_effect"
                        assert new_bonus["mob_effect"] == old_bonus["mob_effect"]
                        assert new_bonus["target"] == old_bonus["target"].lower()
                assert runtime["constraints"]["dimensions"] == original["dimensions"]
                expected_mod = "undergarden" if relative_path.startswith("undergarden/") else "aether"
                assert runtime["neoforge:conditions"] == [
                    {"type": "neoforge:mod_loaded", "modid": expected_mod}
                ]
            elif relative_path == "crafted/naga.json":
                assert len(runtime["bonuses"]) == len(original["bonuses"]) == 1
                old_bonus = original["bonuses"][0]
                new_bonus = runtime["bonuses"][0]
                assert old_bonus["type"] == "apotheosis:drop_transform"
                assert new_bonus["type"] == "apotheotic_additions:original_drop_transform"
                assert new_bonus["gem_class"]["key"] == old_bonus["gem_class"]["key"]
                assert set(new_bonus["gem_class"]["types"]) == normalize_types(
                    old_bonus["gem_class"]["types"]
                )
                assert new_bonus["original_socket_types"] == old_bonus["gem_class"]["types"]
                assert new_bonus["conditions"] == [{
                    "condition": "apotheosis:matches_block",
                    "valid_blocks": "#" + old_bonus["blocks"],
                }]
                assert new_bonus["inputs"] == old_bonus["inputs"]
                assert new_bonus["output"] == {
                    "count": 1,
                    "id": old_bonus["output"]["item"],
                }
                assert new_bonus["values"] == old_bonus["values"]
                assert new_bonus["desc"] == old_bonus["desc"]
                assert runtime["neoforge:conditions"] == [
                    {"type": "neoforge:mod_loaded", "modid": "twilightforest"}
                ]
            else:
                assert len(runtime["bonuses"]) == len(original["bonuses"]) == 1
                old_bonus = original["bonuses"][0]
                new_bonus = runtime["bonuses"][0]
                assert old_bonus["type"] == "apotheosis:attribute"
                assert new_bonus["type"] == "apotheotic_additions:original_attribute"
                assert new_bonus["attribute"] == normalize_attribute(old_bonus["attribute"])
                assert new_bonus["operation"] == OPERATION_MAP[old_bonus["operation"].upper()]
                assert new_bonus["values"] == old_bonus["values"]
                assert new_bonus["original_socket_types"] == old_bonus["gem_class"]["types"]
                assert new_bonus["gem_class"]["key"] == old_bonus["gem_class"]["key"]
                assert set(new_bonus["gem_class"]["types"]) == normalize_types(
                    old_bonus["gem_class"]["types"]
                )

    print(f"PASS: {len(ACTIVE)} packaged runtime gem definitions match original data")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
