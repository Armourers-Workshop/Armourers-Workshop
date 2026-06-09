# setup "v3.2.1" "16.5"
set -e

release_version="${1:-2.0.0-homebaked}"
minecraft_versions="${2:-..}"

# calculate versions.
version="$(echo "$release_version" | cut -d'v' -f2)"
game_version="$(cat "versions/$minecraft_versions/gradle.properties" | sed -n 's/^minecraft_api_version=//p')"

forge_platform="$(cat "versions/$minecraft_versions/forge/gradle.properties" | sed -n 's/^loom\.platform=//p')"
fabric_platform="$(cat "versions/$minecraft_versions/fabric/gradle.properties" | sed -n 's/^loom\.platform=//p')"

cache_dependencies="$(find versions -maxdepth 2 -type f -name gradle.properties -print0 | xargs -0 sed -n '/^[^_]*_api_version=/p' | sort)"

# generate build outputs.
echo "version=$version"
echo "game_version=$game_version"

# generate archives outputs.
echo "forge_archives_loader=$forge_platform"
echo "fabric_archives_loader=$fabric_platform"
echo "forge_archives_name=armourersworkshop-forge-$game_version-$version"
echo "fabric_archives_name=armourersworkshop-fabric-$game_version-$version"
echo "forge_archives_version=$version+$game_version-$forge_platform"
echo "fabric_archives_version=$version+$game_version-$fabric_platform"

# generate primary cache key.
echo "cache_key=build-cache-$(echo "$cache_dependencies" | sha256sum | cut -d' ' -f1)"

# generate primary cache paths.
echo "cache_path<<EOF"
echo ".gradle"
echo "~/.gradle/caches"
echo "~/.gradle/wrapper"
echo "EOF"
