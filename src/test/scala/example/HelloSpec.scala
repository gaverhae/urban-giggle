package example

import org.scalatest._

class Spec extends FlatSpec with Matchers {
  "Built index" should "return documents that match" in {
    val i = Index.parse(
      Seq(
        RawDoc("Hamlet", "to be or not to be that is the question"),
        RawDoc("Not Hamlet", "no matching word")),
      Index.whitespaceSplitter,
      Index.identityToken)
    i.score("to be or not to be") shouldEqual Seq(Result("Hamlet", 1.0))
  }
}
