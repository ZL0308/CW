#!/bin/bash

Input=$1
Output=$2

echo "Converting $Input -> $Output..."

echo "Timestamp,Latitude,Longitude,MinSeaLevelPressure,MaxIntensity" > $Output

cat $Input | grep -E 'UTC|N|mb|knots' | sed -E '/<(\/)?dtg|AL|TS|name|Name|Num|<TR>|TD/d' | sed -E 's/(]]>)|<(\/)?(B|td|tr|table)>//g'| paste -d ' ' - - - - | awk '{print $1, $2, $3, $4, ",", $5, $6, $7, $8, ",",$9, $10, "," $14, $15}' | sed 's/;/ /g' | sed -E 's/\s*,\s*/,/g' | sed 's/[[:space:]]*$//' >> $Output

echo "Done!"
