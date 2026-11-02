#!/usr/bin/env python3
"""Static parity checks for the reviewed 1.21.1 rarities and affixes."""
import json
import pathlib
import sys

ROOT = pathlib.Path(__file__).resolve().parents[1]
ACTIVE_AFFIXES = {
    "armor/attribute/artifact_armor.json",
    "armor/attribute/artifact_hp.json",
    "armor/attribute/artifact_luck.json",
    "armor/attribute/artifact_source.json",
    "armor/attribute/artifact_toughness.json",
    "armor/attribute/esoteric_armor.json",
    "armor/attribute/esoteric_hp.json",
    "armor/attribute/esoteric_luck.json",
    "armor/attribute/esoteric_source.json",
    "armor/attribute/esoteric_toughness.json",
}
RARITIES = {
    "ancient": (725, 1, 5, 1, 9, 5, 0.75),
    "artifact": (750, 5, 8, 0, 3, 4, 0.5),
    "heirloom": (800, 25, 5, 0, 2, 1, 0.7),
    "esoteric": (850, 0, 0, 0, 10, 5, 0.7),
}


def load(path):
    return json.loads(path.read_text(encoding="utf-8"))


def validate():
    problems = []
    runtime_root = ROOT / "src/main/resources/data/apotheotic_additions"
    affix_root = runtime_root / "affixes"
    original_root = ROOT / "original_1.20.1_REFERENCE/data/apotheotic_additions/affixes"
    active = {p.relative_to(affix_root).as_posix() for p in affix_root.rglob("*.json")}
    if active != ACTIVE_AFFIXES:
        problems.append(f"affixes runtime: expected={sorted(ACTIVE_AFFIXES)} actual={sorted(active)}")

    operation = {"ADDITION": "add_value", "MULTIPLY_BASE": "add_multiplied_base",
                 "MULTIPLY_TOTAL": "add_multiplied_total"}
    for relative in sorted(ACTIVE_AFFIXES):
        source = load(original_root / relative)
        current = load(affix_root / relative)
        expected_categories = [f"apotheosis:{name}" for name in source["types"]]
        expected_conditions = [
            {"type": "neoforge:mod_loaded", "modid": condition["modid"]}
            for condition in source.get("conditions", [])
        ]
        checks = {
            "type": current.get("type") == source.get("type"),
            "attribute": current.get("attribute") == source.get("attribute"),
            "operation": current.get("operation") == operation[source["operation"]],
            "values": current.get("values") == source.get("values"),
            "categories": current.get("categories") == expected_categories,
            "conditions": current.get("neoforge:conditions", []) == expected_conditions,
            "definition": current.get("definition") == {
                "affix_type": "stat", "exclusive_set": [],
                "weights": {"weight": 1, "quality": 0}},
        }
        for label, ok in checks.items():
            if not ok: problems.append(f"{relative}: mismatch in {label}")

    rarity_root = runtime_root / "rarities"
    actual_rarities = {p.stem for p in rarity_root.glob("*.json")}
    if actual_rarities != set(RARITIES):
        problems.append(f"rarities runtime: expected={sorted(RARITIES)} actual={sorted(actual_rarities)}")
    for name, (sort_index, weight, quality, removed, affixes, sockets, durability) in RARITIES.items():
        data = load(rarity_root / f"{name}.json")
        rule = data.get("rules", [{}])[0]
        if data.get("sort_index") != sort_index: problems.append(f"{name}: sort_index")
        if data.get("material") != f"apotheotic_additions:{name}_material": problems.append(f"{name}: material")
        for tier in ("haven", "frontier", "ascent", "summit", "pinnacle"):
            if data.get("weights", {}).get(tier) != {"weight": weight, "quality": float(quality)}:
                problems.append(f"{name}: weights.{tier}")
        if rule.get("type") != "apotheotic_additions:original_rarity": problems.append(f"{name}: rule codec")
        if len(rule.get("removed_affix_chances", [])) != removed: problems.append(f"{name}: removed rules")
        if len(rule.get("affixes", [])) != affixes: problems.append(f"{name}: affix rules")
        if len(rule.get("socket_chances", [])) != sockets: problems.append(f"{name}: socket rules")
        if rule.get("durability") != durability: problems.append(f"{name}: durability")
        if "ao_apotheotic_port" in json.dumps(data): problems.append(f"{name}: AO Port reference")
    return problems


if __name__ == "__main__":
    errors = validate()
    for error in errors:
        print("ERROR:", error)
    if errors:
        sys.exit(1)
    print("OK: 4 runtime rarities and 10 runtime affixes match the reviewed original data")
