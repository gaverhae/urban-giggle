package example

import scala.collection.immutable.Map
import scala.io.Source
import java.util.Scanner
import java.io.File

object Hello {
  def main(s: Array[String]): Unit = {
    if (s.length != 1
      || !(new File(s(0)).isDirectory)
      || !(new File(s(0)).listFiles().exists(_.isFile))) {
      throw new IllegalArgumentException("Please specify a single directory with at least one text file in it.")
    }
    val index = Index.parse(
      files(s(0)),
      Index.whitespaceSplitter,
      Index.identityToken)
    val keyb = new Scanner(System.in)
    println()
    while (true) {
      print("search> ")
      val line = keyb.nextLine()
      if (line == ":quit") {
        println("Bye!")
        System.exit(0)
      }
      displayResults(index.scoreSimpleMatch(line).take(10))
    }
  }
  def files(root: String): Iterable[RawDoc] = {
    new File(root)
      .listFiles()
      .filter(_.isFile)
      .map((f) => {
        val buf = Source.fromFile(f)
        val s = buf.getLines.mkString
        buf.close
        RawDoc(f.getName, s)
      })
  }
  def displayResults(rs: Iterable[Result]): Unit = {
    for (r <- rs) {
      println(f"${r.name}%s: ${r.score * 100}%5.2f%%")
    }
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
  def scoreSimpleMatch(s: String): Iterable[Result] = {
    val unitM = m.mapValues((docs) => docs.mapValues((i) => 1))
    score(unitM, s)
  }
  def scoreWithCount(s: String): Iterable[Result] = {
    score(m, s)
  }
  def score(m: Map[String, Map[String, Int]], s: String): Iterable[Result] = {
    val words = parser(s).toSet
    words
      .toSeq
      .flatMap((w) => {
        val docs = m.getOrElse(w, Map()).map { case (n, s) => (n, s) }
        val max = docs.map(_._2).foldLeft(0)(Math.max(_, _))
        docs.map((d) => (d._1, d._2.toDouble / (max * words.size)))
      })
      .foldLeft(Map[String, Double]())((m, d) => {
        val prev: Double = m.getOrElse(d._1, 0)
        m.updated(d._1, prev + d._2)
      })
      .map { case (n, s) => Result(n, s) }
      .toSeq
      .sortBy(_.score)
      .reverse
  }
}
