#!/usr/bin/env python3
"""Overlay the reviewed 26.2 Vulkan port classes without losing earlier migration work."""
import argparse
import collections
import hashlib
import json
from pathlib import Path
import zipfile

parser = argparse.ArgumentParser()
parser.add_argument('base', type=Path)
parser.add_argument('classes', type=Path)
parser.add_argument('output', type=Path)
args = parser.parse_args()
if args.base.resolve() == args.output.resolve():
    parser.error('output must differ from base; installation requires an atomic replacement')
classes = {p.relative_to(args.classes).as_posix(): p.read_bytes() for p in args.classes.rglob('*.class')}
if not classes:
    parser.error('compiled port classes are missing')
roots = {name.split('$', 1)[0].removesuffix('.class') for name in classes}
roots.add('com/apple/library/impl/ClipManagerImpl')
fixed = []
with zipfile.ZipFile(args.base) as source, zipfile.ZipFile(args.output, 'w', zipfile.ZIP_DEFLATED) as result:
    for item in source.infolist():
        name = item.filename
        root = name.split('$', 1)[0].removesuffix('.class')
        if name.endswith('.class') and root in roots:
            continue
        if name == "assets/minecraft/atlases/items.json":
            continue
        data = source.read(name)
        if name == 'assets/minecraft/atlases/gui.json':
            atlas = json.loads(data)
            for entry in atlas.get('sources', []):
                if entry.get('source') == 'item/slot':
                    entry['prefix'] = 'slot/'
            data = (json.dumps(atlas, indent=2) + '\n').encode()
        if name == 'assets/armourers_workshop/models/block/skin-cube-marker.json':
            model = json.loads(data)
            model['textures']['particle'] = model['textures']['marker']
            data = (json.dumps(model, indent=2) + '\n').encode()
        if name == 'assets/armourers_workshop/models/block/outfit-maker.json':
            model = json.loads(data)
            for element in model.get('elements', []):
                faces = element.get('faces', {})
                valid = collections.Counter(f['texture'] for f in faces.values() if f.get('texture', '').startswith('#') and f['texture'][1:] in model['textures'])
                for face in faces.values():
                    if face.get('texture') == '#missing':
                        # Box22 has no valid faces; its wooden tabletop backing uses the particle/plank slot.
                        face['texture'] = valid.most_common(1)[0][0] if valid else '#4'
                        fixed.append(face['texture'])
            data = (json.dumps(model, ensure_ascii=False, indent=2) + '\n').encode()
        result.writestr(item, data)
    atlas_name = 'assets/minecraft/atlases/items.json'
    atlas = json.loads(source.read(atlas_name)) if atlas_name in source.namelist() else {'sources': []}
    entry = {'type': 'single', 'resource': 'armourers_workshop:entity/mannequin'}
    if entry not in atlas['sources']:
        atlas['sources'].append(entry)
    # Written once, avoiding duplicate zip entries on repeat overlays.
    result.writestr(atlas_name, json.dumps(atlas, indent=2) + '\n')
    for name, data in sorted(classes.items()):
        result.writestr(name, data)
with zipfile.ZipFile(args.output) as result:
    if result.testzip() is not None:
        raise ValueError('output CRC failure')
    for name in result.namelist():
        if name.endswith('.class') and name.split('$', 1)[0].removesuffix('.class') in roots:
            data = result.read(name)
            if b'org/lwjgl/opengl' in data:
                raise ValueError('OpenGL reference in migrated class: ' + name)
print(f'Port classes: {len(classes)}; fixed outfit-maker faces: {len(fixed)}')
print(hashlib.sha256(args.output.read_bytes()).hexdigest(), args.output)
