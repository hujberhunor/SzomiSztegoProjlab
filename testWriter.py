#!/usr/bin/env python3
"""
Test Case Creator for Dino Project

This script creates test cases for the Dino Java project by setting up the directory
structure and creating the necessary files (test JSON, input commands, and expected output).

Usage:
    python create_test.py create <test_number> <test_name> <json_content> <be_content> <ki_content>
    python create_test.py from_file <config_file>

Example:
    python create_test.py create 11 "Insect Movement and Cutting" test11.json be.txt ki.txt
    python create_test.py from_file test11_config.json
"""

import os
import sys
import json
import argparse

# Base directory for tests
BASE_DIR = "src/main/java/com/dino/tests"

def create_test(test_number, test_name, json_content, be_content, ki_content):
    """Create a test case with the given parameters."""
    # Create test directory
    test_dir = os.path.join(BASE_DIR, f"test{test_number}")
    os.makedirs(test_dir, exist_ok=True)

    # Write test JSON file
    json_file = os.path.join(test_dir, f"test{test_number}.json")
    with open(json_file, 'w') as f:
        f.write(json_content)

    # Write input commands file
    be_file = os.path.join(test_dir, "be.txt")
    with open(be_file, 'w') as f:
        f.write(be_content)

    # Write expected output file
    ki_file = os.path.join(test_dir, "ki.txt")
    with open(ki_file, 'w') as f:
        f.write(ki_content)

    print(f"Created test {test_number}: {test_name}")
    print(f"Files created in {test_dir}:")
    print(f"  - test{test_number}.json")
    print(f"  - be.txt")
    print(f"  - ki.txt")

    # Generate TestRunner method code
    method_name = convert_to_camel_case(test_name)
    test_method = f"""
public static void test{method_name}() {{
    System.out.println("Running {test_name} test...");
    boolean result = TestOracle.runTest({test_number});
    System.out.println("{test_name} Test Result: " + (result ? "PASSED" : "FAILED"));
}}
"""

    print("\nAdd this method to your TestRunner.java class:")
    print(test_method)
    print(f"And add {test_number} to your runAllTests() method list.")

def create_test_from_file(config_file):
    """Create a test case from a configuration file."""
    with open(config_file, 'r') as f:
        config = json.load(f)

    # Get config values
    test_number = config["test_number"]
    test_name = config["test_name"]

    # Get file contents - either directly or from files
    if "json_content" in config:
        json_content = config["json_content"]
    elif "json_file" in config:
        with open(config["json_file"], 'r') as f:
            json_content = f.read()
    else:
        raise ValueError("Missing json_content or json_file in config")

    if "be_content" in config:
        be_content = config["be_content"]
    elif "be_file" in config:
        with open(config["be_file"], 'r') as f:
            be_content = f.read()
    else:
        raise ValueError("Missing be_content or be_file in config")

    if "ki_content" in config:
        ki_content = config["ki_content"]
    elif "ki_file" in config:
        with open(config["ki_file"], 'r') as f:
            ki_content = f.read()
    else:
        raise ValueError("Missing ki_content or ki_file in config")

    create_test(test_number, test_name, json_content, be_content, ki_content)

def convert_to_camel_case(text):
    """Convert a text to CamelCase by capitalizing each word and removing spaces."""
    return ''.join(word.capitalize() for word in text.split())

def main():
    parser = argparse.ArgumentParser(description="Create test cases for the Dino Java project")
    subparsers = parser.add_subparsers(dest="command", help="Commands")

    # Command to create test directly
    create_parser = subparsers.add_parser("create", help="Create a test with inline content")
    create_parser.add_argument("test_number", type=int, help="The test number")
    create_parser.add_argument("test_name", help="The name of the test")
    create_parser.add_argument("json_file", help="File containing the test JSON content")
    create_parser.add_argument("be_file", help="File containing the input commands")
    create_parser.add_argument("ki_file", help="File containing the expected output")

    # Command to create test from config file
    file_parser = subparsers.add_parser("from_file", help="Create a test from a config file")
    file_parser.add_argument("config_file", help="JSON configuration file")

    args = parser.parse_args()

    if args.command == "create":
        # Read content from files
        with open(args.json_file, 'r') as f:
            json_content = f.read()

        with open(args.be_file, 'r') as f:
            be_content = f.read()

        with open(args.ki_file, 'r') as f:
            ki_content = f.read()

        create_test(args.test_number, args.test_name, json_content, be_content, ki_content)

    elif args.command == "from_file":
        create_test_from_file(args.config_file)

    else:
        parser.print_help()

if __name__ == "__main__":
    main()
