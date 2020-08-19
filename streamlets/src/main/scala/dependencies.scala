//import akka.actor.ActorSystem
//import akka.stream.Materializer
//import akka.stream.alpakka.s3.scaladsl.S3Client
//import akka.stream.alpakka.s3.{MemoryBufferType, S3Settings}
//import com.amazonaws.auth.{AWSStaticCredentialsProvider, BasicAWSCredentials}
//import com.amazonaws.regions.AwsRegionProvider


//object S3client {
//  def Client(access_id:String,access_key:String, region:String )(implicit  system: ActorSystem, materializer: Materializer): S3Client = {
//    val setting = s3settings(access_id, access_key, region)
//    new S3Client(setting)(system, materializer)
//  }
//}

//class s3settings(access_key_id: String, secret_access_key:String, region:String)
//object s3settings{
//  def apply(access_key_id: String, secret_access_key:String, region:String): S3Settings = {
//    val awsCredentials = new BasicAWSCredentials(access_key_id, secret_access_key)
//    val awsCredentialsProvider = new AWSStaticCredentialsProvider(awsCredentials)
//    val regionProvider = new AwsRegionProvider {
//      def getRegion: String = region
//    }
//    new S3Settings(MemoryBufferType, None, awsCredentialsProvider, regionProvider, false, None)
//  }
//}


