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
    "crafted/cornelia.json": "apotheotic_additions:artifact",
    "crafted/ender_dragon.json": "apotheotic_additions:artifact",
    "crafted/frostmaw.json": "apotheotic_additions:artifact",
    "crafted/harbringer.json": "apotheotic_additions:artifact",
    "crafted/hydra.json": "apotheotic_additions:artifact",
    "crafted/ignis.json": "apotheotic_additions:artifact",
    "crafted/leviathan.json": "apotheotic_additions:artifact",
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
    "modded/hearty.json": "common",
    "modded/midas.json": "epic",
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
}


def normalize_attribute(attribute: str) -> str:
    if attribute == "attributeslib:creative_flight":
        return "neoforge:creative_flight"
    if attribute.startswith("attributeslib:"):
        return "apothic_attributes:" + attribute.split(":", 1)[1]
    return attribute


def normalize_enchantment(enchantment: str) -> str:
    if enchantment == "minecraft:sweeping":
        return "minecraft:sweeping_edge"
    return enchantment if ":" in enchantment else "minecraft:" + enchantment


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
            elif relative_path in {"aether/purity.json", "aether/zanite_alchemist.json"}:
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
                assert runtime["neoforge:conditions"] == [
                    {"type": "neoforge:mod_loaded", "modid": "aether"}
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
