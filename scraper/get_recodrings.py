import os
import re
import sqlite3
import time
from typing import List, Tuple
from gtts import gTTS

DB_FILE = "russian_words.db"
AUDIO_DIR = "audio"


def sanitize_filename(text: str) -> str:
    """Removes non-alphanumeric characters to ensure cross-platform safe filenames."""
    # Keeps letters, numbers, spaces, and underscores
    sanitized = re.sub(r"[^\w\s-]", "", text)
    return re.sub(r"\s+", "_", sanitized).strip("_")


def ensure_audio_directory(directory: str) -> None:
    """Creates the target audio folder if it does not already exist."""
    os.makedirs(directory, exist_ok=True)


def ensure_audio_column(conn: sqlite3.Connection) -> None:
    """Adds an 'audio_path' column to englishWords if it doesn't already exist."""
    with conn:
        cursor = conn.cursor()
        cursor.execute("PRAGMA table_info(englishWords);")
        columns = [row[1] for row in cursor.fetchall()]

        if "audio_path" not in columns:
            cursor.execute(
                "ALTER TABLE englishWords ADD COLUMN audio_path TEXT;"
            )


def fetch_all_words(conn: sqlite3.Connection) -> List[Tuple[int, str]]:
    """Retrieves all word_ids and Russian words from the database."""
    cursor = conn.cursor()
    cursor.execute("SELECT word_id, word FROM englishWords;")
    return cursor.fetchall()


def generate_single_audio(
    word: str, target_path: str, lang: str = "ru"
) -> bool:
    """Generates an MP3 file using Google Text-to-Speech for a given word.

    Returns True on success, False on failure.
    """
    try:
        tts = gTTS(text=word, lang=lang)
        tts.save(target_path)
        return True
    except Exception as e:
        print(f"  [ERROR] Failed TTS generation for '{word}': {e}")
        return False


def update_word_audio_path(
    conn: sqlite3.Connection, word_id: int, audio_path: str
) -> None:
    """Updates the database record with the generated audio file path."""
    query = "UPDATE englishWords SET audio_path = ? WHERE word_id = ?;"
    with conn:
        conn.execute(query, (audio_path, word_id))


def process_word_audio(
    conn: sqlite3.Connection,
    word_id: int,
    word: str,
    output_dir: str = AUDIO_DIR,
) -> str:
    """Handles audio check, generation, and database linking for a single word.

    Returns:
        'downloaded' if newly fetched, 'skipped' if already exists, or 'failed'.
    """
    safe_word = sanitize_filename(word)
    filename = f"{word_id}_{safe_word}.mp3"
    filepath = os.path.join(output_dir, filename)

    # 1. Skip downloading if file exists locally
    if os.path.exists(filepath):
        update_word_audio_path(conn, word_id, filepath)
        return "skipped"

    # 2. Synthesize audio
    success = generate_single_audio(word, filepath)

    # 3. Update database record if successful
    if success:
        update_word_audio_path(conn, word_id, filepath)
        return "downloaded"

    return "failed"


def download_all_word_recordings(
    conn: sqlite3.Connection, delay_seconds: float = 0.25
) -> None:
    """Iterates through all words, downloads audio files, and updates database references."""
    words = fetch_all_words(conn)
    total_words = len(words)

    print(f"Found {total_words} words to process.\n")

    stats = {"downloaded": 0, "skipped": 0, "failed": 0}

    for idx, (word_id, word) in enumerate(words, start=1):
        status = process_word_audio(conn, word_id, word)
        stats[status] += 1

        if status == "downloaded":
            print(
                f"[{idx}/{total_words}] Downloaded audio for word_id={word_id} ('{word}')"
            )
            # Gentle delay to respect rate limits
            time.sleep(delay_seconds)
        elif status == "skipped":
            print(
                f"[{idx}/{total_words}] Skipped existing file for word_id={word_id} ('{word}')"
            )

    print("\n" + "=" * 40)
    print("AUDIO GENERATION SUMMARY")
    print("=" * 40)
    print(f"New Downloads : {stats['downloaded']}")
    print(f"Already Existed: {stats['skipped']}")
    print(f"Failed        : {stats['failed']}")


def main():
    if not os.path.exists(DB_FILE):
        print(
            f"Error: Database file '{DB_FILE}' not found. Please run your scraper first."
        )
        return

    ensure_audio_directory(AUDIO_DIR)

    conn = sqlite3.connect(DB_FILE)
    try:
        ensure_audio_column(conn)
        download_all_word_recordings(conn)
    finally:
        conn.close()


if __name__ == "__main__":
    main()