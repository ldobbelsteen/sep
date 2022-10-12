#!/usr/bin/env python
# coding: utf-8

# Script that checks whether a given CSV file contains diagonal elements
# Author: Xander Smeets (1325523)
#
# Use: python understand_cyclic.py file_deps_matrix.csv

from tabnanny import filename_only
import pandas as pd
import sys
import os
import math

classes_in_cycle = set()

# Assert that the second input is either true or false:
if not (sys.argv[2] == 'true' or sys.argv[2] == 'false'):
    exit(2)

# Import data from earlier CI job
metrics = pd.read_csv(sys.argv[3])
# Only consider classes
if sys.argv[2] == 'true':
    metrics_classes = metrics[metrics['Kind'].str.contains('Class', case=False)].reset_index()
else:
    metrics_classes = metrics[metrics['Kind'].str.contains('File', case=False)].reset_index()


allowed_to_fail = math.floor(metrics_classes.shape[0] * 0.03)

# if sys.argv[2] == 'true':
if 'true' == 'true':
    file_org = open(sys.argv[1], "r")
    file_name_csv = os.path.dirname(os.path.abspath(file_org.name)) + "/file_deps_matrix_new.csv"
    file_out = open(file_name_csv, "w")

    for line in file_org:
        file_out.write(line.replace('"', ''))
    
    file_out.close()
else:
    file_name_csv = sys.argv[1]

df = pd.read_csv(file_name_csv, index_col='Dependent File')

# By default, assume there are no files with cyclic dependencies.
exit_code = 0

# Store a list of cyclic dependencies found so far, so we only show the dependency in one direction
cyclic_found = []

# Loop over all rows & columns, as long as the row exists in the columns and the column exists in the rows
classes = set(df.index.values).intersection(df.columns)

for row in classes:
    for column in classes:
        if not (pd.isnull(df.loc[row, column]) or pd.isnull(df.loc[column, row])):
            # Check whether we already found this dependency
            if not {'row': column, 'column': row} in cyclic_found:
                print("File", row, "is in a cyclic dependency with", column)
                # Add this dependency to the list of found dependencies, to prevent duplicate output
                cyclic_found.append({'row': row, 'column': column})
                # Add the classes in the cycle to the set of classes in a cycle
                classes_in_cycle.add(row)
                classes_in_cycle.add(column)

if len(classes_in_cycle) > allowed_to_fail:
    # Update the exit code, because we have errors
    exit_code = 1

print(str(len(classes_in_cycle)) + " classes are involved in a cycle. The maximum is " + str(allowed_to_fail) + ".")
# Exit with the exit code produced by the script
exit(exit_code)
