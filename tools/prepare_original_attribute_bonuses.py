#!/usr/bin/env python3
"""Prepare 85 actual ORIGINAL attribute-gem bonuses without widening equipment targets.

Generated profiles are STAGING, not datapacks: original Forge gem_class must be
matched to an exact NeoForge 1.21.1 loot category before moving them to runtime.
"""
import argparse
import json
from pathlib import Path
from zipfile import ZipFile

KNOWN_OPERATION_MAP = {
    'ADDITION': 'add_value',
    'MULTIPLY_BASE': 'add_multiplied_base',
    'MULTIPLY_TOTAL': 'add_multiplied_total',
    'multiply_total': 'add_multiplied_total',
}
VALID_TIERS = {'common', 'uncommon', 'rare', 'epic', 'mythic', 'ancient',
               'apotheotic_additions:artifact', 'apotheotic_additions:heirloom',
               'apotheotic_additions:esoteric'}


def convert(original, relpath, index):
    assert original['type'] == 'apotheosis:attribute'
    assert original['operation'] in KNOWN_OPERATION_MAP, (relpath, index)
    vals = original['values']
    assert vals and set(vals) <= VALID_TIERS, (relpath, index)
    assert all(type(v) in (int, float) for v in vals.values()), (relpath, index)
    assert 'gem_class' in original, (relpath, index)
    return {
        'type': 'apotheotic_additions:original_attribute',
        'attribute': original['attribute'],
        'operation': KNOWN_OPERATION_MAP[original['operation']],
        'values': vals,
        # Do not synthesize a native GemClass until each exact target is verified.
        '__requires_exact_gem_class': original['gem_class'],
        '__origin': f'{relpath}#bonuses[{index}]',
    }


def main():
    ap = argparse.ArgumentParser()
    ap.add_argument('original_jar', type=Path)
    ap.add_argument('project', type=Path)
    args = ap.parse_args()
    prefix = 'data/apotheotic_additions/gems/'
    output = args.project / 'conversion_neoforge_1.21.1_NOT_RUNTIME' / 'ranked_attribute_bonuses'
    output.mkdir(parents=True, exist_ok=True)
    number = 0
    with ZipFile(args.original_jar) as zipfile:
        files = sorted(p for p in zipfile.namelist() if p.startswith(prefix) and p.endswith('.json'))
        assert len(files) == 67
        for path in files:
            original = json.loads(zipfile.read(path))
            entries = [convert(b, path[len(prefix):], i)
                       for i, b in enumerate(original.get('bonuses', []))
                       if b['type'] == 'apotheosis:attribute']
            number += len(entries)
            if not entries:
                continue
            dest = output / path[len(prefix):]
            dest.parent.mkdir(parents=True, exist_ok=True)
            dest.write_text(json.dumps({'original_gem': path[len(prefix):-5],
                                        'attribute_bonuses': entries}, indent=2) + '\n')
    assert number == 85, f'Expected exactly 85 attribute bonuses, got {number}'
    print(f'OK: prepared {number} unmodified value sets as custom original_attribute bonuses; NOT runtime-activated')

if __name__ == '__main__':
    main()
