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
    "crafted/cornelia.json": "apotheotic_additions:artifact",
    "crafted/frostmaw.json": "apotheotic_additions:artifact",
    "crafted/hydra.json": "apotheotic_additions:artifact",
    "crafted/ignis.json": "apotheotic_additions:artifact",
    "crafted/maledictus.json": "apotheotic_additions:artifact",
    "crafted/monstrosity.json": "apotheotic_additions:artifact",
    "crafted/slider.json": "apotheotic_additions:artifact",
    "crafted/snow_queen.json": "apotheotic_additions:artifact",
    "crafted/warden.json": "apotheotic_additions:artifact",
    "modded/armor.json": "common",
    "modded/hearty.json": "common",
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


def normalize_attribute(attribute: str) -> str:
    if attribute.startswith("attributeslib:"):
        return "apothic_attributes:" + attribute.split(":", 1)[1]
    return attribute


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
            assert len(runtime["bonuses"]) == len(original["bonuses"]) == 1

            if minimum_rank == "apotheotic_additions:artifact":
                assert original["min_rarity"] == minimum_rank
                assert runtime["min_purity"] == "perfect"
            else:
                assert "min_rarity" not in original
                assert "min_purity" not in runtime

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

    print(f"PASS: {len(ACTIVE)} runtime gems match original weights, ranks, classes, and values")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
