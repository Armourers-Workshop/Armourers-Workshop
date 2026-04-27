# release "mod_version" "minecraft_version"
set -e

mod_version=$1
minecraft_supported_versions="16.5 18.2 19.2 20.1 21.1 26.1"
minecraft_versions="${2:-minecraft_supported_versions}"
release_path="build/release/$mod_version"

if [[ "$1" == "" ]]; then
  for minecraft_version in $minecraft_versions; do
    ./gradlew --no-daemon -p versions/$minecraft_version
    ./gradlew --no-daemon build
  done
  exit 0
fi

mkdir -p "$release_path"
mkdir -p "$release_path/dev"
mkdir -p "$release_path/sources"

# build all versions
for minecraft_version in $minecraft_versions; do
  echo build of $minecraft_version
  ./gradlew --no-daemon -Pversion=$mod_version -p versions/$minecraft_version
  ./gradlew --no-daemon -Pversion=$mod_version build

  echo archive fabric of $minecraft_version
  mv "fabric/build/libs/armourersworkshop-fabric-$minecraft_version-$mod_version.jar" "$release_path"
  mv "fabric/build/libs/armourersworkshop-fabric-$minecraft_version-$mod_version-dev-shadow.jar" "$release_path/dev"
  mv "fabric/build/libs/armourersworkshop-fabric-$minecraft_version-$mod_version-sources.jar" "$release_path/sources"

  echo archive forge of $minecraft_version
  mv "forge/build/libs/armourersworkshop-forge-$minecraft_version-$mod_version.jar" "$release_path"
  mv "forge/build/libs/armourersworkshop-forge-$minecraft_version-$mod_version-dev-shadow.jar" "$release_path/dev"
  mv "forge/build/libs/armourersworkshop-forge-$minecraft_version-$mod_version-sources.jar" "$release_path/sources"
done
