"""Check the compiled 26.2 callback that crashed during AW child-block removal."""
import re
import subprocess
import sys

classpath = sys.argv[1]
result = subprocess.run(['javap', '-classpath', classpath, '-c', '-p',
    'moe.plushie.armourers_workshop.compat.core.block.AbstractBlockImpl'],
    check=True, text=True, capture_output=True)
match = re.search(r'protected void affectNeighborsAfterRemoval\([^\n]+\);\s+Code:\n(.*?)(?=\n  (?:protected|public|private))', result.stdout, re.S)
assert match, '26.2 removal callback missing'
body = match.group(1)
assert 'getBlockState:' in body, 'FAIL: removal callback supplies null replacement state'
assert re.search(r'getBlockState:.*?\n\s+\d+: iload\s+4[ \t]*\n\s+\d+: aconst_null[ \t]*\n\s+\d+: invokevirtual.*?onRemove:', body, re.S), 'FAIL: replacement state/piston flag not forwarded to onRemove'
print('PASS: compiled removal callback resolves replacement state and forwards movedByPiston')
