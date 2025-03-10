import re
from collections import defaultdict

def parse_markdown_table(table: str):
    time_data = defaultdict(float)
    lines = table.strip().split("\n")
    
    for line in lines[2:]:  # Skip header and separator
        parts = line.split("|")
        if len(parts) < 4:
            continue
        time_str = parts[2].strip()
        names = parts[3].strip().replace("<br><br>", ", ").replace(" ", ", ")
        
        # Convert time to float
        time_match = re.match(r"(\d+\.?\d*)", time_str.replace(",", "."))
        if time_match:
            time_value = float(time_match.group(1))
        else:
            continue
        
        # Process names
        for name in names.split(","):
            name = name.strip()
            if name:
                time_data[name] += time_value
    
    return time_data

# Example input
markdown_table = """
| 2025.02.24 21:30 | 1 óra | Hujber | Tevékenység: Class diagram tervezése |
| :---- | :---- | :---- | :---- |
| 2025.02.25 20:30 | 1 óra | Hujber Vidra Kővári Szabó Kovács | **Értekezlet**. **Döntések**: Feladatkiosztás, class nevek eldöntése és az alapvető felépítése |
| 2025.02.26 10:40 | 0,5 óra | Hujber Vidra Kővári Szabó Kovács | **Értekezlet**. **Döntések**: Class diagram további felépítése |
| 2025.02.26 19:00 | 1,5 óra | Hujber | **Tevékenység**: Class diagram  |
| 2025.02.26 19:50 | 1 óra | Kovács | **Tevékenység**: Osztályok leírása |
| 2025.02.26 20:00 | 2 óra | Vidra | **Tevékenység**: Class diagram, objektum katalógus |
| 2025.02.27 10:50 | 4,25 óra | Kovács | **Tevékenység**: Osztályok leírásának folytatása |
| 2025.02.27 14:00 | 2 óra | Hujber | **Tevékenység**: Class diagram újrakezdése, csontváz felépítése  |
| 2025.02.28 10:30 | 0,5 óra | Vidra | **Tevékenység**: Naplózás |
| 2025.02.28 16:30 | 3 óra | Hujber Vidra Kővári Szabó Kovács | **Értekezlet. Döntések:** Játéklogika részletei, class diagram felépítése, egyes játéklogikák működése |
| 2025.03.01 12:45 | 2,5 óra | Kovács | **Tevékenység:** Osztályok leírása |
| 2025.03.01 15:55 | 3,5 óra | Kovács | **Tevékenység:** Osztályok leírása |
| 2025.03.01 22:00 | 0,5 óra | Vidra | **Tevékenység:** Objektum katalógus, osztályok leírásának formázása, naplózás |
| 2025.03.01  | 3,25 óra | Szabó | **Tevékenység:** Szekvencia diagrammok készítése |
| 2025.03.02 10:30 | 2 óra | Szabó | **Tevékenység:** Szekvencia folytatása |
| 2025.03.02 13:00 | 1,75 óra | Hujber Vidra Kővári Szabó Kovács | **Értekezlet:** Class diagram átfutása. **Döntések:** Metódus logikák finomítása, szekvencia diagramok |
| 2025.03.02 18:00 | 1 óra | Hujber | **Tevékenység:**␋Class diagram utómunkák, dokumentum formázása, egyéb kis javítások |"""

time_summary = parse_markdown_table(markdown_table)

# Calculate total time
total_time = sum(time_summary.values())

# Calculate percentage for each person
time_percentage = {name: (time / total_time) * 100 for name, time in time_summary.items()}

# Create a formatted output
summary_output = {name: {"hours": time, "percentage": f"{percentage:.2f}%"} for name, (time, percentage) in zip(time_summary.keys(), time_percentage.items())}

import pandas as pd
df = pd.DataFrame.from_dict(summary_output, orient='index')

# Display the table
import ace_tools as tools
tools.display_dataframe_to_user(name="Time Summary", dataframe=df)

