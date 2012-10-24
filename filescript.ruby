ruby -e 'a=STDIN.readlines;100.times do;b=[];4.times do; b << a[rand(a.size)].chomp end; puts b.join(" "); end' < /usr/share/dict/words > file.txt
