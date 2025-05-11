import sqlite3
import threading
import time

DB_PATH = 'words.db'  # Replace with your actual database path
DAILY_WORD = ('hello', 'שלום')  # Default word

"""

Table: hebrewWords
  - word (TEXT)
  - meaning (TEXT)
  - word_id (INTEGER)
  - origin_place (TEXT)

Table: sqlite_sequence
  - name ()
  - seq ()

Table: englishWords
  - word (TEXT)
  - meaning (TEXT)
  - word_id (INTEGER)
  - origin_place (TEXT)

Table: user_details_on_words
  - word (TEXT)
  - word_id (INTEGER)
  - amountOfStars (INTEGER)
  - knowledge_level (TEXT)
  - isWordMark (BOOL)
  """
def open_db():
    return sqlite3.connect(DB_PATH)


def exec_command(command: str, args_for_command: tuple = ()) -> list:
    """
    Executes an SQLite command.
    Returns a list of rows for SELECT, or an empty list for others.
    """
    result = []
    try:
        conn = open_db()
        cursor = conn.cursor()
        cursor.execute(command, args_for_command)

        if command.strip().upper().startswith("SELECT"):
            result = cursor.fetchall()
        else:
            conn.commit()
            result = []

        cursor.close()
        conn.close()
    except sqlite3.Error as e:
        print(f"SQLite error: {e}")
        return []

    return result


def fetch_random_word():
    command = "SELECT word,meaning FROM englishWords ORDER BY RANDOM() LIMIT 1"
    result = exec_command(command)
    return result[0] if result else None


def update_daily_word():
    global DAILY_WORD
    while True:
        word = fetch_random_word()
        if word:
            DAILY_WORD = word
        #else we keep it as is
        time.sleep(10)
        #time.sleep(24*60*60)#24H

def at_start():
    t = threading.Thread(target=update_daily_word)
    t.daemon = True
    t.start()


