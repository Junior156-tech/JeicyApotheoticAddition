#!/usr/bin/env python3
"""Checks that NOT_RUNTIME drafts aren't accidentally placed into runtime."""
import pathlib,json,sys
root=pathlib.Path(__file__).resolve().parents[1]
paths=list((root/'src/main/resources').rglob('*.json'))
for f in paths:
 obj=json.loads(f.read_text(encoding='utf-8'))
 if isinstance(obj,dict):
  bad=[x for x in ('_migration_notes','__unported_min_rarity','_original_rules_unported') if x in obj]
  if bad:raise ValueError(f'{f}: draft keys present {bad}')
print('OK runtime JSON count:',len(paths),'(does not confirm Minecraft loading)')
