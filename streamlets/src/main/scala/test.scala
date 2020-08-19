import akka.actor.ActorSystem
import akka.stream.alpakka.s3.scaladsl.S3Client
import akka.stream.alpakka.s3.{MemoryBufferType, S3Settings}
import akka.stream.scaladsl.Sink
import akka.stream.{ActorMaterializer, Materializer}
import com.amazonaws.auth.{AWSStaticCredentialsProvider, BasicAWSCredentials}
import com.amazonaws.regions.AwsRegionProvider
import scala.util.{Failure, Success}
//import akka.stream.scaladsl.Sink
//import scala.concurrent.Future
object test   {
  def main(args: Array[String]) = {

    implicit val systems = ActorSystem.apply ("S3stream")

    val access_key_id = "ASIAUCURAXPOSBBLRJCN"
    val secret_access_key = "V01f3C0iBUuLWz+flpDGOvAA+r30u3PKN8qNkJIe"
    val region = "us-east-1"
    val bucket = "hbc-createorder-test"

    //class s3settings(access_key_id: String, secret_access_key:String, region:String)
    object s3settings{
      def apply(access_key_id: String, secret_access_key:String, region:String): S3Settings = {
        val awsCredentials = new BasicAWSCredentials(access_key_id, secret_access_key)
        val awsCredentialsProvider = new AWSStaticCredentialsProvider(awsCredentials)
        val regionProvider = new AwsRegionProvider {
          def getRegion: String = region
        }
        new S3Settings(MemoryBufferType, None, awsCredentialsProvider, regionProvider, false, None)
      }
    }

    object S3client {
      def Client(access_id:String,access_key:String, region:String )(implicit  system: ActorSystem, materializer: Materializer): S3Client = {
        val setting = s3settings(access_id, access_key, region)
        new S3Client(setting)(system, materializer)
      }
    }
    implicit val mat: Materializer = ActorMaterializer()
    val S3 = S3client.Client(access_key_id,secret_access_key, region)
    val keySource = S3.listBucket(bucket, prefix = None).map(_.key).runWith(Sink.headOption)
    import scala.concurrent.ExecutionContext.Implicits.global
    keySource.onComplete{
      case Success(v) => println(v.head)
      case Failure(t) => throw t
    }
   // val filename = "C:\\Users\\H895718\\Desktop\\Projects and Tasks\\SFCC\\cloned\\Test_Scala_Sbt\\streamlets\\src\\main\\resources\\test.xml"
    //val source = akka.stream.scaladsl.Source(filename).map(Source.fromFile(_).getLines().mkString).runWith(Sink.foreach(println))
   //val paths = Paths.get("C:\\Users\\H895718\\Desktop\\Projects and Tasks\\SFCC\\cloned\\Test_Scala_Sbt\\streamlets\\src\\main\\resources\\test.xml")
   // val res = FileIO.fromPath(paths).via(XmlParsing.parser).map(.to(Sink.foreach(println)).run()
    //Thread.sleep(10000)
    //println(res)
   // val lines = Source.fromFile(filename).getLines.mkString
   // val xmlL = scala.xml.XML.loadString(lines)
    //println(xmlL)
    //val parse = scalaxb.fromXML[OrderList](xmlL)
  // println(parse)
   // val xm = scalaxb.toXML[OrderList](parse, None, "OrderList" , generated.defaultScope)
   // println(xm)
  }
}
