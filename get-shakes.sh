#!/usr/bin/env bash

set -o nounset
set -o errexit

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null && pwd )"

cd $DIR

mkdir -p files
cd files
curl -s http://www.gutenberg.org/ebooks/1112.txt.utf-8 > romeo-and-juliet.txt
curl -s http://www.gutenberg.org/files/1524/1524-0.txt > hamlet.txt
curl -s http://www.gutenberg.org/files/23042/23042-0.txt > tempest.txt
curl -s http://www.gutenberg.org/ebooks/2264.txt.utf-8 > macbeth.txt
