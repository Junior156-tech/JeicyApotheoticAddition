from pathlib import Path
import json
root=Path(__file__).resolve().parent
for folder,count in [("affixes",263),("gems",67),("recipes",229)]:
 files=list((root/"migration_draft_NOT_RUNTIME/data/apotheotic_additions"/folder).rglob('*.json'))
 assert len(files)==count,(folder,len(files))
 for f in files:json.loads(f.read_text(encoding='utf-8'))
 print("OK",folder,len(files))
print("Los JSON se leen; esto NO prueba compatibilidad con los codecs de Apotheosis.")
