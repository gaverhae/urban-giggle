package example

import scala.collection.immutable.Map

object Hello {
  def main(s: Array[String]): Unit = {
    if (s.length != 1) {
      throw new IllegalArgumentException("Please specify a single directory.")
    }
    println("hello")
  }
}

case class RawDoc(name: String, content: String)
case class Result(name: String, score: Double)

object Index {
  def parse(s: Iterable[RawDoc], splitter: (String) => Iterable[String], tokenizer: (String) => String): Index = {
    var m = Map[String, Map[String, Int]]()
    for (
      RawDoc(name, content) <- s;
      word <- splitter(content).map(tokenizer)
    ) {
      val prev = m.getOrElse(word, Map[String, Int]())
      m = m.updated(word, prev.updated(name, prev.getOrElse(name, 0) + 1))
    }
    new Index(m, (s) => splitter(s).map(tokenizer))
  }
  def whitespaceSplitter(s: String): Iterable[String] = {
    s.split(" ")
  }
  def identityToken(s: String): String = {
    s
  }
}

class Index(m: Map[String, Map[String, Int]], parser: (String) => Iterable[String]) {
  def score(s: String): Iterable[Result] = {
    val results = parser(s)
      .flatMap(m.getOrElse(_, Map()).map { case (n, s) => (n, s) })
      .foldLeft(Map[String, Int]()) { case (m, (n, s)) => m.updated(n, m.getOrElse(n, 0) + s) }
      .map { case (n, s) => (n, s) }
    val total = results.foldLeft(0)(_ + _._2)
    results
      .map { case (n, s) => Result(n, s / total) }
      .toSeq
      .sortBy(_.score)
      .reverse
  }
}
