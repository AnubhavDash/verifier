#!/bin/bash
#
# (c) Copyright 2025 Swiss Post Ltd.
#

hash_filename="${project.artifactId}_${project.version}_checksum.md"
hash_path="target/reproducible-build"

echo '| Artefact | Version | Checksum |' > "$hash_path/$hash_filename"
echo '|----------|---------|----------|' >> "$hash_path/$hash_filename"

for file in ./verifier-assembly/target/verifier-assembly-*.zip; do
  if [[ -f "$file" ]]; then
    filename=$(basename "$file")
    checksum=$(sha256sum "$file" | awk '{print $1}')
    echo "| **$filename** | ${project.version} | $checksum |" >> "$hash_path/$hash_filename"
  fi
done
