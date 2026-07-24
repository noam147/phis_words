import os
import re

AUDIO_DIR = "audio"


def rename_files_to_id_only(directory: str = AUDIO_DIR) -> None:
    """Renames files formatted like '{id}_{word}.mp3' to '{id}.mp3' in the specified directory."""
    if not os.path.exists(directory):
        print(f"Error: Directory '{directory}' does not exist.")
        return

    files = os.listdir(directory)
    renamed_count = 0
    skipped_count = 0

    print(f"Processing {len(files)} files in '{directory}'...\n")

    for filename in files:
        # Match filenames starting with digits, followed by an underscore or rest of name
        # Examples: "51_знать.mp3", "52_мой.mp3"
        match = re.match(r"^(\d+)(_.*)?\.mp3$", filename, re.IGNORECASE)

        if match:
            file_id = match.group(1)
            new_filename = f"{file_id}.mp3"

            old_path = os.path.join(directory, filename)
            new_path = os.path.join(directory, new_filename)

            # Skip if it's already named purely 'n.mp3'
            if filename == new_filename:
                skipped_count += 1
                continue

            # Check if destination file already exists to prevent accidental overwrites
            if os.path.exists(new_path):
                print(
                    f"[SKIP] Target file '{new_filename}' already exists! (Leaving '{filename}')"
                )
                skipped_count += 1
                continue

            # Rename file
            os.rename(old_path, new_path)
            renamed_count += 1
            print(f"Renamed: '{filename}' -> '{new_filename}'")
        else:
            skipped_count += 1

    print("\n" + "=" * 40)
    print("RENAMING SUMMARY")
    print("=" * 40)
    print(f"Renamed : {renamed_count}")
    print(f"Skipped : {skipped_count}")


if __name__ == "__main__":
    rename_files_to_id_only()