import sqlite3

conn = sqlite3.connect("russian_words.db")
cursor = conn.cursor()

# Get total count
cursor.execute("SELECT COUNT(*) FROM englishWords")
print(f"Total rows: {cursor.fetchone()[0]}\n")

# Preview first 10 rows
cursor.execute("SELECT * FROM englishWords")
for row in cursor.fetchall():
    print(row)


# Preview first 10 rows
cursor.execute("SELECT * FROM user_details_on_words LIMIT 10")
for row in cursor.fetchall():
    print(row)

conn.close()