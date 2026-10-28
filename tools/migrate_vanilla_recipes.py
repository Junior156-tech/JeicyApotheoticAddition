#!/usr/bin/env python3
"""Migrate vanilla recipe JSONs from original ApotheoticAdditions V2.2.2.

Outputs STAGING ONLY. Refuses to activate files that use custom ingredients,
NBT-loaded gem results, Forge conditions not safely convertible, or optional
mods whose 1.21.1 registry IDs cannot be validated here.
"""
import argparse
import copy
import json
from collections import Counter
from pathlib import Path
from zipfile import ZipFile

NS = 'apotheotic_additions'
ALLOW = {
    'minecraft:crafting_shaped', 'minecraft:crafting_shapeless',
    'minecraft:stonecutting', 'minecraft:smelting',
    'minecraft:blasting', 'minecraft:smithing_transform',
}
KNOWN_BASE_NS = {'minecraft', 'apotheosis', 'apothic_enchanting', NS}
VALID_RESULT_KEYS = {'id','count','components'}
MOVED_ITEM_IDS = {
    'apotheosis:ancient_material': 'apotheotic_additions:ancient_material',
    'apotheosis:beeshelf': 'apothic_enchanting:beeshelf',
    'apotheosis:blazing_hellshelf': 'apothic_enchanting:blazing_hellshelf',
    'apotheosis:echoing_sculkshelf': 'apothic_enchanting:echoing_sculkshelf',
    'apotheosis:heart_seashelf': 'apothic_enchanting:heart_seashelf',
}


def convert_ingredient(d):
    if not isinstance(d, dict):
        raise ValueError('unexpected ingredient type')
    if 'item' in d and set(d) <= {'item'}:
        # Minecraft 1.21.1's Ingredient codec still requires an object (or an
        # array of objects). Compact item strings are not accepted here.
        item = d['item']
        # Apotheosis 8.8.0 removed ancient_material, but the original recipes
        # use Mythic and Ancient together. Preserve that distinction with the
        # compatibility item registered by this port.
        item = MOVED_ITEM_IDS.get(item, item)
        return {'item': item}
    if 'tag' in d and set(d) <= {'tag'}:
        # Replacing forge tags with common (c:) tags is NOT automatic: may change ingredients.
        if d['tag'].startswith('forge:'):
            raise ValueError('legacy Forge tag: requires semantic mapping')
        return {'tag': d['tag']}
    raise ValueError('nonvanilla/custom ingredient')


def convert_result(result, count=None):
    if isinstance(result,str):
        return {'id':result, **({'count':count} if count is not None else {})}
    if not isinstance(result, dict) or 'item' not in result:
        raise ValueError('missing result item')
    if set(result)-{'item','count'}:
        # Old NBT gems are NOT safe to silently convert to a plain gem item.
        raise ValueError('NBT/components or custom result needs DataComponents port')
    return {'id':result['item'], **({'count':result['count']} if 'count' in result else {})}


def convert_recipe(src):
    d=copy.deepcopy(src)
    typ=d['type']
    if typ not in ALLOW: raise ValueError('non-vanilla recipe type')
    # Conditions for optional mods may be migrated syntactically, but must not be
    # activated without proving target mod/item/tag IDs exist on 1.21.1.
    conditions=d.pop('conditions',[])
    mapped=[]
    for c in conditions:
        if c.get('type')=='forge:mod_loaded':
            mapped.append({'type':'neoforge:mod_loaded','modid':c['modid']})
        elif c.get('type')=='apotheosis:module' and c.get('module')=='enchantment':
            # 1.21.1 Apotheosis requires the separate ApothicEnchanting mod;
            # the old 1.20.1 optional module is no longer independently toggleable.
            continue
        else:
            raise ValueError('unrecognised condition: '+str(c.get('type')))
    if mapped: d['neoforge:conditions']=mapped
    if typ in ('minecraft:crafting_shaped','minecraft:crafting_shapeless'):
        if typ.endswith('shaped'):
            d['key']={k:convert_ingredient(v) for k,v in d['key'].items()}
        else:
            d['ingredients']=[convert_ingredient(v) for v in d['ingredients']]
        d['result']=convert_result(d['result'])
    elif typ=='minecraft:stonecutting':
        d['ingredient']=convert_ingredient(d['ingredient'])
        d['result']=convert_result(d['result'],d.pop('count',1))
    elif typ in ('minecraft:smelting','minecraft:blasting'):
        d['ingredient']=convert_ingredient(d['ingredient'])
        d['result']=convert_result(d['result'])
    elif typ=='minecraft:smithing_transform':
        for key in ('base','addition','template'):
            d[key]=convert_ingredient(d[key])
        d['result']=convert_result(d['result'])
    return d


def refs(d):
    result=[]
    def walk(x):
        if isinstance(x,dict):
            for k,v in x.items():
                if k in ('id','item','tag') and isinstance(v,str):
                    result.append(v)
                else: walk(v)
        elif isinstance(x,list):
            for y in x:walk(y)
    walk(d)
    return result


def main():
    p=argparse.ArgumentParser()
    p.add_argument('original_jar',type=Path)
    p.add_argument('output_dir',type=Path)
    a=p.parse_args()
    prefix='data/apotheotic_additions/recipes/'
    counts=Counter(); report=[]; seen=set()
    out=a.output_dir
    for part in ('STAGING_ONLY/vanilla','MANUAL_REVIEW'):
        (out/part).mkdir(parents=True,exist_ok=True)
    with ZipFile(a.original_jar) as z:
        names=sorted(x for x in z.namelist() if x.startswith(prefix) and x.endswith('.json'))
        for name in names:
            original=json.loads(z.read(name))
            relative=name[len(prefix):]
            record={'original':name,'type':original.get('type')}
            try:
                d=convert_recipe(original)
                dependencies=refs(d)
                if any(':' in v and v.split(':')[0] not in KNOWN_BASE_NS for v in dependencies):
                    raise ValueError('references external mod/tag that needs 1.21.1 ID verification')
                if d.get('neoforge:conditions'):
                    raise ValueError('optional mod or module condition; stage for manual review')
                assert 'result' in d
                if d['result']['id'] in ('apotheotic_additions:heirloom_material','apotheotic_additions:esoteric_material'):
                    raise ValueError('higher-tier material must not become a standard crafting output')
                path=out/'STAGING_ONLY'/'vanilla'/relative
                path.parent.mkdir(parents=True,exist_ok=True)
                path.write_text(json.dumps(d,ensure_ascii=False,indent=2)+'\n',encoding='utf8')
                record.update({'status':'MIGRATED_STAGING','output':str(path.relative_to(out))})
                counts['vanilla_staging']+=1
            except (KeyError,ValueError,AssertionError,TypeError) as exc:
                record.update({'status':'MANUAL_REVIEW','reason':str(exc)})
                counts['manual_review']+=1
            report.append(record)
            seen.add(name)
    counts['total_original_recipes']=len(seen)
    report_doc={'counts':dict(counts),'note':'Only STAGING; no files enabled at mod runtime until 1.21.1 dependency+datapack load tests.', 'recipes':report}
    (out/'migration_report.json').write_text(json.dumps(report_doc,indent=2,ensure_ascii=False)+'\n',encoding='utf8')
    print(json.dumps(counts,indent=2))
    assert counts['total_original_recipes']==229
    assert counts['vanilla_staging']+counts['manual_review']==229

if __name__=='__main__':main()
