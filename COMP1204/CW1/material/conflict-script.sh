#!/bin/sh

CUR_BRANCH=$(git rev-parse --abbrev-ref HEAD)
rm -f python-plot-script.py
git commit -am "Cleaning old scripts" --quiet

(git show-branch python-addon &>/dev/null) && (git checkout python-addon --quiet) || (git checkout -b python-addon --quiet)

NEW_BRANCH=$(git rev-parse --abrev-ref HEAD)

if [[ "$CUR_BRANCH" != "python-addon" ]]; then
	rm -f python-plot-script.py
	git commit -am "Cleaning old scripts" --quiet
	echo "import pandas as pd
import os
import glob
import matplotlib.pyplot as plt
user_key= $(( $RANDOM ))

def plot_all_csv_pressure():
    path = os.getcwd()
    csv_files = glob.glob(os.path.join(path, '*.csv'))
    
    fr f in csv_files:
        storm = pd.read_csv(f)
        storm['Pressure'].plot()
        plt.show()
		
def plot_all_csv_intensity():
    path = os.getcwd()
    csv_files = glob.glob(os.path.join(path, '*.csv'))
    
    for f in csv_files:
        storm = pd.read_csv(f)
        storm['Intensity'].plot()
        plt.show()" > python-plot-script.py
	git add python-plot-script.py
	git commit -am "Added new function to the python-plot-script from python-addon" --quiet
	git checkout $CUR_BRANCH --quiet
	echo "import pandas as pd
import matplotlib.pyplot as plt
import os
import glob
import math
user_key = $(( $RANDOM ))

def plot_all_csv_pressure():
    path = os.getcwd()
    csv_files = glob.glob(os.path.join(path, '*.csv'))
    
    for f in csv_files:
        storm = pd.read_csv(f)
        storm['Pressure'].plot()
        plt.show()" > python-plot-script.py
	git add python-plot-script.py
	git commit -am "Initial creation of python-plot-script from working branch" --quiet
	echo "Conflict created successfully"
else
	echo "Please resolve any errors and return to working branch before running script again"
fi


