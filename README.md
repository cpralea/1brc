My attempt at solving the [1 Billion Row Challenge](https://1brc.dev/).

At the time of implementation (Dec 2025) and on the hardware used for development (AMD Ryzen AI 9 HX 370, 96GB 5600MT/s), the solution is about 4+ times slower than [the top contest entry](https://github.com/gunnarmorling/1brc/blob/main/src/main/java/dev/morling/onebrc/CalculateAverage_serkan_ozal.java) relying on OpenJDK.

Build and run:
```
$ ./gradlew build
$ time java -jar app/build/libs/app-all.jar -p Advanced -o measurements.out measurements.txt
real    0m5.798s
user    2m9.923s
sys     0m0.913s
```
