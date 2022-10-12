import io
import sys
import logging
import os

# Boolean which stores whether this file is empty; if so, delete it at the end
should_delete_file = True

# First argument: file in which to look for test case info
input_filename = sys.argv[1]

# Second argument: file in which to store output
# This should probably be based on the name of the input file
output_filename = sys.argv[2]

# Check whether input and output files were specified
assert input_filename is not None
assert output_filename is not None

# Variable to store whether any check has failed
exit_code = 0

# Set up logger
logger = logging.getLogger('Javadoc')

# Open the files
input_file = io.open(input_filename, "r", encoding="utf-8")
output_file = io.open(output_filename, "a")

# Open template files
# Based on https://www.geeksforgeeks.org/python-append-content-of-one-text-file-to-another/
BEGIN_FILE = io.open(os.getenv('CI_PROJECT_DIR') + '/ci/UTP_simple_begin.tex', "r", encoding="utf-8")
END_FILE   = io.open(os.getenv('CI_PROJECT_DIR') + '/ci/UTP_simple_end.tex', "r", encoding="utf-8")

def read_line():
    # Read a line from the file
    global line
    line = input_file.readline()[0:-1].lstrip(' ')  # spaces at the beginning of the string are removed


# Read the first line
read_line()

# Javadoc files should start with an HTML doctype declaration
if line.lower() != "<!DOCTYPE html>".lower():
    raise Exception("First line of file " + input_filename + " does not declare HTML doctype. Is this valid Javadoc?")

# Get the test class name
while "<title>" not in line:
    read_line()

# Now, the test class title is in the line. Extract it.
class_name = line.replace("<title>", "").split()[0]
output_file.write("\\subsection{"+ class_name +"}\n")

# Print the longtable template after the section header
output_file.write(BEGIN_FILE.read())

# We should first skip to the methods section.
while line != "<h2>Method Details</h2>":
    read_line()

    # Check whether we reached EOF
    if (not line) or len(line) == 0:
        break
    else:
        pass


def process_method(method_name):
    # Check whether input is correct
    global line
    assert method_name is not None
    description = None
    is_test = False
    while line != "</section>":  # i.e. while we have not arrived at the end of the method
        read_line()
        if "@Test" in line:
            is_test = True
        if line == "<dt>Unit test description:</dt>":
            # the next lines contains the actual description
            at_utp_description = ""
            read_line() # read the first line of the actual description
            while not "</dd>" in line:
                at_utp_description += " " + line
                read_line()
            # Add the final line's contents (i.e. the one with </dd>
            at_utp_description += " " + line

            description = at_utp_description.replace("<dd>", "").replace("</dd>", "")

    # Write local exit code: if any part is missing, do not write this test to output
    local_exit_code = 0

    # Not test parts should not be marked as missing description as they should not be in the UTP anyways
    if not is_test:
        return

    # Check whether test case has a description
    try:
        if description is None:
            raise Exception("Test case " + input_filename[:-4] + method_name + "() does not have a description.")
    except Exception as e:
        logger.error(str(e))
        local_exit_code = 1

    # Start printing to file
    if local_exit_code == 0:
        global output_file
        # Requires the following additional commands to be defined in LaTeX source file:
        # - \possibleSectionHeader (can contain a counter and emit a new section for certain values of that counter)
        # - \testIdentifier        (command which prints a test identifier, like the requirements IDs in the URD)
        # - \java                  (for code highlighting; can be defined as \newcommand{\java}{\mintinline{java}{#1}})

        # Replace @code tags in Javadoc
        def replace_code_tag(string):
            return string.replace("<code>", "\java{").replace("</code>", "}")
        description = replace_code_tag(description)


        output_file.write("\\hline\n")
        output_file.write("\\testIdentifier & \\java{" + method_name + "()} & " + description + "\\\\\n")
        print("Finished writing test case " + method_name + "() to output file.")
        global should_delete_file
        should_delete_file = False
    else:
        logger.error("Missing information for test case " + input_filename[:-4] + method_name + "(). Skipping writing of information.")
        global exit_code
        exit_code = 1


# Go over all methods
while True:
    if line[0:4] == "<h3>":
        # We have arrived at a method header
        # Thus, we extract the method name
        method = line[4:-5]  # removes <h3> from begin and </h3> from end
        process_method(method_name=method)

    # This is not the line we are looking for; skip to the next line
    read_line()

    # Check whether we reached EOF
    if (not line) or len(line) == 0 or line[0:5] == "</ul>":
        break
    else:
        pass

# Print the end of the longtable template at the end of the file
output_file.write(END_FILE.read())

# Close files
input_file.close()
output_file.close()
BEGIN_FILE.close()
END_FILE.close()

# Delete the file if it would produce an empty table
if should_delete_file:
    os.remove(input_filename)

# Indicate the exit code to Bash
exit(exit_code)
