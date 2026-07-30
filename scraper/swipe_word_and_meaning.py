import sys
import pyperclip

output = []

for line in sys.stdin:
    line = line.rstrip()
    if not line:
        output.append("")
        continue

    parts = line.split(maxsplit=1)
    if len(parts) == 2:
        output.append(f"{parts[1]}\t{parts[0]}")
    else:
        output.append(line)

result = "\n".join(output)

pyperclip.copy(result)
print("Copied to clipboard:")
print(result)