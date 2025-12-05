package moe.plushie.armourers_workshop.core.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Pattern;

/**
 * A loose version similar to Maven/Gradle version ordering.
 * Supports arbitrary version formats such as:
 * 1.0.0, 1.0, 1.0.0.0, 1, v1.2.3, 2025.01.12, 1.0.0-alpha-1, SNAPSHOT, release-1.0.0
 * <p>
 * Comparison rules (similar to Maven):
 * - Number segments compare numerically.
 * - Non-numeric segments compare lexically.
 * - Numeric < non-numeric.
 */
public class Version implements Comparable<Version> {

    protected final Segment[] segments;
    protected final String value;

    public Version(String value, Segment[] segments) {
        this.value = value;
        this.segments = segments;
    }

    public static Version parse(String version) {
        var segments = Segment.semver(version);
        if (segments == null) {
            segments = Segment.loose(version);
        }
        return new Version(version, segments.toArray(new Segment[0]));
    }

    @Override
    public int compareTo(Version that) {
        var len = Math.max(size(), that.size());
        for (var i = 0; i < len; ++i) {
            var result = get(i).compareTo(that.get(i));
            if (result != 0) {
                return result;
            }
        }
        return 0;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Version that)) return false;
        return value.equals(that.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public String toString() {
        return value;
    }

    public int size() {
        return segments.length;
    }

    public Segment get(int index) {
        if (index < segments.length) {
            return segments[index];
        }
        return Segment.EMPTY;
    }

    public static class Segment implements Comparable<Segment> {

        private static final Segment EMPTY = new Segment(null);

        /**
         * A regular expression for validating strict Semantic Versioning 2.0.0 strings.
         * <p>
         * This pattern strictly follows the official <a href="https://semver.org/">SemVer 2.0.0 specification</a>.
         * <p>
         * Valid examples (these will match):
         * - "1.0.0"
         * - "2.1.3"
         * - "0.9.1"
         * - "1.0.0-alpha"
         * - "1.0.0-alpha.1"
         * - "1.0.0-rc.1"
         * - "1.0.0+build.123"
         * - "1.0.0-alpha+exp.sha.5114f85"
         * <p>
         * Invalid examples (these will NOT match):
         * - "1.0"                 (missing patch)
         * - "1.0.0.0"             (extra numeric segment)
         * - "v1.0.0"              (prefix 'v' not allowed)
         * - "01.2.3"              (leading zero not allowed)
         * - "1.02.3"              (leading zero not allowed)
         * - "1.0.0-"              (dangling hyphen)
         * - "1.0.0+build.."       (empty metadata segment)
         * - "1..0.0"              (empty numeric segment)
         */
        private static final Pattern SEMVER = Pattern.compile("^[vV]?(0|[1-9]\\d*)\\.(0|[1-9]\\d*)\\.(0|[1-9]\\d*)(?:-((?:0|[1-9]\\d*|\\d*[a-zA-Z-][0-9a-zA-Z-]*)(?:\\.(?:0|[1-9]\\d*|\\d*[a-zA-Z-][0-9a-zA-Z-]*))*))?(?:\\+([0-9a-zA-Z-]+(?:\\.[0-9a-zA-Z-]+)*))?$");

        private static final String[] PREFIXES = {"release", "rel", "version", "ver", "v", "r"};
        private static final HashMap<String, Integer> WEIGHTS = new HashMap<>();

        private final Object value;

        public Segment(Object result) {
            this.value = result;
        }

        public static ArrayList<Segment> loose(String version) {
            // split version into segment by delimiters: [.-_+]
            var segments = new ArrayList<Segment>();
            for (var part : strip(version).split("[._\\-+]+")) {
                if (part.isEmpty()) {
                    segments.add(Segment.EMPTY);
                } else if (part.matches("\\d+")) {
                    segments.add(new Segment(Long.parseLong(part)));
                } else {
                    segments.add(new Segment(part));
                }
            }
            return segments;
        }

        public static ArrayList<Segment> semver(String version) {
            // is a standard semver?
            var matcher = SEMVER.matcher(version);
            if (!matcher.matches()) {
                return null;
            }
            var segments = new ArrayList<Segment>();
            // major.minor.patch
            segments.add(new Segment(Long.parseLong(matcher.group(1)))); // major
            segments.add(new Segment(Long.parseLong(matcher.group(2)))); // minor
            segments.add(new Segment(Long.parseLong(matcher.group(3)))); // patch
            // -prerelease.x.y.z
            var pre = matcher.group(4);
            if (pre != null) {
                for (var part : pre.split("\\.")) { //
                    if (part.matches("\\d+")) {
                        segments.add(new Segment(Long.parseLong(part)));
                    } else {
                        segments.add(new Segment(part));
                    }
                }
            }
            // +build.x.y.z
            return segments;
        }


        /**
         * Strips common prefixes from version string.
         */
        private static String strip(String version) {
            var lower = version.toLowerCase();
            for (var prefix : PREFIXES) {
                if (lower.startsWith(prefix)) {
                    var index = prefix.length();
                    if (lower.startsWith("-", index)) {
                        index += 1;
                    }
                    if (index < version.length() && Character.isDigit(version.charAt(index))) {
                        return version.substring(index);
                    }
                }
            }
            return version;
        }

        @Override
        public int compareTo(Segment that) {
            // -1: this < that, 0: this == that, 1: this > that
            if (value == null && that.value == null) {
                return 0;
            }
            // number < string < empty
            if (value instanceof Long lhs) {
                if (that.value instanceof Long rhs) {
                    return lhs.compareTo(rhs); // long(this) < long(that)
                }
                if (that.value instanceof String) {
                    return -1; // long(this) < string(that)
                }
                return 1; // long(this) > empty(that)
            }
            //  number < string < empty
            if (value instanceof String lhs) {
                if (that.value instanceof String rhs) {
                    var lw = WEIGHTS.get(lhs.toLowerCase());
                    var rw = WEIGHTS.get(rhs.toLowerCase());
                    if (lw != null) {
                        if (rw != null) {
                            return lw.compareTo(rw); // widget(this) < widget(that)
                        }
                        return -1; // widget(this) < non-widget(that)
                    }
                    if (rw != null) {
                        return 1; // non-widget(this) > widget(that)
                    }
                    return lhs.compareToIgnoreCase(rhs); // string(this) < string(that)
                }
                if (that.value instanceof Long) {
                    return 1; // string(this) > long(that)
                }
                return -1; // string(this) < empty(that)
            }
            // empty(this) > number(that) | string(that)
            return 1;
        }

        static {
            WEIGHTS.put("alpha", 1);
            WEIGHTS.put("a", 1);

            WEIGHTS.put("beta", 2);
            WEIGHTS.put("b", 2);

            WEIGHTS.put("milestone", 3);
            WEIGHTS.put("m", 3);

            WEIGHTS.put("rc", 4);
            WEIGHTS.put("cr", 4);

            WEIGHTS.put("snapshot", 5);
            WEIGHTS.put("dev", 6);

            WEIGHTS.put("preview", 7);
            WEIGHTS.put("pre", 7);
        }
    }
}
