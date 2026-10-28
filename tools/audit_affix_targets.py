#!/usr/bin/env python3
"""Extract exact original affix equipment targets, preventing category widening.
A category from the AO port is usable as a REFERENCE ONLY where exact equality is demonstrated.
All other targets must have native Java predicates/tags verified against Apotheosis 8.8.0.
"""
import argparse
import json
from pathlib import Path
from zipfile import ZipFile
from collections import Counter

# Only one-to-one categories observed in AO 0.4.8 datapack.
DIRECT_CATEGORIES = {'helmet','chestplate','leggings','boots','shield','bow','trident'}
# Intentionally NO sword -> melee_weapon and NO pickaxe -> breaker.

def main():
    p=argparse.ArgumentParser()
    p.add_argument('original_jar',type=Path)
    p.add_argument('project_root',type=Path)
    args=p.parse_args()
    prefix='data/apotheotic_additions/affixes/'
    entries=[]
    stats=Counter()
    with ZipFile(args.original_jar) as z:
        for file in sorted(n for n in z.namelist() if n.startswith(prefix) and n.endswith('.json')):
            data=json.loads(z.read(file))
            original_types=data.get('types')
            id_='apotheotic_additions:'+file[len(prefix):-5]
            if original_types is None:
                status='NO_TARGETS_IN_SOURCE'
                categories=[]
            elif all(t in DIRECT_CATEGORIES for t in original_types):
                status='ONE_TO_ONE_CATEGORIES_CANDIDATE'
                categories=['apotheosis:'+t for t in original_types]
            else:
                status='REQUIRES_EXACT_JAVA_TARGET'
                categories=[]
            entry={'id':id_, 'legacy_codec':data['type'], 'original_types':original_types,
                   'exact_vanilla_category_candidates':categories, 'status':status}
            stats[status]+=1
            entries.append(entry)
    assert len(entries)==263
    result={ 'counts':dict(stats), 'source':'ApotheoticAdditionsV2.2.2.jar',
       'warning':'Candidates are NOT runtime-verified. Broader Apotheosis classes cannot replace sword, heavy_weapon, pickaxe, shovel or crossbow.',
       'entries':entries }
    out=args.project_root/'tools/AFFIX_EXACT_TARGETS_AUDIT.json'
    out.write_text(json.dumps(result, indent=2)+'\n')
    print(json.dumps(stats,indent=2))

if __name__=='__main__':main()
