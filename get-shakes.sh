#!/usr/bin/env bash

set -o nounset
set -o errexit

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null && pwd )"

cd $DIR

mkdir -p files
cd files
curl -s http://www.gutenberg.org/cache/epub/1112/pg1112.txt > romeo-and-juliet.txt
curl -s http://www.gutenberg.org/files/1524/1524-0.txt > hamlet.txt
curl -s http://www.gutenberg.org/files/23042/23042-0.txt > tempest.txt
curl -s http://www.gutenberg.org/cache/epub/2264/pg2264.txt > macbeth.txt
