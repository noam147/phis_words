import re
import sqlite3
from typing import List, Tuple
import requests
from bs4 import BeautifulSoup

DB_FILE = "russian_words.db"
ORIGIN_NAME = "russian master"


def setup_database(conn: sqlite3.Connection) -> None:
    """Creates all required database tables if they do not exist."""

    # 1. Main Russian/English Words Table
    create_english_words_table = """
    CREATE TABLE IF NOT EXISTS englishWords (
        word_id INTEGER PRIMARY KEY AUTOINCREMENT,
        word TEXT NOT NULL,
        meaning TEXT NOT NULL,
        origin_place TEXT NOT NULL
    );
    """

    # 2. Hebrew Words Table
    create_hebrew_words_table = """
    CREATE TABLE IF NOT EXISTS hebrewWords (
        word_id INTEGER PRIMARY KEY AUTOINCREMENT,
        word TEXT NOT NULL,
        meaning TEXT NOT NULL,
        origin_place TEXT NOT NULL
    );
    """

    # 3. User Details / Learning Progress Table
    create_user_details_table = """
    CREATE TABLE IF NOT EXISTS user_details_on_words (
        word_id INTEGER PRIMARY KEY,
        word TEXT NOT NULL,
        amountOfStars INTEGER DEFAULT 0,
        knowledge_level TEXT DEFAULT '1',
        isWordMark INTEGER DEFAULT 0,
        FOREIGN KEY (word_id) REFERENCES englishWords (word_id)
    );
    """

    with conn:
        conn.execute(create_english_words_table)
        conn.execute(create_hebrew_words_table)
        conn.execute(create_user_details_table)
        print("Database schema initialized successfully.")

def clean_text(text: str) -> str:
    """Replaces non-breaking spaces, normalizes whitespace, and strips out parenthetical

    content like '(see above)' or '(see #2)'.
    """
    # Replace non-breaking space \xa0 with standard space
    text = text.replace("\xa0", " ")

    # Remove anything inside parentheses (including parentheses themselves)
    text = re.sub(r"\([^)]*\)", "", text)
    
    text = text.split(";")[0]

    # Collapse multiple spaces into one and trim edges
    return re.sub(r"\s+", " ", text).strip()
   

def fetch_and_parse_page(url: str) -> List[Tuple[str, str]]:
    """Fetches a single page URL and returns a list of (word, meaning) tuples.

    Handles both standard pages and Page 1 table structures automatically.
    """
    headers = {"User-Agent": "Mozilla/5.0"}

    try:
        response = requests.get(url, headers=headers, timeout=10)
        response.raise_for_status()
        response.encoding = response.apparent_encoding
    except requests.RequestException as e:
        print(f"Failed to fetch {url}: {e}")
        return []

    soup = BeautifulSoup(response.text, "html.parser")
    table = soup.find("table", class_="topwords")

    if not table:
        print(f"Could not find table 'topwords' on: {url}")
        return []

    extracted_data = []
    rows = table.find_all("tr", class_=["rowFirst", "rowSecond"])

    for row in rows:
        # Target the <td> with class "word" directly
        word_td = row.find("td", class_="word")

        if word_td:
            # The meaning is always the NEXT <td> after the word <td>
            meaning_td = word_td.find_next_sibling("td")

            if meaning_td:
                raw_word = word_td.get_text(strip=True)
                raw_meaning = meaning_td.get_text(strip=True)

                word = clean_text(raw_word)
                meaning = clean_text(raw_meaning)
                
                if word and meaning:
                    extracted_data.append((word, meaning))

    return extracted_data

def insert_words(
    conn: sqlite3.Connection,
    words_data: List[Tuple[str, str]],
    origin: str = ORIGIN_NAME,
) -> int:
    """Inserts a list of (word, meaning) pairs into englishWords and auto-populates

    user_details_on_words with the matching word_id and word.
    """
    if not words_data:
        return 0

    insert_english_query = """
    INSERT INTO englishWords (word, meaning, origin_place)
    VALUES (?, ?, ?)
    RETURNING word_id, word;
    """

    insert_user_details_query = """
    INSERT INTO user_details_on_words (word_id, word, amountOfStars, knowledge_level, isWordMark)
    VALUES (?, ?, 0, '1', 0);
    """

    records_to_insert = [(word, meaning, origin) for word, meaning in words_data]

    inserted_count = 0
    with conn:
        cursor = conn.cursor()

        # Insert words and capture auto-generated word_id along with the word
        user_details_records = []
        for record in records_to_insert:
            cursor.execute(insert_english_query, record)
            row = cursor.fetchone()
            if row:
                word_id, word = row
                user_details_records.append((word_id, word))

        # Insert matching records into user_details_on_words
        if user_details_records:
            cursor.executemany(insert_user_details_query, user_details_records)
            inserted_count = len(user_details_records)

    return inserted_count
    
    

def get_url_for_page(page_num: int) -> str:
    """Returns the correct URL based on page number."""
    if page_num == 1:
        return "https://masterrussian.com/vocabulary/most_common_words.htm"
    return (
        f"https://masterrussian.com/vocabulary/most_common_words_{page_num}.htm"
    )
    
def scrape_all_pages(
    conn: sqlite3.Connection, start_page: int = 1, end_page: int = 12
) -> int:
    """Iterates through specified pages (1 to 12), scrapes, and stores results."""
    total_inserted = 0

    for page_num in range(start_page, end_page + 1):
        url = get_url_for_page(page_num)
        print(f"Scraping page {page_num}: {url} ...")

        page_words = fetch_and_parse_page(url)
        inserted_count = insert_words(conn, page_words, origin=ORIGIN_NAME)

        total_inserted += inserted_count
        print(f"  -> Extracted & inserted {inserted_count} words.")

    return total_inserted


def main():
    # Connect to local SQLite database file
    conn = sqlite3.connect(DB_FILE)

    try:
        setup_database(conn)
        
        total = scrape_all_pages(conn, start_page=1, end_page=12)
        print(
            f"\nFinished! Total inserted records: {total} into '{DB_FILE}'."
        )
    finally:
        conn.close()


if __name__ == "__main__":
    main()