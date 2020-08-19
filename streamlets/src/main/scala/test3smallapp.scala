import java.nio.file.Paths
import java.sql.{Date, Timestamp}
import java.time.format.DateTimeFormatter
import java.time.{LocalDate, LocalDateTime}

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.alpakka.slick.scaladsl.{Slick, SlickSession}
import akka.stream.scaladsl.{FileIO, Flow, Sink, Source}
import akka.util.ByteString
import slick.jdbc.SetParameter
import spray.json.{DefaultJsonProtocol, _}

import scala.util.{Failure, Success}
import scala.concurrent.ExecutionContext.Implicits.global
object test3smallapp extends App {

  //////////////////

  case class HbcTransactionLog (
                                 Shopping_Transaction_SD: Seq[ShoppingTransactionSd],
                                 Shopping_Transaction_Line_SD: Seq[ShoppingTransactionLineSd],
                                 Payment_Line_SD: Seq[PaymentLineSd],
                                 Shopping_Transaction_SD_Update: Option[Seq[Shopping_Transaction_SD_Update]] = None
                               )

  case class PaymentLineSd (
                             ExecutionType: String,
                             DAY_DT: String,
                             SCAN_TIME: String,
                             STR_NBR: String,
                             REG_NBR: String,
                             TXN_NBR: String,
                             PaymentLineNum: String,
                             TenderType: Int,
                             TenderRefNbr: String,
                             TenderAmt: Int,
                             PaymentLineScanKeyIND: String,
                             TenderTypeOverrideCD: Int,
                             TenderRefNBROverrideCD: Int,
                             BillPlanNBR: String
                           )



  case class Shopping_Transaction_SD_Update (
                                            ExecutionType: String,
                                            WhereCondition: String,
                                            SetColumn: String
                                          )


  case class RootInterface (
                             HBCTransactionLog: HbcTransactionLog
                           )



  case class ShoppingTransactionLineSd (
                                         ExecutionType: String,
                                         DAY_DT: String,
                                         SCAN_TIME: String,
                                         STR_NBR: String,
                                         REG_NBR: String,
                                         TXN_NBR: String,
                                         ShoppingTransactionLineNum: String,
                                         SKU_NBR: Int,
                                         UPC_NBR: String,
                                         CAT_NBR: String,
                                         DEPT_NBR: String,
                                         ItemQty: String,
                                         OriginalUnitAmt: String,
                                         MarkdownAmt: String,
                                         DollarOffAmt: String,
                                         PercentOffAmt: String,
                                         NetItemAmt: String,
                                         GSTInd: String,
                                         PSTInd: String,
                                         LineItemVoidInd: String,
                                         LineItemScannedInd: String,
                                         LineItemNonDiscountInd: String,
                                         GiftEnclosureInd: String,
                                         LineItemStatusCD: String,
                                         StatusReasonCD: String,
                                         OverrideCD: String,
                                         RegistrationNBR: String,
                                         GR_NBR: String,
                                         GR_Item_NBR: String,
                                         ORG_TXN_DAY_DT: String,
                                         ORG_TXN_STR_NBR: Int,
                                         ORG_TXN_REG_NBR: Int,
                                         ORG_TXN_TXN_NBR: Int
                                       )



  case class ShoppingTransactionSd (
                                     ExecutionType: String,
                                     DAY_DT: String,
                                     SCAN_TIME: String,
                                     STR_NBR: String,
                                     REG_NBR: String,
                                     TXN_NBR: String,
                                     ShoppingTransactionType: String,
                                     OrgShoppingTransactionType: String,
                                     ShoppingTransactionStatus: Int,
                                     StatusReason: Int,
                                     ExchangeInd: Int,
                                     ReturnScannedInd: Int,
                                     AssociateNBR: String,
                                     AssociateDiscNBR: Int,
                                     PSTAmt: String,
                                     GSTAmt: String,
                                     OverrideCD: Int,
                                     ORG_TXN_DAY_DT: String,
                                     ORG_TXN_STR_NBR: String,
                                     ORG_TXN_REG_NBR: String,
                                     ORG_TXN_TXN_NBR: Int,
                                     TXN_Load_DT_Time: String
                                   )



