set key outside top right
set key autotitle columnheader
set terminal pngcairo size 1024,768 enhanced font 'Verdana,10'
set output 'graph.png'
set xlabel 'Date'
set ylabel 'Deaths'
set title 'Cumulative Deaths'

set xdata time
set timefmt "%d/%m/%Y"
set format x "%Y-%m"
set xtics 60*60*24*30*2
