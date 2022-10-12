import io
import sys
import logging

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


def read_line():
    # Read a line from the file
    global line
    line = input_file.readline()[0:-1].lstrip(' ')  # spaces at the beginning of the string are removed


# Read the first line
read_line()

# Javadoc files should start with an HTML doctype declaration
if line.lower() != "<!DOCTYPE html>".lower():
    raise Exception("First line of file " + input_filename + " does not declare HTML doctype. Is this valid Javadoc?")

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
    test_items = None
    input_specs = None
    output_specs = None
    env_needs = None
    is_test = False
    while line != "</section>":  # i.e. while we have not arrived at the end of the method
        read_line()
        if "@Test" in line:
            is_test = True
        if line == "<dt>Unit test description:</dt>":
            read_line()  # the next line contains the actual description
            description = line[4:-5]  # removes <dd> from begin and </dd> from end
        if line == "<dt>Test items:</dt>":
            read_line()  # the next line contains the actual test items
            test_items = line[4:-5]  # removes <dd> from begin and </dd> from end
        if line == "<dt>Input specifications:</dt>":
            read_line()  # the next line contains the actual input specifications
            input_specs = line[4:-5]  # removes <dd> from begin and </dd> from end
        if line == "<dt>Output specifications:</dt>":
            read_line()  # the next line contains the actual output specifications
            output_specs = line[4:-5]  # removes <dd> from begin and </dd> from end
        if line == "<dt>Environmental needs:</dt>":
            read_line()  # the next line contains the actual environmental needs
            env_needs = line[4:-5]  # removes <dd> from begin and </dd> from end

    # Write local exit code: if any part is missing, do not write this test to output
    local_exit_code = 0

    # Check whether test case has a description
    try:
        if description is None:
            raise Exception("Test case " + method_name + "() does not have a description.")
    except Exception as e:
        if not is_test:
            logger.error(str(e))
        local_exit_code = 1

    # Check whether test case has test items
    try:
        if not is_test:
            raise Exception("Test case " + method_name + "() does not have test items.")
    except Exception as e:
        if not is_test:
            logger.error(str(e))
        local_exit_code = 1

    # Check whether test case has input specifications
    try:
        if not is_test:
            raise Exception("Test case " + method_name + "() does not have input specifications.")
    except Exception as e:
        if not is_test:
            logger.error(str(e))
        local_exit_code = 1

    # Check whether test case has output specifications
    try:
        if not is_test:
            raise Exception("Test case " + method_name + "() does not have output specifications.")
    except Exception as e:
        if not is_test:
            logger.error(str(e))
        local_exit_code = 1

    # Check whether test case has environmental needs
    try:
        if not is_test:
            raise Exception("Test case " + method_name + "() does not have environmental needs.")
    except Exception as e:
        if not is_test:
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
        test_items = replace_code_tag(test_items)
        input_specs = replace_code_tag(input_specs)
        output_specs = replace_code_tag(output_specs)
        env_needs = replace_code_tag(env_needs)
        output_file.write("\\possibleSectionHeader\\noindent\n")
        output_file.write("\\begin{tabular}{p{4.5cm}p{9.5cm}}\n")
        output_file.write("\\testIdentifier & \\java{" + method_name + "()}\\\\\n")
        output_file.write("\\hline\n")
        output_file.write("\\multicolumn{2}{p{13.7cm}}{" + description + "} \\\\\n")
        output_file.write("\\hdashline\n")
        output_file.write("\\textbf{Test items:} & " + test_items + "\\\\\n")
        output_file.write("\\textbf{Input specifications:} & " + input_specs + "\\\\\n")
        output_file.write("\\textbf{Output specifications:} & " + output_specs + "\\\\\n")
        output_file.write("\\textbf{Environmental needs:} & " + env_needs + "\\\\\n")
        output_file.write("\\hline\n")
        output_file.write("\\end{tabular}\n")
        output_file.write("\\newline\\newline\\newline\n")
        print("Finished writing test case " + method_name + "() to output file.")
    else:
        if not is_test:
            logger.error("Missing information for test case " + method_name + "(). Skipping writing of information.")
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

# Close files
input_file.close()
output_file.close()
exit(exit_code)
