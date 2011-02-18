#!/bin/bash

CP=conf:.
for jar in lib/*.jar ; do
  CP=$CP:$jar
done

java -cp $CP net.jakobnielsen.aptivator.cli.AptivatorCli $1