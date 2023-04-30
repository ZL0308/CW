#!/bin/bash

# Create an empty array to store the temporary files
declare -a data

# Create a command which will be excuted by the gnuPlot
plot_command="plot "

# Fetch the top 10 death country and iterate them, for each loop, grab the date, sum of the death and geoId from the table, and put them into a temp file after processing
# Append the gnuPlot command
for geoId in $(sqlite3 coronavirus.db "SELECT geoId FROM CountriesAndTerritories JOIN CasesAndDeaths CAD on CountriesAndTerritories.ID = CAD.geo_ID GROUP BY geoId ORDER BY SUM(deaths) DESC LIMIT 10;")
do
    Deaths=$(mktemp)
    sqlite3 coronavirus.db "SELECT dateRep, SUM(deaths) OVER (ORDER BY year, month, day) AS cumulativeDeaths, '$geoId' as country FROM CasesAndDeaths JOIN Date D ON D.ID = CasesAndDeaths.data_ID JOIN CountriesAndTerritories CAT ON CAT.ID = CasesAndDeaths.geo_ID WHERE geoId = '$geoId' ORDER BY year, month, day;" | tr '|' ' ' > "$Deaths"
    plot_command+=" '$Deaths' using 1:2 with lines title columnheader(3),"
    data+=("$Deaths") # Add the temporary file to the array, and delete later
done

# Remove the final comma from the gnuPlot
plot_command="${plot_command%,}"

# Run the gnuPlot script by using a heredoc.
# Set the key's position on the top right of picture, out of the graph.
# Set the size and the font of the picture.
# Set the output file
# Set the X,Y label and the title
# Set the x data type as time
# Set the time format which is the same format as dateRep
# Set the display format
# Set the distance between two major tick marks
# Rotate the x coordinates
gnuplot << EOF
set key outside top right
set key autotitle columnheader
set terminal pngcairo size 1000,750 enhanced font 'Verdana,10'
set output 'graph.png'
set xlabel 'Date'
set ylabel 'Deaths'
set title 'Cumulative Deaths'
set xdata time
set timefmt "%d/%m/%Y"
set format x "%Y-%m-%d"
set xtics 60*60*24*30*2
set xtics mirror rotate by -60
$plot_command
EOF

# Delete all the temporary files by using a loop
for data in "${data[@]}"; do
    rm "$data"
done