  object HbcTransactionLogSer extends DefaultJsonProtocol   {

    implicit val paymentLineSdSer = jsonFormat14(PaymentLineSd)
    implicit val shoppingTransactionSdUpdateSer = jsonFormat3(Shopping_Transaction_SD_Update)
    implicit val shopingTransactionLinesSdSer = new RootJsonFormat[ShoppingTransactionLineSd] {
      def read(json: JsValue) = {
        val executiontype =json.asJsObject.getFields("ExecutionType").toList.head.toString
        val day_dt = json.asJsObject.getFields("DAY_DT").toList.head.convertTo[String]
        val scan_time = json.asJsObject.getFields("SCAN_TIME").toList.head.convertTo[String]
        val str_nbr = json.asJsObject.getFields("STR_NBR").toList.head.convertTo[String]
        val reg_nbr = json.asJsObject.getFields("REG_NBR").toList.head.convertTo[String]
        val txn_nbr = json.asJsObject.getFields("TXN_NBR").toList.head.convertTo[String]
        val shopingtransactionlineno = json.asJsObject.getFields("ShoppingTransactionLineNum").toList.head.convertTo[String]
        val sku_nbr = json.asJsObject.getFields("SKU_NBR").toList.head.toString
        val upc_nbr = json.asJsObject.getFields("UPC_NBR").toList.head.toString
        val cat_nbr = json.asJsObject.getFields("CAT_NBR").toList.head.convertTo[String]
        val dept_nbr = json.asJsObject.getFields("DEPT_NBR").toList.head.convertTo[String]
        val itemqty = json.asJsObject.getFields("ItemQty").toList.head.convertTo[String]
        val originalunitamt = json.asJsObject.getFields("OriginalUnitAmt").toList.head.convertTo[String]
        val markdownamt = json.asJsObject.getFields("MarkdownAmt").toList.head.convertTo[String]
        val dollaroffamt = json.asJsObject.getFields("DollarOffAmt").toList.head.convertTo[String]
        val percentoffamt = json.asJsObject.getFields("PercentOffAmt").toList.head.convertTo[String]
        val netitemamt = json.asJsObject.getFields("NetItemAmt").toList.head.convertTo[String]
        val gstind = json.asJsObject.getFields("GSTInd").toList.head.convertTo[String]
        val pstind = json.asJsObject.getFields("PSTInd").toList.head.convertTo[String]
        val lineitemvoidind = json.asJsObject.getFields("LineItemVoidInd").toList.head.convertTo[String]
        val lineitemscannedind = json.asJsObject.getFields("LineItemScannedInd").toList.head.convertTo[String]
        val lineitemnondiscountintd = json.asJsObject.getFields("LineItemNonDiscountInd").toList.head.convertTo[String]
        val giftenclouserind = json.asJsObject.getFields("GiftEnclosureInd").toList.head.convertTo[String]
        val lineitemstatuscd = json.asJsObject.getFields("LineItemStatusCD").toList.head.convertTo[String]
        val statusreasoncd = json.asJsObject.getFields("StatusReasonCD").toList.head.convertTo[String]
        val overidecd = json.asJsObject.getFields("OverrideCD").toList.head.convertTo[String]
        val regestrationnbr = json.asJsObject.getFields("RegistrationNBR").toList.head.convertTo[String]
        val gr_nbr = json.asJsObject.getFields("GR_NBR").toList.head.convertTo[String]
        val gr_item_nbr = json.asJsObject.getFields("GR_Item_NBR").toList.head.convertTo[String]
        val org_txn_day_dt = json.asJsObject.getFields("ORG_TXN_DAY_DT").toList.head.convertTo[String]
        val org_txn_str_nbr = json.asJsObject.getFields("ORG_TXN_STR_NBR").toList.head.toString
        val org_txnreg_nbr = json.asJsObject.getFields("ORG_TXN_REG_NBR").toList.head.toString
        val org_txn_txn_nbr = json.asJsObject.getFields("ORG_TXN_TXN_NBR").toList.head.toString

        ShoppingTransactionLineSd(executiontype,day_dt,scan_time,str_nbr,reg_nbr,txn_nbr,shopingtransactionlineno,sku_nbr.toInt,upc_nbr,cat_nbr,dept_nbr,itemqty,
          originalunitamt,markdownamt,dollaroffamt,percentoffamt,netitemamt,gstind,pstind,lineitemvoidind,lineitemscannedind,lineitemnondiscountintd,
          giftenclouserind,lineitemstatuscd,statusreasoncd,overidecd,regestrationnbr,gr_nbr,gr_item_nbr,org_txn_day_dt,
          org_txn_str_nbr.toInt,org_txnreg_nbr.toInt,org_txn_txn_nbr.toInt)

      }
      def write(obj: ShoppingTransactionLineSd) = JsObject()
    }
    implicit val shoppingTransactionSdsSer = jsonFormat22(ShoppingTransactionSd)
    implicit val hbcTransactionLogSer = jsonFormat4(HbcTransactionLog)
    implicit val rootInterfaceSer = jsonFormat1(RootInterface)
  }

def condition(str:String,k:Int):String = {
  val day_dt = str.split(",")(0).split("=")(1)
  val STR_NBR = str.split(",")(1).split("=")(1)
  val REG_NBR = str.split(",")(2).split("=")(1)
  val TXN_NBR = str.split(",")(3).split("=")(1)
  List(day_dt,STR_NBR,REG_NBR, TXN_NBR )(k)
}

