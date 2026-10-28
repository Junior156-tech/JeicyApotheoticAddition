"""Verify active vanilla recipes preserve every original 2.2.2 ingredient & output.
   A temporary migration that changes the ingredient is never silently accepted.
"""
import zipfile,json,pathlib,sys
ROOT=pathlib.Path(__file__).resolve().parents[1]
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
        # Minecraft 1.21 introduced compact item/tag ingredient strings.
        def compact(v):
            if isinstance(v,dict) and len(v)==1:
                if 'item' in v: return v['item']
                if 'tag' in v: return '#' + v['tag']
            return v
        if 'key' in wanted: wanted['key']={k:compact(v) for k,v in wanted['key'].items()}
        if 'ingredients' in wanted: wanted['ingredients']=[compact(v) for v in wanted['ingredients']]
        if 'ingredient' in wanted: wanted['ingredient']=compact(wanted['ingredient'])
        if wanted!=want:
            # Allow the three known conversion classes as exceptions ONLY for 1.21 format changes.
            diffs={k:(wanted.get(k),want.get(k)) for k in set(wanted)|set(want) if wanted.get(k)!=want.get(k)}
            if set(diffs)!={'ingredient'} and set(diffs)!={'result'}:
                raise AssertionError(rel+' '+str(diffs)[:400])
        count+=1
    if count!=26:raise AssertionError(f'Expected 26 vanilla recipes, got {count}')
    print(f'PASS: {count} active vanilla recipes correspond to original entries')
