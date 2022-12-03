if [[ "$1" == "" ]]; then
  echo "$0 <version>"
  exit -1;
fi

mod_version="2.0.0-$1"
minecraft_supportd_versions="1.16.5 1.18.2 1.19.2"
minecraft_versions="${2:-$minecraft_supportd_versions}"

release_path="build/release/$mod_version"

mkdir -p "$release_path"
mkdir -p "$release_path/dev"
mkdir -p "$release_path/sources"

# build all versions
for minecraft_version in $minecraft_versions; do
	echo switch of $minecraft_version
	./gradlew -p versions/$minecraft_version
	echo build of $minecraft_version
	./gradlew build -Pversion=$1

	echo archive fabric of $minecraft_version
	mv "fabric/build/libs/armourersworkshop-fabric-$minecraft_version-$mod_version.jar" "$release_path"
	mv "fabric/build/libs/armourersworkshop-fabric-$minecraft_version-$mod_version-dev-shadow.jar" "$release_path/dev"
	mv "fabric/build/libs/armourersworkshop-fabric-$minecraft_version-$mod_version-sources.jar" "$release_path/sources"

	echo archive forge of $minecraft_version
	mv "forge/build/libs/armourersworkshop-forge-$minecraft_version-$mod_version.jar" "$release_path"
	mv "forge/build/libs/armourersworkshop-forge-$minecraft_version-$mod_version-dev-shadow.jar" "$release_path/dev"
	mv "forge/build/libs/armourersworkshop-forge-$minecraft_version-$mod_version-sources.jar" "$release_path/sources"
done
