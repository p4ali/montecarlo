#!/usr/bin/env bash

ext=${1:-pdf}
terminal=
output=

# We only plot the first N trials for better visual result
N=10

portfolios=( "aggressive" "conservative" )

plot () {

  if [[ $ext == "pdf" ]]; then
    terminal="postscript"
    output="|ps2pdf - output/$1.$ext"
  elif [[ $ext == "png" ]]; then
    terminal="png size 800,400"
    output="output/$1.$ext"
  else
    echo "Wrong ext: "+$ext
    exit 1
  fi

  echo "ext=$ext"
  echo "terminal=$terminal"
  echo "output=$output"

  gnuplot <<EOF
  set terminal $terminal
  set output '$output'

  set xlabel 'Year'
  set ylabel 'FutureValue% = (1+ReturnRate)/(1+Inflation)'

  #plot for [i=1:20] 'output/$1.dat' using 1:i+1 with linespoints title 'Trial '.i
  plot for [i=1:$N] 'output/$1.dat' using 1:i+1 with linespoints title ''
EOF

  open output/$1.$ext
}

for p in "${portfolios[@]}"
do
  plot $p
done


