#!/bin/bash
LIB="../../lib"

javac -cp .:$LIB/core.jar:$LIB/vecmath.jar:$LIB/objimport.jar:$LIB/BSim.jar:$LIB/bsim-osp.jar:$LIB/core.jar:$LIB/jcommander-1.49-SNAPSHOT.jar GeneticAlgorithm.java
java  -cp ..:$LIB/core.jar:$LIB/vecmath.jar:$LIB/objimport.jar:$LIB/BSim.jar:$LIB/bsim-osp.jar:$LIB/core.jar:$LIB/jcommander-1.49-SNAPSHOT.jar GeneticAlgorithm.GeneticAlgorithm
rm *.class
