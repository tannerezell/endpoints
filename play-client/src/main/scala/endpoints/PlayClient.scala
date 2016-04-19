package endpoints

import cats.data.Xor
import play.api.libs.ws.{WSClient, WSRequest, WSResponse}

import scala.concurrent.{ExecutionContext, Future}

abstract class PlayClient(wsClient: WSClient)(implicit ec: ExecutionContext) extends EndpointsAlg {

  class Path[A](val apply: A => String) extends PathOps[A]

  def staticPathSegment(segment: String) = new Path((_: Unit) => segment)

  def dynamicPathSegment = new Path((s: String) => s)

  def chainedPaths[A, B](first: Path[A], second: Path[B])(implicit fc: FlatConcat[A, B]): Path[fc.Out] =
    new Path((ab: fc.Out) => {
      val (a, b) = fc.unapply(ab)
      first.apply(a) ++ "/" ++ second.apply(b)
    })


  type Request[A] = A => Future[WSResponse]

  type RequestEntity[A] = (A, WSRequest) => Future[WSResponse]

  def get[A](path: Path[A]) =
    a => wsClient.url(path.apply(a)).get()

  def post[A, B](path: Path[A], entity: RequestEntity[B])(implicit fc: FlatConcat[A, B]): Request[fc.Out] =
    (ab: fc.Out) => {
      val (a, b) = fc.unapply(ab)
      val wsRequest = wsClient.url(path.apply(a))
      entity(b, wsRequest)
    }


  type Response[A] = WSResponse => Xor[Throwable, A]

  val emptyResponse: Response[Unit] = _ => Xor.Right(())


  type Endpoint[I, O] = I => Future[Xor[Throwable, O]]

  def endpoint[A, B](request: Request[A], response: Response[B]) =
    a => request(a).map(response)

}