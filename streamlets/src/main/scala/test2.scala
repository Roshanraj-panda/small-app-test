import java.nio.file.Paths

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.scaladsl.{Compression, FileIO, Flow, Sink}
import akka.util.ByteString
import generated.{Order, OrderList}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.Success
import scala.xml.XML


object test2 {
  def main(args: Array[String]) = {
 //   val conf = ConfigFactory.load("reference.conf")
//    //println(buffer)
//    implicit val systems = ActorSystem.apply ("S3stream")
//    implicit val mat: Materializer = ActorMaterializer()
//    val filename = List("""streamlets/src/main/resources/test.xml""")
 //   //val source = akka.stream.scaladsl.Source(filename).map(Source.fromFile(_).getLines().mkString).runWith(Sink.head)
  //  val source = akka.stream.scaladsl.Source(filename).map(Source.fromFile(_).getLines().mkString)
 //   val flow:Flow[String, List[Order], NotUsed] = Flow[String].mapAsync(1) {
 //     v =>
 //       val m = scalaxb.fromXML[OrderList](scala.xml.XML.loadString(v))
  //      //println(m)
  //     Future.successful(m.Order.toList)
  //  }.map {
  //    v => println(v)
  //  v
  //  }

    val file = Paths.get("streamlets/src/main/resources/OrdersExportFile_1_2020_06_30_02_49.xml.gz")
    implicit val systems = ActorSystem.apply ("S3stream")


    val source = FileIO.fromPath(file)
    val unzip:Flow[ByteString, ByteString, NotUsed] = Flow[ByteString].via(Compression.gunzip())
    val flow:Flow[ByteString, Seq[Order], NotUsed] = Flow[ByteString].map(_.utf8String).map {
      v =>
        val xml = XML.loadString(v)
        val olist = scalaxb.fromXML[OrderList](xml)
        olist.Order
    }
    import scala.concurrent.Await
    import scala.concurrent.duration._
     val m = source.via(unzip).via(flow).runWith(Sink.head)
   val t = Await.result(m, 5 seconds )
    val head = t.head
    val xml = scalaxb.toXML[Order](head, "order", generated.defaultScope)
    println(xml)

    println(t)
     val n = m.onComplete{
       case Success(v) => v.head
         val head = v.head
         val xml = scalaxb.toXML[Order](head, "order", generated.defaultScope)
         xml
     }

    //m.onComplete {
      //case scala.util.Success(v) => println(v)
     // case scala.util.Failure(t) => throw  t
   // }
println(n)


   // println(source)
   // source.onComplete {
     // case Success(v) => {
       // val m = scalaxb.fromXML[OrderList](scala.xml.XML.loadString(v))
       // m.Order.toList.map {
         // o => print(s"\n$o")
       // }
        //println(m)
      //}
     // case Failure(t) => throw t
    }

    //val paths = Paths.get("C:\\Users\\H895718\\Desktop\\Projects and Tasks\\SFCC\\cloned\\Test_Scala_Sbt\\streamlets\\src\\main\\resources\\test.xml")
    // val res = FileIO.fromPath(paths)
    //Thread.sleep(10000)
    //println(res)
     //val lines = Source.fromFile(filename).getLines.mkString
     //val xmlL = scala.xml.XML.loadString(lines)
    //println(xmlL)
    //val parse = scalaxb.fromXML[OrderList](xmlL)
    // println(parse)
    // val xm = scalaxb.toXML[OrderList](parse, None, "OrderList" , generated.defaultScope)
    // println(xm)


}
