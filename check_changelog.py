#!/usr/bin/env python3
"""Rebuild changelog.md from versioned release notes and compare byte-for-byte.

Run from the repository root, e.g.:
    python3 check_changelog.py src/main/resources/changelog.md

By default, reads src/main/resources/release_notes/<major>.<minor>.md
and writes changelog.rebuilt.md (never overwrites the original).
"""

import argparse
from pathlib import Path
import re
import sys

VERSION_FILENAME = re.compile(r"(\d+)\.(\d+)\.md")


def main() -> int:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("original", type=Path, help="Path to the original changelog.md")
    parser.add_argument(
        "--notes-dir", type=Path,
        default=Path("src/main/resources/release_notes"),
        help="Directory containing versioned Markdown files",
    )
    parser.add_argument(
        "--output", type=Path, default=Path("changelog.rebuilt.md"),
        help="Where to write the rebuilt changelog (default: changelog.rebuilt.md)",
    )
    args = parser.parse_args()

    if not args.notes_dir.is_dir():
        parser.error(f"Release notes directory not found: {args.notes_dir}")
    if not args.original.is_file():
        parser.error(f"Original changelog not found: {args.original}")
    if args.original.resolve() == args.output.resolve():
        parser.error("Output must not overwrite the original changelog")

    # Sort numerically: 10.0 before 9.2, and 1.400 before 1.399.
    versions = []
    for path in args.notes_dir.iterdir():
        if path.is_file() and path.suffix.lower() == ".md":
            match = VERSION_FILENAME.fullmatch(path.name)
            if match is None:
                parser.error(f"Unexpected Markdown filename: {path.name}")
            versions.append(((int(match[1]), int(match[2])), path))
    if not versions:
        parser.error(f"No version files found in: {args.notes_dir}")
    versions.sort(key=lambda item: item[0], reverse=True)

    # No reformatting, newline conversion, trimming or added separators.
    generated = b"".join(path.read_bytes() for _, path in versions)
    args.output.parent.mkdir(parents=True, exist_ok=True)
    args.output.write_bytes(generated)
    original = args.original.read_bytes()

    print(f"Joined {len(versions)} versions: {versions[0][1].name} .. {versions[-1][1].name}")
    print(f"Generated: {args.output} ({len(generated)} bytes)")
    print(f"Original:  {args.original} ({len(original)} bytes)")

    if original == generated:
        print("OK: Byte-for-byte identical.")
        return 0

    first = next((i for i, (a, b) in enumerate(zip(original, generated)) if a != b),
                 min(len(original), len(generated)))
    print(f"DIFFERENT: First difference at byte offset {first} (zero-based).")
    print(f"  Original around offset:  {original[max(first - 25, 0):first + 35]!r}")
    print(f"  Generated around offset: {generated[max(first - 25, 0):first + 35]!r}")
    print("To inspect all differences, run:")
    print(f"  git diff --no-index -- {args.original} {args.output}")
    return 1


if __name__ == "__main__":
    sys.exit(main())
