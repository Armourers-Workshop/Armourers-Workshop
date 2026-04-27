# setup "v3.2.1" "16.5"
set -e

release_version="${1:-2.0.0-homebaked}"
minecraft_versions="${2:-21.1}"

# generate game version.
cat "versions/$minecraft_versions/gradle.properties" \
  | sed -n 's/^minecraft_api_version=//p' \
  | sed 's/^/game_version=/'

# generate build version.
echo "$release_version" \
  | cut -d'v' -f2 \
  | sed 's/^/release_version=/'

# generate primary cache key.
find versions -maxdepth 2 -type f -name gradle.properties -print0 \
  | xargs -0 sed -n '/^[^_]*_api_version=/p' \
  | sort \
  | sha256sum \
  | cut -d' ' -f1 \
  | sed 's/^/cache_key=build-cache-/'

# generate primary cache paths.
echo "cache_path<<EOF"
echo ".gradle"
echo "~/.gradle/caches"
echo "~/.gradle/wrapper"
echo "EOF"
