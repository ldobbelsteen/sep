#!/usr/bin/env python
# coding: utf-8

# Script that computes the fan-out in a given module, then verifies whether this adheres to the code quality
# assessment criteria
# Author: Xander Smeets (1325523)
#
# Use: python understand_fan_out.py file_deps_csv.csv

import pandas as pd
import sys
import math

# Import data from earlier CI job
metrics = pd.read_csv(sys.argv[2])
# Only consider classes
metrics_classes = metrics[metrics['Kind'].str.contains('File', case=False)].reset_index()
allowed_to_fail = math.floor(metrics_classes.shape[0] * 0.03) # number of files that may fail code quality checks

# Define the threshold value
MAX_FAN_OUT = 16

# Import the dependencies CSV file
df = pd.read_csv(sys.argv[1])

# num_classes = df['From File'].nunique()
# allowed_to_fail = math.floor(num_classes * 0.03)

# Group the columns by the file from which the linking occurs, then sum the columns
grouped_by_from_file = df.groupby('From File').sum()

# Remove all files which adhere to the standards
fan_out_non_compliant = grouped_by_from_file[grouped_by_from_file['To Entities'] >= MAX_FAN_OUT]

# Store default exit code
exit_code = 0

# Print all non-compliant modules
fails = 0
for idx, row in fan_out_non_compliant.iterrows():
    print("File", str(idx), "has coupled with", str(row['To Entities']), "modules, which is more than the allowed",
          str(MAX_FAN_OUT-1) + ".")
    fails += 1
if (fails > allowed_to_fail):
    exit_code = 1


# Exit with the produced exit code
exit(exit_code)
