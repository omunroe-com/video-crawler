package indexer

import java.io.{File, PrintWriter}

import org.json.{JSONArray, JSONObject}
import util.{Commands, Directory}

object MetadataCrawler {
  def main(args: Array[String]): Unit = {
    val cmd = new Commands
    cmd.withTempDirectory(
      new MetadataCrawler(_, "heavybit").run
    )
  }
}

class MetadataCrawler(directory: Directory, crawler: String) extends Crawler[Map[String, AnyRef]](directory) {
  lazy val config = {
    val cmd = new Commands
    new JSONObject(
      cmd.node("config.js " + crawler)
    )
  }

  override lazy val startPage = List(
    config.getString("start")
  )

  override lazy val nextPage =
    config.getString("nextPage")

  override lazy val domain =
    config.getString("domain")

  override lazy val dataPage =
    config.getString("dataPage")

  override lazy val maxPage: Integer = {
    if (config.has("maxPage")) {
      config.getInt("maxPage")
    } else {
      1000
    }
  }

  override def onPage(url: String, value: File): Map[String, AnyRef] = {
    val cmd = new Commands
    val jsonData = new JSONObject(
      cmd.node("metadata.js \"" + crawler + "\" \"" + value.toPath + "\"")
    )

    // todo youtube (heavybit doesn't need this, so skip for now)

    import scala.collection.JavaConverters._
    jsonData.toMap.asScala.toMap
  }

  override def onComplete(values: List[Map[String, AnyRef]]): Unit = {
    import scala.collection.JavaConverters._
    val listOfValues =
      values.map(
        m => m.asJava
      ).asJava

    val toSave = new JSONArray(
      listOfValues
    )

    //println(toSave.toString(2))

    new PrintWriter("C:\\projects\\image-annotation\\data\\talks\\" + crawler + ".json") {
      write(toSave.toString(2))
      close
    }

  }
}
