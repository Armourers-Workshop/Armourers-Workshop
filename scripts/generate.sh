# generate
set -e

version_dirs=$(find versions -maxdepth 1 -type d -name '[0-9]*.[0-9]*' -print | sort)

for version_dir in $version_dirs; do
  ./gradlew --no-daemon -p "$version_dir"
  ./gradlew --no-daemon configureLaunch
done