  def setvalues(str:String) = {
    val st = str.split(",").map(v => set(v)).reduce((a,b) => a + "," + b)
    st
  }

  def set(str:String) = {
    val z = str.split("=")
    s"${z.head } = ${z(1).trim}"
  }

  /////////////////////////////////////////////////////////



  ////////////////////////////////////




 // println(date)


  val host = "localhost"
  val url = "jdbc:postgresql://localhost/test"
  val user = "postgres"
  val password = "postgres"




  implicit val system = ActorSystem.apply ("slick")


  implicit val session: SlickSession =
    SlickSession.forConfig("slick-postgres")
  system.registerOnTermination(() => session.close())
  import session.profile.api._
case class User(id:Int, username:String)

  import HbcTransactionLogSer._

  val file = Paths.get("streamlets/src/main/resources/test_message.json")
  val source = FileIO.fromPath(file)
  val flow1:Flow[ByteString,RootInterface,NotUsed ] = Flow[ByteString].map(_.utf8String).map {
   v =>  system.log.info(v)
v.parseJson.convertTo[RootInterface]
  }

  val flow2:Flow[RootInterface,Unit,NotUsed] = Flow[RootInterface].map {
    rf =>
    //println(rif.HBCTransactionLog.Payment_Line_SD.toList.head.DAY_DT)

      val src1 = Source(rf.HBCTransactionLog.Payment_Line_SD.toList)
        .via(
          Slick.flow {
            pls =>
              implicit val SetDate = SetParameter[java.time.LocalDate](
                (st,pp) => pp.setDate(Date.valueOf(st))
              )
              sqlu"INSERT INTO Payment_Line_SD VALUES(${LocalDate.parse(pls.DAY_DT.replace("'",""), DateTimeFormatter.ofPattern("yyyy-MM-dd"))},${pls.SCAN_TIME.toInt},${pls.STR_NBR.toInt},${pls.REG_NBR.toInt},${pls.TXN_NBR.toInt},${pls.PaymentLineNum.toInt},${pls.TenderType},${pls.TenderRefNbr},${BigDecimal.decimal(pls.TenderAmt)},${pls.PaymentLineScanKeyIND.toInt},${pls.TenderTypeOverrideCD},${pls.TenderRefNBROverrideCD},${pls.BillPlanNBR} )"
          }
        )
        .runWith(Sink.ignore)
      src1.onComplete {
        case Success(v) =>
          val msg =
            s"${v} no of records inserted for the table Payment_Line_SD"
          system.log.info(s"${msg}")
          msg
        case Failure(v) =>
          val fmsg = s"failed with ${v}"
          fmsg
      }

      val src2 = Source(
        rf.HBCTransactionLog.Shopping_Transaction_Line_SD.toList
      ).via(
        Slick.flow {
          pls =>
            implicit val SetDate = SetParameter[java.time.LocalDate](
              (st,pp) => pp.setDate(Date.valueOf(st))
            )
            sqlu"INSERT INTO Shopping_Transaction_Line_SD VALUES(${LocalDate.parse(pls.DAY_DT.replace("'", ""), DateTimeFormatter.ofPattern("yyyy-MM-dd"))},${pls.SCAN_TIME.toInt},${pls.STR_NBR.toInt},${pls.REG_NBR.toInt},${pls.TXN_NBR.toInt},${pls.ShoppingTransactionLineNum.toInt},${pls.SKU_NBR},${BigDecimal.decimal(pls.UPC_NBR.toFloat)},${pls.CAT_NBR.toInt},${pls.DEPT_NBR.toInt},${pls.ItemQty.toInt},${BigDecimal.decimal(pls.OriginalUnitAmt.toFloat)},${BigDecimal.decimal(pls.MarkdownAmt.toFloat)},${BigDecimal.decimal(pls.DollarOffAmt.toFloat)},${BigDecimal.decimal(pls.PercentOffAmt.toFloat)},${BigDecimal.decimal(pls.NetItemAmt.toFloat)},${pls.PSTInd.toInt},${pls.GSTInd.toInt},${pls.LineItemVoidInd.toInt},${pls.LineItemScannedInd.toInt},${pls.LineItemNonDiscountInd.toInt},${pls.GiftEnclosureInd.toInt},${pls.LineItemStatusCD.toInt},${pls.StatusReasonCD.toInt},${pls.OverrideCD.toInt},${BigDecimal.decimal(pls.RegistrationNBR.toInt)},${pls.GR_NBR.toInt},${pls.GR_Item_NBR.toInt},${LocalDate.parse(pls.ORG_TXN_DAY_DT.replace("'",""),DateTimeFormatter.ofPattern("yyyy-MM-dd"))},${pls.ORG_TXN_STR_NBR},${pls.ORG_TXN_REG_NBR},${pls.ORG_TXN_TXN_NBR})"
        }
      )
        .runWith(Sink.ignore)
      src2.onComplete {
        case Success(v) =>
          val msg =
            s"${v} no of records inserted for the table Shopping_Transaction_Line_SD"
          system.log.info(s"$msg")
          msg
        case Failure(v) =>
          val fmsg = s"failed with $v"
          fmsg
      }

      val src3 = Source(
        rf.HBCTransactionLog.Shopping_Transaction_SD.toList
      ).via(
        Slick.flow {
          pls =>
            implicit val SetDate = SetParameter[java.time.LocalDate](
              (st,pp) => pp.setDate(Date.valueOf(st))
            )
            implicit val SetTime = SetParameter[LocalDateTime](
              (st,pp) => pp.setTimestamp(Timestamp.valueOf(st))
            )
            sqlu"INSERT INTO shopping_transaction_sd VALUES(${LocalDate.parse(pls.DAY_DT.replace("'", ""), DateTimeFormatter.ofPattern("yyyy-MM-dd"))},${pls.SCAN_TIME.toInt},${pls.STR_NBR.toInt},${pls.REG_NBR.toInt},${pls.TXN_NBR.toInt},${pls.ShoppingTransactionType},${pls.OrgShoppingTransactionType},${pls.ShoppingTransactionStatus},${pls.StatusReason},${pls.ExchangeInd},${pls.ReturnScannedInd},${BigDecimal.decimal(pls.AssociateNBR.toFloat)},${BigDecimal.decimal(pls.AssociateDiscNBR)},${BigDecimal.decimal(pls.PSTAmt.toFloat)},${BigDecimal.decimal(pls.GSTAmt.toFloat)},${pls.OverrideCD},${LocalDate.parse(pls.ORG_TXN_DAY_DT.replace("'",""),DateTimeFormatter.ofPattern("yyyy-MM-dd"))},${pls.ORG_TXN_STR_NBR.toInt},${pls.ORG_TXN_REG_NBR.toInt},${pls.ORG_TXN_TXN_NBR},${LocalDateTime.now})"
        }
      )
        .runWith(Sink.ignore)
      src3.onComplete {
        case Success(v) =>
          val msg =
            s"${v} no of records inserted for the table Shopping_Transaction_SD"
          system.log.info(s"${msg}")
          msg
        case Failure(v) =>
          val fmsg = s"failed with ${v}"
          fmsg
      }
      if (rf.HBCTransactionLog.Shopping_Transaction_SD_Update.nonEmpty)
        rf.HBCTransactionLog.Shopping_Transaction_SD_Update.map {

          case r: Seq[Shopping_Transaction_SD_Update] =>
            val src4 = Source(r.toList)
              .via(Slick.flow { pls =>
                implicit val SetDate =
                  SetParameter[java.time.LocalDate](
                    (st, pp) => pp.setDate(Date.valueOf(st))
                  )
                sqlu"UPDATE  shopping_transaction_sd SET shoppingtransactiontype = 'NSVD' WHERE day_dt = ${LocalDate.parse(condition(pls.WhereCondition,0).replace("'",""),DateTimeFormatter.ofPattern("yyyy-MM-dd"))} AND str_nbr = ${condition(pls.WhereCondition,1).toInt} AND reg_nbr = ${condition(pls.WhereCondition,2).toInt} AND txn_nbr = ${condition(pls.WhereCondition,3).toInt}"
              })
              .runWith(Sink.ignore)
            src4.onComplete {
              case Success(v) =>
                val msg =
                  s"$v no of records updated for the table Shopping_Transaction_SD"
                system.log.info(s"$msg")
                msg
              case Failure(v) =>
                val fmsg = s"failed with $v"
                fmsg
            }
          case _ =>
            system.log.error(
              "Error Occured No record for shopping_transaction_sd_update."
            )
        }
  //    val users = (21 to 25).map(i => User(i, s"Name$i"))
  //    Source(users)
  //      .via(
  //  //add an optional first argument to specify the parallelism factor (Int)
  //  Slick.flow(user => sqlu"INSERT INTO test2 VALUES(${user.id}, ${user.username})")
  //      ).runWith(Sink.ignore)
  }

  source.via(flow1).via(flow2).runWith(Sink.ignore)

  // implicit val getUserResult = GetResult(r => User(r.nextInt, r.nextString))
 // val done: Future[Done] =
//    Slick
 //     .source(sql"SELECT  * from test2".as[User]).map {
 //     v =>
 //       val id = v.id
 //     system.log.info(s"id value if ${id}")
 //   }
 //     .log("user")
 //     .runWith(Sink.ignore)


  //val users = (11 to 15).map(i => User(i, s"Name$i"))
  //val done2: Future[Done] =
  //  Source(users)
  //    .via(
        //add an optional first argument to specify the parallelism factor (Int)
  //      Slick.flow(user => sqlu"INSERT INTO test2 VALUES(${user.id}, ${user.username})")
  //    ).log("nr-of-inserted-rows").runWith(Sink.ignore)

//println(s"value is ${done.value}")

  //////////////////////////

}



