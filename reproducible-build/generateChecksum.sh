#!/bin/bash

hash_filename="checksum.md"
hash_path="target"
echo '| Artefact | Version | Checksum |' > "$hash_path/$hash_filename"
echo '|----------|---------|----------|' >> "$hash_path/$hash_filename"

for file in target/verifier-assembly-*.zip; do
  if [[ -f "$file" ]]; then
    filename=$(basename "$file")
    version=$1
    checksum=$(sha256sum "$file" | awk '{print $1}')
    echo "| **$filename** | $version | $checksum |" >> "$hash_path/$hash_filename"
  fi
done