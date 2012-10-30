TextDiff
========

A multi-threads version of TextDiff

```bash
#!/bin/bash

stotal=0
ptotal=0
file1="../1.txt"
file2="../2.txt"
for x in {1..5}
do
 echo "Running $file1 $file2"   
for i in {1..10}
do
        t0=`python -c 'import time; print repr(time.time()*1000000)'`
        java main $file1 $file2 s
        t1=`python -c 'import time; print repr(time.time()*1000000)'`

        t0=${t0/.*}
        t1=${t1/.*}
        stotal=$(($t1 - $t0))

        t0=`python -c 'import time; print repr(time.time()*1000000)'`
        java main $file1 $file2 p
        t1=`python -c 'import time; print repr(time.time()*1000000)'`
        t0=${t0/.*}
        t1=${t1/.*}
        ptotal=$(($t1 - $t0))

        if diff sequential parallel > /dev/null; then
        echo same
        else
        echo different
        fi
done
echo $stotal
echo $ptotal
echo "$(echo "scale=2; ($stotal - $ptotal) * 100 / $stotal" | bc)"
  if [ $x -eq 1 ]; then
	 file1="../file1_1M.txt"
	 file2="../file2_1M.txt"
	elif [ $x -eq 2 ]; then
	 file2="../file2_3M.txt"
	 file1="../file1_3M.txt"
	elif [ $x -eq 3 ]; then
	 file2="../file2_5M.txt"
	 file1="../file1_5M.txt"
	elif [ $x -eq 4 ]; then
          file2="../file2_10M.txt"
          file1="../file1_10M.txt"
	fi
done

```