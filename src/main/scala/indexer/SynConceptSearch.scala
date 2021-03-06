package indexer

import org.json.JSONObject
import util.Semantic

import scala.collection.JavaConverters._

object SynConceptSearch {
  def first(document: JSONObject, strings: Seq[String]): Option[String] = {
    strings.filter(
      (key) => document.has(key) && (
        Option(document.get(key)) match {
          case Some("") => false
          case None => false
          case _ => true
        }
        )
    ).headOption map {
      document.get(_).toString
    }
  }

  def truncate(distanceTitle: Double): String =
    (distanceTitle.toString.substring(0, 4))

  def main(args: Array[String]): Unit = {
    val w2v = new Semantic("D:\\projects\\clones\\pathToSaveModel1.txt")
    w2v.init

    import scala.collection.JavaConversions._
    val query =
      List("writing").map(
        (term) =>
          List(
            "auto_transcript_txt_en:\"" + term + "\"^10",
            "title_s:" + term + "^10"
          )
      ).flatten.mkString(" OR ") + " " +
      List("writing").map(
        (term) => {
          val pTerms: java.util.List[String] = List[String](term).asJava
          val nTerms: java.util.List[String] = List[String]().asJava
          val near =
            w2v.model.get.wordsNearest(pTerms, nTerms, 25)

          "(" +
            near.map(
              (term2: String) => {
                val distanceTranscript = (Math.PI - Math.acos(w2v.model.get.similarity(term, term2))) + 1
                val distanceTitle = (Math.PI - Math.acos(w2v.model.get.similarity(term, term2))) + 1
                "auto_transcript_txt_en:" + term2 + "^" + truncate(distanceTitle) +
                " OR title_s:" + term2 + "^" + truncate(distanceTitle)
              }
            ).mkString(" OR ") + ")"
        }
      ).mkString(" AND ")



    println(query)

    val solr = new Solr(null)
    val documentsSolr =
      solr.list(
        "talks",
        query,
        List("title_s"),
        10
      ).map(
        (document) =>
          document.get("title_s")
      ).mkString("\n")

    println(
      documentsSolr
    )
  }
}