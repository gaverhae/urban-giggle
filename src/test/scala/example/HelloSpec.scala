package example

import org.scalatest._

class Spec extends FlatSpec with Matchers {
  val i = Index.parse(
    Seq(
      RawDoc("Hamlet", "to be or not to be that is the question"),
      RawDoc("Not Hamlet", "no matching word")),
    Index.whitespaceSplitter,
    Index.identityToken)
  "Built index" should "return documents that match" in {
    i.scoreSimpleMatch("to be or not to be") should be(Seq(Result("Hamlet", 1.0)))
    i.scoreSimpleMatch("matching") should be(Seq(Result("Not Hamlet", 1.0)))
    i.scoreSimpleMatch("to matching").size should be(2)
  }
  "Index score" should "be 100 only when all words match" in {
    i.scoreSimpleMatch("to be or not to be and more") should be(Seq(Result("Hamlet", 2.0 / 3)))
  }
}
