# Text search

## Setup

To seed the program with a few text files, use:

```
$ bash get-shakes.sh
```

This will create a `files` folder with a few Shakespeare plays.

Run the program with:

```
sbt "run files"
```

Run the tests with:

```
sbt test
```

To compile a distributable jar:

```
sbt assembly
```

then run it with:

```
java -jar target/scala-2.12/schibsted-assembly-app.jar files
```

## Search algorithm

This search implementation has two main ideas:

* The input text is processed by going through some transformations, and the
  user input goes through the same transformation,
* The program keeps an in-memory table of term to document.

### Transformations

The transformation the input goes through are separated in two functions that
are taken as input to the algorithm; there are two examples for each of those
functions in the code:

* A `splitter` function takes the entire text of the document as a single
  `String` and outputa a list of smaller `String`s. This is typically where we
  define what is a space or a punctuation character. Current examples split on
  either the space character or any character that is not a letter.
* A `tokenizer` function that processes each word to turn it into a token.
  Examples include an identity function and one that turns everything
  lowercase.

More complex behaviour can easily be plugged: the splitter could remove
stopwords, and the tokenizer could turn words into their stem.

The reason for having two functions here is to support combinations of these
behaviour; it may be that as more variants are added it would make sense to add
a third step in-between the two existing ones for filtering out some words. A
more general approach would be to define the "transform" step as a list of
functions that will be applied in order, where each function takes in a single
`String` and return a list of `String`s. Having the code chain these functions
through `flatMap` would then give maximum flexibility (each function can add or
remove items) and composability (those functions could easily be applied in any
combination of order).

### Search index and scoring

The search algorithm I have chosen to implement is a fairly simple one. First,
let's assume we have a transformation from one string to a list of tokens, as
explained in the previous section. That function is applied to each indexed
document, and will also be applied to the input the user gives for each search.

The rest of this discussion will take place in the space defined by the image
of that function; elements of that space will be referred to as tokens.

When indexing a document, this algorithm builds an in-memory map of token to
document to dount of occurrences. The requirements for scoring unfortunately
make the count difficult to use, but the idea is that it should be useful in
sorting documents.

Ingesting all documents would result in a (sparse) in-memory matrix looking
like:

```
          doc1     doc2     doc3 ...
word1        0        2        0
word2        3        0       16
...
```

When searching for a single token, we get the line for that token directly by
supplying that token as a key to the map. The line is represented as a nested
map of document name to count.

When searching for multiple tokens, the algorithm I have chosen to fit the
constraints (100% when all words, 0% when no word, less than 100% if not all
words) is to simply count the number of matching tokens compared to the number
of (unique) input tokens: if the user has input 5 words and the document
matches 3 of them, it gets a score of 60%.

The code also has support for a ranking method that would allow to compare
documents in a more granular fashion, where the score is computed relative to
how many times the word appears in other documents too; this means the more the
given words appear in the document, the higher it will score. However, this
does not respect the constraint that if a document matches all words it should
have a perfect score.
