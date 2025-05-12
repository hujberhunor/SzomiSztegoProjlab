
import os
import re

def count_comments_and_code(file_path):
    with open(file_path, 'r', encoding='utf-8') as file:
        lines = file.readlines()

    code_lines = 0
    comment_lines = 0

    in_block_comment = False
    for line in lines:
        line = line.strip()
        
        # Többsoros Javadoc kommentek kezelése
        if in_block_comment:
            comment_lines += 1
            if '*/' in line:
                in_block_comment = False
            continue
        if '/**' in line:
            comment_lines += 1
            if '*/' in line:
                continue
            in_block_comment = True
            continue
        
        # Egy soros Javadoc kommentek kezelése (///)
        elif line.startswith('///'):
            comment_lines += 1
        # Egy soros kommentek kezelése (//)
        elif line.startswith('//'):
            comment_lines += 1
        elif line:
            code_lines += 1
    
    return code_lines, comment_lines

def analyze_directory(directory_path):
    total_code_lines = 0
    total_comment_lines = 0
    java_files = 0

    # Átvizsgáljuk a könyvtárat és almappáit
    for root, _, files in os.walk(directory_path):
        for file in files:
            if file.endswith('.java'):
                java_files += 1
                file_path = os.path.join(root, file)
                code_lines, comment_lines = count_comments_and_code(file_path)
                total_code_lines += code_lines
                total_comment_lines += comment_lines

    if java_files == 0:
        print("Nincs .java fájl a megadott könyvtárban.")
    else:
        total_lines = total_code_lines + total_comment_lines
        comment_percentage = (total_comment_lines / total_lines) * 100
        code_percentage = (total_code_lines / total_lines) * 100

        print(f"Összes fájl: {java_files}")
        print(f"Kód sorok: {total_code_lines}")
        print(f"Komment sorok: {total_comment_lines}")
        print(f"Kommentek aránya: {comment_percentage:.2f}%")
        print(f"Kód aránya: {code_percentage:.2f}%")

# A kívánt könyvtár elérési útja
directory_path = input("Adja meg a könyvtár elérési útját: ")
analyze_directory(directory_path)
