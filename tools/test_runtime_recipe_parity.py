"""Verify active vanilla recipes preserve every original 2.2.2 ingredient & output.
   A temporary migration that changes the ingredient is never silently accepted.
"""
import zipfile,json,pathlib,sys
ROOT=pathlib.Path(__file__).resolve().parents[1]
MOVED_ITEM_IDS = {
    'apotheosis:ancient_material': 'apotheotic_additions:ancient_material',
    'apotheosis:beeshelf': 'apothic_enchanting:beeshelf',
    'apotheosis:blazing_hellshelf': 'apothic_enchanting:blazing_hellshelf',
    'apotheosis:echoing_sculkshelf': 'apothic_enchanting:echoing_sculkshelf',
    'apotheosis:heart_seashelf': 'apothic_enchanting:heart_seashelf',
}
original=pathlib.Path(sys.argv[1]) if len(sys.argv)>1 else ROOT.parent.parent.parent/'ApotheoticAdditionsV2.2.2.jar'
with zipfile.ZipFile(original) as z:
    base='data/apotheotic_additions/recipes/'
    count=0
    for f in sorted((ROOT/'src/main/resources/data/apotheotic_additions/recipe').rglob('*.json')):
        rel=f.relative_to(ROOT/'src/main/resources/data/apotheotic_additions/recipe').as_posix()
        data=json.loads(f.read_text(encoding='utf-8'))
        actual_name=base+rel
        if actual_name not in z.namelist():
            raise AssertionError('Recipe not present in original: '+rel)
        orig=json.loads(z.read(actual_name))
        wanted=dict(orig); want=dict(data)
        # Old Forge enchantment-module conditions are obsolete on NeoForge+ApothicEnchanting.
        wanted.pop('conditions',None)
        want.pop('conditions',None)
        wanted.pop('group',None); want.pop('group',None)
        if 'result' in wanted:
            r=wanted['result'];
            if isinstance(r,dict) and 'item' in r:
                wanted['result']={'id':r['item'],**({'count':r['count']} if 'count' in r else {})}
            elif isinstance(r,str): wanted['result']={'id':r}
        if 'count' in wanted and isinstance(wanted.get('result'),dict):
            wanted['result']['count']=wanted.pop('count')
        if 'result' in want and isinstance(want['result'],dict) and want['result'].get('count')==1:
            want['result']=dict(want['result']);want['result'].pop('count')
        if 'result' in wanted and isinstance(wanted['result'],dict) and wanted['result'].get('count')==1:
            wanted['result']=dict(wanted['result']);wanted['result'].pop('count')
        # Minecraft 1.21.1 Ingredient codecs still require item/tag objects.
        # Do not compact these to strings: that format fails runtime decoding.
        def migrate_removed_ids(value):
            if isinstance(value, dict):
                return {k: migrate_removed_ids(v) for k, v in value.items()}
            if isinstance(value, list):
                return [migrate_removed_ids(v) for v in value]
            if isinstance(value, str):
                return MOVED_ITEM_IDS.get(value, value)
            return value
        wanted = migrate_removed_ids(wanted)
        if wanted!=want:
            diffs={k:(wanted.get(k),want.get(k)) for k in set(wanted)|set(want) if wanted.get(k)!=want.get(k)}
            raise AssertionError(rel+' '+str(diffs)[:400])
        count+=1
    if count!=26:raise AssertionError(f'Expected 26 vanilla recipes, got {count}')
    print(f'PASS: {count} active vanilla recipes correspond to original entries')
